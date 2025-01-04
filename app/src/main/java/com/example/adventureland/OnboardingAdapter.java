package com.example.adventureland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {
    private final Context context;
    private final List<OnboardingItem> items = List.of(
            new OnboardingItem(R.drawable.gift, "Games & Rewards", "Collect points and unlock rewards as you play and explore the park!"),
            new OnboardingItem(R.drawable.games_copy, "Exclusive Offers!", "Discover special offers, discounts, and promotions right inside the app!"),
            new OnboardingItem(R.drawable.top_up, "Track & Top Up Your Game Card!", "Discover special offers, discounts, and promotions right inside the app!")
    );

    public OnboardingAdapter(Context context, List<OnboardingItem> onboardingItems) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.onboarding_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnboardingItem item = items.get(position);
        holder.imageView.setImageResource(item.imageResId);
        holder.textTitle.setText(item.title);
        holder.textDescription.setText(item.description);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textTitle, textDescription;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            textTitle = view.findViewById(R.id.textTitle);
            textDescription = view.findViewById(R.id.textDescription);
        }
    }
}
