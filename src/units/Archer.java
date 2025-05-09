package units;
import jakarta.xml.bind.annotation.XmlRootElement;
import player.*;
import java.util.List;
import special.*;
@XmlRootElement(name = "archer")
public class Archer extends Unit {
    private static final long serialVersionUID = 1L;
    private List<Unit> playerUnits;
    private Player player;
    public Archer(int id,int x, int y, List<Unit> playerUnits, Player player) {
        super( id,10, 25, x, y, "A");
        this.playerUnits = playerUnits;
        this.player = player;
    }

    public Archer() {
        super(0, 0, 0, 0, 0, ""); // или другие значения по умолчанию
    }

    public void attack(Unit target) {
        target.takeDamage(attack);
    }


    @Override
    public void move(int newX, int newY, Player player, Player enemy) {
        // Лучник не может перемещаться
        System.out.println("Лучник не может перемещаться!");
    }

    public void attackForward(int targetX, int targetY, Player enemy) {
        for (Unit unit : enemy.getUnits()) {
            if (unit.getX() == targetX && unit.getY() == targetY && unit.isAlive()) {
                attack(unit);
                System.out.println("Лучник атаковал врага на (" + targetX + ", " + targetY + ")!");
                return;
            }
        }

        // Атака ратуши
        if (enemy.getTownHall().getX() == targetX && enemy.getTownHall().getY() == targetY) {
            enemy.getTownHall().takeDamage(attack);
            System.out.println("осталось здоровья у ратуши: "+ enemy.getTownHall().getHealth());
            System.out.println("Лучник атаковал ратушу на (" + targetX + ", " + targetY + ")!");
        } else {
            System.out.println("На клетке (" + targetX + ", " + targetY + ") нет цели.");
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
                    respawnAsZombie();
                }
                if(!result){
                    System.out.println("Лучник игрока " + id + " погиб без перерождения");
                }
            } else {
                System.out.println("Лучник компьютера " + id + " погиб.");
            }
        }
    }

    private void respawnAsZombie() {
        // Находим текущего лучника в списке юнитов
        for (int i = 0; i < playerUnits.size(); i++) {
            if (playerUnits.get(i).getId() == this.id) {
                // Заменяем лучника на ходячего мертвеца
                playerUnits.set(i, new Zombie(this.id, this.x, this.y));
                System.out.println("Лучник " + id + " переродился в ходячего мертвеца!");
                break;
            }
        }
    }
}