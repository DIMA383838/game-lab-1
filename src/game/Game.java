package game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import player.*;
import units.*;
import special.*;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.StringWriter;
import jakarta.xml.bind.*;

//найти интерфейс в джаве который переобразует объекты в байтовый поток

public class Game {

    static Player player;
    static Player computer;
    static GameBoard board;
    private boolean gameOver;
    String currentMapFile;
    private static String userName;
    private static String mapName;
    int computerTownHallInitialHealth;
    private static int n;
    private static final String MAPS_DIR = "maps";
    private static final String SAVES_DIR = "saves";
    private static final String USER_MAPS_DIR = MAPS_DIR + "/" + userName;
    private static final String USER_SAVES_DIR = SAVES_DIR + "/" + userName;

    public Game(Player player, Player computer, GameBoard board, String userName) {
        this.player = player;
        this.computer = computer;
        this.board = board;
        this.gameOver = false;
        this.userName = userName;
        this.computerTownHallInitialHealth = computer.getTownHall().getHealth();

        // Создаем директории (убрано создание юнитов по умолчанию)
        new File(MAPS_DIR + "/" + userName).mkdirs();
        new File(SAVES_DIR + "/" + userName).mkdirs();
    }


    public boolean checkGameOver() {
        if (computer.isTownHallDestroyed()) {
            System.out.println("Вы победили! Ратуша компьютера разрушена!");
            return true;
        }
        if (player.isTownHallDestroyed()) {
            System.out.println("Вы проиграли! Ваша ратуша разрущена!");
            return false;
        }

        return false;
    }

    public boolean checkGameOver1() {

        if (player.isTownHallDestroyed()) {
            System.out.println("Вы проиграли! Ваша ратуша разрущена!");
            return true;
        }

        return false;
    }

    // Улучшенный метод выбора карты
    private static String selectMap(Scanner scanner, String userName) {
        System.out.println("\n=== Доступные карты ===");
        File mapsDir = new File(MAPS_DIR + "/" + userName);

        if (!mapsDir.exists()) {
            System.out.println("Папка с картами не найдена. Будет создана новая.");
            mapsDir.mkdirs();
            return null;
        }

        File[] mapFiles = mapsDir.listFiles((dir, name) -> name.endsWith(".map"));

        if (mapFiles == null || mapFiles.length == 0) {
            System.out.println("Нет доступных карт. Будет использована карта по умолчанию.");
            return null;
        }

        // Вывод списка карт с нумерацией
        System.out.println("Список ваших карт:");
        for (int i = 0; i < mapFiles.length; i++) {
            String mapName = mapFiles[i].getName().replace(".map", "");
            System.out.printf("%2d. %s\n", i + 1, mapName);
        }
        System.out.println(" 0. Создать новую карту");

        System.out.print("\nВыберите карту (введите номер): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 0) {
            return null; // Будет создана карта по умолчанию
        } else if (choice > 0 && choice <= mapFiles.length) {
            String selectedMap = mapFiles[choice - 1].getName().replace(".map", "");
            System.out.println("Выбрана карта: " + selectedMap);
            return selectedMap;
        } else {
            System.out.println("Неверный выбор. Будет использована карта по умолчанию.");
            return null;
        }
    }


    public void checkForAutosave() {
        int currentHealth = computer.getTownHall().getHealth();
        if (currentHealth < computerTownHallInitialHealth) {
            if (currentMapFile == null) {
                System.out.println("Автосохранение невозможно: карта не выбрана");
                return;
            }

            System.out.println("\n=== Автосохранение ===");

            // 1. Сохраняем состояние карты
            saveCurrentMapState();

            // 2. Сохраняем состояние игры
            String saveName = currentMapFile;
            saveGameState(saveName);

            System.out.println("Прогресс сохранен в: " + saveName);
        }
    }

