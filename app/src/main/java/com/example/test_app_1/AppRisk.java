package com.example.test_app_1;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "app_risks")
public class AppRisk {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String packageName;
    public String appName;
    public String riskLevel; // HIGH, MEDIUM, LOW
    public int riskPriority; // 3=HIGH, 2=MEDIUM, 1=LOW
    public String permissions; // JSON or comma separated string
    public long lastScanTime;
    public boolean isNewRisk;
    public String riskChangeDescription;

    public AppRisk() {
        // Default constructor for Room
    }

    @Ignore
    public AppRisk(String packageName, String appName, String riskLevel, int riskPriority, String permissions, long lastScanTime) {
        this.packageName = packageName;
        this.appName = appName;
        this.riskLevel = riskLevel;
        this.riskPriority = riskPriority;
        this.permissions = permissions;
        this.lastScanTime = lastScanTime;
        this.isNewRisk = false;
        this.riskChangeDescription = "";
    }
}