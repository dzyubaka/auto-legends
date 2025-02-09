package ru.dzyubaka.autolegends;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class SetupFragment extends Fragment {

    Button startButton;
    private int selected = -1;
    private final ArrayList<UnitType> setup = new ArrayList<>(Arrays.asList(
            UnitType.PALADIN,
            UnitType.FIRE_MAGE,
            UnitType.SKELETON,
            UnitType.SKELETON,
            UnitType.SKELETON
    ));

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setup, container, false);
        LinearLayout setupLayout = root.findViewById(R.id.setup);
        startButton = root.findViewById(R.id.button);
        startButton.setText("Start\n" + ((MainActivity) requireActivity()).user.getLevel() + " lvl");
        for (int i = 0; i < 5; i++) {
            int closure = i;
            ImageView unitImageView = (ImageView) setupLayout.getChildAt(i);
            unitImageView.setImageResource(Unit.getDrawable(setup.get(i)));
            unitImageView.setOnClickListener(v -> {
                selected = closure;
                ListView listView = new ListView(requireContext());
                int resource = R.layout.item_unit;
                ArrayList<UnitType> available = new ArrayList<>();
                available.add(UnitType.SKELETON);

                for (UnitType unitType : ((MainActivity) requireActivity()).user.getInventory()) {
                    if (!setup.contains(unitType)) {
                        available.add(unitType);
                    }
                }

                listView.setAdapter(new ArrayAdapter<>(requireContext(), resource, available) {
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
                AlertDialog dialog = new AlertDialog.Builder(requireContext())
                        .setTitle("Select unit")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setView(listView)
                        .show();
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    ((ImageView) setupLayout.getChildAt(selected)).setImageResource(Unit.getDrawable(available.get(position)));
                    setup.set(selected, available.get(position));
                    dialog.dismiss();
                });
            });
        }

        startButton.setOnClickListener(v ->
                startActivityForResult(new Intent(requireContext(), BattleActivity.class)
                        .putExtra("unitTypes", setup.toArray(new UnitType[0]))
                        .putExtra("level", 1), 0));

        return root;
    }
}