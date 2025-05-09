package units;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.Player;

import special.Ghost;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MageTest {
    private Mage mage;
    private Player player;
    private Player enemy;
    private List<Unit> playerUnits;

    @BeforeEach
    public void setUp() {
        playerUnits = new ArrayList<>();
        player = new Player("player1",2,2,200);
        enemy = new Player("player2",4,4,200);
        mage = new Mage(1, 3, 3, playerUnits, player);
        playerUnits.add(mage);
    }

    @Test
    public void testInitialStats() {
        assertEquals(40, mage.getHealth());
        assertEquals(25, mage.getAttack());
        assertEquals("M", mage.getSymbol());
    }

    @Test
    public void testValidMoves() {
        // Вперед
        mage.move(3, 4, player, enemy);
        assertEquals(3, mage.getX());
        assertEquals(4, mage.getY());

        // Вправо
        mage.move(4, 4, player, enemy);
        assertEquals(4, mage.getX());
        assertEquals(4, mage.getY());

        // Влево
        mage.move(3, 4, player, enemy);
        assertEquals(3, mage.getX());
        assertEquals(4, mage.getY());
    }

    @Test
    public void testInvalidMoves() {
        int initialX = mage.getX();
        int initialY = mage.getY();

        // Диагональ
        mage.move(4, 4, player, enemy);
        assertEquals(initialX, mage.getX());
        assertEquals(initialY, mage.getY());

        // Далеко вперед
        mage.move(3, 5, player, enemy);
        assertEquals(initialX, mage.getX());
        assertEquals(initialY, mage.getY());
    }

    @Test
    public void testAttackEntireRow() {

        TestUnit enemy1 = new TestUnit(2, 3, 4, 100);
        TestUnit enemy2 = new TestUnit(3, 3, 5, 100);

        TestUnit enemy3 = new TestUnit(4, 2, 3, 100);

        enemy.getUnits().add(enemy1);
        enemy.getUnits().add(enemy2);
        enemy.getUnits().add(enemy3);

        mage.attackForward(enemy);

        assertEquals(75, enemy1.getHealth());
        assertEquals(75, enemy2.getHealth());
        assertEquals(100, enemy3.getHealth());
    }

    @Test
    public void testAttackTownHallSameRow() {
        enemy.getTownHall().setPosition(3, 5);
        int initialHealth = enemy.getTownHall().getHealth();

        mage.attackForward(enemy);
        assertEquals(initialHealth - 25, enemy.getTownHall().getHealth());
    }

    @Test
    public void testRespawnAsGhost() {

        List<Unit> units = new ArrayList<>();
        Player testPlayer = new Player("player3",3,4,100);


        Mage mage1 = new Mage(1, 3, 3, units, testPlayer);
        units.add(mage1);


        assertEquals(1, units.size());
        assertSame(mage1, units.get(0));

        mage1.takeDamageTestGhost(40);

        assertEquals(1, units.size());
        assertTrue(units.get(0) instanceof Ghost);

        Ghost ghost = (Ghost) units.get(0);
        assertEquals(mage1.getX(), ghost.getX());
        assertEquals(mage1.getY(), ghost.getY());
        assertEquals(mage1.getId(), ghost.getId());
    }


    private static class TestUnit extends Unit {
        public TestUnit(int id, int x, int y, int health) {
            super(id, health, 0, x, y, "T");
        }
    }
}
