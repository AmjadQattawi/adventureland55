package com.example.adventureland.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adventureland.CheckBalanceActivity;
import com.example.adventureland.R;
import com.example.adventureland.RechargeCardActivity;
import com.example.adventureland.CardAdapter;
import com.example.adventureland.CardAdapter.CardItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CardFragment extends Fragment {

    private ImageView backIcon;
    private CardView checkBalanceIcon;
    private EditText cardNumberInput;
    private RecyclerView recyclerView;

    private CardAdapter cardAdapter;
    private final List<CardItem> cardList = new ArrayList<>();

    private DatabaseReference userCardsRef;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.card, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userCardsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cards");

        backIcon = view.findViewById(R.id.back_button);
        checkBalanceIcon = view.findViewById(R.id.check_button);
        cardNumberInput = view.findViewById(R.id.card_number_input);
        recyclerView = view.findViewById(R.id.cards_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cardAdapter = new CardAdapter(getContext(), cardList);
        recyclerView.setAdapter(cardAdapter);

        setupCardNumberFormatter();
        setupSwipeToDelete();
        loadUserCards();

        backIcon.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        checkBalanceIcon.setOnClickListener(v -> {
            String cardNumber = cardNumberInput.getText().toString().replace("-", "").trim();
            if (TextUtils.isEmpty(cardNumber) || cardNumber.length() != 16) {
                Toast.makeText(getContext(), "Please enter a valid 16-digit card number", Toast.LENGTH_SHORT).show();
                return;
            }
            checkCardInFirebase(cardNumber);
        });
    }

    private void setupCardNumberFormatter() {
        cardNumberInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        cardNumberInput.addTextChangedListener(new TextWatcher() {
            boolean isFormatting;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String digitsOnly = s.toString().replaceAll("-", "");
                if (digitsOnly.length() > 16) digitsOnly = digitsOnly.substring(0, 16);

                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digitsOnly.length(); i++) {
                    if (i > 0 && i % 4 == 0) formatted.append("-");
                    formatted.append(digitsOnly.charAt(i));
                }

                cardNumberInput.setText(formatted.toString());
                cardNumberInput.setSelection(formatted.length());
                isFormatting = false;
            }
        });
    }

    private void checkCardInFirebase(String cardNumber) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {
                boolean found = false;

                for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                    DataSnapshot cardSnapshot = userSnapshot.child("cards").child(cardNumber);
                    if (cardSnapshot.exists()) {
                        String balance = String.valueOf(cardSnapshot.child("balance").getValue());
                        String lastUsage = cardSnapshot.child("lastUsage").exists()
                                ? cardSnapshot.child("lastUsage").getValue().toString()
                                : "0000/00/00 00:00";

                        String lastCharge = cardSnapshot.child("lastCharge").exists()
                                ? cardSnapshot.child("lastCharge").getValue().toString()
                                : "0000/00/00 00:00";


                        Intent intent = new Intent(getContext(), CheckBalanceActivity.class);
                        intent.putExtra("balance", balance != null ? balance : "0.000");
                        intent.putExtra("lastUsage", lastUsage != null ? lastUsage : "0000/00/00 00:00");
                        intent.putExtra("lastCharge", lastCharge != null ? lastCharge : "0000/00/00 00:00");
                        intent.putExtra("cardId", cardNumber);
                        startActivity(intent);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    showCardNotFoundDialog(cardNumber);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCardNotFoundDialog(String cardNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.cardnotfound, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.button_add).setOnClickListener(v -> {
            userCardsRef.child(cardNumber).child("balance").setValue("0.000");
            String currentTime = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm", java.util.Locale.getDefault()).format(new java.util.Date());

            userCardsRef.child(cardNumber).child("lastUsage").setValue(currentTime);
            userCardsRef.child(cardNumber).child("lastCharge").setValue(currentTime);

            Toast.makeText(getContext(), "Card added successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadUserCards();
        });

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
    }

    private void loadUserCards() {
        cardList.clear();
        userCardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                    String cardId = cardSnapshot.getKey();
                    String balance = String.valueOf(cardSnapshot.child("balance").getValue());
                    String lastUsage = cardSnapshot.child("lastUsage").exists()
                            ? cardSnapshot.child("lastUsage").getValue().toString()
                            : "0000/00/00 00:00";

                    String lastCharge = cardSnapshot.child("lastCharge").exists()
                            ? cardSnapshot.child("lastCharge").getValue().toString()
                            : "0000/00/00 00:00";


                    cardList.add(new CardItem(cardId, balance, lastUsage, lastCharge));
                }
                cardAdapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Error loading cards", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String cardId = cardAdapter.getCardIdAt(position);

                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_deletecard, null);
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setView(dialogView)
                        .setCancelable(false)
                        .create();

                Window window = dialog.getWindow();
                if (window != null) {
                    window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    window.setGravity(Gravity.CENTER);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }


                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                Button deleteButton = dialogView.findViewById(R.id.button_add);
                if (deleteButton != null) {
                    deleteButton.setOnClickListener(v -> {
                        cardAdapter.removeAt(position);
                        Toast.makeText(getContext(), "Card deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }

                Button cancelButton = dialogView.findViewById(R.id.button_cancel);
                if (cancelButton != null) {
                    cancelButton.setOnClickListener(v -> {
                        cardAdapter.notifyItemChanged(position);
                        dialog.dismiss();
                    });
                }

                dialog.show();
            }


            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;
                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#EAEAEA"));

                c.drawRect(itemView.getRight() + dX, itemView.getTop(),
                        itemView.getRight(), itemView.getBottom(), paint);

                Drawable deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.trash);
                if (deleteIcon != null) {
                    int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 3;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
                    int iconRight = itemView.getRight() - iconMargin;
                    int iconLeft = iconRight - deleteIcon.getIntrinsicWidth();

                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    deleteIcon.draw(c);

                    Paint textPaint = new Paint();
                    textPaint.setColor(Color.parseColor("#EC221F"));
                    textPaint.setTextSize(32f);
                    textPaint.setTextAlign(Paint.Align.CENTER);

                    Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium);
                    if (typeface != null) {
                        textPaint.setTypeface(typeface);
                    }

                    float textX = iconLeft + deleteIcon.getIntrinsicWidth() / 2f;
                    float textY = iconBottom + 60;
                    c.drawText("Delete", textX, textY, textPaint);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

}
