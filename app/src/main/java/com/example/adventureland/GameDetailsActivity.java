package com.example.adventureland;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameDetailsActivity extends AppCompatActivity {

    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_design);
        ImageView backArrow = findViewById(R.id.backgame);
        backArrow.setOnClickListener(v -> finish());

        TextView title = findViewById(R.id.nameOfGame);
        ImageView image = findViewById(R.id.imageOfGame);
        TextView description = findViewById(R.id.desc_game);
        TextView price = findViewById(R.id.price_txt);
        TextView age = findViewById(R.id.age_txt);
        RatingBar ratingBar = findViewById(R.id.ratingBar3);
        ImageView favoriteIcon = findViewById(R.id.loveIcon);
        updateFavoriteIcon(favoriteIcon);

        favoriteIcon.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            updateFavoriteIcon(favoriteIcon);
        });

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
    }

    private void updateFavoriteIcon(ImageView favoriteIcon) {
        if (isFavorite) {
            favoriteIcon.setImageResource(R.drawable.circular_heart_fill);
        } else {
            favoriteIcon.setImageResource(R.drawable.circular_heart_empty);
        }
    }
}