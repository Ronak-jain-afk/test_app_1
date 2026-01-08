package com.example.test_app_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RiskListFragment extends Fragment {

    private static final String ARG_RISK_LEVEL = "risk_level";
    private String mRiskLevel; // "HIGH", "MEDIUM", "LOW"
    private AppRiskAdapter mAdapter;

    public static RiskListFragment newInstance(String riskLevel) {
        RiskListFragment fragment = new RiskListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RISK_LEVEL, riskLevel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mRiskLevel = getArguments().getString(ARG_RISK_LEVEL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_risk_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AppRiskAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppViewModel viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        viewModel.getAllAppRisks().observe(getViewLifecycleOwner(), appRisks -> {
            if (appRisks != null) {
                List<AppRisk> filteredList = new ArrayList<>();
                for (AppRisk risk : appRisks) {
                    if (risk.riskLevel.equals(mRiskLevel)) {
                        filteredList.add(risk);
                    }
                }
                mAdapter.setAppRisks(filteredList);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_risk_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sort_name) {
            mAdapter.sortByName(true);
            return true;
        } else if (item.getItemId() == R.id.action_sort_risk) {
            mAdapter.sortByRisk(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}