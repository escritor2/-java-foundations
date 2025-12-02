package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import senai.projeto.gabriel.Player;

public class PlayerBullet {
    private Sprite sprite;
    private int durability;
    private String type;
    private float lifeTime;


    public PlayerBullet(Texture texture, String type, float x, float y, int durability, float lifeTime) {
        this.sprite = new Sprite(texture);
        this.type = type;
        this.lifeTime = lifeTime;
        int width, height;
        if(this.type.equals("especial")){
            width = 2;
            height = 10000;
        }else{
            width = 10;
            height = 30;
        }
        this.sprite.setSize(width, height);
        this.sprite.setPosition(x, y);
        this.durability = durability;


    }

    public void update(float deltaTime, float speed, Player player) {
        sprite.translateY(speed * deltaTime);
        if(this.type.equals("especial"))this.lifeTime -= deltaTime;
        if(this.type.equals("especial")){
            sprite.setPosition(player.getSprite().getX() + player.getSprite().getWidth() / 2, player.getSprite().getY() + 15);
        }
    }

    public float getLifeTime() {return this.lifeTime;}
    public boolean isOffScreen(float screenHeight) {
        return sprite.getY() > screenHeight;
    }

    public boolean hitEnemy() {
        durability--;
        return durability <= 0;
    }

    public String getType() {
        return type;
    }

    // Getters
    public Sprite getSprite() { return sprite; }
    public Rectangle getBounds() { return sprite.getBoundingRectangle(); }
}
