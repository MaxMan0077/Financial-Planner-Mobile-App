package com.example.financialplanner02;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IncomeSourcesAdapter extends RecyclerView.Adapter<IncomeSourcesAdapter.IncomeSourceViewHolder> {

    private List<IncomeSource> incomeSources;

    public IncomeSourcesAdapter(List<IncomeSource> incomeSources) {
        this.incomeSources = incomeSources;
    }

    public IncomeSource getIncomeSource(int position) {
        return incomeSources.get(position);
    }

    @NonNull
    @Override
    public IncomeSourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_income_source, parent, false);
        return new IncomeSourceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeSourceViewHolder holder, int position) {
        IncomeSource incomeSource = incomeSources.get(position);
        holder.nameTextView.setText(incomeSource.getName());
        holder.amountTextView.setText(String.format("£%.2f", incomeSource.getAmount()));
    }

    @Override
    public int getItemCount() {
        return incomeSources.size();
    }

    public static class IncomeSourceViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView amountTextView;

        public IncomeSourceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
        }
    }

    public void removeItem(int position) {
        incomeSources.remove(position);
        notifyItemRemoved(position);
    }
}
