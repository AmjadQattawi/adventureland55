package com.example.adventureland;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Offers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offers);


        ImageView backArrow = findViewById(R.id.back_button);
        backArrow.setOnClickListener(v -> finish());
    }
}
