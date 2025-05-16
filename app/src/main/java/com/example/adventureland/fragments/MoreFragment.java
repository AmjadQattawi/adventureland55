package com.example.adventureland.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.adventureland.FAQactivity;
import com.example.adventureland.LoginActivity;
import com.example.adventureland.R;

public class MoreFragment extends Fragment {

    private ImageView backIcon;
    private View logoutButton;
    private View faqOption;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings, container, false); // تأكد أن settings.xml هو ملف واجهة صفحة "More"
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backIcon = view.findViewById(R.id.back_More);
        logoutButton = view.findViewById(R.id.logout_option);
        faqOption = view.findViewById(R.id.faq_option); // تأكد من وجود هذا العنصر في settings.xml

        // الرجوع إلى الخلف
        backIcon.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // فتح شاشة FAQ
        faqOption.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FAQactivity.class);
            startActivity(intent);
        });

        View accOption = view.findViewById(R.id.acc_option);

        accOption.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.adventureland.AccountActivity.class);
            startActivity(intent);
        });

        // تسجيل الخروج
        logoutButton.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.logout, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();

        Button okButton = dialogView.findViewById(R.id.button_ok);
        Button cancelButton = dialogView.findViewById(R.id.button_cancel);

        okButton.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }
}
