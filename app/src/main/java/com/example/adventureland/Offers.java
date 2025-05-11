package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Offers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers);

        ConstraintLayout individualBlock = findViewById(R.id.individualOfferBlock);
        individualBlock.setOnClickListener(v -> {
            Intent intent = new Intent(Offers.this, IndividualOffersActivity.class);
            startActivity(intent);
        });

        ConstraintLayout schoolBlock = findViewById(R.id.schoolOfferBlock);
        schoolBlock.setOnClickListener(v -> {
            Intent intent = new Intent(Offers.this, SchoolOffersActivity.class);
            startActivity(intent);
        });

        ConstraintLayout birthdayBlock = findViewById(R.id.birthdayOfferBlock);
        birthdayBlock.setOnClickListener(v -> {
            Intent intent = new Intent(Offers.this, BirthdayOffersActivity.class);
            startActivity(intent);
        });


        ImageView backArrow = findViewById(R.id.back_button);
        backArrow.setOnClickListener(v -> finish());
    }
}
