package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhoneNumber, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignup;
    private ImageView googleLogin, facebookLogin, twitterLogin, DrawerIcon;
    private DrawerLayout drawerLayout;
    private ImageView closeDrawer;
    private LinearLayout checkBalanceSection, aboutSection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Views
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvSignup = findViewById(R.id.tv_signup);
        googleLogin = findViewById(R.id.google_login);
        facebookLogin = findViewById(R.id.facebook_login);
        twitterLogin = findViewById(R.id.twitter_login);
        DrawerIcon = findViewById(R.id.drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        closeDrawer = findViewById(R.id.close_drawer);
        checkBalanceSection = findViewById(R.id.check_balance_section);
        aboutSection = findViewById(R.id.about_section);

        // Open the drawer when DrawerIcon is clicked
        DrawerIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Close the drawer when closeDrawer is clicked
        closeDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Navigate to FirstActivity when "Check your card balance" is clicked
        checkBalanceSection.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, BalanceActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer after navigation
        });

        // Navigate to AboutActivity when "About" is clicked
        aboutSection.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AboutActivity.class);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START); // Close drawer after navigation
        });
        // Login Button Click
        btnLogin.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString();
            String password = etPassword.getText().toString();

//            if (phoneNumber.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
//            }
        });

        // Navigate to SignUpActivity
        tvSignup.setOnClickListener(v -> {
            // Implement sign-up navigation here
            Intent i=new Intent(LoginActivity.this,SignupActivity.class);
            startActivity(i);
        });

        // Social Media Logins
        googleLogin.setOnClickListener(v -> Toast.makeText(this, "Google Login Clicked", Toast.LENGTH_SHORT).show());
        facebookLogin.setOnClickListener(v -> Toast.makeText(this, "Facebook Login Clicked", Toast.LENGTH_SHORT).show());
        twitterLogin.setOnClickListener(v -> Toast.makeText(this, "Twitter Login Clicked", Toast.LENGTH_SHORT).show());
    }
}
