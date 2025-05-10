package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class RewardsActivity extends AppCompatActivity {

    private TextView pointsTextView, allTab, earnedTab, spentTab;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private DatabaseReference transactionsRef, pointsRef;
    private CardView allTabCard, earnedTabCard, spentTabCard, redeemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewards);

        pointsTextView = findViewById(R.id.points_value);
        recyclerView = findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allTabCard = findViewById(R.id.all_tab);
        earnedTabCard = findViewById(R.id.earned_tab);
        spentTabCard = findViewById(R.id.spent_tab);
        redeemButton = findViewById(R.id.redeem_button);

        allTab = findViewById(R.id.all_tab_text);
        earnedTab = findViewById(R.id.tv_earned_tab);
        spentTab = findViewById(R.id.tv_spent_tab);

        allTabCard.setOnClickListener(v -> changeTab("all"));
        earnedTabCard.setOnClickListener(v -> changeTab("earned"));
        spentTabCard.setOnClickListener(v -> changeTab("spent"));

        redeemButton.setOnClickListener(v -> {
            Intent intent = new Intent(RewardsActivity.this, RedeemActivity.class);
            startActivity(intent);
        });

        transactionList = new ArrayList<>();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        transactionsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("transactions");
        pointsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("points");

        // تحميل المعاملات
        transactionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    transactionList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Transaction transaction = snapshot.getValue(Transaction.class);
                        if (transaction != null) {
                            transactionList.add(transaction);
                        }
                    }

                    if (adapter == null) {
                        adapter = new TransactionAdapter(transactionList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateList(transactionList);
                    }

                } catch (Exception e) {
                    Log.e("FirebaseError", "Error reading data", e);
                    Toast.makeText(RewardsActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error loading data", databaseError.toException());
                Toast.makeText(RewardsActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        });

        // تحميل النقاط من المسار الصحيح
        pointsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Long points = snapshot.getValue(Long.class);
                if (points != null) {
                    pointsTextView.setText(String.valueOf(points));
                } else {
                    pointsTextView.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RewardsActivity.this, "Failed to load points", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView backArrow = findViewById(R.id.back_button);
        backArrow.setOnClickListener(v -> finish());

        changeTab("all");
    }

    private void changeTab(String tab) {
        allTabCard.setCardBackgroundColor(getResources().getColor(R.color.very_light_purple));
        earnedTabCard.setCardBackgroundColor(getResources().getColor(R.color.very_light_purple));
        spentTabCard.setCardBackgroundColor(getResources().getColor(R.color.very_light_purple));

        allTab.setTextColor(getResources().getColor(R.color.Default));
        earnedTab.setTextColor(getResources().getColor(R.color.Default));
        spentTab.setTextColor(getResources().getColor(R.color.Default));

        switch (tab) {
            case "all":
                allTabCard.setCardBackgroundColor(getResources().getColor(R.color.purple));
                allTab.setTextColor(getResources().getColor(R.color.white));
                filterTransactions("all");
                break;
            case "earned":
                earnedTabCard.setCardBackgroundColor(getResources().getColor(R.color.purple));
                earnedTab.setTextColor(getResources().getColor(R.color.white));
                filterTransactions("earned");
                break;
            case "spent":
                spentTabCard.setCardBackgroundColor(getResources().getColor(R.color.purple));
                spentTab.setTextColor(getResources().getColor(R.color.white));
                filterTransactions("spent");
                break;
        }
    }

    private void filterTransactions(String tab) {
        List<Transaction> filteredList = new ArrayList<>();

        for (Transaction transaction : transactionList) {
            if (tab.equals("all") || (tab.equals("earned") && transaction.getType().equals("earned")) ||
                    (tab.equals("spent") && transaction.getType().equals("spent"))) {
                filteredList.add(transaction);
            }
        }

        if (adapter != null) {
            adapter.updateList(filteredList);
        } else {
            Log.e("AdapterError", "Adapter is not initialized");
        }
    }
}
