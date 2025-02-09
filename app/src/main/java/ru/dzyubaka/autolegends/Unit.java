package ru.dzyubaka.autolegends;

import java.util.Map;

class Unit implements Cloneable {

    static Map<UnitType, Unit> prototypes = Map.ofEntries(
            Map.entry(UnitType.PALADIN, new Unit(
                    UnitType.PALADIN,
                    UnitClass.TANK,
                    "Paladin",
                    R.drawable.paladin,
                    100,
                    20,
                    20,
                    20,
                    200,
                    0,
                    0,
                    Rarity.RARE
            )),
            Map.entry(UnitType.FIRE_MAGE, new Unit(
                    UnitType.FIRE_MAGE,
                    UnitClass.MAGE,
                    "Fire Mage",
                    R.drawable.fire_mage,
                    50,
                    30,
                    5,
                    5,
                    50,
                    0,
                    30,
                    Rarity.RARE
            )),
            Map.entry(UnitType.SKELETON, new Unit(
                    UnitType.SKELETON,
                    UnitClass.TANK,
                    "Skeleton Warrior",
                    R.drawable.skeleton,
                    50,
                    20,
                    10,
                    10,
                    100,
                    0,
                    0,
                    Rarity.RARE
            )),
            Map.entry(UnitType.PRIEST, new Unit(
                    UnitType.PRIEST,
                    UnitClass.SUPPORT,
                    "Priest",
                    R.drawable.priest,
                    40,
                    15,
                    5,
                    5,
                    40,
                    0,
                    20,
                    Rarity.EPIC
            )),
            Map.entry(UnitType.FAIRY, new Unit(
                    UnitType.FAIRY,
                    UnitClass.SUPPORT,
                    "Fairy",
                    R.drawable.fairy,
                    20,
                    10,
                    0,
                    0,
                    10,
                    10,
                    10,
                    Rarity.EPIC
            )),
            Map.entry(UnitType.DRUID, new Unit(
                    UnitType.DRUID,
                    UnitClass.WARRIOR,
                    "Druid",
                    R.drawable.druid,
                    60,
                    20,
                    10,
                    10,
                    60,
                    0,
                    30,
                    Rarity.LEGENDARY
            )),
            Map.entry(UnitType.BEAR, new Unit(
                    UnitType.BEAR,
                    UnitClass.WARRIOR,
                    "Bear",
                    R.drawable.bear,
                    100,
                    35,
                    20,
                    20,
                    150,
                    0,
                    0,
                    Rarity.LEGENDARY
            )),
            Map.entry(UnitType.MUSKETEER, new Unit(
                    UnitType.MUSKETEER,
                    UnitClass.WARRIOR,
                    "Musketeer",
                    R.drawable.musketeer,
                    60,
                    40,
                    0,
                    0,
                    30,
                    0,
                    0,
                    Rarity.RARE
            )),
            Map.entry(UnitType.GOLEM, new Unit(
                    UnitType.GOLEM,
                    UnitClass.TANK,
                    "Golem",
                    R.drawable.golem,
                    300,
                    5,
                    20,
                    20,
                    300,
                    0,
                    0,
                    Rarity.RARE
            )),
            Map.entry(UnitType.ASSASSIN, new Unit(
                    UnitType.ASSASSIN,
                    UnitClass.WARRIOR,
                    "Assassin",
                    R.drawable.assassin,
                    50,
                    35,
                    10,
                    10,
                    60,
                    10,
                    30,
                    Rarity.EPIC
            )),
            Map.entry(UnitType.STAR_MAGE, new Unit(
                    UnitType.STAR_MAGE,
                    UnitClass.MAGE,
                    "Star Mage",
                    R.drawable.star_mage,
                    50,
                    30,
                    5,
                    5,
                    50,
                    0,
                    30,
                    Rarity.EPIC
            )),
            Map.entry(UnitType.ORC, new Unit(
                    UnitType.ORC,
                    UnitClass.TANK,
                    "Orc",
                    R.drawable.orc,
                    100,
                    20,
                    5,
                    5,
                    150,
                    0,
                    20,
                    Rarity.RARE
            )),
            Map.entry(UnitType.VAMPIRE, new Unit(
                    UnitType.VAMPIRE,
                    UnitClass.MAGE,
                    "Vampire",
                    R.drawable.vampire,
                    60,
                    20,
                    0,
                    0,
                    50,
                    0,
                    40,
                    Rarity.LEGENDARY
            ))
    );

    private final UnitType unitType;
    private final UnitClass unitClass;
    private final String name;
    private final int drawable;
    private int health;
    private int maxHealth;
    private final int damage;
    private int physicalArmor;
    private int magicalArmor;
    private int aggro;
    private int mana;
    private int maxMana;
    private int stun;
    private Rarity rarity;

    Unit(
            UnitType unitType,
            UnitClass unitClass,
            String name,
            int drawable,
            int health,
            int damage,
            int physicalArmor,
            int magicalArmor,
            int aggro,
            int mana,
            int maxMana,
            Rarity rarity
    ) {
        this.unitType = unitType;
        this.unitClass = unitClass;
        this.name = name;
        this.drawable = drawable;
        this.health = health;
        this.maxHealth = health;
        this.damage = damage;
        this.physicalArmor = physicalArmor;
        this.magicalArmor = magicalArmor;
        this.aggro = aggro;
        this.mana = mana;
        this.maxMana = maxMana;
        this.rarity = rarity;
    }

    static Unit create(UnitType unitType) {
        return prototypes.get(unitType).clone();
    }

    public static String getName(UnitType unitType) {
        return prototypes.get(unitType).name;
    }

    public static int getDrawable(UnitType unitType) {
        return prototypes.get(unitType).drawable;
    }

    public static String descript(UnitType unitType) {
        Unit unit = prototypes.get(unitType);
        return String.format("HP %d, DMG %d, PRES %d, MRES %d, AGR %d",
                unit.health, unit.damage, unit.physicalArmor, unit.magicalArmor, unit.aggro);
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public UnitClass getUnitClass() {
        return unitClass;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void addHealth(int health) {
        this.health = Math.min(Math.max(this.health + health, 0), maxHealth);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getDamage() {
        return damage;
    }

    public int getPhysicalArmor() {
        return physicalArmor;
    }

    public void setPhysicalArmor(int physicalArmor) {
        this.physicalArmor = physicalArmor;
    }

    public int getMagicalArmor() {
        return magicalArmor;
    }

    public void setMagicalArmor(int magicalArmor) {
        this.magicalArmor = magicalArmor;
    }

    public int getAggro() {
        return aggro;
    }

    public void zeroAggro() {
        this.aggro = 0;
    }

    public int getMana() {
        return mana;
    }

    public void zeroMana() {
        this.mana = 0;
    }

    public void addMana(int mana) {
        this.mana = Math.min(Math.max(this.mana + mana, 0), 100);
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public int getStun() {
        return stun;
    }

    public void setStun(int stun) {
        this.stun = stun;
    }

    void takeDamage(int damage, DamageType damageType) {
        switch (damageType) {
            case PHYSICAL:
                health -= (int) (damage * (1 - physicalArmor / 100f));
                break;
            case MAGICAL:
                health -= (int) (damage * (1 - magicalArmor / 100f));
                break;
            case PURE:
                health -= damage;
                break;
        }
    }

    @Override
    public Unit clone() {
        try {
            return (Unit) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
