package game;

import org.junit.jupiter.api.*;
import player.Player;

import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private static final String TEST_USER = "testUser";
    private static final String TEST_MAP = "testMap";
    private static final String TEST_SAVE = "testSave";

    private static Path mapsDir;
    private static Path savesDir;

    private Game game;
    private MapEditor editor;

    @BeforeAll
    static void setupAll() throws IOException {
        // Создаем тестовые директории
        mapsDir = Paths.get("maps", TEST_USER);
        savesDir = Paths.get("saves", TEST_USER);

        Files.createDirectories(mapsDir);
        Files.createDirectories(savesDir);

        clearDirectory(mapsDir);
        clearDirectory(savesDir);
    }
/*
    @AfterAll
    static void cleanupAll() throws IOException {
        // Удаляем тестовые файлы после всех тестов
        clearDirectory(mapsDir);
        clearDirectory(savesDir);
    }


 */
    @BeforeEach
    void setup() {
        GameBoard board = new GameBoard();
        Player player = new Player("Player", 4, 0, 100);
        Player computer = new Player("Computer", 4, 11, 100);
        game = new Game(player, computer, board, TEST_USER);
        editor = new MapEditor(TEST_USER);
    }

    private static void clearDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    @Test
    void AestAutosaveWithoutMap() {

        game.currentMapFile = null;

        game.computer.getTownHall().takeDamage(100);
        game.checkForAutosave();

        Path saveFile = savesDir.resolve(TEST_MAP + ".save");
        assertFalse(Files.exists(saveFile));
    }

    @Test
    void testAddTerrain() {
        editor.addTerrainforTest(2, 5, 1); // Добавляем болото
        assertEquals("s", editor.board.getCell(2, 5));

        editor.addTerrainforTest(3, 5, 2); // Добавляем лес
        assertEquals("f", editor.board.getCell(3, 5));

        editor.addTerrainforTest(2, 5, 3); // Очищаем клетку
        assertEquals(".", editor.board.getCell(2, 5));
    }

    @Test
    void testAddPlayerUnit() {
        editor.addPlayerUnitforTest(1, 1, 1); // Пехотинец
        assertEquals("I", editor.board.getCell(1, 1));

        editor.addPlayerUnitforTest(2, 2, 2); // Лучник
        assertEquals("A", editor.board.getCell(2, 2));

        editor.addPlayerUnitforTest(3, 3, 3); // Кавалерия
        assertEquals("C", editor.board.getCell(3, 3));

        editor.addPlayerUnitforTest(4, 4, 4); // Маг
        assertEquals("M", editor.board.getCell(4, 4));
    }

    @Test
    void testAddComputerUnit() {
        editor.addComputerUnitforTest(1, 1, 1); // Пехотинец
        assertEquals("i", editor.board.getCell(1, 1));

        editor.addComputerUnitforTest(2, 2, 2); // Лучник
        assertEquals("a", editor.board.getCell(2, 2));

        editor.addComputerUnitforTest(3, 3, 3); // Кавалерия
        assertEquals("c", editor.board.getCell(3, 3));

        editor.addComputerUnitforTest(4, 4, 4); // Маг
        assertEquals("m", editor.board.getCell(4, 4));
    }

    @Test
    void testSetTownHalls() {
        editor.setPlayerTownHallforTest(0, 0);
        assertEquals("T", editor.board.getCell(0, 0));

        editor.setComputerTownHallforTest(9, 11);
        assertEquals("t", editor.board.getCell(9, 11));
    }


    @Test
    void testSaveMap() throws IOException {

        editor.setPlayerTownHallforTest(0, 0);
        editor.setComputerTownHallforTest(9, 11);
        editor.addPlayerUnitforTest(1, 1, 1);
        editor.addComputerUnitforTest(8, 10, 1);
        editor.addTerrainforTest(5, 5, 1);


        editor.saveMapforTest(TEST_MAP);


        Path mapFile = mapsDir.resolve(TEST_MAP + ".map");
        assertTrue(Files.exists(mapFile));


        List<String> lines = Files.readAllLines(mapFile);
        assertEquals("T...........", lines.get(0));
        assertEquals(".I..........", lines.get(1));
        assertEquals("...........t", lines.get(9));
    }


    @Test
    void testModifyExistingMap() throws IOException {

        editor.setPlayerTownHallforTest(0, 0);

        editor.saveMapforTest(TEST_MAP);

        editor.loadMapforTest(TEST_MAP,1);
        editor.addPlayerUnitforTest(1, 1, 1);
        editor.saveMapforTest(TEST_MAP);


        Path mapFile = mapsDir.resolve(TEST_MAP + ".map");
        List<String> lines = Files.readAllLines(mapFile);
        assertEquals(".I..........", lines.get(1));
    }


    @Test
    void testLoadMap()  {

        editor.setPlayerTownHallforTest(0, 0);
        editor.setComputerTownHallforTest(9, 11);
        editor.saveMapforTest(TEST_MAP);

        editor.loadMapforTest(TEST_MAP,1);

        assertEquals("T", editor.board.getCell(0, 0));
        assertEquals("t", editor.board.getCell(9, 11));
    }


    @Test
    void testSaveAndLoadGameState() throws IOException, ClassNotFoundException {

        game.currentMapFile = TEST_MAP;

        game.saveGameState(TEST_SAVE);

        Path saveFile = savesDir.resolve(TEST_SAVE + ".save");
        assertTrue(Files.exists(saveFile));

        Game loadedGame = Game.loadGameState(TEST_SAVE, TEST_USER);

        assertEquals(TEST_MAP, loadedGame.currentMapFile);
        assertNotNull(loadedGame.player);
        assertNotNull(loadedGame.computer);
        assertNotNull(loadedGame.board);
    }


    @Test
    void testAutosaveOnTownHallDamage()  {

        game.currentMapFile = TEST_MAP;

        int initialHealth = game.computer.getTownHall().getHealth();
        game.computerTownHallInitialHealth = initialHealth;

        game.computer.getTownHall().takeDamage(500);
        game.checkForAutosave();

        Path mapFile = mapsDir.resolve(TEST_MAP + ".map");
        Path saveFile = savesDir.resolve(TEST_MAP + ".save");

        assertTrue(Files.exists(mapFile));
        assertTrue(Files.exists(saveFile));
    }



}

