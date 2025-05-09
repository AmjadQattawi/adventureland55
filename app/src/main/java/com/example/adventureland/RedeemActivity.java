package com.example.adventureland;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RedeemActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RedeemAdapter redeemAdapter;
    private ArrayList<RedeemItem> redeemItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redeem);

        recyclerView = findViewById(R.id.recyclerViewOffers);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));



        redeemItemList = new ArrayList<>();
        // تـجـربـة
        redeemItemList.add(new RedeemItem("5 JOD Play card", "100", R.drawable.giftcard));
        redeemItemList.add(new RedeemItem("10 JOD Play card", "200", R.drawable.giftcard));
        redeemItemList.add(new RedeemItem("25 JOD Play card", "400", R.drawable.giftcard));
        redeemItemList.add(new RedeemItem("50 JOD Play card", "700", R.drawable.giftcard));

        redeemAdapter = new RedeemAdapter(this, redeemItemList);
        recyclerView.setAdapter(redeemAdapter);

        ImageView backArrow = findViewById(R.id.back_button);
        backArrow.setOnClickListener(v -> finish());
    }
}
