package com.example.adventureland;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardTransactionAdapter extends RecyclerView.Adapter<CardTransactionAdapter.ViewHolder> {

    private Context context;
    private List<CardTransaction> transactions;

    public CardTransactionAdapter(Context context, List<CardTransaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public CardTransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardTransactionAdapter.ViewHolder holder, int position) {
        CardTransaction tx = transactions.get(position);
        holder.title.setText(tx.getTitle());
        holder.date.setText(tx.getDate());

        // تحديد الشكل حسب نوع العملية
        if ("reward".equalsIgnoreCase(tx.getType())) {
            holder.amount.setText("+" + tx.getAmount());
            holder.amount.setTextColor(Color.parseColor("#2ECC71")); // أخضر
        } else if ("charge".equalsIgnoreCase(tx.getType())) {
            holder.amount.setText("+" + tx.getAmount());
            holder.amount.setTextColor(Color.parseColor("#3F3A8A")); // بنفسجي غامق
        } else {
            holder.amount.setText(tx.getAmount() + " JOD");
            holder.amount.setTextColor(Color.DKGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cardTransaction_action);
            amount = itemView.findViewById(R.id.transaction_amount);
            date = itemView.findViewById(R.id.cardTransaction_date);
        }
    }

    public void updateList(List<CardTransaction> newList) {
        this.transactions = newList;
        notifyDataSetChanged();
    }

}
