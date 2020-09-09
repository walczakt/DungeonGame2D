// The basis of the code was written by pr Trundle. I made the rest.
package uk.ac.bradford.dungeongame;

//Added import Entity type
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import uk.ac.bradford.dungeongame.Entity.EntityType;

public class GameEngine {

    //Expanded for a few new types. They all have their use described in the raport.
    public enum TileType {
        WALL, FLOOR, CHEST, STAIRS, BROKENWALL, TELEPORT, RIVER, BRIDGE, CHESTOPEN, POTION, MEDKIT
    }

    public static final int DUNGEON_WIDTH = 25;

    public static final int DUNGEON_HEIGHT = 18;

    public static final int MAX_MONSTERS = 40;

    public static final double WALL_CHANCE = 0.05;

    private Random rng = new Random();

    //Starts at 1, but increases each time player steps on stairs. There are few varuables that involve depth, so the game gets harder each time.
    private int depth = 1;

    private GameGUI gui;

    //Contains all the tiles in the current level
    private TileType[][] tiles;

    //Contains list of possible spawns for monsters and players.
    private ArrayList<Point> spawns;

    //Contains points of teleport on current level
    private ArrayList<Point> teleports;

    //It represends current player's upgrades. Its public static, because GameGui class uses this string.
    public static String eq = "nothing";

    //Added for potion check, its public static since DungeonInputHandler class usses this Boolean.
    public static Boolean haspotion = false;
    //Hold player entity
    private Entity player;
    //Holds arrays of all monsters
    private Entity[] monsters;

    public GameEngine(GameGUI gui) {
        this.gui = gui;
        startGame();
    }

    //This method is very chaotic, as I was constatly changing and re-desinging it.
    //However, the main goal for generateLevel() is to generate random level each time a player steps on a steps or whenver the game starts.
    private TileType[][] generateLevel() {
        TileType[][] level = new TileType[DUNGEON_WIDTH][DUNGEON_HEIGHT];
        //Fills the level with floors
        for (int i = 0; i < DUNGEON_WIDTH; i++) {
            for (int j = 0; j < DUNGEON_HEIGHT; j++) {
                level[i][j] = TileType.FLOOR;
            }
        }//Random change for a floor to become a wall
        for (int i = 0; i < DUNGEON_WIDTH; i++) {
            for (int j = 0; j < DUNGEON_HEIGHT; j++) {
                int Wall_chance = rng.nextInt(100 - 1 + 1) + 1;
                if (Wall_chance <= 10) {
                    level[i][j] = TileType.WALL;
                }
            }
        }
        //These make the borders of the game.
        for (int i = 0; i < DUNGEON_HEIGHT; i++) {
            level[0][i] = TileType.WALL;
        }
        for (int i = 0; i < DUNGEON_HEIGHT; i++) {
            level[DUNGEON_WIDTH - 1][i] = TileType.WALL;
        }
        for (int i = 0; i < DUNGEON_WIDTH; i++) {
            level[i][0] = TileType.WALL;
        }
        for (int i = 0; i < DUNGEON_WIDTH; i++) {
            level[i][DUNGEON_HEIGHT - 1] = TileType.WALL;
        }
        
        //A river is randomly generated somewhere in the middle of the map.
        int riverx = rng.nextInt((14 - 7) + 1) + 7;
        for (int i = 1; i < DUNGEON_HEIGHT - 1; i++) {
            level[riverx][i] = TileType.RIVER;
        }
        //There is always 1 bridge on the river
        level[riverx][rng.nextInt((14 - 7) + 1) + 7] = TileType.BRIDGE;

        for (int j = 0; j < 10; j++) {
            int a = rng.nextInt((23 - 3) + 1) + 3;
            int b = rng.nextInt(17 - 3 + 1) + 3;
            if (level[a][b] == TileType.FLOOR) {
                level[a][b] = TileType.TELEPORT;
                break;
            }
        }
        for (int j = 0; j < 10; j++) {
            int a = rng.nextInt((23 - 3) + 1) + 3;
            int b = rng.nextInt(17 - 3 + 1) + 3;
            if (level[a][b] == TileType.FLOOR) {
                level[a][b] = TileType.MEDKIT;
                break;
            }
        }
        for (int j = 0; j < 10; j++) {
            int a = rng.nextInt((23 - 3) + 1) + 3;
            int b = rng.nextInt(17 - 3 + 1) + 3;
            if (level[a][b] == TileType.FLOOR) {
                level[a][b] = TileType.TELEPORT;
                break;
            }
        }

        for (int j = 0; j < 1000; j++) {
            int a = rng.nextInt((23 - 3) + 1) + 3;
            int b = rng.nextInt(17 - 3 + 1) + 3;
            if (level[a][b] == TileType.FLOOR) {
                level[a][b] = TileType.STAIRS;
                break;
            }
        }

        for (int j = 0; j < 10; j++) {
            int a = rng.nextInt((23 - 3) + 1) + 3;
            int b = rng.nextInt(17 - 3 + 1) + 3;
            if (level[a][b] == TileType.FLOOR) {
                level[a][b] = TileType.CHEST;
                break;
            }
        }

        //these make sure that the bridge is always possible to cross
        for (int i = 1; i < DUNGEON_HEIGHT - 1; i++) {
            if (level[riverx - 2][i] == TileType.WALL) {
                level[riverx - 2][i] = TileType.FLOOR;
            }
        }
        for (int i = 1; i < DUNGEON_HEIGHT - 1; i++) {
            if (level[riverx - 1][i] == TileType.WALL) {
                level[riverx - 1][i] = TileType.FLOOR;
            }
        }
        for (int i = 1; i < DUNGEON_HEIGHT - 1; i++) {
            if (level[riverx + 1][i] == TileType.WALL) {
                level[riverx + 1][i] = TileType.FLOOR;
            }
        }
        for (int i = 1; i < DUNGEON_HEIGHT - 1; i++) {
            if (level[riverx + 1][i] == TileType.WALL) {
                level[riverx + 1][i] = TileType.FLOOR;
            }
        }

        return level;
    }

