package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.adventureland.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhoneNumber, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignup;
    private ImageView googleLogin, facebookLogin, twitterLogin, DrawerIcon;
    private DrawerLayout drawerLayout;
    private ImageView closeDrawer;
    private LinearLayout checkBalanceSection, aboutSection;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;

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

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Set Drawer Actions
        setupDrawerActions();

        // Set Login Button Action
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void setupDrawerActions() {
        // Open the drawer when DrawerIcon is clicked
        DrawerIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Close the drawer when closeDrawer is clicked
        closeDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Navigate to BalanceActivity when "Check your card balance" is clicked
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

        // Navigate to SignupActivity
        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Social Media Logins
        googleLogin.setOnClickListener(v -> Toast.makeText(this, "Google Login Clicked", Toast.LENGTH_SHORT).show());
        facebookLogin.setOnClickListener(v -> Toast.makeText(this, "Facebook Login Clicked", Toast.LENGTH_SHORT).show());
        twitterLogin.setOnClickListener(v -> Toast.makeText(this, "Twitter Login Clicked", Toast.LENGTH_SHORT).show());
    }

    private void handleLogin() {
        String phone = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            etPhoneNumber.setError("Please enter your phone number");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Please enter your password");
            return;
        }

        String fullPhoneNumber = "+962" + phone;

        // Check user data in Realtime Database
        usersRef.orderByChild("phone").equalTo(fullPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String dbPassword = userSnapshot.child("password").getValue(String.class);

                        if (dbPassword != null && dbPassword.equals(password)) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            // Navigate to HomeFragment
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_contain, new HomeFragment())
                                    .addToBackStack(null)  // Optional, adds it to the back stack
                                    .commit();

                            return;
                        }
                    }
                    Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
