package com.example.test_app_1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private AppViewModel mAppViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup ViewPager2 and TabLayout
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        
        RiskPagerAdapter pagerAdapter = new RiskPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("High Risk"); break;
                case 1: tab.setText("Medium Risk"); break;
                case 2: tab.setText("Low Risk"); break;
            }
        }).attach();

        mAppViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        
        TextView riskCountText = findViewById(R.id.riskCountText);
        
        mAppViewModel.getDashboardState().observe(this, state -> {
            if (state != null) {
                updateDashboardText(riskCountText, state.highRiskCount, state.newRiskCount);
            }
        });

        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            mAppViewModel.scanApps();
        });
        
        // Initial scan
        mAppViewModel.scanApps();
    }
    
    private void updateDashboardText(TextView textView, int highRiskCount, int newRiskCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("Total High Risk Apps: ").append(highRiskCount).append("\n");
        if (newRiskCount > 0) {
            sb.append("Recent Changes: ").append(newRiskCount).append(" apps have new risks!");
        } else {
             sb.append("Recent Changes: No new risks detected.");
        }
        
        textView.setText(sb.toString());
    }

    private static class RiskPagerAdapter extends FragmentStateAdapter {
        public RiskPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return RiskListFragment.newInstance("HIGH");
                case 1: return RiskListFragment.newInstance("MEDIUM");
                default: return RiskListFragment.newInstance("LOW");
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}