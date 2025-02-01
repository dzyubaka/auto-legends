package ru.dzyubaka.autolegends;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private int selected = -1;
    private final ArrayList<UnitType> setup = new ArrayList<>(Arrays.asList(
            UnitType.PALADIN,
            UnitType.FIRE_MAGE,
            UnitType.SKELETON,
            UnitType.SKELETON,
            UnitType.SKELETON
    ));
    private User user;
    private TextView moneyTextView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout pickLayout = findViewById(R.id.setup);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setSelectedItemId(R.id.setup);

        user = (User) getIntent().getSerializableExtra("user");
        moneyTextView = findViewById(R.id.money);
        moneyTextView.setText(user.getMoney() + " money");
        ((TextView) findViewById(R.id.diamonds)).setText(user.getDiamonds() + " diamonds");
        button = findViewById(R.id.button);
        button.setText("Start\n" + user.getLevel() + " lvl");

        for (int i = 0; i < 5; i++) {
            int closure = i;
            ImageView unitImageView = (ImageView) pickLayout.getChildAt(i);
            unitImageView.setImageResource(Unit.getDrawable(setup.get(i)));
            unitImageView.setOnClickListener(v -> {
                selected = closure;
                ListView listView = new ListView(this);
                int resource = R.layout.item_unit;
                ArrayList<UnitType> available = new ArrayList<>();
                available.add(UnitType.SKELETON);

                for (UnitType unitType : user.getInventory()) {
                    if (!setup.contains(unitType)) {
                        available.add(unitType);
                    }
                }

                listView.setAdapter(new ArrayAdapter<>(this, resource, available) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                        LinearLayout layout = (LinearLayout) inflater.inflate(resource, parent, false);
                        ((ImageView) layout.findViewById(R.id.imageView)).setImageResource(Unit.getDrawable(available.get(position)));
                        ((TextView) layout.findViewById(R.id.nameTextView)).setText(Unit.getName(available.get(position)));
                        ((TextView) layout.findViewById(R.id.descriptionTextView)).setText(Unit.descript(available.get(position)));
                        return layout;
                    }
                });
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Select unit")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setView(listView)
                        .show();
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    ((ImageView) pickLayout.getChildAt(selected)).setImageResource(Unit.getDrawable(available.get(position)));
                    setup.set(selected, available.get(position));
                    dialog.dismiss();
                });
            });
        }

        button.setOnClickListener(v ->
                startActivityForResult(new Intent(this, BattleActivity.class)
                        .putExtra("unitTypes", setup.toArray())
                        .putExtra("level", 1), 0));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            AutoLegendsService.instance.levelUp(user.getLogin()).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    user = response.body();
                    moneyTextView.setText(user.getMoney() + " money");
                    button.setText("Start\n" + user.getLevel() + " lvl");
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("You won!")
                            .setMessage("+100 money")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }

                @Override
                public void onFailure(Call<User> call, Throwable throwable) {

                }
            });
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("You lose")
                    .setMessage("Try again")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }

}