package units;
import jakarta.xml.bind.annotation.XmlRootElement;
import player.*;

import special.Skeleton;

import java.util.List;
@XmlRootElement(name = "infantry")
public class Infantry extends Unit {
    private static final long serialVersionUID = 1L;
    private List<Unit> playerUnits;
    private Player player;
    public Infantry(int id,int x, int y, List<Unit> playerUnits, Player player) {
        super(id, 35,50, x, y,"I");
        this.playerUnits = playerUnits;
        this.player = player;
    }
    public Infantry() {
        super(0, 0, 0, 0, 0, ""); // или другие значения по умолчанию
    }
    /*@Override
    public void attack(Unit target) {
        target.takeDamage(attack);
    }


     */
/*
    @Override
    public void move(int newX, int newY, Player player, Player enemy) {
        if((newX == 2 && newY == 5) || (newX == 6 && newY == 6)){
            takeDamage(10);
            System.out.println("В болото нельзя натсупать");
        }else
               if (!Unit.isCellOccupiedByAnyUnit(newX, newY, player, enemy)) {

             // Пехотинец может двигаться только на одну клетку вперед по оси Y
             if (newX == this.x && newY == this.y + 1) {
                this.y = newY;
             } else {
                   System.out.println("Пехотинец может двигаться только на одну клетку вперед!");
              }

              }else System.out.println("Клетка (" + newX + ", " + newY + ") занята другим юнитом!");



    }


 */
    public void movecomp(int newX, int newY, Player comp, Player enemy) {
        // Пехотинец может двигаться только на одну клетку вперед по оси Y

        if(!isCellOccupiedByAnyUnit(newX,newY,comp,enemy)) {
            if (true) {
                this.x = newX;
                this.y = newY;
            } else {
                System.out.println("Пехотинец может двигаться только на одну клетку вперед!");
            }
        }else  System.out.println("Клетка (" + newX + ", " + newY + ") занята другим юнитом!");



    }
    /*
    public void attackForward(Player enemy) {
        int targetX = this.x;
        int targetY = this.y + 1; // Клетка перед пехотинцем

        for (Unit unit : enemy.getUnits()) {
            if (unit.getX() == targetX && unit.getY() == targetY && unit.isAlive()) {
                attack(unit);
                System.out.println("Пехотинец атаковал врага на (" + targetX + ", " + targetY + ")!");
                return;
            }
        }

        // Атака ратуши
        if (enemy.getTownHall().getX() == targetX && enemy.getTownHall().getY() == targetY) {
            enemy.getTownHall().takeDamage(attack);
            System.out.println("Лучник атаковал ратушу на (" + targetX + ", " + targetY + ")!");
        } else {
            System.out.println("На клетке (" + targetX + ", " + targetY + ") нет цели.");
        }
        System.out.println("Перед пехотинцем нет врага.");
    }


     */
    public void attackForwardcomp(Player enemy) {
        int targetX = this.x;
        int targetY = this.y - 1; // Клетка перед пехотинцем

        for (Unit unit : enemy.getUnits()) {
            if (unit.getX() == targetX && unit.getY() == targetY && unit.isAlive()) {
                attack(unit);
                System.out.println("Пехотинец атаковал врага на (" + targetX + ", " + targetY + ")!");
                return;
            }
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
                    // Перерождаемся в Чёрного принца только для кавалерии игрока
                    respawnAsSkeleton();
                }
                if(!result){
                    System.out.println("Пехотинец игрока " + id + " погиб без перерождения");
                }

            } else {
                // Пехотинец компьютера просто умирает
                System.out.println("Пехотинец компьютера " + id + " погиб.");
            }
        }
    }

    private void respawnAsSkeleton() {
        // Находим текущего пехотинца в списке юнитов
        for (int i = 0; i < playerUnits.size(); i++) {
            if (playerUnits.get(i).getId() == this.id) {
                // Заменяем пехотинца на скелета
                playerUnits.set(i, new Skeleton(this.id, this.x, this.y));
                System.out.println("Пехотинец " + id + " переродился в скелета!");
                break;
            }
        }
    }

}
