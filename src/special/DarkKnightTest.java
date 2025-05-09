
package special;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.Player;
import units.Unit;

import static org.junit.jupiter.api.Assertions.*;

public class DarkKnightTest {
    private DarkKnight darkKnight;
    private Player player;
    private Player enemy;

    @BeforeEach
    public void setUp() {
        darkKnight = new DarkKnight(1, 3, 3);
        player = new Player("hg",1,1,100);
        enemy = new Player("E",2,2,100);
    }//@BeforeEach означает, что этот метод будет выполняться перед каждым тестом в классе

    @Test
    public void testInitialStats() {
        assertEquals(80, darkKnight.getHealth());
        assertEquals(50, darkKnight.getAttack());
        assertEquals("D", darkKnight.getSymbol());
    }

    @Test
    public void testValidMoves() {

        darkKnight.move(3, 4, player, enemy);
        assertEquals(3, darkKnight.getX());
        assertEquals(4, darkKnight.getY());

        darkKnight.move(3, 6, player, enemy);
        assertEquals(3, darkKnight.getX());
        assertEquals(6, darkKnight.getY());

        darkKnight.move(4, 6, player, enemy);
        assertEquals(4, darkKnight.getX());
        assertEquals(6, darkKnight.getY());

        darkKnight.move(3, 6, player, enemy);
        assertEquals(3, darkKnight.getX());
        assertEquals(6, darkKnight.getY());
    }

    @Test
    public void testInvalidMoves() {

        int initialX = darkKnight.getX();
        int initialY = darkKnight.getY();

        // Пытаемся сделать недопустимые ходы
        darkKnight.move(3, 2, player, enemy); // Назад
        assertEquals(initialX, darkKnight.getX());
        assertEquals(initialY, darkKnight.getY());

        darkKnight.move(5, 5, player, enemy); // Диагональ
        assertEquals(initialX, darkKnight.getX());
        assertEquals(initialY, darkKnight.getY());
    }

    @Test
    public void testAttackForward() {

        Unit enemyUnit = new TestUnit(2, 3, 5, 100);
        enemy.getUnits().add(enemyUnit);

        darkKnight.attackForward(enemy);
        assertEquals(50, enemyUnit.getHealth());
    }

    @Test
    public void testAttackTownHall() {

        enemy.getTownHall().setPosition(3, 5);
        int initialHealth = enemy.getTownHall().getHealth();

        darkKnight.attackForward(enemy);
        assertEquals(initialHealth - 50, enemy.getTownHall().getHealth());
    }

    @Test
    public void testRegenerate() {

        darkKnight.takeDamage(30);
        assertEquals(50, darkKnight.getHealth());


        darkKnight.regenerate();
        assertTrue(darkKnight.getHealth() >= 75);
    }

    private class TestUnit extends Unit {
        public TestUnit(int id, int x, int y, int health) {
            super(id, health, 0, x, y, "T");
        }
    }
}