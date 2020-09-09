package uk.ac.bradford.dungeongame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import uk.ac.bradford.dungeongame.GameEngine.TileType;


public class GameGUI extends JFrame {
    
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    public static final int HEALTH_BAR_HEIGHT = 3;
    
    Canvas canvas;
    
    public GameGUI() {
        initGUI();
    }
    
    public void registerKeyHandler(DungeonInputHandler i) {
        addKeyListener(i);
    }
    
    private void initGUI() {
        add(canvas = new Canvas());  
        setTitle("Dungeon");
        setSize(816, 615);
        setLocationRelativeTo(null);     
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void updateDisplay(TileType[][] tiles, Entity player, Entity[] monsters) {
        canvas.update(tiles, player, monsters);
    }
    
}


class Canvas extends JPanel {

    //All of the changes in this classes were just to add more graphics. I based on the few
    //images that were already in, only changed names accordingly.
    private BufferedImage floor;
    private BufferedImage wall;
    private BufferedImage player;
    private BufferedImage monster;
    private BufferedImage stairs;
    private BufferedImage chest;
    private BufferedImage brokenwall;
    private BufferedImage teleport;
    private BufferedImage river;
    private BufferedImage bridge;
    private BufferedImage chestopen;
    private BufferedImage playergg;
    private BufferedImage playergn;
    private BufferedImage playerng;
    private BufferedImage potion;
    private BufferedImage medkit;
    
    
    TileType[][] currentTiles; 
    Entity currentPlayer;       
    Entity[] currentMonsters;  

    public Canvas() {
        loadTileImages();
    }
    

