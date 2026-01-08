package com.example.test_app_1;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AppRepository {
    private AppRiskDao mAppRiskDao;
    private LiveData<List<AppRisk>> mAllAppRisks;
    private AppDatabase mDb;

    AppRepository(Application application) {
        mDb = AppDatabase.getDatabase(application);
        mAppRiskDao = mDb.appRiskDao();
        mAllAppRisks = mAppRiskDao.getAllAppRisks();
    }

    LiveData<List<AppRisk>> getAllAppRisks() {
        return mAllAppRisks;
    }
    
    List<AppRisk> getAppRisksSync() {
        return mAppRiskDao.getAppRisksSync();
    }
    
    LiveData<Integer> getHighRiskCount() {
        return mAppRiskDao.getHighRiskCount();
    }

    LiveData<Integer> getNewRiskCount() {
        return mAppRiskDao.getNewRiskCount();
    }

    void replaceRisks(List<AppRisk> appRisks) {
        // Run in transaction on background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDb.runInTransaction(() -> {
                mAppRiskDao.deleteAll();
                mAppRiskDao.insertAll(appRisks);
            });
        });
    }
    
    void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mAppRiskDao.deleteAll();
        });
    }
}