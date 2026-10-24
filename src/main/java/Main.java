import units.Person;
import units.monsters.AlienMonster;
import units.monsters.BigMonster;
import units.monsters.Monster;

import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final int BOARD_SIZE = 7;
    private static final double MONSTER_DENSITY = 0.7;

    private static final int MONSTER_COUNT =
            (int) (BOARD_SIZE * (BOARD_SIZE - 1) * MONSTER_DENSITY);

    private static final String[][] board = new String[BOARD_SIZE][BOARD_SIZE];
    private static final Monster[] monsters = new Monster[MONSTER_COUNT];

    private static int difficulty = 1;

    private static int step = 0;
    private static long start;
    private static long end;

    private static final Person person = new Person(BOARD_SIZE);

    private static final String personIcon = "\uD83E\uDDD9";
    private static final String castle = "\uD83C\uDFF0";
    private static final String pointOfInterest = "❓";

    private static final String leftBlock = " | ";
    private static final String rightBlock = " |";
    private static final String wall = " + " + "—— + ".repeat(BOARD_SIZE);

    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    private static boolean isGameFinished = false;
    private static boolean hasLost = false;
    private static boolean wasLastChanceUsed = false;
    private static boolean hasFoundKey = false;
    private static int pointsOfInterestLeft = 0;
    private static int score = 0;

    public static void main(String[] args) {

        int castleX = 1 + random.nextInt(BOARD_SIZE);
        int castleY = 1;

        for (int y = 1; y <= BOARD_SIZE; y++) {
            for (int x = 1; x <= BOARD_SIZE; x++) {
                board[y - 1][x - 1] = "  ";
            }
        }

        for (int i = 0; i < MONSTER_COUNT; i++) {
            Monster monster;
            int randomInt = random.nextInt(21);
            if (randomInt <= 7) {
                monster = new BigMonster(random, BOARD_SIZE, board);
            } else if (randomInt <= 10) {
                monster = new AlienMonster(random, BOARD_SIZE, board);
            } else {
                monster = new Monster(random, BOARD_SIZE, board);
            }

            monsters[i] = monster;
            board[monster.getY() - 1][monster.getX() - 1] = monster.getImage();
        }
        board[castleY - 1][castleX - 1] = castle;

        while (pointsOfInterestLeft < 3) {
            int[] cords = findRandomFreeCell(1, BOARD_SIZE - 2, 1, BOARD_SIZE);
            board[cords[1] - 1][cords[0] - 1] = pointOfInterest;
            pointsOfInterestLeft++;
        }


        System.out.println(
                "Привет! Ты готов начать играть в игру?"
                        + "(Напиши: ДА или НЕТ)"
        );

        String answer = scanner.nextLine();

        System.out.println("Ваш ответ:\t" + answer.toUpperCase());

        switch (answer.toUpperCase()) {
            case "ДА":
                System.out.println("Выбери сложность игры (от 1 до 5):");
                difficulty = scanner.nextInt();
                System.out.println("Выбранная сложность:\t" + difficulty);
                start = System.currentTimeMillis();
                do {
                    board[person.getY() - 1][person.getX() - 1] = personIcon;

                    renderBoard();

                    printGameStatus();

                    inputMove();

                    if (person.getHp() == 0 && !wasLastChanceUsed) {
                        System.out.println("Последний шанс!");
                        if (useLastChance()) {
                            System.out.println("Тебе повезло, зельем оказалось лекарство (+1 жизнь)");
                            person.heal(1);
                            wasLastChanceUsed = true;
                        } else {
                            System.out.println("Неповезло! Ты выпил яд..");
                        }
                    }
                    if (person.getHp() == 0) {
                        System.out.println("Закончились жизни.");
                        isGameFinished = true;
                        hasLost = true;
                    }
                } while (!isGameFinished);
                end = System.currentTimeMillis();
                printEndGame();
                break;

            case "НЕТ":
                System.out.println("Жаль, приходи ещё!");
                break;

            default:
                System.out.println("Данные введены некорректно");
                break;
        }
    }

    private static void renderBoard() {
        for (String[] row : board) {
            System.out.println(wall);

            for (String cell : row) {
                System.out.print(leftBlock);
                System.out.print(cell);
            }

            System.out.println(rightBlock);
        }
        System.out.println(wall);

    }

    private static void inputMove() {
        System.out.println("Введите куда будет ходить персонаж (ход возможен только по вертикали и горизонтали на одну клетку)" +
                "\nКоординаты персонажа - (x: " + person.getX() + ", y: " + person.getY() + ")");

        int x = scanner.nextInt();
        int y = scanner.nextInt();

        System.out.println(x + ", " + y);

        if (person.isMoveCorrect(x, y)) {
            String next = board[y - 1][x - 1];
            switch (next) {
                case "  " -> {
                    board[person.getY() - 1][person.getX() - 1] = "  ";
                    person.moveTo(x, y);
                    step++;
                    System.out.println("Ход корректный; Новые координаты: " + person.getX() + ", " + person.getY());
                }
                case castle -> {
                    if (!hasFoundKey) {
                        System.out.println("Башня заперта на ключ!");
                    } else {
                        System.out.println("Вы прошли игру!");
                        isGameFinished = true;
                    }
                }
                case Monster.IMAGE, BigMonster.IMAGE -> {
                    for (Monster monster : monsters) {
                        if (monster.conflictPerson(x, y)) {
                            boolean isNormalMonster = monster.getClass().getSimpleName().equals("Monster");
                            if (monster.taskMonster(scanner, random, difficulty)) {
                                System.out.println("Верно! Ты победил монстра!");
                                board[person.getY() - 1][person.getX() - 1] = "  ";
                                person.moveTo(x, y);
                                if (isNormalMonster) score += 20;
                                else score += 50;
                                step++;
                            } else {
                                System.out.println("Ты проиграл эту битву..");
                                person.takeDamage(isNormalMonster ? 1 : 2);
                            }
                            break;
                        }
                    }
                }
                case AlienMonster.IMAGE -> {
                    for (Monster monster : monsters) {
                        if (monster.conflictPerson(x, y)) {
                            board[person.getY() - 1][person.getX() - 1] = "  ";
                            if (monster.taskMonster(scanner, random, difficulty)) {
                                System.out.println("Верно!");
                                score += 100;
                                System.out.println("Пришелец телепортировал вас на случайный квадрат.");
                                int[] cords = findRandomFreeCell(1, person.getY() + 1, 1, BOARD_SIZE);
                                person.moveTo(cords[0], cords[1]);
                            } else {
                                System.out.println("Неверно! Повышение сложности...");
                                System.out.println("Пришелец вернул вас в начало.");
                                person.moveTo(random.nextInt(1, BOARD_SIZE + 1), BOARD_SIZE);
                                difficulty++;
                            }
                            board[y - 1][x - 1] = "  ";
                            step++;
                            break;
                        }
                    }
                }
                case pointOfInterest -> {
                    if (pointsOfInterestLeft > 1 && (random.nextInt(3) != 2 || hasFoundKey)) {
                        if (random.nextBoolean()) {
                            System.out.println("Вы нашли лекарство! (+1 жизнь)");
                            person.heal(1);
                        } else {
                            System.out.println("Вас укусил ядовитый паук! (-1 жизнь)");
                            person.takeDamage(1);
                        }
                    } else {
                        System.out.println("Вы нашли ключ от башни!");
                        hasFoundKey = true;
                    }
                    pointsOfInterestLeft--;

                    board[person.getY() - 1][person.getX() - 1] = "  ";
                    board[y - 1][x - 1] = "  ";
                    person.moveTo(x, y);
                    step++;
                }
            }
        } else {
            System.out.println("Некорректный ход");
        }
    }

    private static boolean useLastChance() {
        int poison = random.nextInt(1, 4);
        System.out.println("Ты нашёл 3 одинаковых по виду зелья, какое из них выпьешь? (1, 2 или 3)");
        return scanner.nextInt() != poison;
    }

    private static int[] findRandomFreeCell(int rowStart, int rowEnd, int colStart, int colEnd) {
        int x, y;

        do {
            y = random.nextInt(rowStart, rowEnd + 1);
            x = random.nextInt(colStart, colEnd + 1);
        } while (!board[y - 1][x - 1].equals("  "));

        return new int[]{x, y};
    }

    private static void printGameStatus() {
        System.out.println("Ход:\t" + step);
        System.out.println("Количество жизней:\t" + person.getHp());
        System.out.println("Сложность:\t" + difficulty);
        System.out.println("Счёт:\t" + score + "\n");
    }

    private static void printEndGame() {
        String title = hasLost
                ? "ПОРАЖЕНИЕ"
                : "ПОБЕДА!";
        long durationMs = end - start;
        long minutes = (durationMs / 1000) / 60;
        long seconds = (durationMs / 1000) % 60;
        String timeStr = String.format("%d мин %d сек", minutes, seconds);
        String message = hasLost
                ? "Ты пал в этом приключении..."
                : "Ты смог попасть в башню!";

        String line = "╔══════════════════════════════╗";
        String bottomLine = "╚══════════════════════════════╝";

        System.out.println("\n" + line);
        System.out.printf("║ %-28s ║%n", title);
        System.out.println("╠══════════════════════════════╣");
        System.out.printf("║ %-28s ║%n", message);
        System.out.println("╠══════════════════════════════╣");
        System.out.printf("║ Сложность:  %-16d ║%n", difficulty);
        System.out.printf("║ Ходы:       %-16d ║%n", step);
        System.out.printf("║ Время:      %-16s ║%n", timeStr);
        System.out.printf("║ Счёт:       %-16d ║%n", score);
        System.out.println(bottomLine);
    }

}