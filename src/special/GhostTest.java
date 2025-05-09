package special;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.Player;
import units.Unit;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GhostTest {
    private Ghost ghost;
    private Player player;
    private Player enemy;
    private List<Unit> playerUnits;

    @BeforeEach
    public void setUp() {
        playerUnits = new ArrayList<>();
        player = new Player("player1",10,10,100);
        enemy = new Player("player2",5,5,100);
        ghost = new Ghost(1, 1, 1);
    }

    @Test
    public void testInitialStats() {
        assertEquals(30, ghost.getHealth());
        assertEquals(15, ghost.getAttack());
        assertEquals("G", ghost.getSymbol());
    }

    @Test
    public void testValidMoves() {

        ghost.move(1, 2, player, enemy);
        assertEquals(1, ghost.getX());
        assertEquals(2, ghost.getY());

        ghost.move(1, 4, player, enemy);
        assertEquals(1, ghost.getX());
        assertEquals(4, ghost.getY());

        ghost.move(2,4,player,enemy);
        assertEquals(2,ghost.getX());
        assertEquals(4,ghost.getY());


        ghost.move(1, 4, player, enemy);
        assertEquals(1, ghost.getX());
        assertEquals(4, ghost.getY());





    }

    @Test
    public void testInvalidMoves() {
        int initialX = ghost.getX();
        int initialY = ghost.getY();


        ghost.move(4, 4, player, enemy);
        assertEquals(initialX, ghost.getX());
        assertEquals(initialY, ghost.getY());


        ghost.move(3, 2, player, enemy);
        assertEquals(initialX, ghost.getX());
        assertEquals(initialY, ghost.getY());
    }

    @Test
    public void testMoveToSwamp() {
        int initialHealth = ghost.getHealth();
        ghost.move(2, 5, player, enemy); // Болото
        assertEquals(initialHealth - 10, ghost.getHealth());
    }

    @Test
    public void testMoveThroughForest() {
        int initialHealth = ghost.getHealth();
        ghost.move(4, 6, player, enemy); // Лес
        assertEquals(initialHealth - 20, ghost.getHealth());

    }

    @Test
    public void testAttackRow() {
        // Создаем врагов на той же строке
        TestUnit enemy1 = new TestUnit(2, 1, 3, 100);
        TestUnit enemy2 = new TestUnit(3, 1, 5, 100);
        // И на другой строке
        TestUnit enemy3 = new TestUnit(4, 2, 3, 100);

        enemy.getUnits().add(enemy1);
        enemy.getUnits().add(enemy2);
        enemy.getUnits().add(enemy3);

        ghost.attackRow(enemy);

        assertEquals(85, enemy1.getHealth());
        assertEquals(85, enemy2.getHealth());
        assertEquals(100, enemy3.getHealth());

    }

    @Test
    public void testAttackTownHallSameRow() {
        enemy.getTownHall().setPosition(1, 6);
        int initialHealth = enemy.getTownHall().getHealth();
        ghost.attackRow(enemy);
        assertEquals(initialHealth - 15, enemy.getTownHall().getHealth());
    }

    @Test
    public void testRegenerate() {
        ghost.takeDamage(20);
        ghost.regenerate();
        assertTrue(ghost.getHealth() >= 25);
    }



   private static class TestUnit extends Unit {
        public TestUnit(int id, int x, int y, int health) {
            super(id, health, 0, x, y, "T");
        }
    }
}