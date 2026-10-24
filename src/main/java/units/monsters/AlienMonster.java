package units.monsters;

import java.util.Random;
import java.util.Scanner;

public class AlienMonster extends Monster {
    public static final String IMAGE = "\uD83D\uDC7E";

    public AlienMonster(Random random, int sizeBoard, String[][] board) {
        super(random, sizeBoard, board);
    }
    @Override
    public String getImage() {
        return AlienMonster.IMAGE;
    }

    @Override
    public boolean taskMonster(Scanner scanner, Random random, int difficulty){
        System.out.println("Решите задачу:");
        int a = random.nextInt(2 + difficulty, 5 + difficulty);
        System.out.println("Какой следующий элемент последовательности: "
                + a + ", "
                + a * a + ", "
                + a * a * a + ", ?"

        );
        int answer = scanner.nextInt();
        return answer == a * a * a * a;
    }

}
