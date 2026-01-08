package com.example.test_app_1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class AppDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String packageName = getIntent().getStringExtra("package_name");
        String appName = getIntent().getStringExtra("app_name");
        String permissions = getIntent().getStringExtra("permissions");

        TextView appNameView = findViewById(R.id.detailAppName);
        TextView permissionsListView = findViewById(R.id.permissionsList);
        TextView riskExplanationView = findViewById(R.id.riskExplanation);
        Button openSettingsButton = findViewById(R.id.openSettingsButton);

        appNameView.setText(appName);
        
        if (permissions != null && !permissions.isEmpty()) {
            String formattedPermissions = permissions.replace(",", "\n• ");
            permissionsListView.setText("• " + formattedPermissions);
            
            // Generate Risk Explanation based on permissions
            riskExplanationView.setText(generateRiskExplanation(permissions));
        } else {
            permissionsListView.setText("No dangerous permissions granted.");
            riskExplanationView.setText("This app does not have any known dangerous permissions.");
            riskExplanationView.setTextColor(getColor(android.R.color.darker_gray));
        }

        openSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", packageName, null);
            intent.setData(uri);
            startActivity(intent);
        });
    }

    private String generateRiskExplanation(String permissions) {
        StringBuilder explanation = new StringBuilder();
        
        if (permissions.contains("CAMERA")) {
            explanation.append("• Camera: Can take photos and record videos at any time.\n");
        }
        if (permissions.contains("RECORD_AUDIO") || permissions.contains("MICROPHONE")) {
            explanation.append("• Microphone: Can record audio at any time.\n");
        }
        if (permissions.contains("ACCESS_FINE_LOCATION") || permissions.contains("ACCESS_COARSE_LOCATION")) {
            explanation.append("• Location: Can track your precise movement and location.\n");
        }
        if (permissions.contains("READ_CONTACTS")) {
            explanation.append("• Contacts: Can read your contact list and share it.\n");
        }
        if (permissions.contains("READ_SMS") || permissions.contains("RECEIVE_SMS")) {
            explanation.append("• SMS: Can read your text messages, including 2FA codes.\n");
        }
        if (permissions.contains("READ_CALL_LOG")) {
            explanation.append("• Call Log: Can see who you call and who calls you.\n");
        }
        if (permissions.contains("READ_EXTERNAL_STORAGE") || permissions.contains("WRITE_EXTERNAL_STORAGE")) {
             explanation.append("• Storage: Can access your files and photos.\n");
        }
        
        if (explanation.length() == 0) {
            return "This app has permissions that are considered risky, but specific details for the ones listed are not in our high-priority database.";
        }
        
        return explanation.toString().trim();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}