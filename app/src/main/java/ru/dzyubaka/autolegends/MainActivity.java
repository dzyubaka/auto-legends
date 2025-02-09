package ru.dzyubaka.autolegends;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    User user;
    private TextView moneyTextView;
    private TextView diamondsTextView;
    private final ShopFragment shopFragment = new ShopFragment();
    private final SetupFragment setupFragment = new SetupFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = (User) getIntent().getSerializableExtra("user");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, setupFragment)
                .commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setSelectedItemId(R.id.setup);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.shop) {
                fragment = shopFragment;
            } else if (item.getItemId() == R.id.setup) {
                fragment = setupFragment;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_frame, fragment)
                    .commit();
            return true;
        });
        moneyTextView = findViewById(R.id.money);
        diamondsTextView = findViewById(R.id.diamonds);
        update();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            AutoLegendsService.instance.levelUp(user.getLogin()).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    user = response.body();
                    moneyTextView.setText(user.getMoney() + " money");
                    setupFragment.startButton.setText("Start\n" + user.getLevel() + " lvl");
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

    void update() {
        moneyTextView.setText(user.getMoney() + " money");
        diamondsTextView.setText(user.getDiamonds() + " diamonds");
    }
}