    public void heal() {
        player.changeHealth(depth * 15);
        haspotion = false;
    }

    //Its use for changing player postition later on
    private Point teleport1() {
        Point t1 = new Point(rng.nextInt(21) + 2, rng.nextInt(14) + 2);
        return t1;
    }

    private Point teleport2() {
        Point t1 = new Point(rng.nextInt(21) + 2, rng.nextInt(14) + 2);
        return t1;
    }

    private ArrayList<Point> getSpawns() {
        ArrayList<Point> s = new ArrayList<Point>();
        //Add code here to find tiles in the level array that are suitable spawn points
        for (int i = 0; i < DUNGEON_WIDTH; i++) {
            for (int j = 0; j < DUNGEON_HEIGHT; j++) {
                if (tiles[i][j] == TileType.FLOOR) {
                    Point p = new Point(i, j);
                    s.add(p);
                }
            }
        }
        //Add these points to the ArrayList s
        return s;
    }

    //Its also used for the proper teleports work
    private ArrayList<Point> teleporty() {
        ArrayList<Point> s = new ArrayList<Point>();
        for (int i = 0; i < DUNGEON_WIDTH; i++) {
            for (int j = 0; j < DUNGEON_HEIGHT; j++) {
                if (tiles[i][j] == TileType.TELEPORT) {
                    Point p = new Point(i, j);
                    s.add(p);
                }
            }
        }
        return s;
    }

    private Entity[] spawnMonsters() {

        Entity[] monsters = new Entity[MAX_MONSTERS];

        for (int i = 0; i < 1 * depth; i++) {
            int r = rng.nextInt(spawns.size());
            Point spawnpoint = new Point(spawns.get(r).x, spawns.get(r).y);
            monsters[i] = new Entity(20 * depth, spawnpoint.x, spawnpoint.y, EntityType.MONSTER);
            spawns.remove(r);
        }

        return monsters;
    }

    private Entity spawnPlayer() {
        //I know it's not optimal, but I didn't really know how to randomise one element from an array.
        //In the end, I created a different variable that holds a value from a random element from that array.
        int r = rng.nextInt(spawns.size());
        Point spawnpoint = new Point(spawns.get(r).x, spawns.get(r).y);
        Entity player = new Entity(100, spawnpoint.x, spawnpoint.y, EntityType.PLAYER);
        spawns.remove(r);
        return player;    //Should be changed to return an Entity (the player) instead of null
    }

    //this class checks and returns the class of a tile
    public TileType RodzajPodloza(int osX, int osY) {
        return tiles[osX][osY];
    }

    //this class checks if there is a monster on the tile
    public boolean JestPotwor(int osX, int osY) {
        //[M][][][][][][][][][]
        for (int i = 0; i < depth; i++) {

            // I had to add the null check, becaouse all the dead monster would be a problem
            if (monsters[i] != null) {
                if (monsters[i].getX() == osX && monsters[i].getY() == osY) {
                    return true;
                }
            }

        }
        return false;
    }

