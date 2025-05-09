
package units;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.xml.bind.annotation.*;
import player.*;

import java.io.Serializable;
import java.util.Random;
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Infantry.class, name = "infantry"),
        @JsonSubTypes.Type(value = Archer.class, name = "archer"),
        @JsonSubTypes.Type(value = Cavalry.class, name = "cavalry"),
        @JsonSubTypes.Type(value = Mage.class, name = "mage"),
        // Добавьте все остальные подклассы Unit
})
@XmlSeeAlso({Infantry.class, Archer.class, Cavalry.class, Mage.class})
@XmlAccessorType(XmlAccessType.FIELD)

abstract public class Unit implements Serializable {
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    private static final long serialVersionUID = 1L;
         @XmlAttribute
        protected int health;
         @XmlAttribute
        protected int attack;

        @XmlAttribute
        protected int x;

        @XmlAttribute
        protected int y;

        @XmlAttribute
        protected String symbol;

        @XmlAttribute
        protected int id;

    public Unit() {
        this(0, 0, 0, 0, 0, ""); // значения по умолчанию
    }
        public Unit(
                 int id,
                 int health,
                 int attack,
                 int x,
                 int y,
                String symbol) {
            this.id = id;
            this.health = health;
            this.attack = attack;
            this.x = x;
            this.y = y;
            this.symbol = symbol;
        }

    public int getId() {
        return id;
    }

    public int getHealth() {
        return health;
    }

    public int getAttack() {
        return attack;
    }

    public void takeDamage(int damage) {
        health -= damage;

        if (health < 0 || health==0) {
            health = 0;
        }

    }
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
    public void setHealth(int health) {
        this.health = health;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public boolean isAlive() {
        return health > 0;
    }


    public static boolean isCellOccupiedByAnyUnit(int newX, int newY, Player player, Player enemy) {

        for (Unit unit : player.getUnits()) {
            if (unit.getX() == newX && unit.getY() == newY && unit.isAlive()) {
                return true;
            }
        }
        // Проверяем юнитов компьютера
        for (Unit unit : enemy.getUnits()) {
            if (unit.getX() == newX && unit.getY() == newY && unit.isAlive()) {
                return true;
            }
        }

        return false;
    }

    //определение статических методов и полей которые принадлежат классу, а не экземплярам этого класса.
    // Это означает, что вы можете вызывать статические методы без создания объекта класса.
    //Статический метод принадлежит классу а не объекту, что нужно для задач не требующим доступа к данным объекта

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getSymbol() {
        return symbol;
    }

    public void attack(Unit target) {
        target.takeDamage(this.attack);
        System.out.println(this.symbol + " атаковал " + target.getSymbol() + " на (" + target.getX() + ", " + target.getY() + ")!");
    }


    //public abstract void attack(Unit target);
    @Override
    public String toString() {
        return id +" - "+symbol+
                "( здоровье: " + health +
                ", атака:" + attack +
                ", x=" + getX() +
                ", y=" + getY() +
                ")";
    }

    public static boolean tossCoin(){
    Random random = new Random();
    return random.nextBoolean();
    }

    public void attackForward(Player enemy) {
        int targetX = this.x;
        int targetY = this.y + 1; // Клетка перед

        for (Unit unit : enemy.getUnits()) {
            if (unit.getX() == targetX && unit.getY() == targetY && unit.isAlive()) {
                attack(unit);
                return;
            }
        }

        // Атака ратуши
        if (enemy.getTownHall().getX() == targetX && enemy.getTownHall().getY() == targetY) {
            enemy.getTownHall().takeDamage(attack);
            System.out.println("юнит атаковал ратушу на (" + targetX + ", " + targetY + ")!");
        } else {
            System.out.println("На клетке (" + targetX + ", " + targetY + ") нет цели.");
        }

    }

}
