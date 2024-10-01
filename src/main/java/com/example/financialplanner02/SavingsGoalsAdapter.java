package com.example.financialplanner02;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SavingsGoalsAdapter extends RecyclerView.Adapter<SavingsGoalsAdapter.SavingsGoalViewHolder> {
    private List<SavingsGoal> savingsGoalList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public SavingsGoalsAdapter(List<SavingsGoal> savingsGoalList, OnItemClickListener listener) {
        this.savingsGoalList = savingsGoalList;
        this.onItemClickListener = listener;
    }

    public static class SavingsGoalViewHolder extends RecyclerView.ViewHolder {
        public TextView savingsGoalName;

        public SavingsGoalViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            savingsGoalName = itemView.findViewById(R.id.savings_goal_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public SavingsGoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.savings_goal_item, parent, false);
        return new SavingsGoalViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SavingsGoalViewHolder holder, int position) {
        SavingsGoal currentItem = savingsGoalList.get(position);
        holder.savingsGoalName.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return savingsGoalList.size();
    }
}
