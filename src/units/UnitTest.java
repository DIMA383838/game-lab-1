package units;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.Player;
import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {
    private TestUnit unit;
    private Player player;
    private Player enemy;

    @BeforeEach
    public void setUp() {
        player = new Player("player1",3,5,100);
        enemy = new Player("player2",3,10,100);
        unit = new TestUnit(1, 100, 10, 3, 3, "U");
    }

    @Test
    public void testInitialization() {
        assertEquals(1, unit.getId());
        assertEquals(100, unit.getHealth());
        assertEquals(10, unit.getAttack());
        assertEquals(3, unit.getX());
        assertEquals(3, unit.getY());
        assertEquals("U", unit.getSymbol());
    }

    @Test
    public void testTakeDamage() {
        unit.takeDamage(30);
        assertEquals(70, unit.getHealth());
        unit.takeDamage(100);
        assertEquals(0, unit.getHealth());
        assertFalse(unit.isAlive());
    }

    @Test
    public void testMoveValid() {
        unit.move(3, 4, player, enemy);
        assertEquals(3, unit.getX());
        assertEquals(4, unit.getY());
    }

    @Test
    public void testMoveInvalidDirection() {
        unit.move(4, 4, player, enemy);
        assertEquals(3, unit.getX());
        assertEquals(3, unit.getY());
    }

    @Test
    public void testMoveToOccupiedCell() {
        TestUnit blocker = new TestUnit(2, 100, 5, 3, 4, "B");
        player.getUnits().add(blocker);

        unit.move(3, 4, player, enemy);
        assertEquals(3, unit.getX());
        assertEquals(3, unit.getY());
    }

    @Test
    public void testMoveToSwamp() {
        int initialHealth = unit.getHealth();
        unit.move(2, 5, player, enemy);
        assertEquals(initialHealth - 10, unit.getHealth());
        assertEquals(3, unit.getX());
        assertEquals(3, unit.getY());
    }

    @Test
    public void testAttackUnit() {
        TestUnit target = new TestUnit(2, 50, 5, 3, 4, "T");
        enemy.getUnits().add(target);
        unit.attack(target);
        assertEquals(40, target.getHealth());
    }

    @Test
    public void testAttackForwardToUnit() {
        TestUnit target = new TestUnit(2, 50, 5, 3, 4, "T");
        enemy.getUnits().add(target);
        unit.attackForward(enemy);
        assertEquals(40, target.getHealth());
    }

    @Test
    public void testAttackForwardToTownHall() {
        enemy.getTownHall().setPosition(3, 4);
        int initialHealth = enemy.getTownHall().getHealth();
        unit.attackForward(enemy);
        assertEquals(initialHealth - 10, enemy.getTownHall().getHealth());
    }

    @Test
    public void testIsCellOccupied() {
        assertFalse(Unit.isCellOccupiedByAnyUnit(0, 0, player, enemy));
        TestUnit testUnit = new TestUnit(3, 100, 5, 5, 5, "T");
        player.getUnits().add(testUnit);
        assertTrue(Unit.isCellOccupiedByAnyUnit(5, 5, player, enemy));
    }

    @Test
    public void testDeadUnitDoesNotOccupyCell() {
        TestUnit deadUnit = new TestUnit(4, 0, 0, 6, 6, "D");
        player.getUnits().add(deadUnit);
        assertFalse(Unit.isCellOccupiedByAnyUnit(6, 6, player, enemy));
    }



    private static class TestUnit extends Unit {
        public TestUnit(int id, int health, int attack, int x, int y, String symbol) {
            super(id, health, attack, x, y, symbol);
        }
    }
}