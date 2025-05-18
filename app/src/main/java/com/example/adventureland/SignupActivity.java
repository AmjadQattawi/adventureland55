package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etPhoneNumber, etPassword, etOtp;
    private Button btnSignup;
    private TextView tvLogin;
    private ImageView googleSignup, facebookSignup, twitterSignup, drawerIcon, closeDrawer;
    private DrawerLayout drawerLayout;
    private LinearLayout checkBalanceSection, aboutSection;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersRef;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeViews();
        initializeFirebase();
        setupDrawerActions();
        setupButtonActions();
    }

    private void initializeViews() {
        etName = findViewById(R.id.et_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        etOtp = findViewById(R.id.et_otp);
        btnSignup = findViewById(R.id.btn_signup);
        tvLogin = findViewById(R.id.tv_login);
        drawerIcon = findViewById(R.id.drawer);
        closeDrawer = findViewById(R.id.close_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        checkBalanceSection = findViewById(R.id.check_balance_section);
        aboutSection = findViewById(R.id.about_section);
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void setupDrawerActions() {
        drawerIcon.setOnClickListener(v -> drawerLayout.openDrawer(Gravity.START));
        closeDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(Gravity.START)) {
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        checkBalanceSection.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, BalanceActivity.class);
            intent.putExtra("fromGuest", true);  // ✅ تحديد أن المستخدم ضيف (غير مسجل دخول)
            startActivity(intent);
            drawerLayout.closeDrawer(Gravity.START);
        });


        aboutSection.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, FAQactivity.class));
            drawerLayout.closeDrawer(Gravity.START);
        });
    }

    private void setupButtonActions() {
        btnSignup.setOnClickListener(v -> handleSignup());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });

    }
//////

    private void handleSignup() {
        String name = etName.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            showToast("Please fill in all fields");
            return;
        }

        // تحقق من أن الرقم مكون من 9 أرقام
        if (!isPhoneNineDigits(phone)) {
            showToast("Phone number must be exactly 9 digits");
            return;
        }

        // تحقق من أن كلمة المرور تحتوي على 6 أحرف على الأقل
        if (password.length() < 6) {
            showToast("Password must be at least 6 characters");
            return;
        }

        // تحقق من أن الرقم يبدأ بـ 77 أو 78 أو 79
        if (!isPhoneStartsWithAllowedPrefixes(phone)) {
            showToast("Phone number must start with 77, 78, or 79");
            return;
        }

        // أضف رمز البلد
        String fullPhoneNumber = "+962" + phone;

        // إخفاء EditText للـ OTP في البداية
        etOtp.setVisibility(View.GONE);

        // التحقق من وجود الهاتف قبل إرسال رمز OTP
        checkPhoneNumberInFirebase(fullPhoneNumber, name, password);
    }

    private boolean isPhoneNineDigits(String phone) {
        // تحقق من أن الرقم يحتوي على 9 أرقام فقط
        return phone.length() == 9 && phone.matches("[0-9]+");
    }

    private boolean isPhoneStartsWithAllowedPrefixes(String phone) {
        // تحقق من أن الرقم يبدأ بـ 77 أو 78 أو 79
        return phone.startsWith("77") || phone.startsWith("78") || phone.startsWith("79");
    }

    private void checkPhoneNumberInFirebase(String phoneNumber, String name, String password) {
        usersRef.orderByChild("phone").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // الرقم موجود، أرسل OTP
                    showToast("Account already exists with this phone number.");
                } else {
                    // الرقم غير موجود - أرسل رمز OTP
                    sendVerificationCode(phoneNumber);

                    // اجعل EditText للـ OTP مرئيًا
                    etOtp.setVisibility(View.VISIBLE);
                    btnSignup.setText("Verify Code");

                    btnSignup.setOnClickListener(v -> {
                        String code = etOtp.getText().toString().trim();
                        if (TextUtils.isEmpty(code)) {
                            showToast("Enter the verification code");
                            return;
                        }
                        verifyCode(code, name, phoneNumber, password);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showToast("Error checking phone number");
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                showToast("Verification Completed");
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                showToast("Verification Failed: " + e.getMessage());
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                super.onCodeSent(s, token);
                                verificationId = s;
                                showToast("Code Sent");
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String code, String name, String phone, String password) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveUserToDatabase(name, phone, password);
            } else {
                showToast("Verification Failed");
            }
        });
    }

    private String getCurrentTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }


    private void saveUserToDatabase(String name, String phone, String password) {
        String email = phone + "@gmail.com";
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = firebaseAuth.getCurrentUser().getUid();
                User user = new User(name, phone, password);
                usersRef.child(userId).setValue(user);

                Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();

                loadHomeFragment(); // استدعاء الدالة لتحميل HomeFragment
                // إضافة 50 نقطة عند إنشاء الحساب
                DatabaseReference pointsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("points");
                pointsRef.setValue(50);

// تسجيل المعاملة
                DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("transactions").push();
                transactionRef.setValue(new Transaction("earned", "Signup Bonus", 50, getCurrentTimestamp()));
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadHomeFragment() {
        findViewById(R.id.scroll_view_main).setVisibility(View.GONE); // إخفاء المحتوى الرئيسي
        findViewById(R.id.fragment_container).setVisibility(View.VISIBLE); // إظهار حاوية الفراجمنت

        Intent i = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void showToast(String message) {
        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public static class User {
        public String name;
        public String phone;
        public String password;

        public User() {
        }

        public User(String name, String phone, String password) {
            this.name = name;
            this.phone = phone;
            this.password = password;
        }
    }




}