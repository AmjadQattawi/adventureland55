package com.example.adventureland.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.adventureland.GamesActivity;
import com.example.adventureland.Game;
import com.example.adventureland.GameAdapter;
import com.example.adventureland.R;
import com.example.adventureland.SlideAdapter;
import com.example.adventureland.SlideItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private ImageView gameIcon, cardIcon;
    private TextView seeAll;

    private ViewPager2 viewPager2;
    private RecyclerView topGamesRecyclerView;
    private Handler slideHandler = new Handler();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameIcon = view.findViewById(R.id.gamesIcon);
        cardIcon = view.findViewById(R.id.cardIcon);
        seeAll = view.findViewById(R.id.seeAll_txt);

        viewPager2 = view.findViewById(R.id.slider);
        List<SlideItem> sliderItem = new ArrayList<>();
        sliderItem.add(new SlideItem(R.drawable.slide1));
        sliderItem.add(new SlideItem(R.drawable.slide2));
        sliderItem.add(new SlideItem(R.drawable.slide3));

        viewPager2.setAdapter(new SlideAdapter(sliderItem, viewPager2));

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_ALWAYS);

        CompositePageTransformer compositionTransform = new CompositePageTransformer();
        compositionTransform.addTransformer(new MarginPageTransformer(40));
        compositionTransform.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        viewPager2.setPageTransformer(compositionTransform);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(sliderRunnable);
                slideHandler.postDelayed(sliderRunnable, 2000);
            }
        });

        topGamesRecyclerView = view.findViewById(R.id.recyclerTopGames);
        topGamesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<Game> topGames = getTopGames();
        GameAdapter topGamesAdapter = new GameAdapter(getContext(), topGames);
        topGamesRecyclerView.setAdapter(topGamesAdapter);

        gameIcon.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), GamesActivity.class);
            startActivity(i);
        });

        cardIcon.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutContainer, new CardFragment())
                .addToBackStack(null)
                .commit());

        seeAll.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), GamesActivity.class);
            startActivity(i);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    private List<Game> getTopGames() {
        List<Game> allGames = getAllGames();
        return allGames.subList(0, Math.min(allGames.size(), 5));
    }

    private List<Game> getAllGames() {
        List<Game> allGames = new ArrayList<>();
        allGames.add(new Game("Bumper Cars", 4, 4.0f, R.drawable.bumpercars, "Drive small, electric-powered cars equipped with rubber bumpers and collide safely with other players in a controlled arena. This game is perfect for kids and adults looking for friendly competition and lots of laughs.", 0.650));
        allGames.add(new Game("Ferris Wheel", 4, 3.5f, R.drawable.ferriswheel, "Enjoy a relaxing ride on a giant wheel with enclosed seats, offering a bird's-eye view of the amusement park and its surroundings. It's ideal for families and anyone seeking a peaceful yet memorable experience.", 0.500));
        allGames.add(new Game("Swing Ride", 8, 4.5f, R.drawable.swingride, "Feel the breeze as you soar through the air in a circular motion on this exciting swing ride. It’s thrilling yet comfortable, perfect for those seeking a mix of adrenaline and joy.", 0.750));
        return allGames;
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        slideHandler.postDelayed(sliderRunnable, 3000);
    }
}
