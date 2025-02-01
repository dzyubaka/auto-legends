package ru.dzyubaka.autolegends;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.HashMap;

class Team {
    Unit[] units;
    LinearLayout layout;
    HashMap<UnitClass, Integer> unitClasses;

    Team(UnitType[] unitTypes, LinearLayout layout) {
        units = createUnits(unitTypes);
        this.layout = layout;
        countClasses();
        applyBonuses();
        showUnits(layout, units);
    }

    private void applyBonuses() {
        applyTankBonus(units, unitClasses.get(UnitClass.TANK));
        applyMageBonus(units, unitClasses.get(UnitClass.MAGE));
        applySupportBonus(units, unitClasses.get(UnitClass.SUPPORT));
    }

    private void applySupportBonus(Unit[] units, int count) {
        for (int i = 0; i < 5; i++) {
            units[i].setMaxHealth(units[i].getMaxHealth() + count * 10);
            units[i].setHealth(units[i].getHealth() + count * 10);
        }
    }

    private Unit[] createUnits(UnitType[] unitTypes) {
        Unit[] units = new Unit[5];

        for (int i = 0; i < 5; i++) {
            units[i] = Unit.create(unitTypes[i]);
        }

        return units;
    }

    private void countClasses() {
        unitClasses = new HashMap<>();

        for (int i = 0; i < 5; i++) {
            Integer count = unitClasses.get(units[i].getUnitClass());
            count = count != null ? count : 0;
            unitClasses.put(units[i].getUnitClass(), count + 1);
        }

        for (UnitClass unitClass : UnitClass.values()) {
            if (!unitClasses.containsKey(unitClass)) {
                unitClasses.put(unitClass, 0);
            }
        }
    }

    private void applyTankBonus(Unit[] units, int count) {
        for (int i = 0; i < 5; i++) {
            if (units[i].getUnitClass() == UnitClass.TANK) {
                units[i].setPhysicalArmor(units[i].getPhysicalArmor() + count / 2 * 10);
                units[i].setMagicalArmor(units[i].getMagicalArmor() + count / 2 * 10);
            }
        }
    }

    private void applyMageBonus(Unit[] units, int count) {
        for (int i = 0; i < 5; i++) {
            if (units[i].getUnitClass() == UnitClass.MAGE) {
                units[i].setMaxMana(units[i].getMaxMana() - count / 2 * 10);
                if (units[i].getMaxMana() <= 0) {
                    units[i].setMaxMana(5);
                }
            }
        }
    }

    private void showUnits(LinearLayout unitsLayout, Unit[] units) {
        for (int i = 0; i < 5; i++) {
            LinearLayout unitLayout = (LinearLayout) unitsLayout.getChildAt(i);
            ((ImageView) unitLayout.getChildAt(1)).setImageResource(Unit.getDrawable(units[i].getUnitType()));
            ProgressBar healthProgressBar = (ProgressBar) unitLayout.getChildAt(2);
            healthProgressBar.setMax(units[i].getMaxHealth());
            healthProgressBar.setProgress(units[i].getHealth());
            ProgressBar manaProgressBar = (ProgressBar) unitLayout.getChildAt(3);
            manaProgressBar.setVisibility(units[i].getMaxMana() > 0 ? View.VISIBLE : View.INVISIBLE);
            manaProgressBar.setMax(units[i].getMaxMana());
            manaProgressBar.setProgress(units[i].getMana());
        }
    }

}
