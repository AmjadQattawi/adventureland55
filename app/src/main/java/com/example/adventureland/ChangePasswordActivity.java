package com.example.adventureland;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextCurrentPassword, editTextNewPassword, editTextConfirmNewPassword;
    private AppCompatButton buttonUpdatePassword;
    private ImageView backMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmNewPassword = findViewById(R.id.editTextConfirmNewPassword);
        buttonUpdatePassword = findViewById(R.id.buttonUpdatePassword);
        backMore = findViewById(R.id.back_More);

        backMore.setOnClickListener(view -> finish());

        buttonUpdatePassword.setOnClickListener(view -> {
            String currentPassword = editTextCurrentPassword.getText().toString().trim();
            String newPassword = editTextNewPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmNewPassword.getText().toString().trim();

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ChangePasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // ترسل البيانات إلى السيرفر هنا
            Toast.makeText(ChangePasswordActivity.this, "Password change request sent", Toast.LENGTH_SHORT).show();
        });
    }
}

