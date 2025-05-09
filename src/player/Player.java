package player;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.Game;
import jakarta.xml.bind.annotation.*;
import units.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

@XmlRootElement(name = "player")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement
    private String name;

    @XmlElement
    private TownHall townHall;

    @XmlElementWrapper(name = "units")
    @XmlElement(name = "unit")
    private List<Unit> units;


    private Game game;


    public Player() {
        this("Default", 0, 0, 0); // или другие значения по умолчанию
    }
    public Player(String name, int townHallX, int townHallY, int initialGold) {
        this.name = name;
        this.townHall = new TownHall(townHallX, townHallY, initialGold);
        this.units = new ArrayList<>(); // Только пустой список, без добавления юнитов
    }

    public void addUnit(Unit unit) {
        units.add(unit);
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void restoreFromData(Player source) {
        // Восстанавливаем TownHall
        this.townHall.setX(source.getTownHall().getX());
        this.townHall.setY(source.getTownHall().getY());
        this.townHall.setHealth(source.getTownHall().getHealth());
        this.townHall.setGold(source.getTownHall().getGold());

        // Очищаем текущие юниты
        this.units.clear();

        // Добавляем все юниты из сохраненного состояния
        this.units.addAll(source.getUnits());
    }
    public void setGame(Game game) {
        this.game = game;
    }
public void setName(String name){
        this.name=name;
}
public void setUnits(List<Unit> units){
        this.units=units;
}
public void setTownHall(TownHall townHall){
        this.townHall=townHall;
}
    public Game getGame() {
        return game;
    }
    public TownHall getTownHall() {
        return townHall;
    }

    public String getName() {
        return name;
    }

    public boolean isTownHallDestroyed() {
        return townHall.isDestroyed();
    }

    public boolean isTownHallAlive() {
        return townHall.isAliveTown();
    }

    public void removeDeadUnits() {
        Iterator<Unit> iterator = units.iterator();
        while (iterator.hasNext()) {
            Unit unit = iterator.next();
            if (!unit.isAlive()) {
                iterator.remove();
                System.out.println("Юнит " + unit.getSymbol() + " погиб и удален с поля.");
            }
        }
    }

}

