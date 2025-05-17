package com.example.adventureland;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

        ImageView filterIcon = findViewById(R.id.filterIcon);
        filterIcon.setOnClickListener(v -> showFilterBottomSheet());

        recyclerView = findViewById(R.id.games_rv_games);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gameList = new ArrayList<>();
        gameList.add(new Game("Bumper Cars", 4, 4.0f, R.drawable.bumpercars,
                "Drive small, electric-powered cars equipped with rubber bumpers and collide safely with other players in a controlled arena. This game is perfect for kids and adults looking for friendly competition and lots of laughs.", 1.500));
        gameList.add(new Game("Ferris Wheel", 4, 3.5f, R.drawable.ferriswheel,
                "Enjoy a relaxing ride on a giant wheel with enclosed seats, offering a bird's-eye view of the amusement park and its surroundings. It's ideal for families and anyone seeking a peaceful yet memorable experience.", 1.250));
        gameList.add(new Game("Swing Ride", 8, 4.5f, R.drawable.swingride,
                "Feel the breeze as you soar through the air in a circular motion on this exciting swing ride. It’s thrilling yet comfortable, perfect for those seeking a mix of adrenaline and joy.", 1.250));
        gameList.add(new Game("Roller Coaster", 10, 5.0f, R.drawable.rollercoaster,
                "Experience the ultimate thrill with high-speed loops, drops, and turns on this exhilarating ride. Perfect for adrenaline lovers looking for a heart-racing adventure.", 2.000));
        gameList.add(new Game("Carousel", 3, 5.0f, R.drawable.carousel,
                "Ride beautifully crafted, brightly colored horses as they spin gently to cheerful music. This classic ride is perfect for younger children and those who love nostalgic attractions.", 1.250));
        gameList.add(new Game("Haunted House", 8, 5.0f, R.drawable.hauntedhouse,
                "Enter a spooky, thrilling adventure filled with eerie sounds and mysterious surprises. Ideal for those who enjoy a good scare.", 2.500));
        gameList.add(new Game("Teacup Ride", 4, 5.0f, R.drawable.teacup,
                "Spin around in colorful teacups that you can control, offering a fun, lighthearted ride for families and younger children.", 1.250));
        gameList.add(new Game("Drop Tower", 12, 5.0f, R.drawable.droptower,
                "Experience the heart-stopping sensation of a sudden free fall from great heights. A must-try for the bravest thrill-seekers.", 2.500));

        adapter = new GameAdapter(this, gameList, GameAdapter.TYPE_FULL);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void searchList(String newText) {
        List<Game> dataSearchList = new ArrayList<>();
        for (Game data : gameList) {
            if (data.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                dataSearchList.add(data);
            }
        }
        if (dataSearchList.isEmpty()) {
            Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
        } else {
            adapter.setSearchList(dataSearchList);
        }
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.filter_bottom_sheet, null);
        bottomSheetDialog.setContentView(sheetView);

        RadioGroup radioGroup = sheetView.findViewById(R.id.filterOptionsGroup);
        Button applyButton = sheetView.findViewById(R.id.btnApplyFilter);

        applyButton.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == R.id.filterAge) {
                sortByAge();
            } else if (selectedId == R.id.filterRating) {
                sortByRating();
            } else if (selectedId == R.id.filterAlphabet) {
                sortByAlphabet();
            } else if (selectedId == R.id.filterPrice) {
                sortByPrice();
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void sortByAge() {
        List<Game> sortedList = new ArrayList<>(gameList);
        Collections.sort(sortedList, Comparator.comparingInt(Game::getAge));
        adapter.setSearchList(sortedList);
    }

    private void sortByRating() {
        List<Game> sortedList = new ArrayList<>(gameList);
        sortedList.sort((g1, g2) -> Float.compare(g2.getRating(), g1.getRating()));
        adapter.setSearchList(sortedList);
    }

    private void sortByAlphabet() {
        List<Game> sortedList = new ArrayList<>(gameList);
        sortedList.sort(Comparator.comparing(Game::getTitle));
        adapter.setSearchList(sortedList);
    }

    private void sortByPrice() {
        List<Game> sortedList = new ArrayList<>(gameList);
        sortedList.sort(Comparator.comparingDouble(Game::getPrice));
        adapter.setSearchList(sortedList);
    }
}
