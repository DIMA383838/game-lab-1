package game;

import java.util.Scanner;
import java.io.*;

public class MapEditor {
    GameBoard board;
    private Scanner scanner;
    private String userName;

    public MapEditor(String userName) {
        this.board = new GameBoard();
        this.scanner = new Scanner(System.in);
        this.userName = userName;
    }

    public void startEditor() {
        System.out.println("Редактор карт");
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addTerrain();
                    break;
                case 2:
                    addPlayerUnit();
                    break;
                case 3:
                    addComputerUnit();
                    break;
                case 4:
                    setPlayerTownHall();
                    break;
                case 5:
                    setComputerTownHall();
                    break;
                case 6:
                    saveMap();
                    break;
                case 7:
                    loadMap();
                    break;
                case 8:
                    board.printBoard();
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\nМеню редактора:");
        System.out.println("1 - Добавить/изменить рельеф");
        System.out.println("2 - Добавить юнита игрока");
        System.out.println("3 - Добавить юнита компьютера");
        System.out.println("4 - Установить ратушу игрока");
        System.out.println("5 - Установить ратушу компьютера");
        System.out.println("6 - Сохранить карту");
        System.out.println("7 - Загрузить карту");
        System.out.println("8 - Показать карту");
        System.out.println("9 - Выйти из редактора");
        System.out.print("Выберите действие: ");
    }

    private void addTerrain() {
        System.out.print("Введите координаты (x y): ");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.println("Выберите тип рельефа:");
        System.out.println("1 - Болото (s)");
        System.out.println("2 - Лес (f)");
        System.out.println("3 - Очистить клетку");
        int type = scanner.nextInt();

        switch (type) {
            case 1:
                board.setCell(x, y, "s");
                break;
            case 2:
                board.setCell(x, y, "f");
                break;
            case 3:
                board.setCell(x, y, ".");
                break;
            default:
                System.out.println("Неверный тип рельефа!");
        }
    }

    private void addPlayerUnit() {
        System.out.print("Введите координаты (x y): ");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.println("Выберите тип юнита:");
        System.out.println("1 - Пехотинец (I)");
        System.out.println("2 - Лучник (A)");
        System.out.println("3 - Кавалерия (C)");
        System.out.println("4 - Маг (M)");
        int type = scanner.nextInt();

        String symbol = ".";
        switch (type) {
            case 1:
                symbol = "I";
                break;
            case 2:
                symbol = "A";
                break;
            case 3:
                symbol = "C";
                break;
            case 4:
                symbol = "M";
                break;
            default:
                System.out.println("Неверный тип юнита!");
                return;
        }

        board.setCell(x, y, symbol);
    }


    private void addComputerUnit() {
        System.out.print("Введите координаты (x y): ");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.println("Выберите тип юнита:");
        System.out.println("1 - Пехотинец (i)");
        System.out.println("2 - Лучник (a)");
        System.out.println("3 - Кавалерия (c)");
        System.out.println("4 - Маг (m)");
        int type = scanner.nextInt();

        String symbol = ".";
        switch (type) {
            case 1:
                symbol = "i";
                break;
            case 2:
                symbol = "a";
                break;
            case 3:
                symbol = "c";
                break;
            case 4:
                symbol = "m";
                break;
            default:
                System.out.println("Неверный тип юнита!");
                return;
        }

        board.setCell(x, y, symbol);
    }


private void setPlayerTownHall() {
    System.out.print("Введите координаты ратуши игрока (x y): ");
    int x = scanner.nextInt();
    int y = scanner.nextInt();
    // Очистка предыдущей позиции
    for (int i = 0; i < GameBoard.HEIGHT; i++) {
        for (int j = 0; j < GameBoard.WIDTH; j++) {
            if (board.getCell(i, j).equals("T")) {
                board.setCell(i, j, ".");
            }
        }
    }
    board.setCell(x, y, "T");
}

