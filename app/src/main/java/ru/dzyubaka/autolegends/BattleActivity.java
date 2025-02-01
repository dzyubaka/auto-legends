package ru.dzyubaka.autolegends;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BattleActivity extends AppCompatActivity {

    private static final String TAG = "BattleActivity";
    private static final Random random = new Random();
    private boolean playerTurn = true;
    private int attackerIndex = -1;
    private final Timer timer = new Timer();
    private boolean attacked;

    private Animation attackAnimation;
    private Animation stunAnimation;

    private Team alliesTeam;
    private Team enemiesTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        Intent intent = getIntent();

        attackAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        stunAnimation = AnimationUtils.loadAnimation(this, R.anim.vibrate);

        UnitType[][] levels = {
                {
                        UnitType.PALADIN,
                        UnitType.PALADIN,
                        UnitType.PALADIN,
                        UnitType.PALADIN,
                        UnitType.PALADIN
                }
        };

        alliesTeam = new Team((UnitType[]) intent.getSerializableExtra("unitTypes"), findViewById(R.id.alliesLayout));
        enemiesTeam = new Team(levels[intent.getIntExtra("level", -1) - 1], findViewById(R.id.enemiesLayout));

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                attacked = false;
                while (!attacked) {
                    if (playerTurn) {
                        attackerIndex = (attackerIndex + 1) % 5;
                        turn(alliesTeam, enemiesTeam);
                    } else {
                        turn(enemiesTeam, alliesTeam);
                    }
                    playerTurn = !playerTurn;
                }
            }
        }, 0, 750);
    }

    private void turn(Team attackersTeam, Team defendersTeam) {
        if (attackersTeam.units[attackerIndex].getHealth() > 0) { // если юнит жив

            if (attackersTeam.units[attackerIndex].getStun() > 0) {
                Log.d(TAG, (playerTurn ? "ally  " : "enemy ") + attackerIndex + " skips   turn");
                attackersTeam.units[attackerIndex].setStun(attackersTeam.units[attackerIndex].getStun() - 1);
                if (attackersTeam.units[attackerIndex].getStun() <= 0) {
                    runOnUiThread(() -> ((LinearLayout) attackersTeam.layout.getChildAt(attackerIndex)).getChildAt(0).setVisibility(View.INVISIBLE));
                }
                attackersTeam.layout.getChildAt(attackerIndex).startAnimation(stunAnimation);
                attacked = true;
                return;
            }

            // мульти-атака воина. не двигай никуда эту хуйню!!!
            if ((attackersTeam.units[attackerIndex].getMaxMana() == 0 || attackersTeam.units[attackerIndex].getMaxMana() > attackersTeam.units[attackerIndex].getMana()) && attackersTeam.units[attackerIndex].getUnitClass() == UnitClass.WARRIOR && random.nextInt(100) < attackersTeam.unitClasses.get(UnitClass.WARRIOR) / 2 * 20) {
                warriorMultiAttack(defendersTeam, attackersTeam);
            }

            int totalWeight = sumWeight(defendersTeam.units);
            if (totalWeight == 0) {
                timer.cancel();
                setResult(playerTurn ? RESULT_OK : RESULT_CANCELED);
                finish();
                return;
            }
            int victimIndex = getVictimIndex(defendersTeam.units, totalWeight);
            if (attackersTeam.units[attackerIndex].getMaxMana() > 0) { // если юнит вообще имеет ману
                if (attackersTeam.units[attackerIndex].getMana() >= attackersTeam.units[attackerIndex].getMaxMana()) { // если мана максимальная
                    useUltimate(attackersTeam, defendersTeam, victimIndex);
                } else { // если юнит может иметь ману, но она не максимальная
                    attack(defendersTeam.units, victimIndex, attackersTeam.units, attackerIndex);
                    attackersTeam.units[attackerIndex].addMana(10);
                }
                updateMana(attackersTeam.layout, attackersTeam.units, attackerIndex);
            } else { // если юнит вообще не имеет маны
                attack(defendersTeam.units, victimIndex, attackersTeam.units, attackerIndex);
            }

            // если юнит мёртв, то он не вызывает агрессии
            if (defendersTeam.units[victimIndex].getHealth() <= 0) {
                defendersTeam.units[victimIndex].zeroAggro();
            }

            attackersTeam.layout.getChildAt(attackerIndex).startAnimation(attackAnimation);
            updateHealth(defendersTeam.layout, defendersTeam.units, victimIndex);
            attacked = true;
        }
    }

    private void useUltimate(Team attackersTeam, Team defendersTeam, int victimIndex) {
        if (attackersTeam.units[attackerIndex].getUnitType() == UnitType.FIRE_MAGE) {
            useFireMageUltimate(defendersTeam, victimIndex, attackersTeam);
        } else if (attackersTeam.units[attackerIndex].getUnitType() == UnitType.PRIEST) {
            usePriestUltimate(attackersTeam);
        } else if (attackersTeam.units[attackerIndex].getUnitType() == UnitType.FAIRY) {
            useFairyUltimate(attackersTeam);
        } else if (attackersTeam.units[attackerIndex].getUnitType() == UnitType.DRUID) {
            useDruidUltimate(attackersTeam);
        } else if (attackersTeam.units[attackerIndex].getUnitType() == UnitType.ASSASSIN) {
            useAssassinUltimate(defendersTeam, attackersTeam);
        } else if (attackersTeam.units[attackerIndex].getUnitType() == UnitType.STAR_MAGE) {
            useStarMageUltimate(defendersTeam, attackersTeam);
        } else if (attackersTeam.units[attackerIndex].getUnitType() == UnitType.ORC) {
            useOrcUltimate(defendersTeam, attackersTeam, victimIndex);
        } else if (attackersTeam.units[attackerIndex].getUnitType() == UnitType.VAMPIRE) {
            useVampireUltimate(defendersTeam, attackersTeam);
        }
    }

    private void useVampireUltimate(Team defendersTeam, Team attackersTeam) {
        for (int i = 0; i < 5; i++) {
            if (defendersTeam.units[i].getHealth() > 0) {
                int damage = attackersTeam.units[attackerIndex].getDamage() / 2;
                defendersTeam.units[i].takeDamage(damage, DamageType.MAGICAL);
                updateHealth(defendersTeam.layout, defendersTeam.units, i);
                attackersTeam.units[attackerIndex].addHealth(damage);
            }
        }
        updateHealth(attackersTeam.layout, attackersTeam.units, attackerIndex);
        attackersTeam.units[attackerIndex].zeroMana();
    }

    private void useOrcUltimate(Team defendersTeam, Team attackersTeam, int victimIndex) {
        Log.d(TAG, (playerTurn ? "ally  " : "enemy ") + attackerIndex + " stuns   " + (playerTurn ? "enemy " : "ally  ") + victimIndex);
        defendersTeam.units[victimIndex].takeDamage(attackersTeam.units[attackerIndex].getDamage() * 2, DamageType.PHYSICAL);
        defendersTeam.units[victimIndex].setStun(1);
        runOnUiThread(() -> ((LinearLayout) defendersTeam.layout.getChildAt(victimIndex)).getChildAt(0).setVisibility(View.VISIBLE));
        attackersTeam.units[attackerIndex].zeroMana();
    }

    private void useStarMageUltimate(Team defendersTeam, Team attackersTeam) {
        for (int i = 0; i < 5; i++) {
            if (defendersTeam.units[i].getHealth() > 0) {
                defendersTeam.units[i].takeDamage(attackersTeam.units[attackerIndex].getDamage() / 2, DamageType.MAGICAL);
                updateHealth(defendersTeam.layout, defendersTeam.units, i);
            }
        }
        attackersTeam.units[attackerIndex].zeroMana();
    }

    private void useAssassinUltimate(Team defendersTeam, Team attackersTeam) {
        int defenderIndex = -1;

        for (int i = 0; i < 5; i++) {
            if (defendersTeam.units[i].getHealth() > 0) {
                defenderIndex = i;
                break;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (defendersTeam.units[i].getHealth() > 0 && defendersTeam.units[defenderIndex].getHealth() > defendersTeam.units[i].getHealth()) {
                defenderIndex = i;
            }
        }

        if (defenderIndex == -1) {
            timer.cancel();
            setResult(playerTurn ? RESULT_OK : RESULT_CANCELED);
            finish();
            return;
        }

        defendersTeam.units[defenderIndex].takeDamage(attackersTeam.units[attackerIndex].getDamage(), DamageType.PURE);
        updateHealth(defendersTeam.layout, defendersTeam.units, defenderIndex);
        attackersTeam.units[attackerIndex].zeroMana();
    }

    private void warriorMultiAttack(Team defendersTeam, Team attackersTeam) {
        int totalWeight = sumWeight(defendersTeam.units);
        if (totalWeight == 0) {
            timer.cancel();
            setResult(playerTurn ? RESULT_OK : RESULT_CANCELED);
            finish();
            return;
        }
        int victimIndex = getVictimIndex(defendersTeam.units, totalWeight);
        attack(defendersTeam.units, victimIndex, attackersTeam.units, attackerIndex);
        updateHealth(defendersTeam.layout, defendersTeam.units, victimIndex);
    }

    private void useFireMageUltimate(Team defendersTeam, int victimIndex, Team attackersTeam) {
        defendersTeam.units[victimIndex].takeDamage(attackersTeam.units[attackerIndex].getDamage() * 3, DamageType.MAGICAL);
        attackersTeam.units[attackerIndex].zeroMana();
    }

    private void usePriestUltimate(Team attackersTeam) {
        for (int i = 0; i < 5; i++) {
            if (attackersTeam.units[i].getHealth() > 0) {
                attackersTeam.units[i].addHealth(attackersTeam.units[attackerIndex].getDamage());
                updateHealth(attackersTeam.layout, attackersTeam.units, i);
            }
        }
        attackersTeam.units[attackerIndex].zeroMana();
    }

    private void useFairyUltimate(Team attackersTeam) {
        int index = getLowestHealthIndex(attackersTeam.units);
        attackersTeam.units[index].addHealth(attackersTeam.units[attackerIndex].getDamage() * 3);
        updateHealth(attackersTeam.layout, attackersTeam.units, index);
        attackersTeam.units[attackerIndex].zeroMana();
    }

    private void useDruidUltimate(Team attackersTeam) {
        attackersTeam.units[attackerIndex] = Unit.create(UnitType.BEAR);
        attackersTeam.units[attackerIndex].setMaxHealth(attackersTeam.units[attackerIndex].getMaxHealth() + attackersTeam.unitClasses.get(UnitClass.SUPPORT) * 10);
        attackersTeam.units[attackerIndex].setHealth(attackersTeam.units[attackerIndex].getHealth() + attackersTeam.unitClasses.get(UnitClass.SUPPORT) * 10);
        updateImage(attackersTeam.layout, attackersTeam.units, attackerIndex);
        updateMaxHealth(attackersTeam.layout, attackersTeam.units, attackerIndex);
        updateHealth(attackersTeam.layout, attackersTeam.units, attackerIndex);
        hideMana(attackersTeam.layout, attackerIndex);
    }

    private void attack(Unit[] victimUnits, int victimIndex, Unit[] attackerUnits, int attackerIndex) {
        Log.d(TAG, (playerTurn ? "ally  " : "enemy ") + attackerIndex + " attacks " + (playerTurn ? "enemy " : "ally  ") + victimIndex);
        victimUnits[victimIndex].takeDamage(attackerUnits[attackerIndex].getDamage(), DamageType.PHYSICAL);
    }

    private void updateMana(LinearLayout layout, Unit[] units, int index) {
        ((ProgressBar) ((LinearLayout) layout.getChildAt(index)).getChildAt(3)).setProgress(units[index].getMana(), true);
    }

    private void hideMana(LinearLayout layout, int index) {
        ((LinearLayout) layout.getChildAt(index)).getChildAt(3).setVisibility(View.INVISIBLE);
    }

    private void updateMaxHealth(LinearLayout layout, Unit[] units, int index) {
        ((ProgressBar) ((LinearLayout) layout.getChildAt(index)).getChildAt(2)).setMax(units[index].getMaxHealth());
    }

    private void updateHealth(LinearLayout layout, Unit[] units, int index) {
        ((ProgressBar) ((LinearLayout) layout.getChildAt(index)).getChildAt(2)).setProgress(units[index].getHealth(), true);
    }

    private int getLowestHealthIndex(Unit[] units) {
        int index = -1;

        for (int i = 0; i < units.length; i++) {
            if (units[i].getHealth() > 0) {
                index = i;
                break;
            }
        }

        for (int i = 0; i < units.length; i++) {
            if (units[i].getHealth() > 0 && (float) units[i].getHealth() / units[i].getMaxHealth() < (float) units[index].getHealth() / units[index].getMaxHealth()) {
                index = i;
            }
        }

        return index;
    }

    private static int sumWeight(Unit[] units) {
        int totalWeight = 0;
        for (Unit unit : units) {
            totalWeight += unit.getAggro();
        }
        return totalWeight;
    }

    private static int getVictimIndex(Unit[] units, int totalWeight) {
        int randomNumber = random.nextInt(totalWeight);

        int cumulativeWeight = 0;
        for (int i = 0; i < units.length; i++) {
            cumulativeWeight += units[i].getAggro();
            if (randomNumber < cumulativeWeight) {
                return i;
            }
        }

        throw new RuntimeException();
    }

    private void updateImage(LinearLayout layout, Unit[] units, int index) {
        ((ImageView) ((LinearLayout) layout.getChildAt(index)).getChildAt(1)).setImageResource(Unit.getDrawable(units[index].getUnitType()));
    }

}