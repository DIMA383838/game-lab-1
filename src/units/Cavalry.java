package units;
import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import player.*;
import special.DarkKnight;

@XmlRootElement(name = "cavalry")
 public class Cavalry extends Unit {
     private static final long serialVersionUID = 1L;
     private static final Logger log= LoggerFactory.getLogger(Cavalry.class);
    private Player player;//private Player enemy;
    private List<Unit> playerUnits;
    public Player enemy;

    public Cavalry(int id,int x, int y,List<Unit> playerUnits, Player player) {
        super(id,30, 30, x, y, "C");
        this.playerUnits = playerUnits;
        this.player = player;
        this.enemy=enemy;
        //log.info("Создана новая кавалерия (ID: {}, X: {}, Y: {})", id, x, y); // INFO 1
    }
     public Cavalry() {
         super(0, 0, 0, 0, 0, ""); // или другие значения по умолчанию
     }
     public void takeDamageTest(int damage) {
         health -= damage;
         if (health <= 0) {
             health = 0;
             respawnAsBlackPrince();
         }
     }


    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            if (player.getName().equals("Player")) {
                boolean result=tossCoin();
                if (result) {
                    log.error("Кавалерия {} погибла, но перерождается в Чёрного рыцаря", id); // ERROR 1
                    // Перерождаемся в Чёрного принца только для кавалерии игрока
                    respawnAsBlackPrince();
                }
                if(!result){
                    log.error("Кавалерия {} погибла без перерождения", id); // ERROR 2
                    System.out.println("Кавалерия игрока " + id + " погибла без перерождения");
                }
            }else {
                // Кавалерия компьютера просто умирает
                System.out.println("Кавалерия компьютера " + id + " погибла.");
            }
        }
    }

    private void respawnAsBlackPrince() {
        // Находим текущую кавалерию в списке юнитов
        for (int i = 0; i < playerUnits.size(); i++) {
            if (playerUnits.get(i).getId() == this.id) {
                // Заменяем кавалерию на Чёрного принца
                playerUnits.set(i, new DarkKnight(this.id, this.x, this.y));
                System.out.println("Кавалерия " + id + " переродилась в Чёрного принца!");
                break;
            }
        }
    }



    @Override
    public void move(int newX, int newY, Player player, Player enemy) {
        log.info("Кавалерия {} пытается переместиться в ({}, {})", id, newX, newY); // INFO 2
        // кавалерия может двигаться на 2 клетки вперёд или 1 в сторону
        if((newX == 2 && newY == 5) || (newX == 6 && newY == 6)){
            log.warn("Кавалерия {} пытается войти в болото! Потеря здоровья", id); // WARN 1
            takeDamage(10);
            System.out.println("В болото нельзя натсупать вы потеряли 10 здоровья");
        }else if((newX == 3 || newX == 4 || newX == 5) && newY == 6) {
            log.warn("Кавалерия {} движется через лес с штрафом", id); // WARN 2
            System.out.println("По лесу кавалерия может двигаться только на одну клетку, вы потеряли 20 здоровья ");
            takeDamage(20);
            this.x = newX;
            this.y = newY-1;
        }else
        {
            if (!isCellOccupiedByAnyUnit(newX, newY, player, enemy)) {

                if ((newX == this.x && newY == this.y + 1) || (newX == this.x && newY == this.y + 2) || (newX == this.x + 1 && newY == this.y) || (newX == this.x - 1 && newY == this.y)) {
                    this.x = newX;
                    this.y = newY;
                } else {
                    System.out.println("кавалерия может двигаться на две или одну клетки вперёд или 1 клетку в сторону!");
                }
            } else System.out.println("Клетка (" + newX + ", " + newY + ") занята другим юнитом!");
        }
    }

    public void movecomp(int newX, int newY, Player comp, Player enemy) {
        if(!isCellOccupiedByAnyUnit(newX,newY,comp,enemy)) {
            // кавалерия может двигаться на 2 клетки вперёд или 1 в сторону
            if (true) {
                this.x = newX;
                this.y = newY - 1;
            } else {
                System.out.println("кавалерия может двигаться на две или одну клетки вперёд или 1 клетку в сторону!");

            }
        }else  System.out.println("Клетка (" + newX + ", " + newY + ") занята другим юнитом!");

    }

    @Override
    public void attack(Unit target) {
        target.takeDamage(attack);
    }


    @Override
    public void attackForward(Player enemy) {
        int targetX = this.x;
        int targetY = this.y + 1; // Клетка перед кавалерией

        for (Unit unit : enemy.getUnits()) {
            if (unit.getX() == targetX && unit.getY() == targetY && unit.isAlive()) {
                attack(unit);
                System.out.println("Кавалерия " + id + " атаковала врага на (" + targetX + ", " + targetY + ")!");
                return;
            }
        }
        System.out.println("Перед кавалерией " + id + " нет врага.");

        if (enemy.getTownHall().getX() == targetX && enemy.getTownHall().getY() == targetY) {
            enemy.getTownHall().takeDamage(attack);
            System.out.println("Кавалерия атаковала ратушу на (" + targetX + ", " + targetY + ")!");
        }
    }


     public void move1(int newX, int newY, Player player) {
         log.info("Кавалерия {} пытается переместиться в ({}, {})", id, newX, newY); // INFO 2
         // кавалерия может двигаться на 2 клетки вперёд или 1 в сторону
         if((newX == 2 && newY == 5) || (newX == 6 && newY == 6)){
             log.warn("Кавалерия {} пытается войти в болото! Потеря здоровья", id); // WARN 1
             takeDamage(10);
             System.out.println("В болото нельзя натсупать вы потеряли 10 здоровья");
         }else if((newX == 3 || newX == 4 || newX == 5) && newY == 6) {
             log.warn("Кавалерия {} движется через лес с штрафом", id); // WARN 2
             System.out.println("По лесу кавалерия может двигаться только на одну клетку, вы потеряли 20 здоровья ");
             takeDamage(20);
             this.x = newX;
             this.y = newY-1;
         }else
         {


                 if ((newX == this.x && newY == this.y + 1) || (newX == this.x && newY == this.y + 2) || (newX == this.x + 1 && newY == this.y) || (newX == this.x - 1 && newY == this.y)) {
                     this.x = newX;
                     this.y = newY;
                 } else {
                     System.out.println("кавалерия может двигаться на две или одну клетки вперёд или 1 клетку в сторону!");
                 }

         }
     }

     public void interactiveMovement() {
         System.out.println("Введите направление (1-вперёд, 2-влево, 3-вправо):");

         try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
             String input = reader.readLine();

             switch(input) {
                 case "1":
                     System.out.println("Двигаемся вперёд!");
                     move1(x, y + 1, player);
                     break;
                 case "2":
                     System.out.println("Двигаемся влево!");
                     move1(x - 1, y, player);
                     break;
                 case "3":
                     System.out.println("Двигаемся вправо!");
                     move1(x + 1, y, player);
                     break;
                 default:
                     System.out.println("Неверный ввод!");
             }
         } catch (IOException e) {
             System.out.println("Ошибка ввода: " + e.getMessage());
         }
     }

 }