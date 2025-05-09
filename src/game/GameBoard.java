package game;
import player.*;
import units.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GameBoard implements Serializable{
    public static final int WIDTH = 12;
    public static final int HEIGHT = 10;
    private String[][] board;
    private Scanner scanner;
    // Типы местности

    public GameBoard() {
        board = new String[HEIGHT][WIDTH];

        clearBoard();
        generateTerrain(); // Генерация типов местности
    }


    public void clearBoard() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board[i][j] = ".";

            }
        }
    }

    // Генерация типов местности
    public void generateTerrain() {
        // Добавляем болота
        board[2][5] = "s";
        board[6][6] = "s";

        // Добавляем леса
        board[3][5] = "f";
        board[3][6] = "f";
        board[4][5] = "f";
        board[4][6] = "f";
        board[5][5] = "f";
        board[5][6] = "f";
    }

    public void updateBoard(Player player1, Player player2) {
        clearBoard();
        generateTerrain();

        // Ратуши
        setCell(player1.getTownHall().getX(), player1.getTownHall().getY(), "T");
        setCell(player2.getTownHall().getX(), player2.getTownHall().getY(), "t");

        // Юниты игрока 1
        for (Unit unit : player1.getUnits()) {
            if (unit.isAlive()) {
                setCell(unit.getX(), unit.getY(), unit.getSymbol());
            }
        }

        // Юниты игрока 2
        for (Unit unit : player2.getUnits()) {
            if (unit.isAlive()) {
                setCell(unit.getX(), unit.getY(), unit.getSymbol().toLowerCase());
            }
        }
    }

    public void printBoard() {
        // Вывод верхних координат (ось X)
        System.out.print("  "); // Отступ для оси Y
        for (int j = 0; j < WIDTH; j++) {
            System.out.printf("%2d  ", j); // Номера столбцов (ось X)
        }
        System.out.print(" Y ");
        System.out.println();

        // Вывод поля с координатами строк (ось Y)
        for (int i = 0; i < HEIGHT; i++) {
            System.out.printf("%2d ", i); // Номера строк (ось Y)
            for (int j = 0; j < WIDTH; j++) {
                String cell = board[i][j];
                // Раскрашиваем ратуши
                if (cell.equals("T") || cell.equals("t")) {
                    System.out.print(ConsoleColors.red(cell) + "\t");
                } else {
                    System.out.print(cell + "\t");
                }

            }

            System.out.println();
        }
        System.out.print(" X ");
    }

    public String[][] getBoard() {
        return board;
    }

    public void loadFromFile(String filename, Player player, Player computer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            clearBoard();
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < HEIGHT) {
                for (int col = 0; col < line.length() && col < WIDTH; col++) {
                    char c = line.charAt(col);
                    setCell(row, col, String.valueOf(c));

                    if (c == 'T') {
                        player.getTownHall().setPosition(row, col);
                    } else if (c == 't') {
                        computer.getTownHall().setPosition(row, col);
                    }
                }
                row++;
            }
        }
    }


    public String getCell(int x, int y) {
        if (x >= 0 && x < HEIGHT && y >= 0 && y < WIDTH) {
            return board[x][y];
        }
        return ".";
    }

    public void setCell(int x, int y, String value) {
        if (x >= 0 && x < HEIGHT && y >= 0 && y < WIDTH) {
            board[x][y] = value;
        }

    }

    public void createPlayerUnitsFromBoard(Player player) {

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                String cell = board[i][j];
                switch (cell) {
                    case "I" ->
                            player.addUnit(new Infantry(player.getUnits().size() + 1, i, j, player.getUnits(), player));
                    case "A" ->
                            player.addUnit(new Archer(player.getUnits().size() + 1, i, j, player.getUnits(), player));
                    case "C" ->
                            player.addUnit(new Cavalry(player.getUnits().size() + 1, i, j, player.getUnits(), player));
                    case "M" -> player.addUnit(new Mage(player.getUnits().size() + 1, i, j, player.getUnits(), player));

                }

            }
        }

    }

    public void createCompUnitsFromBoard(Player computer) {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                String cell = board[i][j];
                switch (cell) {
                    case "i" ->
                            computer.addUnit(new Infantry(computer.getUnits().size() + 1, i, j, computer.getUnits(), computer));
                    case "a" ->
                            computer.addUnit(new Archer(computer.getUnits().size() + 1, i, j, computer.getUnits(), computer));
                    case "c" ->
                            computer.addUnit(new Cavalry(computer.getUnits().size() + 1, i, j, computer.getUnits(), computer));
                    case "m" ->
                            computer.addUnit(new Mage(computer.getUnits().size() + 1, i, j, computer.getUnits(), computer));
                }

            }
        }
    }



}