package com.example.adventureland;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BirthdayOffersActivity extends AppCompatActivity {

    private RecyclerView offerRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_offers);

        offerRecyclerView = findViewById(R.id.birthdayOfferRecyclerView);
        offerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("Birthday kid plays for free", "With minimum 5 guests"));
        offers.add(new Offer("Free party room with 10+ guests", "Reservation required 1 day in advance"));
        offers.add(new Offer("Buy 5 tickets, get 3 free", "Valid on same day use"));
        offers.add(new Offer("Birthday package: JD 60 for 3 hours", "Full access for all guests"));
        offers.add(new Offer("Free gift for every birthday child", "Ask our staff at the front desk"));

        OfferAdapter adapter = new OfferAdapter(this, offers, "birthday");
        offerRecyclerView.setAdapter(adapter);

        ImageView back = findViewById(R.id.back_button);
        back.setOnClickListener(v -> finish());
    }
}