    // this method moves the player (its easier to just call that method)
    public void PrzesunGracza(int OsX, int OsY) {

        int NowyX = player.getX() + OsX;
        int NowyY = player.getY() + OsY;

        if (RodzajPodloza(NowyX, NowyY) == TileType.RIVER) {

        }//On that depends reward that player gets
        else if (RodzajPodloza(NowyX, NowyY) == TileType.CHEST) {
            tiles[NowyX][NowyY] = TileType.CHESTOPEN;
            if ("nothing".equals(eq)) {
                int n = rng.nextInt(2);
                if (n == 0) {
                    eq = "gn";
                } else {
                    eq = "ng";
                }
            } else if (("gn".equals(eq)) || ("ng".equals(eq))) {
                eq = "gg";
            } else if ("gg".equals(eq)) {

            }
            player.setPosition(player.getX(), player.getY());
        } else if (RodzajPodloza(NowyX, NowyY) == TileType.CHESTOPEN) {

        } else if (RodzajPodloza(NowyX, NowyY) == TileType.MEDKIT) {
            heal();
            tiles[NowyX][NowyY] = TileType.FLOOR;
            player.setPosition(NowyX, NowyY);
        } else if (!JestPotwor(NowyX, NowyY) && RodzajPodloza(NowyX, NowyY) == TileType.TELEPORT) {
            if (NowyX == teleports.get(0).x) {
                player.setPosition(teleports.get(1).x, teleports.get(1).y);
            } else if (NowyX == teleports.get(1).x) {
                player.setPosition(teleports.get(0).x, teleports.get(0).y);
            }
        } //picks up the potion
        else if (RodzajPodloza(NowyX, NowyY) == TileType.POTION) {
            haspotion = true;
            player.setPosition(NowyX, NowyY);
            tiles[NowyX][NowyY] = TileType.FLOOR;
        } else if (!JestPotwor(NowyX, NowyY) && RodzajPodloza(NowyX, NowyY) != TileType.WALL) {
            player.setPosition(NowyX, NowyY);
        } else if (JestPotwor(NowyX, NowyY)) {
            hitMonster(monsters[KtoryPotwor(NowyX, NowyY)]);
        }

    }

    public int KtoryPotwor(int whichX, int whichY) {
        int cos = 0;
        for (int i = 0; i < depth; i++) {
            if (monsters[i] != null) {

                if (monsters[i].getX() == whichX && monsters[i].getY() == whichY) {
                    cos = i;

                }
            }
        }
        return cos;
    }

    public void movePlayerLeft() {

        PrzesunGracza(-1, 0);
    }

    public void movePlayerRight() {
        PrzesunGracza(1, 0);
    }

    public void movePlayerUp() {
        PrzesunGracza(0, -1);
    }

    public void movePlayerDown() {

        PrzesunGracza(0, 1);
    }

    private void hitMonster(Entity m) {
        if ("nothing".equals(eq) || "gn".equals(eq)) {
            m.changeHealth(depth * (-7));
        } else if ("ng".equals(eq) || "gg".equals(eq)) {
            m.changeHealth(2 * (depth * -5));
        }
    }

    private void moveMonsters() {
        for (int i = 0; i < depth; i++) {

            // i had to add the null check, becaouse all the dead monster would be a problem
            if (monsters[i] != null) {
                moveMonster(monsters[i]);
            }
        }
    }

    //smal method to check for bridge position, its use in PrzesunPotwora
    private Point most() {
        Point p = new Point();
        for (int i = 0; i < DUNGEON_WIDTH; i++) {
            for (int j = 0; j < DUNGEON_HEIGHT; j++) {
                if (tiles[i][j] == TileType.BRIDGE) {
                    p = new Point(i, j);
                }
            }
        }
        return p;
    }

    public void PrzesunPotwora(Entity n, int OX, int OY) {
        int NewX = n.getX() + OX;
        int NewY = n.getY() + OY;

        //in this case the brackets are empty, because i dont want them to do anything
        if (NewX == player.getX() && NewY == player.getY()) {
            hitPlayer();
        } else if (JestPotwor(NewX, NewY)) {
        } else if (RodzajPodloza(NewX, NewY) == TileType.RIVER) {
            //move down for the bridge
            if ((most().x > n.getX() && most().y > n.getY() || (most().y > n.getY() && n.getX() > most().x))) {
                n.setPosition(n.getX(), n.getY() + 1);
            }
            //move up for the bridge
            if ((most().x > n.getX() && most().y < n.getY() || (most().y < n.getY() && n.getX() > most().x))) {
                n.setPosition(n.getX(), n.getY() - 1);
            }
            //move left for the bridge
            if ((n.getY() == most().y) && (n.getX() < most().x)) {
                n.setPosition(n.getX() + 1, n.getY());
            }
            //move right for the bridge
            if ((n.getY() == most().y) && (n.getX() > most().x)) {
                n.setPosition(n.getX() - 1, n.getY());
            }
        } else {
            n.setPosition(NewX, NewY);
        }

    }

