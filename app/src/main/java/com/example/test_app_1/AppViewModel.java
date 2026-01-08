package com.example.test_app_1;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppViewModel extends AndroidViewModel {
    private AppRepository mRepository;
    private LiveData<List<AppRisk>> mAllAppRisks;
    private MediatorLiveData<DashboardState> mDashboardState = new MediatorLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String TAG = "AppViewModel";

    public AppViewModel(@NonNull Application application) {
        super(application);
        mRepository = new AppRepository(application);
        mAllAppRisks = mRepository.getAllAppRisks();
        
        LiveData<Integer> highRiskCount = mRepository.getHighRiskCount();
        LiveData<Integer> newRiskCount = mRepository.getNewRiskCount();
        
        mDashboardState.addSource(highRiskCount, count -> combineDashboardState(count, newRiskCount.getValue()));
        mDashboardState.addSource(newRiskCount, count -> combineDashboardState(highRiskCount.getValue(), count));
    }
    
    private void combineDashboardState(Integer high, Integer newRisk) {
        int h = (high == null) ? 0 : high;
        int n = (newRisk == null) ? 0 : newRisk;
        mDashboardState.setValue(new DashboardState(h, n));
    }

    LiveData<List<AppRisk>> getAllAppRisks() {
        return mAllAppRisks;
    }
    
    LiveData<DashboardState> getDashboardState() {
        return mDashboardState;
    }

    public void scanApps() {
        executorService.execute(() -> {
            try {
                PackageManager pm = getApplication().getPackageManager();
                // Flag 128 is GET_META_DATA
                List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                
                // Fetch existing data to compare for changes
                List<AppRisk> existingRisks = mRepository.getAppRisksSync();
                Map<String, Set<String>> existingPermissionsMap = new HashMap<>();
                
                if (existingRisks != null) {
                    for (AppRisk risk : existingRisks) {
                        Set<String> perms = new HashSet<>();
                        if (risk.permissions != null && !risk.permissions.isEmpty()) {
                            String[] split = risk.permissions.split(",");
                            for (String s : split) perms.add(s);
                        }
                        existingPermissionsMap.put(risk.packageName, perms);
                    }
                }

                List<AppRisk> newRisksList = new ArrayList<>();

                for (ApplicationInfo appInfo : packages) {
                    if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        continue; // Skip system apps
                    }
                    
                    try {
                        PackageInfo packageInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS);
                        String[] requestedPermissions = packageInfo.requestedPermissions;
                        
                        if (requestedPermissions != null) {
                            List<String> grantedPermissions = new ArrayList<>();
                            String riskLevel = "LOW";
                            int riskPriority = 1;
                            
                            for (int i = 0; i < requestedPermissions.length; i++) {
                                if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                                    String perm = requestedPermissions[i];
                                    grantedPermissions.add(perm);
                                    
                                    try {
                                        PermissionInfo permInfo = pm.getPermissionInfo(perm, 0);
                                        // Use bitwise check for protection level
                                        int protectionLevel = permInfo.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE;
                                        boolean isDangerous = protectionLevel == PermissionInfo.PROTECTION_DANGEROUS;
                                        
                                        if (isDangerous) {
                                             if (perm.contains("CAMERA") || perm.contains("LOCATION") || perm.contains("RECORD_AUDIO") || perm.contains("CONTACTS")) {
                                                 riskLevel = "HIGH";
                                                 riskPriority = 3;
                                             } else if (riskPriority < 2) {
                                                 riskLevel = "MEDIUM";
                                                 riskPriority = 2;
                                             }
                                        }
                                    } catch (PackageManager.NameNotFoundException e) {
                                        // Ignore
                                    }
                                }
                            }
                            
                            if (!grantedPermissions.isEmpty()) {
                                // Use TextUtils.join for compatibility across all API levels
                                String permString = TextUtils.join(",", grantedPermissions);
                                
                                AppRisk appRisk = new AppRisk(
                                        appInfo.packageName, 
                                        pm.getApplicationLabel(appInfo).toString(), 
                                        riskLevel, 
                                        riskPriority,
                                        permString, 
                                        System.currentTimeMillis());
                                
                                // Check for new permissions
                                if (existingPermissionsMap.containsKey(appInfo.packageName)) {
                                    Set<String> oldPerms = existingPermissionsMap.get(appInfo.packageName);
                                    List<String> newPerms = new ArrayList<>();
                                    for (String p : grantedPermissions) {
                                        if (!oldPerms.contains(p)) {
                                            newPerms.add(p);
                                        }
                                    }
                                    
                                    if (!newPerms.isEmpty()) {
                                        appRisk.isNewRisk = true;
                                        appRisk.riskChangeDescription = "New permissions granted: " + newPerms.size();
                                    }
                                } else if (!existingPermissionsMap.isEmpty()) {
                                    appRisk.isNewRisk = true;
                                    appRisk.riskChangeDescription = "New app detected";
                                }
                                
                                newRisksList.add(appRisk);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing package: " + appInfo.packageName, e);
                    }
                }
                
                mRepository.replaceRisks(newRisksList);
                
            } catch (Exception e) {
                Log.e(TAG, "Error in scanApps", e);
            }
        });
    }
}