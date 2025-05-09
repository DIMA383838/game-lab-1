package special;
import units.*;
import player.*;
public class Ghost extends Unit {

     public Ghost(int id, int x, int y) {
         super(id, 30, 15, x, y, "G");

     }
    public Ghost() {
        super(0, 0, 0, 0, 0, ""); // или другие значения по умолчанию
    }
     @Override
     public void attack(Unit target) {
         target.takeDamage(attack);
     }

     @Override
     public void move(int newX, int newY, Player player, Player enemy) {

         if ((newX == 2 && newY == 5) || (newX == 6 && newY == 6)) {
             takeDamage(10);
             System.out.println("В болото нельзя натсупать вы потеряли 10 здоровья");
         } else if ((newX == 3 || newX == 4 || newX == 5) && newY == 6) {
             System.out.println("Вы в лесу и вы потеряли 20 здоровья ");
             takeDamage(20);

         } else {
             if (!Unit.isCellOccupiedByAnyUnit(newX, newY, player, enemy)) {

                 if ((newX == this.x && newY == this.y + 1) || (newX == this.x && newY == this.y + 2) || (newX == this.x + 1 && newY == this.y) || (newX == this.x - 1 && newY == this.y)) {
                     this.x = newX;
                     this.y = newY;
                 } else {
                     System.out.println("Дух может двигаться на две или одну клетки вперёд или 1 клетку в сторону!");
                 }
             } else System.out.println("Клетка (" + newX + ", " + newY + ") занята другим юнитом!");
         }
     }

     public void attackRow(Player enemy) {
         System.out.println("Дух атакует весь ряд на x = " + this.x + "!");

         for (Unit unit : enemy.getUnits()) {
             if (unit.getX() == this.x && unit.isAlive()) { // Проверяем, что юнит на той же строке
                 attack(unit);
                 System.out.println("Дух атаковал врага на (" + unit.getX() + ", " + unit.getY() + ")!");
             }
         }
         int targetX = this.x;
         if (enemy.getTownHall().getX() == targetX) {
             enemy.getTownHall().takeDamage(attack);
             System.out.println("Дух атаковал ратушу на ряду Х (" + targetX + ")!");
         }

     }

     public void regenerate() {
         for (; health <= 25; health += 5) {
         }
         System.out.println("Дух " + id + " восстановил здоровье до " + health + "!");
     }
 }