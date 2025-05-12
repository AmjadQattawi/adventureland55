package com.example.adventureland;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CardStatementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardTransactionAdapter adapter;
    private List<CardTransaction> fullList = new ArrayList<>();
    private DatabaseReference cardTransactionsRef, generalTransactionsRef;

    private CardView allTab, chargesTab, usageTab;
    private TextView tvAll, tvCharges, tvUsages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_statement);

        TextView balanceText = findViewById(R.id.balance_amount);
        TextView lastUsageText = findViewById(R.id.last_usage_value);
        TextView lastChargeText = findViewById(R.id.last_charge_value);

        String balance = getIntent().getStringExtra("balance");
        String lastUsage = getIntent().getStringExtra("lastUsage");
        String lastCharge = getIntent().getStringExtra("lastCharge");

        balanceText.setText(balance + " JOD");
        lastUsageText.setText(lastUsage);
        lastChargeText.setText(lastCharge);

        recyclerView = findViewById(R.id.recyclerViewCardTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CardTransactionAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        allTab = findViewById(R.id.tab_all);
        chargesTab = findViewById(R.id.charges_tab);
        usageTab = findViewById(R.id.usages_tab);

        tvAll = findViewById(R.id.tv_all);
        tvCharges = findViewById(R.id.tv_charges);
        tvUsages = findViewById(R.id.tv_usages);

        allTab.setOnClickListener(v -> changeTab("all"));
        chargesTab.setOnClickListener(v -> changeTab("charge"));
        usageTab.setOnClickListener(v -> changeTab("usage"));

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        String cardId = getIntent().getStringExtra("cardId");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cardTransactionsRef = FirebaseDatabase.getInstance()
                .getReference("users").child(userId).child("cards").child(cardId).child("transactions");

        generalTransactionsRef = FirebaseDatabase.getInstance()
                .getReference("users").child(userId).child("transactions");

        loadAllTransactions();
        changeTab("all");
    }

    private void loadAllTransactions() {
        fullList.clear();

        // حمل من معاملات البطاقة
        cardTransactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    CardTransaction transaction = snap.getValue(CardTransaction.class);
                    if (transaction != null) {
                        fullList.add(transaction);
                    }
                }

                // ثم من المعاملات العامة (مثل: redeem)
                generalTransactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            CardTransaction transaction = snap.getValue(CardTransaction.class);
                            if (transaction != null && "charge".equalsIgnoreCase(transaction.getType())) {
                                fullList.add(transaction);
                            }
                        }
                        changeTab("all");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(CardStatementActivity.this, "Failed to load extra charges", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CardStatementActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeTab(String type) {
        int selectedColor = getResources().getColor(R.color.purple);
        int defaultColor = getResources().getColor(R.color.very_light_purple);
        int white = getResources().getColor(R.color.white);
        int textDefault = getResources().getColor(R.color.Default);

        allTab.setCardBackgroundColor(defaultColor);
        chargesTab.setCardBackgroundColor(defaultColor);
        usageTab.setCardBackgroundColor(defaultColor);

        tvAll.setTextColor(textDefault);
        tvCharges.setTextColor(textDefault);
        tvUsages.setTextColor(textDefault);

        switch (type) {
            case "all":
                allTab.setCardBackgroundColor(selectedColor);
                tvAll.setTextColor(white);
                adapter.updateList(fullList);
                break;

            case "charge":
                chargesTab.setCardBackgroundColor(selectedColor);
                tvCharges.setTextColor(white);
                filterListByType("charge");
                break;

            case "usage":
                usageTab.setCardBackgroundColor(selectedColor);
                tvUsages.setTextColor(white);
                filterListByType("usage");
                break;
        }
    }

    private void filterListByType(String type) {
        List<CardTransaction> filtered = new ArrayList<>();
        for (CardTransaction tx : fullList) {
            if (tx.getType().equalsIgnoreCase(type)) {
                filtered.add(tx);
            }
        }
        adapter.updateList(filtered);
    }
}
