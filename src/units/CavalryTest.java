package units;

import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import player.Player;
import special.DarkKnight;
import java.util.ArrayList;
import java.util.List;


public class CavalryTest {

    private Player player=new Player("Test player1",3,3,100);
    private Player enemy=new Player("Test player2",4,5,100);;
   // private List<Unit> playerUnits=new ArrayList<>();

    private Cavalry cavalry=new Cavalry(1,1,1,player.getUnits(),player);

    @Test
    public void testInitialStats() {
        assertEquals(30, cavalry.getHealth());
        assertEquals(30, cavalry.getAttack());
        assertEquals("C", cavalry.getSymbol());
    }

    @Test
    public void testMoveOneCellForward() {
        cavalry.move(1, 2, player, enemy);
        assertEquals(1, cavalry.getX());
        assertEquals(2, cavalry.getY());
    }

    @Test
    public void testMoveTwoCellsForward() {
        cavalry.move(1, 3, player, enemy);
        assertEquals(1, cavalry.getX());
        assertEquals(3, cavalry.getY());
    }

    @Test
    public void testMoveOneCellSideways() {

        cavalry.move(2, 1, player, enemy);
        assertEquals(2, cavalry.getX());
        assertEquals(1, cavalry.getY());


        cavalry.move(3, 1, player, enemy);
        assertEquals(3, cavalry.getX());
        assertEquals(1, cavalry.getY());
    }



    @Test
    public void testMoveToSwamp() {
        int initialHealth = cavalry.getHealth();
        cavalry.move(2, 5, player, enemy); // Болото
        assertEquals(1, cavalry.getX());
        assertEquals(1, cavalry.getY()); //представим что он стоит перед болотом
        assertEquals(initialHealth - 10, cavalry.getHealth());
    }

    @Test
    public void testMoveThroughForest() {
        Cavalry cavalry1 = new Cavalry(4, 4, 3, new ArrayList<>(), enemy);
        int initialHealth = cavalry1.getHealth();
        cavalry1.move(4, 6, player, enemy); // Лес
        assertEquals(4, cavalry1.getX());
        assertEquals(5, cavalry1.getY());
        assertEquals(initialHealth - 20, cavalry1.getHealth());
    }



    @Test
    public void testRespawnAsBlackPrince() {

        List<Unit> units = new ArrayList<>();
        Player testPlayer = new Player("pl",3,4,100);


        Cavalry cavalry = new Cavalry(1, 3, 3, units, testPlayer);
        units.add(cavalry);


        assertEquals(1, units.size());
        assertSame(cavalry, units.get(0));

        cavalry.takeDamageTest(40);

        assertEquals(1, units.size());
        assertTrue(units.get(0) instanceof DarkKnight);

        DarkKnight darkKnight = (DarkKnight) units.get(0);
        assertEquals(cavalry.getX(), darkKnight.getX());
        assertEquals(cavalry.getY(), darkKnight.getY());
        assertEquals(cavalry.getId(), darkKnight.getId());


    }


    @Test
    public void testComputerMovement() {
        Cavalry computerCavalry = new Cavalry(2, 3, 3, new ArrayList<>(), enemy);
        computerCavalry.movecomp(3, 4, enemy, player);
        assertEquals(3, computerCavalry.getX());
        assertEquals(3, computerCavalry.getY());
    }





        @Test
        void shouldAttackEnemyInFront() {

            TestUnit enemyUnit = new TestUnit(2, 1, 2);
            enemy.addUnit(enemyUnit);

            cavalry.attackForward(enemy);

            assertEquals(70, enemyUnit.getHealth());
        }

        @Test
        void shouldAttackTownHallInFront() {

            enemy.getTownHall().setPosition(1, 2);

            cavalry.attackForward(enemy);

            assertEquals(970, enemy.getTownHall().getHealth());
        }


       private static class TestUnit extends Unit {
            TestUnit(int id, int x, int y) {
                super(id, 100, 0, x, y, "U");
            }
       }

    Cavalry cavalry2=new Cavalry(1,2,2,player.getUnits(),player);
    @Test
    public void testFullInputOutputCapture() {

        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        try {

            String simulatedInput = "1\n";
            ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedInput.getBytes());
            System.setIn(testInput);

            //  Перехватываем вывод
             ByteArrayOutputStream outputCapture = new ByteArrayOutputStream();
            PrintStream testOutput = new PrintStream(outputCapture);
            System.setOut(testOutput);

            cavalry2.interactiveMovement();

            String consoleOutput = outputCapture.toString();

            assertTrue(consoleOutput.contains("Введите направление"));
            assertTrue(consoleOutput.contains("Двигаемся вперёд!"));

            assertEquals(2, cavalry2.getX());
            assertEquals(3, cavalry2.getY());

        } finally {

            System.setOut(originalOut);
            System.setIn(originalIn);
        }
    }

}






