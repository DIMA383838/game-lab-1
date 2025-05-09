package special;

import units.Unit;
import player.Player;

public class DarkKnight extends Unit {
    public DarkKnight(int id, int x, int y) {
        super(id, 80, 50, x, y, "D");
    }
    public DarkKnight() {
        super(0, 0, 0, 0, 0, ""); // или другие значения по умолчанию
    }
    @Override
    public void attack(Unit target) {
        target.takeDamage(attack);
    }

    @Override
    public void move(int newX, int newY, Player player, Player enemy) {

        if (!isCellOccupiedByAnyUnit(newX, newY, player, enemy)) {
            if ((newX == this.x && newY == this.y + 1) || (newX == this.x && newY == this.y + 2) ||
                    (newX == this.x + 1 && newY == this.y) || (newX == this.x - 1 && newY == this.y)||(newX==this.x&&newY==this.y+3)) {
                this.x = newX;
                this.y = newY;
            } else {
                System.out.println("Чёрный принц может двигаться на две или одну клетки вперёд или 1 клетку в сторону!");
            }
        } else {
            System.out.println("Клетка (" + newX + ", " + newY + ") занята другим юнитом!");
        }
    }
    @Override
    public void attackForward(Player enemy) {
        int targetX = this.x;
        int targetY = this.y + 2; // Клетка перед тёмным

        for (Unit unit : enemy.getUnits()) {
            if (unit.getX() == targetX && unit.getY() == targetY && unit.isAlive()) {
                attack(unit);
                System.out.println("Тёмный принц" + id + " атаковала врага на (" + targetX + ", " + targetY + ")!");
                return;
            }
        }
        System.out.println("Перед тёмным принцем " + id + " нет врага.");

        if (enemy.getTownHall().getX() == targetX && enemy.getTownHall().getY() == targetY) {
            enemy.getTownHall().takeDamage(attack);
            System.out.println("Тёмный принц атаковал ратушу на (" + targetX + ", " + targetY + ")!");
        }
    }

    public void regenerate() {
        for (; health <= 75; health += 5) {
        }
        System.out.println("Тёмный принц " + id + " восстановил здоровье до " + health + "!");
    }

}