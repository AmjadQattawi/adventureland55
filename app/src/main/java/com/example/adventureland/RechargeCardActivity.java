package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.adventureland.R;

public class RechargeCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rechargecard);

        CardView rechargeButton = findViewById(R.id.recharge_button);

        rechargeButton.setOnClickListener(v -> {
            Intent intent = new Intent(RechargeCardActivity.this, PaymentDetailActivity.class);
            startActivity(intent);
        });
    }
}
