package ru.dzyubaka.autolegends;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText nameEditText = findViewById(R.id.name);
        EditText loginEditText = findViewById(R.id.login);
        EditText passwordEditText = findViewById(R.id.password);
        Switch registerSwitch = findViewById(R.id.register_switch);
        Button button = findViewById(R.id.button);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        registerSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                nameEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        button.setOnClickListener(view -> {
            view.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            String login = loginEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            Call<User> call;

            if (registerSwitch.isChecked()) {
                call = AutoLegendsService.instance.register(login, nameEditText.getText().toString(), password);
            } else {
                call = AutoLegendsService.instance.login(login, password);
            }

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    if (user != null) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                .putExtra("user", user));
                    } else {
                        view.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable throwable) {
                    view.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Cannot connect to the server", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}