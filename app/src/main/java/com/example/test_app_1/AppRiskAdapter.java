package com.example.test_app_1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppRiskAdapter extends RecyclerView.Adapter<AppRiskAdapter.AppRiskViewHolder> {

    private List<AppRisk> mAppRisks;
    private final Context mContext;

    public AppRiskAdapter(Context context) {
        mContext = context;
        mAppRisks = new ArrayList<>();
    }

    @NonNull
    @Override
    public AppRiskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_risk, parent, false);
        return new AppRiskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppRiskViewHolder holder, int position) {
        if (mAppRisks != null) {
            AppRisk current = mAppRisks.get(position);
            holder.appNameItemView.setText(current.appName);
            holder.riskLevelItemView.setText(current.riskLevel);
            
            // App Icon
            try {
                Drawable icon = mContext.getPackageManager().getApplicationIcon(current.packageName);
                holder.appIconItemView.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                holder.appIconItemView.setImageResource(android.R.drawable.sym_def_app_icon);
            }

            if ("HIGH".equals(current.riskLevel)) {
                holder.riskLevelItemView.setTextColor(Color.RED);
                holder.riskDescriptionItemView.setText("Critical permissions granted");
            } else if ("MEDIUM".equals(current.riskLevel)) {
                holder.riskLevelItemView.setTextColor(Color.parseColor("#FFA500")); // Orange
                holder.riskDescriptionItemView.setText("Some dangerous permissions");
            } else {
                holder.riskLevelItemView.setTextColor(Color.GREEN);
                holder.riskDescriptionItemView.setText("Few or no dangerous permissions");
            }
            
            if (current.isNewRisk) {
                holder.changeTrackerItemView.setVisibility(View.VISIBLE);
                holder.changeTrackerItemView.setText(current.riskChangeDescription);
            } else {
                holder.changeTrackerItemView.setVisibility(View.GONE);
            }
            
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, AppDetailActivity.class);
                intent.putExtra("package_name", current.packageName);
                intent.putExtra("app_name", current.appName);
                intent.putExtra("permissions", current.permissions);
                mContext.startActivity(intent);
            });

            // Quick Actions (Context Menu)
            holder.itemView.setOnLongClickListener(v -> {
                PopupMenu popup = new PopupMenu(mContext, holder.itemView);
                popup.getMenu().add("View Details");
                // Add more actions here if needed
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("View Details")) {
                         Intent intent = new Intent(mContext, AppDetailActivity.class);
                         intent.putExtra("package_name", current.packageName);
                         intent.putExtra("app_name", current.appName);
                         intent.putExtra("permissions", current.permissions);
                         mContext.startActivity(intent);
                    }
                    return true;
                });
                popup.show();
                return true;
            });

        } else {
            holder.appNameItemView.setText("No App");
        }
    }

    void setAppRisks(List<AppRisk> newAppRisks) {
        if (mAppRisks == null) {
            mAppRisks = new ArrayList<>();
        }
        
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new AppRiskDiffCallback(mAppRisks, newAppRisks));
        mAppRisks = new ArrayList<>(newAppRisks);
        diffResult.dispatchUpdatesTo(this);
    }
    
    // Sorting helper methods
    public void sortByName(boolean ascending) {
        List<AppRisk> sortedList = new ArrayList<>(mAppRisks);
        Collections.sort(sortedList, (o1, o2) -> {
            if (ascending) return o1.appName.compareToIgnoreCase(o2.appName);
            else return o2.appName.compareToIgnoreCase(o1.appName);
        });
        setAppRisks(sortedList);
    }
    
    public void sortByRisk(boolean highToLow) {
        List<AppRisk> sortedList = new ArrayList<>(mAppRisks);
        Collections.sort(sortedList, (o1, o2) -> {
            if (highToLow) return Integer.compare(o2.riskPriority, o1.riskPriority);
            else return Integer.compare(o1.riskPriority, o2.riskPriority);
        });
        setAppRisks(sortedList);
    }

    @Override
    public int getItemCount() {
        if (mAppRisks != null)
            return mAppRisks.size();
        else return 0;
    }

    class AppRiskViewHolder extends RecyclerView.ViewHolder {
        private final TextView appNameItemView;
        private final TextView riskLevelItemView;
        private final TextView riskDescriptionItemView;
        private final TextView changeTrackerItemView;
        private final ImageView appIconItemView;

        private AppRiskViewHolder(View itemView) {
            super(itemView);
            appNameItemView = itemView.findViewById(R.id.appName);
            riskLevelItemView = itemView.findViewById(R.id.riskLevel);
            riskDescriptionItemView = itemView.findViewById(R.id.riskDescription);
            changeTrackerItemView = itemView.findViewById(R.id.changeTracker);
            appIconItemView = itemView.findViewById(R.id.appIcon);
        }
    }
    
    static class AppRiskDiffCallback extends DiffUtil.Callback {
        private final List<AppRisk> oldList;
        private final List<AppRisk> newList;

        public AppRiskDiffCallback(List<AppRisk> oldList, List<AppRisk> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).id == newList.get(newItemPosition).id;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            AppRisk oldItem = oldList.get(oldItemPosition);
            AppRisk newItem = newList.get(newItemPosition);
            return oldItem.appName.equals(newItem.appName) &&
                   oldItem.riskLevel.equals(newItem.riskLevel) &&
                   oldItem.riskPriority == newItem.riskPriority &&
                   oldItem.isNewRisk == newItem.isNewRisk;
        }
    }
}