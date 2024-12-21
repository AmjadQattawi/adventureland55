package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etPhoneNumber, etPassword;
    private Button btnSignup;
    private TextView tvLogin;
    private ImageView googleSignup, facebookSignup, twitterSignup;
    private DrawerLayout drawerLayout;
    private ImageView DrawerIcon;
    private ImageView closeDrawer;
    private LinearLayout checkBalanceSection, aboutSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Views
        etName = findViewById(R.id.et_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        btnSignup = findViewById(R.id.btn_signup);
        tvLogin = findViewById(R.id.tv_login);
        googleSignup = findViewById(R.id.google_signup);
        facebookSignup = findViewById(R.id.facebook_signup);
        twitterSignup = findViewById(R.id.twitter_signup);
        DrawerIcon = findViewById(R.id.drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        closeDrawer = findViewById(R.id.close_drawer);
        checkBalanceSection = findViewById(R.id.check_balance_section);
        aboutSection = findViewById(R.id.about_section);

        // Open the drawer when DrawerIcon is clicked
        DrawerIcon.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));

        // Close the drawer when closeDrawer is clicked
        closeDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.START)) {
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        // Navigate to FirstActivity when "Check your card balance" is clicked
        checkBalanceSection.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, BalanceActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(Gravity.START); // Close drawer after navigation
        });

        // Navigate to AboutActivity when "About" is clicked
        aboutSection.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, AboutActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(Gravity.START); // Close drawer after navigation
        });
//        findViewById(R.id.check_balance_section).setOnClickListener(v -> {
//            Intent intent = new Intent(ٍSignupActivity.this, CheckBalanceActivity.class);
//            startActivity(intent);
//        });
//
//        // Handle "About" click
//        findViewById(R.id.about_section).setOnClickListener(v -> {
//            Intent intent = new Intent(SignupActivity.this, AboutActivity.class);
//            startActivity(intent);
//        });
        // Signup Button Click
        btnSignup.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String phoneNumber = etPhoneNumber.getText().toString();
            String password = etPassword.getText().toString();

//            if (name.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            } else {
                Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show();
//            }
        });

        // Navigate to LoginActivity
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Social Media Signup
        googleSignup.setOnClickListener(v -> Toast.makeText(this, "Google Signup Clicked", Toast.LENGTH_SHORT).show());
        facebookSignup.setOnClickListener(v -> Toast.makeText(this, "Facebook Signup Clicked", Toast.LENGTH_SHORT).show());
        twitterSignup.setOnClickListener(v -> Toast.makeText(this, "Twitter Signup Clicked", Toast.LENGTH_SHORT).show());

        // Drawer Toggle Button

    }
}
