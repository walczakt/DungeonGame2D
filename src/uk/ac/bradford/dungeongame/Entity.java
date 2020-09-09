package uk.ac.bradford.dungeongame;


public class Entity {
    

    //I believe that this class remains unchanged.
    public enum EntityType { PLAYER, MONSTER }
    

    private int maxHealth;
    

    private int health;
    

    private int xPos;
    

    private int yPos;

    private EntityType type;
    

    public Entity(int maxHealth, int x, int y, EntityType type) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        xPos = x;
        yPos = y;
        this.type = type;
    }
    

    public int getX() {
        return xPos;
    }
    

    public int getY() {
        return yPos;
    }
    

    public void setPosition (int x, int y) {
        xPos = x;
        yPos = y;
    }
    

    public void changeHealth(int change) {
        health += change;
        if (health > maxHealth)
            health = maxHealth;
    }
    
    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
    

    public EntityType getType() {
        return type;
    }
}
