package com.example.adventureland.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.adventureland.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifiactionFragment extends Fragment {

    private EditText otpInput1, otpInput2, otpInput3, otpInput4;
    private TextView resentTv;
    private ImageView backVerfication;
    private AppCompatButton verifyBtn;

    private String verificationId;
    private String userPhone;
    private FirebaseAuth firebaseAuth;
    private CountDownTimer countDownTimer;

    private VerificationListener verificationListener;

    public VerifiactionFragment() { }

    public interface VerificationListener {
        void onVerificationSuccess();
    }

    public void setVerificationListener(VerificationListener listener) {
        this.verificationListener = listener;
    }

    public void setUserPhone(String phone) {
        this.userPhone = phone;
        // لا تستدعي sendVerificationCode هنا مباشرة بعد الآن
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.verfication_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        otpInput1 = view.findViewById(R.id.otp_input1);
        otpInput2 = view.findViewById(R.id.otp_input2);
        otpInput3 = view.findViewById(R.id.otp_input3);
        otpInput4 = view.findViewById(R.id.otp_input4);
        resentTv = view.findViewById(R.id.resentTv);
        verifyBtn = view.findViewById(R.id.verifyBtn);
        backVerfication = view.findViewById(R.id.backVerfication);

        firebaseAuth = FirebaseAuth.getInstance();

        setupOtpInputs();
        startCountDown();

        verifyBtn.setOnClickListener(v -> {
            String code = otpInput1.getText().toString().trim()
                    + otpInput2.getText().toString().trim()
                    + otpInput3.getText().toString().trim()
                    + otpInput4.getText().toString().trim();

            if(code.length() < 4) {
                Toast.makeText(getContext(), "Please enter the 4-digit code", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyCode(code);
        });

        backVerfication.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // استدعاء إرسال رمز التحقق هنا بعد التأكد من الربط ووجود رقم الهاتف
        if (userPhone != null && !userPhone.isEmpty()) {
            Activity activity = getActivity();
            if (activity != null) {
                sendVerificationCode(userPhone);
            } else {
                Toast.makeText(getContext(), "Fragment not attached to Activity", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Phone number is empty or null", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupOtpInputs() {
        otpInput1.addTextChangedListener(new GenericTextWatcher(otpInput1, otpInput2));
        otpInput2.addTextChangedListener(new GenericTextWatcher(otpInput2, otpInput3));
        otpInput3.addTextChangedListener(new GenericTextWatcher(otpInput3, otpInput4));
        otpInput4.addTextChangedListener(new GenericTextWatcher(otpInput4, null));
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resentTv.setText("Resend in " + (millisUntilFinished / 1000) + " seconds");
                resentTv.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resentTv.setText("Resend code");
                resentTv.setEnabled(true);
                resentTv.setOnClickListener(v -> {
                    if (userPhone != null && !userPhone.isEmpty() && getActivity() != null) {
                        sendVerificationCode(userPhone);
                        startCountDown();
                    }
                });
            }
        };
        countDownTimer.start();
    }

    private void sendVerificationCode(String phoneNumber) {
        Activity activity = getActivity();
        if (activity == null) {
            Toast.makeText(getContext(), "Fragment not attached to Activity", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                Toast.makeText(getContext(), "Verification completed", Toast.LENGTH_SHORT).show();
                                if(verificationListener != null) {
                                    verificationListener.onVerificationSuccess();
                                }
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(getContext(), "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                super.onCodeSent(s, token);
                                verificationId = s;
                                Toast.makeText(getContext(), "OTP code sent", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String code) {
        if(verificationId == null) {
            Toast.makeText(getContext(), "Verification ID not found. Please request code again.", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(getContext(), "Verification Successful", Toast.LENGTH_SHORT).show();
                if(verificationListener != null) {
                    verificationListener.onVerificationSuccess();
                }
            } else {
                Toast.makeText(getContext(), "Invalid code entered", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GenericTextWatcher implements android.text.TextWatcher {

        private final EditText currentView;
        private final EditText nextView;

        public GenericTextWatcher(EditText currentView, EditText nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void afterTextChanged(android.text.Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
