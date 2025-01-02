package com.example.adventureland;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GamesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private List<Game> gameList;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games);
        ImageView backArrow = findViewById(R.id.back_games);
        backArrow.setOnClickListener(v -> finish());

        searchView = findViewById(R.id.games_sv_searchGames);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.games_rv_games);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        gameList = new ArrayList<>();
        gameList.add(new Game("Bumper Cars", 4, 4.0f, R.drawable.bumpercars, "Drive small, electric-powered cars equipped with rubber bumpers and collide safely with other players in a controlled arena. This game is perfect for kids and adults looking for friendly competition and lots of laughs.", 0.650));
        gameList.add(new Game("Ferris Wheel", 4, 3.5f, R.drawable.ferriswheel, "Enjoy a relaxing ride on a giant wheel with enclosed seats, offering a bird's-eye view of the amusement park and its surroundings. It's ideal for families and anyone seeking a peaceful yet memorable experience.", 0.500));
        gameList.add(new Game("Swing Ride", 8, 4.5f, R.drawable.swingride, "Feel the breeze as you soar through the air in a circular motion on this exciting swing ride. It’s thrilling yet comfortable, perfect for those seeking a mix of adrenaline and joy.", 0.750));
        gameList.add(new Game("Roller Coaster", 10, 5.0f, R.drawable.rollercoaster, "Experience the ultimate thrill with high-speed loops, drops, and turns on this exhilarating ride. Perfect for adrenaline lovers looking for a heart-racing adventure.", 0.400));
        gameList.add(new Game("Carousel", 3, 5.0f, R.drawable.carousel, "Ride beautifully crafted, brightly colored horses as they spin gently to cheerful music. This classic ride is perfect for younger children and those who love nostalgic attractions.", 0.400));
        gameList.add(new Game("Haunted House", 8, 5.0f, R.drawable.hauntedhouse, "Enter a spooky, thrilling adventure filled with eerie sounds and mysterious surprises. Ideal for those who enjoy a good scare.", 0.900));
        gameList.add(new Game("Teacup Ride", 4, 5.0f, R.drawable.teacup, "Spin around in colorful teacups that you can control, offering a fun, lighthearted ride for families and younger children.", 0.550));
        gameList.add(new Game("Drop Tower", 12, 5.0f, R.drawable.droptower, "Experience the heart-stopping sensation of a sudden free fall from great heights. A must-try for the bravest thrill-seekers.", 1.200));


        adapter = new GameAdapter(this, gameList);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void searchList(String newText) {

        List<Game> dataSearchList = new ArrayList<>();
        for (Game data : gameList){
            if (data.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (dataSearchList.isEmpty()){
            Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setSearchList(dataSearchList);
        }

    }


}