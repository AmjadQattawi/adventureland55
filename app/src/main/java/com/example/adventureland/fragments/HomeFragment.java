package com.example.adventureland.fragments;

import android.app.AlertDialog;
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
        viewPager2.setPadding(80,0,80,0);


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