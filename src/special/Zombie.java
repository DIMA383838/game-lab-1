package special;
import player.*;
import units.*;
public class Zombie extends Unit {
    public Zombie(int id, int x, int y) {
        super(id, 20, 15, x, y, "Z"); // Здоровье: 20, Атака: 15, Символ: Z
    }
    public Zombie() {
        super(0, 0, 0, 0, 0, ""); // или другие значения по умолчанию
    }
    @Override
    public void attack(Unit target) {
        target.takeDamage(attack);
    }
/*
    @Override
    public void move(int newX, int newY, Player player, Player enemy) {
        // Логика перемещения ходячего мертвеца
        if (!Unit.isCellOccupiedByAnyUnit(newX, newY, player, enemy)) {
            if ((newX == this.x && newY == this.y + 1) || (newX == this.x && newY == this.y - 1) ||
                    (newX == this.x + 1 && newY == this.y) || (newX == this.x - 1 && newY == this.y)) {
                this.x = newX;
                this.y = newY;
            } else {
                System.out.println("Ходячий мертвец может двигаться только на одну клетку вперёд, назад или в сторону!");
            }
        } else {
            System.out.println("Клетка (" + newX + ", " + newY + ") занята другим юнитом!");
        }
    }

 */
    /*
    public void attackForward(Player enemy) {
        int targetX = this.x;
        int targetY = this.y + 1;

        for (Unit unit : enemy.getUnits()) {
            if (unit.getX() == targetX && unit.getY() == targetY && unit.isAlive()) {
                attack(unit);
                System.out.println("Ходяий мертвец атаковал врага на (" + targetX + ", " + targetY + ")!");
                return;
            }
        }


    }

     */
    public void regenerate() {
        for (; health <= 15; health += 5) {
        }
        System.out.println("Дух " + id + " восстановил здоровье до " + health + "!");
    }
}