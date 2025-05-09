package player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import units.*;
import java.io.Serializable;
import java.util.Scanner;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TownHall implements Serializable {
    private static final long serialVersionUID = 1L;
    private int x, y;
    public int health;
    private int gold;


    // Конструктор по умолчанию для Jackson
    public TownHall() {
        this(0, 0, 0);
    }

    public TownHall(int x, int y, int initialGold) {
        this.x = x;
        this.y = y;
        this.health = 1000;
        this.gold = initialGold;
    }

    // Геттеры и сеттеры
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    // Игнорируем методы, которые не являются свойствами
    @JsonIgnore
    public boolean isDestroyed() {
        return health <= 0;
    }

    @JsonIgnore
    public boolean isAliveTown() {
        return health > 0;
    }


    public void setPosition(int x, int y) {
        this.x = x;  // Прямое присвоение, если поля доступны
        this.y = y;
    }


    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;

    }

    public void addGold(int amount) {
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        } else {
            System.out.println("Недостаточно золота!");
            return false;
        }
    }

    // Меню ратуши
    public void townHallMenu(Player player) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printTownHallMenu();
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    buyInfantry(player);
                    break;
                case 2:
                    buyArcher(player);
                    break;
                case 3:
                    buyMage(player);
                    break;
                case 4:
                    healUnit(player);
                    break;
                case 5:
                    return;

            }
        }
    }

    // Вывод меню ратуши
    private void printTownHallMenu() {
        System.out.println("Меню ратуши:");
        System.out.println("Золото: " + gold);
        System.out.println("1 - Купить пехотинца (50 золота)");
        System.out.println("2 - Купить лучника (70 золота)");
        System.out.println("3 - Купить мага (100 золота)");
        System.out.println("4 - Восстановить здоровье юнита (20 золота)");
        System.out.println("5 - Выйти из меню ратуши");
        System.out.println("Выберите действие: ");
    }
    //получение координат для размещения юнита
    private int getx(){
    System.out.println("Введите координату Х для добавления юнита");
    Scanner scanner = new Scanner(System.in);
    int x =scanner.nextInt();
    return x;

    }
    private int gety(){
        System.out.println("Введите координату Y для добавления юнита");
        Scanner scanner = new Scanner(System.in);
        int y = scanner.nextInt();
        return y;
    }


    // Покупка пехотинца
    public void buyInfantry(Player player) {
        if (spendGold(50)) {
            player.addUnit(new Infantry(player.getUnits().size() + 1, getx(), gety(),player.getUnits(),player ));//заменю гет х и игрик для нормальной работы тестов
            System.out.println("Пехотинец куплен и добавлен на поле!");
        }
    }

    // Покупка лучника
    public void buyArcher(Player player) {
        if (spendGold(70)) {
            player.addUnit(new Archer(player.getUnits().size() + 1, getx(), gety(),player.getUnits(),player ));
            System.out.println("Лучник куплен и добавлен на поле!");
        }
    }

    // Покупка мага
    public void buyMage(Player player) {
        if (spendGold(100)) {
            player.addUnit(new Mage(player.getUnits().size() + 1, getx(), gety(),player.getUnits(),player ));
            System.out.println("Маг куплен и добавлен на поле!");
        }
    }

    // Восстановление здоровья юнита
    public void healUnit(Player player) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите юнита для добавления здоровья:");
        for (Unit unit : player.getUnits()) {
            if (unit.isAlive()) {
                System.out.println(unit.getId() + " - " + unit.getSymbol() + " (здоровье: " + unit.getHealth() + ")");
            }
        }
        int unitChoice = scanner.nextInt();
        Unit unitToHeal = null;
        for (Unit unit : player.getUnits()) {
            if (unit.getId() == unitChoice && unit.isAlive()) {
                unitToHeal = unit;
                break;
            }
        }
        if (unitToHeal != null) {
            if (spendGold(20)) {
                unitToHeal.takeDamage(-20);
                System.out.println("Здоровье юнита " + unitToHeal.getId() + " увеличено");
            }
        } else {
            System.out.println("Неверный выбор юнита");
        }
    }
    public void heafInfTest(Player player){
        int unitChoice = 1;
        Unit unitToHeal = null;
        for (Unit unit : player.getUnits()) {
            if (unit.getId() == unitChoice && unit.isAlive()) {
                unitToHeal = unit;
                break;
            }
        }
        if (unitToHeal != null) {
            if (spendGold(20)) {
                unitToHeal.takeDamage(-20);
                System.out.println("Здоровье юнита " + unitToHeal.getId() + " увеличено");
            }
        } else {
            System.out.println("Неверный выбор юнита");
        }
    }
    public void heafArcTest(Player player){
        int unitChoice = 1;
        Unit unitToHeal = null;
        for (Unit unit : player.getUnits()) {
            if (unit.getId() == unitChoice && unit.isAlive()) {
                unitToHeal = unit;
                break;
            }
        }
        if (unitToHeal != null) {
            if (spendGold(20)) {
                unitToHeal.takeDamage(-5);
                System.out.println("Здоровье юнита " + unitToHeal.getId() + " увеличено");
            }
        } else {
            System.out.println("Неверный выбор юнита");
        }
    }



}