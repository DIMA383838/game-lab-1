package player;
import units.*;
import org.junit.Test;

import java.util.Scanner;

import static org.junit.Assert.*;

public class TownHallTest {
    @Test
    public void testInitialization() {
        TownHall townHall = new TownHall(5, 5, 200);
        assertEquals(5, townHall.getX());
        assertEquals(5, townHall.getY());
        assertEquals(1000, townHall.getHealth());
        assertEquals(200, townHall.getGold());
        assertFalse(townHall.isDestroyed());
    }
    @Test
    public void testTakeDamage() {
        TownHall townHall = new TownHall(0, 0, 0);
        townHall.takeDamage(100);
        assertEquals(900, townHall.getHealth());
        townHall.takeDamage(1000);
        assertEquals(0, townHall.getHealth());
        assertTrue(townHall.isDestroyed());
    }
    @Test
    public void testBuyUnits() {
        Player player = new Player("Test Player", 4, 0, 100);
        TownHall townHall = new TownHall(0, 0, 300);

        // Тестируем покупку пехотинца
        townHall.buyInfantry(player);
        assertEquals(250, townHall.getGold());
        assertEquals(1, player.getUnits().size());
        assertTrue(player.getUnits().get(0) instanceof Infantry);

        // Тестируем покупку лучника
        townHall.buyArcher(player);
        assertEquals(180, townHall.getGold());
        assertEquals(2, player.getUnits().size());
        assertTrue(player.getUnits().get(1) instanceof Archer);

        // Тестируем покупку мага
        townHall.buyMage(player);
        assertEquals(80, townHall.getGold());
        assertEquals(3, player.getUnits().size());
        assertTrue(player.getUnits().get(2) instanceof Mage);


    }


    @Test
    public void testHealInf() {
        Player player = new Player("Test Player", 4, 0, 100);
        TownHall townHall = new TownHall(0, 0, 100);


        Infantry infantry = new Infantry(1, 1, 1, player.getUnits(), player);
        infantry.takeDamage(30);
        int initialHealth = infantry.getHealth();
        player.addUnit(infantry);


        townHall.heafInfTest(player);
        assertEquals(80, townHall.getGold());
        assertEquals(initialHealth + 20, infantry.getHealth());


    }
    @Test
    public void testHealArc() {
        Player player = new Player("Test Player", 4, 0, 100);
        TownHall townHall = new TownHall(0, 0, 100);


        Archer archer = new Archer(1, 1, 1, player.getUnits(), player);
        archer.takeDamage(5);
        int initialHealth = archer.getHealth();
        player.addUnit(archer);


        townHall.heafArcTest(player);
        assertEquals(80, townHall.getGold());
        assertEquals(initialHealth + 5, archer.getHealth());

    }

}
