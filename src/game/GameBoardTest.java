package game;

import org.junit.jupiter.api.Test;
import player.Player;


import static org.junit.Assert.assertEquals;


public class GameBoardTest {

    private Player player1 = new Player("Player1", 4, 0, 100);
    private Player player2 = new Player("Player2", 4, 11, 100);
    private GameBoard gameBoard = new GameBoard();

    @Test
    public void testClearBoard() {
        gameBoard.clearBoard();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 12; j++) {
                assertEquals(".", gameBoard.getCell(i, j));
            }
        }
    }

    @Test
    public void testGenerateTerrain() {
        gameBoard.clearBoard();
        gameBoard.generateTerrain();


        assertEquals("s", gameBoard.getCell(2, 5));
        assertEquals("s", gameBoard.getCell(6, 6));


        assertEquals("f", gameBoard.getCell(3, 5));
        assertEquals("f", gameBoard.getCell(3, 6));
        assertEquals("f", gameBoard.getCell(4, 5));
        assertEquals("f", gameBoard.getCell(4, 6));
        assertEquals("f", gameBoard.getCell(5, 5));
        assertEquals("f", gameBoard.getCell(5, 6));
    }

    @Test
    public void testUpdateBoard() {
        gameBoard.updateBoard(player1,player2);

        assertEquals("T1", gameBoard.getCell(4, 0));
        assertEquals("T2", gameBoard.getCell(4, 11));


        assertEquals("s", gameBoard.getCell(2, 5));


    }



}



