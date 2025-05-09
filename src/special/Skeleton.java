package special;
import player.*;
import units.*;
public class Skeleton extends Unit {
    public Skeleton(int id, int x, int y) {
        super(id, 10, 5, x, y, "S");

    }
    public Skeleton() {
        super(0, 0, 0, 0, 0, ""); // или другие значения по умолчанию
    }
    @Override
    public void attack(Unit target) {
        target.takeDamage(attack);
    }

    @Override
    public void move(int newX, int newY, Player player, Player enemy) {

        if (!Unit.isCellOccupiedByAnyUnit(newX, newY, player, enemy)) {
            if ((newX == this.x && newY == this.y + 1) || (newX == this.x && newY == this.y - 1) ||
                    (newX == this.x + 1 && newY == this.y) || (newX == this.x - 1 && newY == this.y)) {
                this.x = newX;
                this.y = newY;
            } else {
                System.out.println("Скелет может двигаться только на одну клетку вперёд, назад или в сторону!");
            }
        } else {
            System.out.println("Клетка (" + newX + ", " + newY + ") занята другим юнитом!");
        }

    }
/*
    public void attackForward(Player enemy) {
        int targetX = this.x;
        int targetY = this.y + 1; // Клетка перед пехотинцем

        for (Unit unit : enemy.getUnits()) {
            if (unit.getX() == targetX && unit.getY() == targetY && unit.isAlive()) {
                attack(unit);
                System.out.println("Скелет атаковал врага на (" + targetX + ", " + targetY + ")!");
                return;
            }
        }
    }


 */
    public void regenerate() {
        for (; health <= 5; health += 5) {
        }
        System.out.println("Скелет " + id + " восстановил здоровье до " + health + "!");
    }
}