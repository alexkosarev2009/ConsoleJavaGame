package units.monsters;

import java.util.Random;
import java.util.Scanner;

public class Monster {
    public static final String IMAGE = "\uD83E\uDDDF";
    private int x;
    private int y;

    public Monster(Random random, int sizeBoard, String[][] board) {
        do {
            x = 1 + random.nextInt(sizeBoard);
            y = 1 + random.nextInt(sizeBoard - 1);
        } while (!board[y - 1][x - 1].equals("  "));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getImage() {
        return Monster.IMAGE;
    }

    public boolean conflictPerson(int personX, int personY) {
        return personX == x && personY == y;
    }

    public boolean taskMonster(Scanner scanner, Random random, int difficulty) {
        System.out.println("Решите задачу:");

        int a = random.nextInt(10 * difficulty, 100 * difficulty);
        int b = random.nextInt(10 * difficulty, 100 * difficulty);

        int trueAnswer = a + b;

        System.out.println("Реши пример: " + a + " + " + b + " = ?");

        int ans = scanner.nextInt();

        return ans == trueAnswer;
    }
}