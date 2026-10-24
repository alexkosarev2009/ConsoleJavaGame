package units.monsters;

import java.util.Random;
import java.util.Scanner;

public class BigMonster extends Monster {
    public static final String IMAGE = "\uD83E\uDDCC";

    @Override
    public String getImage() {
        return BigMonster.IMAGE;
    }

    public BigMonster(Random random, int sizeBoard, String[][] board) {
        super(random, sizeBoard, board);
    }

    @Override
    public boolean taskMonster(Scanner scanner, Random random, int difficulty){
        System.out.println("Решите задачу:");

        int x = random.nextInt(2 * difficulty, 10 * difficulty);
        int y = random.nextInt(2 * difficulty, 10 * difficulty);
        int z = random.nextInt(2 * difficulty, 10 * difficulty);
        int trueAnswer = x * y - z;

        System.out.println("Реши пример: " + x + " * " + y + " - " + z + " = ?");
        int answer = scanner.nextInt();

        return trueAnswer == answer;
    }
}
