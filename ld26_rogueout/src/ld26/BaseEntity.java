package ld26;

import com.badlogic.gdx.math.MathUtils;

class BaseEntity implements Entity {
    private Dungeon dungeon;
    private String appearance;
    private String description;
    private int hitPoints;
    private int level;
    private int damage;
    private int width;
    private int x;
    private int y;
    private boolean isMonster;
    private boolean isDestructible;

    public BaseEntity(Dungeon dungeon, String s) {
        this(dungeon, s, null, 0);
    }

    public BaseEntity(Dungeon dungeon, String s, String description) {
        this(dungeon, s, description, 0);
    }

    public BaseEntity(Dungeon dungeon, String s, String description, int damage) {
        this.dungeon = dungeon;
        this.appearance = s.substring(0, 1);
        this.description = description;
        this.damage = damage;
        this.width = s.length();
        this.level = 1;
    }

    @Override
    public void remove() {
        dungeon.remove(this);
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    public BaseEntity isMonster(boolean isMonster) {
        this.isMonster = true;
        return this;
    }

    public BaseEntity level(int level) {
        this.level = level;
        hitPoints = level > 0 ? level * MathUtils.random(1, 8) : MathUtils
                .random(1, 4);
        return this;
    }

    @Override
    public String appearance() {
        return appearance;
    }

    @Override
    public int damage() {
        return damage;
    }

    @Override
    public String damageMessage(int damage) {
        return String.format("You hit %s and lose %d health.", description,
                damage);
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isMonster() {
        return isMonster;
    }

    @Override
    public boolean hit(int damage) {
        hitPoints -= damage;
        return !(isMonster || isDestructible) || hitPoints > 0;
    }

    @Override
    public String hitMessage(int damage) {
        if (hitPoints > 0) return String.format("You hit %s.", description);
        return String.format("You kill %s.", description);
    }

    @Override
    public int xp() {
        return isMonster ? level * (level + 6) + 1 : 0;
    }

    public BaseEntity setDestructible(int hitPoints) {
        this.isDestructible = true;
        this.hitPoints = hitPoints;
        return this;
    }

    @Override
    public boolean isDestructible() {
        return isDestructible;
    }

    @Override
    public int hitPoints() {
        return hitPoints;
    }
}
