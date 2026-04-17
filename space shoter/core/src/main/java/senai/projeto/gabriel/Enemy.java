package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class Enemy implements Pool.Poolable {
    private final Sprite sprite;
    private int health;

    public Enemy(Texture texture) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(50, 50);
    }

    public void init(float x, float y) {
        this.sprite.setPosition(x, y);
        this.health = 2; // Basic enemies have 2 health
    }

    public void update(float deltaTime, float speed) {
        sprite.translateY(-speed * deltaTime);
    }

    public boolean isOffScreen() {
        return sprite.getY() + sprite.getHeight() < 0;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    @Override
    public void reset() {
        this.health = 2;
    }

    public Sprite getSprite() { return sprite; }
    public Rectangle getBounds() { return sprite.getBoundingRectangle(); }
    public float getX() { return sprite.getX(); }
    public float getY() { return sprite.getY(); }
    public float getWidth() { return sprite.getWidth(); }
    public float getHeight() { return sprite.getHeight(); }
}