    private void setComputerTownHall() {
        System.out.print("Введите координаты ратуши компьютера (x y): ");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        // Очистка предыдущей позиции
        for (int i = 0; i < GameBoard.HEIGHT; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                if (board.getCell(i, j).equals("t")) {
                    board.setCell(i, j, ".");
                }
            }
        }
        board.setCell(x, y, "t");
    }
    public void saveMap() {

        System.out.print("Введите название карты: ");
        String mapName = scanner.nextLine();
        try (PrintWriter writer = new PrintWriter(new FileWriter("maps/" + userName + "/" + mapName + ".map"))) {
            for (int i = 0; i < GameBoard.HEIGHT; i++) {
                for (int j = 0; j < GameBoard.WIDTH; j++) {
                    writer.print(board.getCell(i, j));
                }
                writer.println();
            }
            System.out.println("Карта сохранена!");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении карты: " + e.getMessage());
        }

    }

    private void loadMap() {
        System.out.println("Доступные карты:");

        File mapsDir = new File("maps/" + userName);
        if (mapsDir.exists() && mapsDir.isDirectory()) {
            File[] files = mapsDir.listFiles((dir, name) -> name.endsWith(".map"));
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    System.out.println((i+1) + " - " + files[i].getName().replace(".map", ""));
                }

                System.out.print("Выберите карту: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice > 0 && choice <= files.length) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(files[choice-1]))) {
                        board.clearBoard();
                        String line;
                        int row = 0;
                        while ((line = reader.readLine()) != null && row < GameBoard.HEIGHT) {
                            for (int col = 0; col < line.length() && col < GameBoard.WIDTH; col++) {

                                board.setCell(row, col, String.valueOf(line.charAt(col)));
                            }
                            row++;
                        }
                        System.out.println("Карта загружена!");
                    } catch (IOException e) {
                        System.out.println("Ошибка при загрузке карты: " + e.getMessage());
                    }
                } else {
                    System.out.println("Неверный выбор!");
                }
            } else {
                System.out.println("Нет доступных карт!");
            }
        } else {
            System.out.println("Папка с картами не найдена!");
        }
    }
    public void addTerrainforTest(int x, int y, int type) {

/*
        System.out.println("Выберите тип рельефа:");
        System.out.println("1 - Болото (s)");
        System.out.println("2 - Лес (f)");
        System.out.println("3 - Очистить клетку");
        int type = scanner.nextInt();


 */
        switch (type) {
            case 1:
                board.setCell(x, y, "s");
                break;
            case 2:
                board.setCell(x, y, "f");
                break;
            case 3:
                board.setCell(x, y, ".");
                break;
            default:
                System.out.println("Неверный тип рельефа!");
        }
    }
    public void addPlayerUnitforTest(int x, int y,int type) {

        String symbol = ".";
        switch (type) {
            case 1:
                symbol = "I";
                break;
            case 2:
                symbol = "A";
                break;
            case 3:
                symbol = "C";
                break;
            case 4:
                symbol = "M";
                break;
            default:
                System.out.println("Неверный тип юнита!");
                return;
        }

        board.setCell(x, y, symbol);
    }
    public void addComputerUnitforTest(int x, int y, int type) {

        String symbol = ".";
        switch (type) {
            case 1:
                symbol = "i";
                break;
            case 2:
                symbol = "a";
                break;
            case 3:
                symbol = "c";
                break;
            case 4:
                symbol = "m";
                break;
            default:
                System.out.println("Неверный тип юнита!");
                return;
        }

        board.setCell(x, y, symbol);
    }
    public void setPlayerTownHallforTest(int x, int y) {

        // Очистка предыдущей позиции
        for (int i = 0; i < GameBoard.HEIGHT; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                if (board.getCell(i, j).equals("T")) {
                    board.setCell(i, j, ".");
                }
            }
        }
        board.setCell(x, y, "T");
    }
    public void setComputerTownHallforTest(int x, int y) {

        // Очистка предыдущей позиции
        for (int i = 0; i < GameBoard.HEIGHT; i++) {
            for (int j = 0; j < GameBoard.WIDTH; j++) {
                if (board.getCell(i, j).equals("t")) {
                    board.setCell(i, j, ".");
                }
            }
        }
        board.setCell(x, y, "t");
    }
    public void loadMapforTest(String mapsDirq,int choice) {
        System.out.println("Доступные карты:");

        File mapsDir = new File("maps/" + userName);
        if (mapsDir.exists() && mapsDir.isDirectory()) {
            File[] files = mapsDir.listFiles((dir, name) -> name.endsWith(".map"));
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    System.out.println((i+1) + " - " + files[i].getName().replace(".map", ""));
                }

                System.out.print("Выберите карту: ");


                if (choice > 0 && choice <= files.length) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(files[choice-1]))) {
                        board.clearBoard();
                        String line;
                        int row = 0;
                        while ((line = reader.readLine()) != null && row < GameBoard.HEIGHT) {
                            for (int col = 0; col < line.length() && col < GameBoard.WIDTH; col++) {

                                board.setCell(row, col, String.valueOf(line.charAt(col)));
                            }
                            row++;
                        }
                        System.out.println("Карта загружена!");
                    } catch (IOException e) {
                        System.out.println("Ошибка при загрузке карты: " + e.getMessage());
                    }
                } else {
                    System.out.println("Неверный выбор!");
                }
            } else {
                System.out.println("Нет доступных карт!");
            }
        } else {
            System.out.println("Папка с картами не найдена!");
        }
    }
    public void saveMapforTest(String mapName) {

        try (PrintWriter writer = new PrintWriter(new FileWriter("maps/" + userName + "/" + mapName + ".map"))) {
            for (int i = 0; i < GameBoard.HEIGHT; i++) {
                for (int j = 0; j < GameBoard.WIDTH; j++) {
                    writer.print(board.getCell(i, j));
                }
                writer.println();
            }
            System.out.println("Карта сохранена!");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении карты: " + e.getMessage());
        }

    }

}
