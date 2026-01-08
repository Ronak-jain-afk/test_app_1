package com.example.test_app_1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface AppRiskDao {
    @Query("SELECT * FROM app_risks ORDER BY riskPriority DESC, appName ASC")
    LiveData<List<AppRisk>> getAllAppRisks();

    @Query("SELECT * FROM app_risks")
    List<AppRisk> getAppRisksSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppRisk appRisk);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AppRisk> appRisks);

    @Query("DELETE FROM app_risks")
    void deleteAll();
    
    @Query("SELECT COUNT(*) FROM app_risks WHERE riskLevel = 'HIGH'")
    LiveData<Integer> getHighRiskCount();

    @Query("SELECT COUNT(*) FROM app_risks WHERE isNewRisk = 1")
    LiveData<Integer> getNewRiskCount();
}