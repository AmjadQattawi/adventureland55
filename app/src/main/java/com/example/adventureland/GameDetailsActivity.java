package com.example.adventureland;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameDetailsActivity extends AppCompatActivity {

    private boolean isFavorite = false;
    private boolean hasRated = false; // لتخزين حالة التقييم
    private DatabaseReference databaseFavorites;
    private DatabaseReference databaseRatings;
    private FirebaseAuth mAuth;
    private RatingBar ratingBar;
    private TextView averageRatingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_design);

        mAuth = FirebaseAuth.getInstance();

        // ✅ التحقق من تسجيل الدخول
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, " please log in first", Toast.LENGTH_SHORT).show();
            // توجه المستخدم إلى شاشة تسجيل الدخول
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // اغلق الشاشة الحالية
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        databaseFavorites = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);
        databaseRatings = FirebaseDatabase.getInstance().getReference("Ratings").child(getIntent().getStringExtra("title"));

        ImageView backArrow = findViewById(R.id.backgame);
        backArrow.setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.nameOfGame);
        ImageView image = findViewById(R.id.imageOfGame);
        TextView description = findViewById(R.id.desc_game);
        TextView price = findViewById(R.id.price_txt);
        TextView age = findViewById(R.id.age_txt);
        ratingBar = findViewById(R.id.ratingBar3);
        averageRatingText = findViewById(R.id.average_rating);
        ImageView favoriteIcon = findViewById(R.id.loveIcon);

        String gameTitle = getIntent().getStringExtra("title");
        int gameImage = getIntent().getIntExtra("image", 0);
        int gameAge = getIntent().getIntExtra("age", 0);
        String gameDescription = getIntent().getStringExtra("description");
        double gamePrice = getIntent().getDoubleExtra("price", 0);
        float gameRating = getIntent().getFloatExtra("rating", 0);

        title.setText(gameTitle);
        age.setText("Age: +" + gameAge);
        image.setImageResource(gameImage);
        description.setText(gameDescription);
        price.setText(String.format(gamePrice + " JD"));
        ratingBar.setRating(gameRating);

        ratingBar.setProgressTintList(getResources().getColorStateList(R.color.gray));

        databaseFavorites.orderByChild("title").equalTo(gameTitle).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isFavorite = true;
                    updateFavoriteIcon(favoriteIcon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GameDetailsActivity.this, "Failed to check favorite status", Toast.LENGTH_SHORT).show();
            }
        });

        favoriteIcon.setOnClickListener(v -> {
            if (isFavorite) {
                removeFromFavorites(gameTitle);
                isFavorite = false;
            } else {
                addToFavorites(gameTitle, gameImage, gameDescription, gamePrice, gameRating, gameAge);
                isFavorite = true;
            }
            updateFavoriteIcon(favoriteIcon);
        });

        loadAverageRating();

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser && !hasRated) {
                ratingBar.setProgressTintList(getResources().getColorStateList(R.color.navy_blue));
                averageRatingText.setTextColor(getResources().getColor(R.color.navy_blue));
                saveRating(rating);
                hasRated = true;
                ratingBar.setIsIndicator(true);
            }
        });

        checkIfRated();
    }


    private void checkIfRated() {
        String userId = mAuth.getCurrentUser().getUid();

        // تحقق إذا كان المستخدم قد قيم اللعبة من قبل
        databaseRatings.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // إذا كان قد قيم اللعبة، تغيير اللون إلى الكحلي وإيقاف التقييم
                    float rating = task.getResult().getValue(Float.class);
                    ratingBar.setRating(rating); // تعيين التقييم المحفوظ على الـ RatingBar
                    ratingBar.setProgressTintList(getResources().getColorStateList(R.color.navy_blue)); // تعيين اللون الكحلي
                    ratingBar.setIsIndicator(true); // تعطيل التقييم
                    averageRatingText.setTextColor(getResources().getColor(R.color.navy_blue)); // تغيير لون النص إلى الكحلي
                    hasRated = true;
                }
            }
        });
    }

    private void updateFavoriteIcon(ImageView favoriteIcon) {
        if (isFavorite) {
            favoriteIcon.setImageResource(R.drawable.circular_heart_fill);
        } else {
            favoriteIcon.setImageResource(R.drawable.circular_heart_empty);
        }
    }

    private void addToFavorites(String title, int image, String description, double price, float rating, int age) {
        Game favoriteGame = new Game(title, age, rating, image, description, price);
        String gameId = databaseFavorites.push().getKey();
        if (gameId != null) {
            databaseFavorites.child(gameId).setValue(favoriteGame).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(GameDetailsActivity.this, "Game added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GameDetailsActivity.this, "Failed to add game to favorites", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void removeFromFavorites(String title) {
        databaseFavorites.orderByChild("title").equalTo(title).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    task.getResult().getChildren().forEach(snapshot -> snapshot.getRef().removeValue());
                    Toast.makeText(GameDetailsActivity.this, "Game removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadAverageRating() {
        String userId = mAuth.getCurrentUser().getUid();

        databaseRatings.get().addOnCompleteListener(ratingTask -> {
            if (ratingTask.isSuccessful() && ratingTask.getResult().exists()) {
                double totalRating = 0;
                int ratingCount = 0;

                for (DataSnapshot snapshot : ratingTask.getResult().getChildren()) {
                    Double rating = snapshot.getValue(Double.class);
                    if (rating != null) {
                        totalRating += rating;
                        ratingCount++;
                    }
                }

                double averageRating = (ratingCount > 0) ? totalRating / ratingCount : 0.0;
                averageRatingText.setText("Average Rating: " + String.format("%.1f", averageRating));
            } else {
                averageRatingText.setText("Average Rating: 0.0");
            }
        });
    }
    private String getCurrentTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    private void saveRating(float rating) {
        String userId = mAuth.getCurrentUser().getUid();

        DatabaseReference pointsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("points");
        pointsRef.get().addOnSuccessListener(snapshot -> {
            long currentPoints = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
            pointsRef.setValue(currentPoints + 50);

            DatabaseReference transactionRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("transactions").push();
            transactionRef.setValue(new Transaction("earned", "Game Rating Bonus", 30, getCurrentTimestamp()));
        });



        databaseRatings.child(userId).setValue(rating).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Rating saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save rating.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}