    private void loadTileImages() {
        try {
            //Thats where the program loads files from
            floor = ImageIO.read(new File("assets/floor.png"));
            assert floor.getHeight() == GameGUI.TILE_HEIGHT &&
                    floor.getWidth() == GameGUI.TILE_WIDTH;
            wall = ImageIO.read(new File("assets/wall.png"));
            assert wall.getHeight() == GameGUI.TILE_HEIGHT &&
                    wall.getWidth() == GameGUI.TILE_WIDTH;
            player = ImageIO.read(new File("assets/player.png"));
            assert player.getHeight() == GameGUI.TILE_HEIGHT &&
                    player.getWidth() == GameGUI.TILE_WIDTH;
            monster = ImageIO.read(new File("assets/monster.png"));
            assert monster.getHeight() == GameGUI.TILE_HEIGHT &&
                    monster.getWidth() == GameGUI.TILE_WIDTH;
            stairs = ImageIO.read(new File("assets/stairs.png"));
            assert stairs.getHeight() == GameGUI.TILE_HEIGHT &&
                    stairs.getWidth() == GameGUI.TILE_WIDTH;
            chest= ImageIO.read (new File ("assets/chest.png"));
            assert chest.getHeight() == GameGUI.TILE_HEIGHT &&
                    chest.getWidth() == GameGUI.TILE_WIDTH;
            brokenwall= ImageIO.read (new File ("assets/brokenwall.png"));
            assert brokenwall.getHeight() == GameGUI.TILE_HEIGHT &&
                    brokenwall.getWidth() == GameGUI.TILE_WIDTH;
            teleport= ImageIO.read (new File ("assets/teleport.png"));
            assert teleport.getHeight() == GameGUI.TILE_HEIGHT &&
                    teleport.getWidth() == GameGUI.TILE_WIDTH;
            river= ImageIO.read (new File ("assets/river.png"));
            assert river.getHeight() == GameGUI.TILE_HEIGHT &&
                    river.getWidth() == GameGUI.TILE_WIDTH;
            bridge= ImageIO.read (new File ("assets/bridge.png"));
            assert bridge.getHeight() == GameGUI.TILE_HEIGHT &&
                    bridge.getWidth() == GameGUI.TILE_WIDTH;
            chestopen= ImageIO.read (new File ("assets/chestopen.png"));
            assert chestopen.getHeight() == GameGUI.TILE_HEIGHT &&
                    chestopen.getWidth() == GameGUI.TILE_WIDTH;
            playergg= ImageIO.read (new File ("assets/playergg.png"));
            assert playergg.getHeight() == GameGUI.TILE_HEIGHT &&
                    playergg.getWidth() == GameGUI.TILE_WIDTH;
            playerng= ImageIO.read (new File ("assets/playerng.png"));
            assert playerng.getHeight() == GameGUI.TILE_HEIGHT &&
                    playerng.getWidth() == GameGUI.TILE_WIDTH;
            playergn= ImageIO.read (new File ("assets/playergn.png"));
            assert playergn.getHeight() == GameGUI.TILE_HEIGHT &&
                    playergn.getWidth() == GameGUI.TILE_WIDTH;
            potion= ImageIO.read (new File ("assets/potion.png"));
            assert potion.getHeight() == GameGUI.TILE_HEIGHT &&
                    potion.getWidth() == GameGUI.TILE_WIDTH;
            medkit= ImageIO.read (new File ("assets/medkit.png"));
            assert medkit.getHeight() == GameGUI.TILE_HEIGHT &&
                    medkit.getWidth() == GameGUI.TILE_WIDTH;
        } catch (IOException e) {
            System.out.println("Exception loading images: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }
    

    public void update(TileType[][] t, Entity player, Entity[] mon) {
        currentTiles = t;
        currentPlayer = player;
        currentMonsters = mon;
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawDungeon(g);
    }

    //Here are the graphics used to draw dungeons ( tile types mostly)
    private void drawDungeon(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (currentTiles != null) {
            for (int i = 0; i < currentTiles.length; i++) {
                for (int j = 0; j < currentTiles[i].length; j++) {
                    if (currentTiles[i][j] != null) {   //checks a tile exists
                        switch (currentTiles[i][j]) {
                            case FLOOR:
                                g2.drawImage(floor, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case WALL:
                                g2.drawImage(wall, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case STAIRS:
                                g2.drawImage(stairs, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case CHEST:
                                  g2.drawImage(chest, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case BROKENWALL:
                                  g2.drawImage(brokenwall, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case TELEPORT:
                                  g2.drawImage(teleport, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case RIVER:
                                  g2.drawImage(river, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case BRIDGE:
                                  g2.drawImage(bridge, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case CHESTOPEN:
                                  g2.drawImage(chestopen, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case POTION:
                                  g2.drawImage(potion, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            case MEDKIT:
                                  g2.drawImage(medkit, i * GameGUI.TILE_WIDTH, j * GameGUI.TILE_HEIGHT, null);
                                break;
                            
                        }
                    }
                }
            }
        }
        if (currentMonsters != null)
            for(Entity mon : currentMonsters)
                if (mon != null) {
                    g2.drawImage(monster, mon.getX() * GameGUI.TILE_WIDTH, mon.getY() * GameGUI.TILE_HEIGHT, null);
                    drawHealthBar(g2, mon);
                }
        //I made here a few changes. It is based on the "eq" String from GameEngine and it is used to draw a player whenever he gets a new item)
        if (currentPlayer != null) {
            if("nothing".equals(GameEngine.eq)){
            g2.drawImage(player, currentPlayer.getX() * GameGUI.TILE_WIDTH, currentPlayer.getY() * GameGUI.TILE_HEIGHT, null);
            drawHealthBar(g2, currentPlayer);
            }
            if("gg".equals(GameEngine.eq)){
            g2.drawImage(playergg, currentPlayer.getX() * GameGUI.TILE_WIDTH, currentPlayer.getY() * GameGUI.TILE_HEIGHT, null);
            drawHealthBar(g2, currentPlayer);
            }
            
            if("ng".equals(GameEngine.eq)){
            g2.drawImage(playerng, currentPlayer.getX() * GameGUI.TILE_WIDTH, currentPlayer.getY() * GameGUI.TILE_HEIGHT, null);
            drawHealthBar(g2, currentPlayer);
            }
            if("gn".equals(GameEngine.eq)){
            g2.drawImage(playergn, currentPlayer.getX() * GameGUI.TILE_WIDTH, currentPlayer.getY() * GameGUI.TILE_HEIGHT, null);
            drawHealthBar(g2, currentPlayer);
            }
            
        }
    }
    
    private void drawHealthBar(Graphics2D g2, Entity e) {
        double remainingHealth = (double)e.getHealth() / (double)e.getMaxHealth();
        g2.setColor(Color.RED);
        g2.fill(new Rectangle2D.Double(e.getX() * GameGUI.TILE_WIDTH, e.getY() * GameGUI.TILE_HEIGHT + 29, GameGUI.TILE_WIDTH, GameGUI.HEALTH_BAR_HEIGHT));
        g2.setColor(Color.GREEN);
        g2.fill(new Rectangle2D.Double(e.getX() * GameGUI.TILE_WIDTH, e.getY() * GameGUI.TILE_HEIGHT + 29, GameGUI.TILE_WIDTH * remainingHealth, GameGUI.HEALTH_BAR_HEIGHT));
    }
}
