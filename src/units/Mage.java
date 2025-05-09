package units;
import jakarta.xml.bind.annotation.XmlRootElement;
import player.*;
import special.Ghost;
import java.util.List;
@XmlRootElement(name = "mage")
public class Mage extends Unit {
    private static final long serialVersionUID = 1L;
    private Player player; //
    private List<Unit> playerUnits;
    public Mage(int id, int x, int y,List<Unit> playerUnits, Player player) {
        super(id, 40, 25, x, y, "M");
        this.playerUnits = playerUnits;
        this.player = player;
    }

    public Mage() {
        super(0, 0, 0, 0, 0, ""); // или другие значения по умолчанию
    }
    @Override
    public void attack(Unit target) {
        target.takeDamage(attack);
    }



    // Атака всего ряда
    @Override
    public void attackForward(Player enemy) {
        System.out.println("Маг атакует весь ряд на x = " + this.x + "!");

        for (Unit unit : enemy.getUnits()) {
            if (unit.getX() == this.x && unit.isAlive()) { // Проверяем, что юнит на той же строке
                attack(unit);
                System.out.println("Маг атаковал врага на (" + unit.getX() + ", " + unit.getY() + ")!");
            }
        }
        int targetX=this.x;
        if (enemy.getTownHall().getX() == targetX) {
            enemy.getTownHall().takeDamage(attack);
            System.out.println("Маг атаковал ратушу на ряду Х (" + targetX + ")!");
        }

    }

    // Перемещение на одну клетку вперед или в сторону
    @Override
    public void move(int newX, int newY, Player player, Player enemy) {
        // Проверяем, что перемещение на одну клетку вперед или в сторону
        if(!isCellOccupiedByAnyUnit(newX,newY,player,enemy)) {
            if ((newX == this.x && newY == this.y + 1) || (newX == this.x + 1 && newY == this.y) || (newX == this.x - 1 && newY == this.y)) {
                this.x = newX;
                this.y = newY;
            } else {
                System.out.println("Маг может двигаться на одну клетку вперёд или на одну клетку в сторону!");
            }
        } else  System.out.println("Клетка (" + newX + ", " + newY + ") занята другим юнитом!");


    }
    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            if (player.getName().equals("Player")) {
                boolean result=tossCoin();
                if (result) {
                    respawnAsGhost();
                }
                if(!result){
                    System.out.println("Маг игрока " + id + " погибла без перерождения");
                }
            }else {
                System.out.println("Маг компьютера " + id + " погибла.");
            }
        }
    }
    private void respawnAsGhost() {

        for (int i = 0; i < playerUnits.size(); i++) {
            if (playerUnits.get(i).getId() == this.id) {

                playerUnits.set(i, new Ghost(this.id, this.x, this.y));
                System.out.println("Маг " + id + " переродилася в духа!");
                break;
            }
        }
    }

    public void takeDamageTestGhost(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;

            respawnAsGhost();

            }
        }
    }