    private void saveCurrentMapState() {
        if (currentMapFile == null) {
            System.out.println("Невозможно сохранить: карта не выбрана");
            return;
        }

        File mapFile = new File(MAPS_DIR + "/" + userName + "/" + currentMapFile + ".map");
        try (PrintWriter writer = new PrintWriter(new FileWriter(mapFile))) {
            // Сохраняем позиции всех объектов
            for (int i = 0; i < GameBoard.HEIGHT; i++) {
                for (int j = 0; j < GameBoard.WIDTH; j++) {
                    // Проверяем юниты игрока
                    boolean unitFound = false;
                    for (Unit unit : player.getUnits()) {
                        if (unit.isAlive() && unit.getX() == i && unit.getY() == j) {
                            writer.print(unit.getSymbol());
                            unitFound = true;
                            break;
                        }
                    }
                    if (unitFound) continue;

                    // Проверяем юниты компьютера
                    for (Unit unit : computer.getUnits()) {
                        if (unit.isAlive() && unit.getX() == i && unit.getY() == j) {
                            writer.print(unit.getSymbol().toLowerCase());
                            unitFound = true;
                            break;
                        }
                    }
                    if (unitFound) continue;

                    // Проверяем ратуши
                    if (player.getTownHall().getX() == i && player.getTownHall().getY() == j) {
                        writer.print("T");
                        continue;
                    }
                    if (computer.getTownHall().getX() == i && computer.getTownHall().getY() == j) {
                        writer.print("t");
                        continue;
                    }

                    // Сохраняем рельеф
                    writer.print(board.getCell(i, j));
                }
                writer.println();
            }
            System.out.println("Текущее состояние карты сохранено в: " + mapFile.getPath());
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении карты: " + e.getMessage());
        }
    }


    public void saveGameState(String saveName) {
        File savesDir = new File(SAVES_DIR + "/" + userName);
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }

