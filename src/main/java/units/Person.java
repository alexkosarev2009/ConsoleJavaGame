package units;

import java.util.Random;

public class Person {
    private int x, y;
    private int hp = 3;
    private final String icon = "\uD83E\uDDD9";

    public Person(int sizeBoard) {
        Random random = new Random();
        setX(1 + random.nextInt(sizeBoard));
        setY(sizeBoard);
    }

    public Person(int x, int y) {
        setX(x);
        setY(y);
    }

    public Person() {
        this(1, 1);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public boolean isMoveCorrect(int x, int y) {
        return this.x == x && Math.abs(this.y - y) == 1 || this.y == y && Math.abs(this.x - x) == 1;
    }

    public void heal(int healAmount) {
        this.hp += healAmount;
    }
    public void takeDamage(int damage) {
        this.hp = Math.max(0, this.hp - damage);
    }

    public int getHp() {
        return hp;
    }

    public String getIcon() {
        return icon;
    }
}

