package com.example.adventureland;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SchoolOffersActivity extends AppCompatActivity {

    private RecyclerView offerRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_offers);

        offerRecyclerView = findViewById(R.id.schoolsOfferRecyclerView);
        offerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("Book 30 tickets for 20 students – Get 10 free tickets", "Valid for school visits during weekdays"));
        offers.add(new Offer("Full-day access for schools – 40% off", "Offer valid until the end of the term"));
        offers.add(new Offer("Free game session for every 15 students", "Enjoy extra time at no cost"));
        offers.add(new Offer("Teachers enter free with student groups", "1 teacher per 10 students"));
        offers.add(new Offer("Book before 10 AM – Get early access rewards", "Limited time offer"));

        OfferAdapter adapter = new OfferAdapter(this, offers, "school");
        offerRecyclerView.setAdapter(adapter);

        ImageView back = findViewById(R.id.back_button);
        back.setOnClickListener(v -> finish());
    }
}