    private void moveMonster(Entity m) {

        int monX = m.getX();
        int monY = m.getY();

        int plaX = player.getX();
        int plaY = player.getY();

        //go down
        if (monX == plaX && plaY > monY) {
            if (RodzajPodloza(monX, monY + 1) == TileType.WALL) {
                tiles[monX][monY + 1] = TileType.BROKENWALL;
            }
            PrzesunPotwora(m, 0, 1);
        } //go down left
        else if (monX > plaX && plaY > monY) {
            if (RodzajPodloza(monX - 1, monY + 1) == TileType.WALL) {
                tiles[monX - 1][monY + 1] = TileType.BROKENWALL;
            }
            PrzesunPotwora(m, -1, 1);
        } //go left 
        else if (monX > plaX && plaY == monY) {
            if (RodzajPodloza(monX - 1, monY) == TileType.WALL) {
                tiles[monX - 1][monY] = TileType.BROKENWALL;
            }
            PrzesunPotwora(m, -1, 0);
        } // go left up
        else if (monX > plaX && plaY < monY) {
            if (RodzajPodloza(monX - 1, monY - 1) == TileType.WALL) {
                tiles[monX - 1][monY - 1] = TileType.BROKENWALL;
            }
            PrzesunPotwora(m, -1, -1);
        } //go up
        else if (monX == plaX && plaY < monY) {
            if (RodzajPodloza(monX, monY - 1) == TileType.WALL) {
                tiles[monX][monY - 1] = TileType.BROKENWALL;
            }
            PrzesunPotwora(m, 0, -1);
        } //go up right
        else if (monX < plaX && plaY < monY) {
            if (RodzajPodloza(monX + 1, monY - 1) == TileType.WALL) {
                tiles[monX + 1][monY - 1] = TileType.BROKENWALL;
            }
            PrzesunPotwora(m, 1, -1);
        } //go right
        else if (monX < plaX && plaY == monY) {
            if (RodzajPodloza(monX + 1, monY) == TileType.WALL) {
                tiles[monX + 1][monY] = TileType.BROKENWALL;
            }
            PrzesunPotwora(m, 1, 0);
        } //go right down
        else if (monX < plaX && plaY > monY) {
            if (RodzajPodloza(monX + 1, monY + 1) == TileType.WALL) {
                tiles[monX + 1][monY + 1] = TileType.BROKENWALL;
            }
            PrzesunPotwora(m, 1, 1);
        }

    }

    private void hitPlayer() {
        if ("nothing".equals(eq) || "gn".equals(eq)) {
            player.changeHealth(depth * (-5));
        } else if ("ng".equals(eq) || "gg".equals(eq)) {
            player.changeHealth((depth * -5) - (depth * -3));
        }
    }

    private void cleanDeadMonsters() {

        for (int i = 0; i < depth; i++) {
            if (monsters[i] != null) {
                if (monsters[i].getHealth() < 1) {
                    int n = rng.nextInt(2);
                    if (n == 0) {
                        tiles[monsters[i].getX()][monsters[i].getY()] = TileType.POTION;
                    }
                    monsters[i] = null;

                }
            }
        }

    }

    private void descendLevel() {
        depth++;
        tiles = generateLevel();
        teleports = teleporty();
        spawns = getSpawns();
        monsters = spawnMonsters();
        placePlayer();
        gui.updateDisplay(tiles, player, monsters);

    }

    private void placePlayer() {
        int r = rng.nextInt(spawns.size());
        Point spawnpoint = new Point(spawns.get(r).x, spawns.get(r).y);
        player.setPosition(spawnpoint.x, spawnpoint.y);
        spawns.remove(r);
    }

    public void doTurn() {
        cleanDeadMonsters();
        moveMonsters();
        if (player != null) {       //checks a player object exists
            if (player.getHealth() < 1) {
                System.exit(0);     //exits the game when player is dead
            }
            if (tiles[player.getX()][player.getY()] == TileType.STAIRS) {
                descendLevel();     //moves to next level if the player is on Stairs
            }
        }
        gui.updateDisplay(tiles, player, monsters);     //updates GUI
    }

    public void startGame() {
        tiles = generateLevel();
        teleports = teleporty();
        spawns = getSpawns();
        monsters = spawnMonsters();
        player = spawnPlayer();
        gui.updateDisplay(tiles, player, monsters);
    }
}