        File saveFile = new File(savesDir, saveName + ".save");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            GameState state = new GameState(player, computer, board, currentMapFile);
            oos.writeObject(state);
            System.out.println("\n=== Сохранение игры ===");
            System.out.println("Игра сохранена под именем: " + saveName);
            System.out.println("Карта: " + (currentMapFile != null ? currentMapFile : "по умолчанию"));
            System.out.println("Здоровье вашей ратуши: " + player.getTownHall().getHealth());
            System.out.println("Здоровье ратуши противника: " + computer.getTownHall().getHealth());
            System.out.println("Файл сохранения: " + saveFile.getPath());
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении игры: " + e.getMessage());
        }
    }



    public static Game loadGameState(String saveName, String userName) throws IOException, ClassNotFoundException {
        File saveFile = new File(SAVES_DIR + "/" + userName + "/" + saveName + ".save");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
            GameState state = (GameState) ois.readObject();

            Game game = new Game(state.player, state.computer, state.board, userName);
            game.currentMapFile = state.mapName;
            return game;
        }
    }

    @XmlRootElement(name = "gameState")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class GameState implements Serializable {
        private static final long serialVersionUID = 1L;

        @XmlElement
        private Player player;

        @XmlElement
        private Player computer;

        @XmlElement
        private GameBoard board;

        @XmlElement
        private String mapName;

        // Конструктор по умолчанию для JAXB
        public GameState() {}

        public GameState(Player player, Player computer, GameBoard board, String mapName) {
            this.player = player;
            this.computer = computer;
            this.board = board;
            this.mapName = mapName;
        }
    }

    public void saveGameToJsonAndXml() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nВведите название для сохранения: ");
        String saveName = scanner.nextLine();

        // Сохраняем в JSON
        saveGameStateToJson(saveName);

        // Сохраняем в XML
        saveGameStateToXml(saveName);

        System.out.println("Игра сохранена в JSON и XML форматах под именем: " + saveName);
    }

    private void saveGameStateToJson(String saveName) {
        File savesDir = new File(SAVES_DIR + "/" + userName);
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }

        File saveFile = new File(savesDir, saveName + ".json");
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            // Создаем упрощенную DTO для сохранения
            Map<String, Object> saveData = new HashMap<>();
            saveData.put("player", serializePlayer(player));
            saveData.put("computer", serializePlayer(computer));
            saveData.put("board", board);
            saveData.put("mapName", currentMapFile);

            mapper.writeValue(saveFile, saveData);
            System.out.println("Игра сохранена в JSON: " + saveFile.getPath());
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении в JSON: " + e.getMessage());
        }
    }

    private Map<String, Object> serializePlayer(Player player) {
        Map<String, Object> playerData = new HashMap<>();
        playerData.put("name", player.getName());

        // Сохраняем TownHall как простой Map
        Map<String, Object> townHallData = new HashMap<>();
        townHallData.put("x", player.getTownHall().getX());
        townHallData.put("y", player.getTownHall().getY());
        townHallData.put("health", player.getTownHall().getHealth());
        townHallData.put("gold", player.getTownHall().getGold());
        playerData.put("townHall", townHallData);
        // Сохраняем юниты как список мапов
        List<Map<String, Object>> unitsData = new ArrayList<>();
        for (Unit unit : player.getUnits()) {
            Map<String, Object> unitData = new HashMap<>();
            unitData.put("type", unit.getClass().getSimpleName());
            unitData.put("id", unit.getId());
            unitData.put("health", unit.getHealth());
            unitData.put("attack", unit.getAttack());
            unitData.put("x", unit.getX());
            unitData.put("y", unit.getY());
            unitData.put("symbol", unit.getSymbol());
            unitsData.add(unitData);
        }
        playerData.put("units", unitsData);

        return playerData;
    }

    public void saveGameStateToXml(String saveName) {
        File savesDir = new File(SAVES_DIR + "/" + userName);
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }

        File saveFile = new File(savesDir, saveName + ".xml");
        try {
            // Создаем копии объектов без циклических ссылок
            GameState state = new GameState();

            // Копируем игрока
            Player playerCopy = new Player();
            playerCopy.setName(player.getName());
            playerCopy.setTownHall(new TownHall(
                    player.getTownHall().getX(),
                    player.getTownHall().getY(),
                    player.getTownHall().getGold()
            ));
            playerCopy.getTownHall().setHealth(player.getTownHall().getHealth());

            // Копируем юнитов игрока
            List<Unit> playerUnits = new ArrayList<>();
            for (Unit unit : player.getUnits()) {
                Unit unitCopy = createUnitCopy(unit);
                playerUnits.add(unitCopy);
            }
            playerCopy.setUnits(playerUnits);

            // Копируем компьютер
            Player computerCopy = new Player();
            computerCopy.setName(computer.getName());
            computerCopy.setTownHall(new TownHall(
                    computer.getTownHall().getX(),
                    computer.getTownHall().getY(),
                    computer.getTownHall().getGold()
            ));
            computerCopy.getTownHall().setHealth(computer.getTownHall().getHealth());

            // Копируем юнитов компьютера
            List<Unit> computerUnits = new ArrayList<>();
            for (Unit unit : computer.getUnits()) {
                Unit unitCopy = createUnitCopy(unit);
                computerUnits.add(unitCopy);
            }
            computerCopy.setUnits(computerUnits);

            // Устанавливаем копии в состояние
            state.player = playerCopy;
            state.computer = computerCopy;
            state.board = this.board;
            state.mapName = this.currentMapFile;

            // Настраиваем JAXB и сохраняем
            JAXBContext context = JAXBContext.newInstance(GameState.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(state, saveFile);

            System.out.println("Игра успешно сохранена в XML: " + saveFile.getPath());
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении в XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Unit createUnitCopy(Unit original) {
        Unit copy;
        if (original instanceof Infantry) {
            copy = new Infantry(original.getId(), original.getX(), original.getY(), null, null);
        } else if (original instanceof Archer) {
            copy = new Archer(original.getId(), original.getX(), original.getY(), null, null);
        } else if (original instanceof Cavalry) {
            copy = new Cavalry(original.getId(), original.getX(), original.getY(), null, null);
        } else if (original instanceof Mage) {
            copy = new Mage(original.getId(), original.getX(), original.getY(), null, null);
        } else {
            throw new IllegalArgumentException("Неизвестный тип юнита");
        }

        // Копируем основные параметры
        copy.setHealth(original.getHealth());
        copy.setAttack(original.getAttack());

        return copy;
    }

    private static Player deserializePlayer(Map<String, Object> playerData) {
        String name = (String) playerData.get("name");

        // Получаем данные TownHall как Map
        Map<String, Object> townHallData = (Map<String, Object>) playerData.get("townHall");

        // Создаем TownHall вручную
        TownHall townHall = new TownHall();
        townHall.setX((int) townHallData.get("x"));
        townHall.setY((int) townHallData.get("y"));
        townHall.setHealth((int) townHallData.get("health"));
        townHall.setGold((int) townHallData.get("gold"));

        Player player = new Player(name, townHall.getX(), townHall.getY(), townHall.getGold());
        player.getTownHall().setHealth(townHall.getHealth()); // Восстанавливаем здоровье

        // Восстанавливаем юниты
        List<Map<String, Object>> unitsData = (List<Map<String, Object>>) playerData.get("units");
        for (Map<String, Object> unitData : unitsData) {
            Unit unit = createUnitFromData(unitData);
            if (unit != null) {
                player.addUnit(unit);
            }
        }

        return player;
    }

    private static Unit createUnitFromData(Map<String, Object> unitData) {
        String type = (String) unitData.get("type");
        int id = (int) unitData.get("id");
        int health = (int) unitData.get("health");
        int attack = (int) unitData.get("attack");
        int x = (int) unitData.get("x");
        int y = (int) unitData.get("y");
        String symbol = (String) unitData.get("symbol");

        Unit unit;
        switch (type) {
            case "Infantry":
                unit = new Infantry(id, x, y, new ArrayList<>(), null);
                break;
            case "Archer":
                unit = new Archer(id, x, y, new ArrayList<>(), null);
                break;
            case "Cavalry":
                unit = new Cavalry(id, x, y, new ArrayList<>(), null);
                break;
            case "Mage":
                unit = new Mage(id, x, y, new ArrayList<>(), null);
                break;

            default:
                return null;
        }

        unit.takeDamage(unit.getHealth() - health);
        return unit;
    }

    public static Game loadGameFromJson(String userName) throws IOException {
        File savesDir = new File(SAVES_DIR + "/" + userName);
        File[] saveFiles = savesDir.listFiles((dir, name) -> name.endsWith(".json"));

        if (saveFiles == null || saveFiles.length == 0) {
            System.out.println("Нет доступных сохранений в JSON.");
            return null;
        }

        System.out.println("\n=== Доступные сохранения в JSON ===");
        for (int i = 0; i < saveFiles.length; i++) {
            String name = saveFiles[i].getName().replace(".json", "");
            System.out.printf("%2d. %s\n", i+1, name);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("\nВыберите сохранение: ");
        int saveChoice = scanner.nextInt();
        scanner.nextLine();

        if (saveChoice > 0 && saveChoice <= saveFiles.length) {
            String saveName = saveFiles[saveChoice-1].getName().replace(".json", "");
            File saveFile = new File(savesDir, saveName + ".json");

            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> saveData = mapper.readValue(saveFile,
                        new TypeReference<Map<String, Object>>() {});

                // Создаем временных игроков для загрузки данных
                Player tempPlayer = createPlayerFromData((Map<String, Object>) saveData.get("player"));
                Player tempComputer = createPlayerFromData((Map<String, Object>) saveData.get("computer"));

                // Создаем игру с новыми игроками (но правильными координатами TownHall)
                Game game = new Game(
                        new Player(tempPlayer.getName(),
                                tempPlayer.getTownHall().getX(),
                                tempPlayer.getTownHall().getY(),
                                tempPlayer.getTownHall().getGold()),
                        new Player(tempComputer.getName(),
                                tempComputer.getTownHall().getX(),
                                tempComputer.getTownHall().getY(),
                                tempComputer.getTownHall().getGold()),
                        mapper.convertValue(saveData.get("board"), GameBoard.class),
                        userName
                );

                // Восстанавливаем точное состояние
                game.player.restoreFromData(tempPlayer);
                game.computer.restoreFromData(tempComputer);

                game.currentMapFile = (String) saveData.get("mapName");
                return game;
            } catch (IOException e) {
                System.out.println("Ошибка при загрузке из JSON: " + e.getMessage());
                throw e;
            }
        }
        return null;
    }
    private static Player createPlayerFromData(Map<String, Object> playerData) {
        String name = (String) playerData.get("name");

        Map<String, Object> townHallData = (Map<String, Object>) playerData.get("townHall");
        TownHall townHall = new TownHall();
        townHall.setX((int) townHallData.get("x"));
        townHall.setY((int) townHallData.get("y"));
        townHall.setHealth((int) townHallData.get("health"));
        townHall.setGold((int) townHallData.get("gold"));

        Player player = new Player(name, townHall.getX(), townHall.getY(), townHall.getGold());
        player.getTownHall().setHealth(townHall.getHealth());

        // Только добавляем юнитов из сохранения, не создаем новых
        List<Map<String, Object>> unitsData = (List<Map<String, Object>>) playerData.get("units");
        for (Map<String, Object> unitData : unitsData) {
            Unit unit = createUnitFromData(unitData);
            if (unit != null) {
                player.addUnit(unit);
            }
        }

        return player;
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Добро пожаловать в игру! ===");
        System.out.print("Введите ваше имя: ");
        String userName = scanner.nextLine().trim();

        System.out.println("\n=== Главное меню ===");
        System.out.println("1. Новая игра");
        System.out.println("2. Загрузить игру");
        System.out.println("3. Редактор карт");
        System.out.println("4. Сохранения в JSON");
        System.out.print("Выберите действие: ");
        int choice = scanner.nextInt();
        scanner.nextLine();


        if (choice == 3) {
            MapEditor editor = new MapEditor(userName);
            editor.startEditor();
            return;
        }

        Game game;

        if (choice == 2) {
            System.out.println("\nВыберите тип загрузки:");
            System.out.println("1 - Стандартное сохранение (.save)");
            System.out.println("2 - JSON сохранение (.json)");
            int loadType = scanner.nextInt();
            scanner.nextLine();

            if (loadType == 2) {
                // Загрузка JSON
                game = loadGameFromJson(userName);
                if (game == null) {
                    System.out.println("Создаем новую игру...");
                    game = startNewGame(userName, scanner);
                }
            } else {
                // Стандартная загрузка
                File savesDir = new File(SAVES_DIR + "/" + userName);
                File[] saveFiles = savesDir.listFiles((dir, name) -> name.endsWith(".save"));

                if (saveFiles == null || saveFiles.length == 0) {
                    System.out.println("Нет доступных сохранений. Начинаем новую игру.");
                    game = startNewGame(userName, scanner);
                } else {
                    System.out.println("\n=== Доступные сохранения ===");
                    for (int i = 0; i < saveFiles.length; i++) {
                        String saveName = saveFiles[i].getName().replace(".save", "");
                        System.out.printf("%2d. %s\n", i+1, saveName);
                    }
                    System.out.println(" 0. Новая игра");

                    System.out.print("\nВыберите сохранение: ");
                    int saveChoice = scanner.nextInt();
                    scanner.nextLine();

                    if (saveChoice > 0 && saveChoice <= saveFiles.length) {
                        String saveName = saveFiles[saveChoice-1].getName().replace(".save", "");
                        game = loadGameState(saveName, userName);
                    } else {
                        game = startNewGame(userName, scanner);
                    }
                }
            }
        } else {
            game = startNewGame(userName, scanner);
        }
            boolean gameOver = false;
            while (!gameOver) {

                n = n + 1;
                board.updateBoard(player, computer);
                board.printBoard();
                if (player.getTownHall().getX() == -1) {
                    player.getTownHall().setPosition(4, 0);
                }
                if (computer.getTownHall().getX() == -1) {
                    player.getTownHall().setPosition(4, 11);
                }
                // Добавляем возможность сохранения в JSON/XML
                if (n % 3 == 0) {
                    System.out.print("\nХотите сохранить игру? (1 - Да, 2 - Нет): ");
                    int saveChoice = scanner.nextInt();
                    if (saveChoice == 1) {
                        System.out.println("Выберите формат сохранения:");
                        System.out.println("1 - Стандартный (.save)");
                        System.out.println("2 - JSON и XML");
                        int formatChoice = scanner.nextInt();
                        scanner.nextLine();

                        if (formatChoice == 2) {
                            game.saveGameToJsonAndXml();
                        } else {
                            System.out.print("Введите название для сохранения: ");
                            String saveName = scanner.nextLine();
                            game.saveGameState(saveName);
                        }
                    }
                }
                // Ход игрока

                System.out.println("\n");
                System.out.println("Выберите юнита:");
                for (Unit unit : player.getUnits()) {
                    if (unit.isAlive()) {
                        System.out.println(unit.toString());
                    }
                }
                System.out.println("Введите 50, если хотите зайти в ратушу");
                int unitChoice = scanner.nextInt();
                Unit playerUnit = null;
                if (unitChoice == 50) {
                    player.getTownHall().townHallMenu(player);
                } else {

                    for (Unit unit : player.getUnits()) {
                        if (unit.getId() == unitChoice && unit.isAlive()) {
                            playerUnit = unit;
                            break;
                        }
                    }
                }

                if (playerUnit == null) {
                    System.out.println("Неверный выбор юнита!");
                    continue;
                }
                System.out.println("Выберите действие: 1 - Переместиться, 2 - Атаковать, 3 - Регенерировать здоровье ");
                int action = scanner.nextInt();

                if (action == 1) {
                    // Перемещение
                    if (playerUnit instanceof Archer) {
                        System.out.println("Лучник не может перемещаться!");
                    } else {
                        System.out.println("Куда переместиться (x y): ");
                        int x = scanner.nextInt();
                        int y = scanner.nextInt();
                        playerUnit.move(x, y, player, computer);

                    }
                } else if (action == 2) {
                    // Атака
                    if (playerUnit instanceof Archer) {
                        System.out.println("Введите координаты для атаки (x y): ");
                        int targetX = scanner.nextInt();
                        int targetY = scanner.nextInt();

                        ((Archer) playerUnit).attackForward(targetX, targetY, computer);
                    } else if (playerUnit instanceof Infantry) {
                        ((Infantry) playerUnit).attackForward(computer);
                    } else if (playerUnit instanceof Cavalry) {
                        ((Cavalry) playerUnit).attackForward(computer);
                    } else if (playerUnit instanceof Mage) {
                        ((Mage) playerUnit).attackForward(computer);
                    } else if (playerUnit instanceof DarkKnight) {
                        ((DarkKnight) playerUnit).attackForward(computer);
                    } else if (playerUnit instanceof Skeleton) {
                        ((Skeleton) playerUnit).attackForward(computer);
                    } else if (playerUnit instanceof Zombie) {
                        ((Zombie) playerUnit).attackForward(computer);
                    } else if (playerUnit instanceof Ghost) {
                        ((Ghost) playerUnit).attackRow(computer);
                    } else System.out.println("Этот юнит пока не может атаковать.");

                    System.out.println("Юниты компьюетра и их характеристики после нашей атаки");
                    for (Unit unit : computer.getUnits()) {
                        if (unit.isAlive()) {
                            System.out.println(unit.toString());
                        }
                    }
                }
                if (action == 3) {
                    // Регенерация
                    if (playerUnit instanceof DarkKnight) {
                        ((DarkKnight) playerUnit).regenerate();
                    } else if (playerUnit instanceof Skeleton) {
                        ((Skeleton) playerUnit).regenerate();
                    } else if (playerUnit instanceof Zombie) {
                        ((Zombie) playerUnit).regenerate();
                    } else if (playerUnit instanceof Ghost) {
                        ((Ghost) playerUnit).regenerate();
                    } else System.out.println("Этот юнит не может регенерировать.");
                }

                if (computer.isTownHallDestroyed()) {
                    System.out.println("Вы победили! Ратуша компьютера разрушена!");
                    gameOver = true;
                    continue;

                }
                game.checkForAutosave();
                // Ход компьютера
                System.out.println("Ход компьютера...");
                // Фильтруем живые юниты компьютера
                List<Unit> aliveComputerUnits = computer.getUnits().stream()
                        .filter(Unit::isAlive)
                        .toList();

                if (!aliveComputerUnits.isEmpty()) {
                    Random random = new Random();
                    // Выбираем случайный живой юнит
                    Unit computerUnit = aliveComputerUnits.get(random.nextInt(aliveComputerUnits.size()));
                    if (computerUnit instanceof Archer) {
                        int targetX = player.getTownHall().getX();
                        int targetY = player.getTownHall().getY();
                        ((Archer) computerUnit).attackForward(targetX, targetY, player);
                    }

                    if (computerUnit instanceof Infantry) {
                        int newX = computerUnit.getX();
                        int newY = computerUnit.getY() - 1;

                        ((Infantry) computerUnit).movecomp(newX, newY, player, computer);
                    }
                    if (computerUnit instanceof Cavalry) {
                        int newX = computerUnit.getX();
                        int newY = computerUnit.getY() - 1;

                        ((Cavalry) computerUnit).movecomp(newX, newY, player, computer);
                    }
                    if (computerUnit instanceof Mage) {
                        ((Mage) computerUnit).attackForward(player);
                    }
                    if (player.isTownHallDestroyed()) {
                        System.out.println("Вы проиграли! Ваша ратуша разрушена!");
                        gameOver = true;
                        continue;

                    }
                } else {
                    System.out.println("У компьютера не осталось живых юнитов!");
                }


            }
            scanner.close();
        }
        /*
        private static Game startNewGame (String userName, Scanner scanner) throws IOException {
            GameBoard board = new GameBoard();
            String mapName = selectMap(scanner, userName);
            Player player = new Player("Player", 4, 0, 100);
            Player computer = new Player("Computer", 4, 11, 100);

            Game game = new Game(player, computer, board, userName);
            game.currentMapFile = mapName;

            if (mapName != null) {
                String mapPath = MAPS_DIR + "/" + userName + "/" + mapName + ".map";
                board.loadFromFile(mapPath, player, computer);
                board.createPlayerUnitsFromBoard(player);
                board.createCompUnitsFromBoard(computer);


            } else {

                player.getTownHall().setPosition(4, 0);
                computer.getTownHall().setPosition(4, 11);

                player.addUnit(new Infantry(1, 3, 1, player.getUnits(), player));
                player.addUnit(new Infantry(2, 5, 1, player.getUnits(), player));
                player.addUnit(new Infantry(3, 6, 1, player.getUnits(), player));
                player.addUnit(new Infantry(4, 4, 1, player.getUnits(), player));
                player.addUnit(new Infantry(5, 2, 1, player.getUnits(), player));
                player.addUnit(new Archer(6, 2, 0, player.getUnits(), player));
                player.addUnit(new Archer(7, 6, 0, player.getUnits(), player));
                player.addUnit(new Cavalry(8, 4, 2, player.getUnits(), player));
                player.addUnit(new Mage(9, 7, 0, player.getUnits(), player));
                player.addUnit(new Mage(10, 1, 0, player.getUnits(), player));


                computer.addUnit(new Infantry(100, 4, 10, computer.getUnits(), computer));
                computer.addUnit(new Infantry(101, 5, 10, computer.getUnits(), computer));
                computer.addUnit(new Infantry(102, 6, 10, computer.getUnits(), computer));
                computer.addUnit(new Infantry(107, 3, 10, computer.getUnits(), computer));
                computer.addUnit(new Infantry(108, 2, 10, computer.getUnits(), computer));
                computer.addUnit(new Cavalry(104, 4, 9, computer.getUnits(), computer));
                computer.addUnit(new Archer(105, 2, 11, computer.getUnits(), computer));
                computer.addUnit(new Archer(106, 6, 11, computer.getUnits(), computer));
                computer.addUnit(new Mage(110, 1, 11, computer.getUnits(), computer));
                computer.addUnit(new Mage(111, 7, 11, computer.getUnits(), computer));

            }

            System.out.println("\n=== Новая игра начата ===");
            System.out.println("Карта: " + (mapName != null ? mapName : "по умолчанию"));
            return game;
        }

         */

    private static Game startNewGame(String userName, Scanner scanner) throws IOException {
        GameBoard board = new GameBoard();
        String mapName = selectMap(scanner, userName);

        // Создаем игроков БЕЗ юнитов
        Player player = new Player("Player", 4, 0, 100);
        Player computer = new Player("Computer", 4, 11, 100);

        Game game = new Game(player, computer, board, userName);
        game.currentMapFile = mapName;

        if (mapName != null) {
            // Загрузка из файла карты
            String mapPath = MAPS_DIR + "/" + userName + "/" + mapName + ".map";
            board.loadFromFile(mapPath, player, computer);
            board.createPlayerUnitsFromBoard(player);
            board.createCompUnitsFromBoard(computer);
        } else {
            player.addUnit(new Infantry(1, 3, 1, player.getUnits(), player));
            player.addUnit(new Infantry(2, 5, 1, player.getUnits(), player));
            player.addUnit(new Infantry(3, 6, 1, player.getUnits(), player));
            player.addUnit(new Infantry(4, 4, 1, player.getUnits(), player));
            player.addUnit(new Infantry(5, 2, 1, player.getUnits(), player));
            player.addUnit(new Archer(6, 2, 0, player.getUnits(), player));
            player.addUnit(new Archer(7, 6, 0, player.getUnits(), player));
            player.addUnit(new Cavalry(8, 4, 2, player.getUnits(), player));
            player.addUnit(new Mage(9, 7, 0, player.getUnits(), player));
            player.addUnit(new Mage(10, 1, 0, player.getUnits(), player));


            computer.addUnit(new Infantry(100, 4, 10, computer.getUnits(), computer));
            computer.addUnit(new Infantry(101, 5, 10, computer.getUnits(), computer));
            computer.addUnit(new Infantry(102, 6, 10, computer.getUnits(), computer));
            computer.addUnit(new Infantry(107, 3, 10, computer.getUnits(), computer));
            computer.addUnit(new Infantry(108, 2, 10, computer.getUnits(), computer));
            computer.addUnit(new Cavalry(104, 4, 9, computer.getUnits(), computer));
            computer.addUnit(new Archer(105, 2, 11, computer.getUnits(), computer));
            computer.addUnit(new Archer(106, 6, 11, computer.getUnits(), computer));
            computer.addUnit(new Mage(110, 1, 11, computer.getUnits(), computer));
            computer.addUnit(new Mage(111, 7, 11, computer.getUnits(), computer));
        }

        return game;
    }
}





