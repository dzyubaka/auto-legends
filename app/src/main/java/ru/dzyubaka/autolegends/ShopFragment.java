package ru.dzyubaka.autolegends;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shop, container, false);
        root.findViewById(R.id.imageView).setOnClickListener(view ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Drop")
                        .setMessage("70% rare\n25% epic\n5% legendary")
                        .setPositiveButton(android.R.string.ok, null)
                        .show());
        root.findViewById(R.id.button).setOnClickListener(view -> {
            if (((MainActivity) requireActivity()).user.getDiamonds() >= 100) {
                AutoLegendsService.instance.openCase(((MainActivity) requireActivity()).user.getLogin()).enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.code() == 201) {
                            ArrayList<UnitType> newInv = new ArrayList<>(response.body().getInventory());
                            newInv.removeAll(((MainActivity) requireActivity()).user.getInventory());
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Case opened")
                                    .setMessage("You've got " + newInv.get(0))
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        } else {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Case opened")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        }
                        ((MainActivity) requireActivity()).user = response.body();
                        ((MainActivity) requireActivity()).update();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable throwable) {

                    }
                });
            }
        });
        return root;
    }
}