package com.example.adventureland;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.transactionAction.setText(transaction.getType());
        holder.transactionPoints.setText(String.valueOf(transaction.getPoints()));
        holder.transactionDate.setText(transaction.getDate());

        if ("earned".equals(transaction.getType())) {
            holder.transactionIndicator.setBackgroundColor(Color.GREEN);
        } else if ("redeemed".equals(transaction.getType())) {
            holder.transactionIndicator.setBackgroundColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    // دالة لتحديث القائمة
    public void updateList(List<Transaction> newList) {
        transactionList = newList;
        notifyDataSetChanged();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionAction, transactionPoints, transactionDate;
        View transactionIndicator;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            transactionAction = itemView.findViewById(R.id.transaction_action);
            transactionPoints = itemView.findViewById(R.id.transaction_points);
            transactionDate = itemView.findViewById(R.id.transaction_date);
            transactionIndicator = itemView.findViewById(R.id.transaction_indicator);
        }
    }
}
