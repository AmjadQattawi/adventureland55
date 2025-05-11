package com.example.adventureland;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IndividualOffersActivity extends AppCompatActivity {

    private RecyclerView offerRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_offers);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());


        offerRecyclerView = findViewById(R.id.IndOfferRecyclerView);
        offerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("Recharge 20 JD and Play for 60 JD!", "Offer valid until the end of the month"));
        offers.add(new Offer("Recharge 30 JD and Play for 130 JD!", "Offer valid until the end of the month"));
        offers.add(new Offer("Recharge 50 JD and Play for 250 JD!", "Offer valid until the end of the month"));
        offers.add(new Offer("Recharge 75 JD and Play for 400 JD!", "Offer valid until the end of the month"));
        offers.add(new Offer("Recharge 100 JD and Play for 600 JD!", "Offer valid until the end of the month"));

        OfferAdapter adapter = new OfferAdapter(this, offers, "individual");
        offerRecyclerView.setAdapter(adapter);
    }
}
