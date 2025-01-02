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

    public void setSearchList(List<Game> dataSearchList) {
        games = dataSearchList;
        notifyDataSetChanged();
    }

    public GameAdapter(Context context, List<Game> games) {
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.game_item_design, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);

        holder.gameTitle.setText(game.getTitle());
        holder.gameAge.setText("Age: +" + game.getAge());
        holder.gameRating.setRating(game.getRating());
        holder.gameImage.setImageResource(game.getImage());
        holder.gameDescription.setText(game.getDescription());

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

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);

            gameTitle = itemView.findViewById(R.id.game_title);
            gameAge = itemView.findViewById(R.id.game_age);
            gameRating = itemView.findViewById(R.id.ratingBar);
            gameImage = itemView.findViewById(R.id.game_image);
            gameDescription = itemView.findViewById(R.id.game_description);
        }
    }
}
