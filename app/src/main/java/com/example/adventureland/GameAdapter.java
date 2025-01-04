package com.example.adventureland;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private Context context;
    private List<Game> games;
    private int layoutType;

    public static final int TYPE_FULL = 0;
    public static final int TYPE_SIMPLE = 1;

    public GameAdapter(Context context, List<Game> games, int layoutType) {
        this.context = context;
        this.games = games;
        this.layoutType = layoutType;
    }

    public void setSearchList(List<Game> dataSearchList) {
        games = dataSearchList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (layoutType == TYPE_SIMPLE) ? R.layout.home_game_item : R.layout.game_item_design;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new GameViewHolder(view, layoutType);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);

        holder.gameTitle.setText(game.getTitle());
        holder.gameImage.setImageResource(game.getImage());

        if (layoutType == TYPE_FULL) {
            holder.gameAge.setText("Age: +" + game.getAge());
            holder.gameRating.setRating(game.getRating());
            holder.gameDescription.setText(game.getDescription());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GameDetailsActivity.class);
            intent.putExtra("title", game.getTitle());
            intent.putExtra("image", game.getImage());
            intent.putExtra("description", game.getDescription());
            intent.putExtra("price", game.getPrice());
            intent.putExtra("rating", game.getRating());
            intent.putExtra("age", game.getAge());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView gameTitle, gameAge, gameDescription;
        RatingBar gameRating;
        ImageView gameImage;

        public GameViewHolder(@NonNull View itemView, int layoutType) {
            super(itemView);

            gameTitle = itemView.findViewById(R.id.game_title);
            gameImage = itemView.findViewById(R.id.game_image);

            if (layoutType == TYPE_FULL) {
                gameAge = itemView.findViewById(R.id.game_age);
                gameRating = itemView.findViewById(R.id.ratingBar);
                gameDescription = itemView.findViewById(R.id.game_description);
            }
        }
    }
}