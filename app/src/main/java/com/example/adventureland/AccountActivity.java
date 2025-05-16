package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class AccountActivity extends AppCompatActivity {

    private EditText editTextDisplayName, editTextPhone;
    private AppCompatButton buttonUpdate, buttonChangePassword;
    private TextView textDeleteAccount;
    private ImageView backMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        editTextDisplayName = findViewById(R.id.editTextDisplayName);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        textDeleteAccount = findViewById(R.id.textDeleteAccount);
        backMore = findViewById(R.id.back_More);

        backMore.setOnClickListener(view -> finish());

        buttonUpdate.setOnClickListener(view -> {
            String name = editTextDisplayName.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();

            // بـــــــــاك
            Toast.makeText(AccountActivity.this, "Updating: " + name + " - " + phone, Toast.LENGTH_SHORT).show();
        });

        buttonChangePassword.setOnClickListener(view -> {
            Intent intent = new Intent(AccountActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        textDeleteAccount.setOnClickListener(view -> {

            Toast.makeText(AccountActivity.this, "Account deletion requested", Toast.LENGTH_SHORT).show();
        });
    }
}

