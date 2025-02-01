package ru.dzyubaka.autolegends;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private String login;
    private int money;
    private int diamonds;
    private int level;
    private List<UnitType> inventory;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getDiamonds() {
        return diamonds;
    }

    public void setDiamonds(int diamonds) {
        this.diamonds = diamonds;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<UnitType> getInventory() {
        return inventory;
    }

    public void setInventory(List<UnitType> inventory) {
        this.inventory = inventory;
    }
}
