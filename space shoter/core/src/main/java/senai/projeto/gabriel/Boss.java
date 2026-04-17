package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class Boss implements Pool.Poolable {
    private final Sprite sprite;
    private int health;
    private int maxHealth;
    private float stateTime;
    private boolean destroyed;
    private float shootTimer;

    public Boss(Texture texture) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(200, 150); // Big boss
    }

    public void init(int health) {
        this.maxHealth = health;
        this.health = health;
        this.sprite.setPosition(Constants.WORLD_WIDTH / 2 - 100, Constants.WORLD_HEIGHT + 200);
        this.destroyed = false;
        this.stateTime = 0;
        this.shootTimer = 0;
    }

    public void update(float delta) {
        stateTime += delta;
        shootTimer += delta;

        // Entry movement
        if (sprite.getY() > Constants.WORLD_HEIGHT - 200) {
            sprite.translateY(-50 * delta);
        } else {
            // Sinusoidal movement
            float x = (Constants.WORLD_WIDTH / 2 - 100) + MathUtils.sin(stateTime) * 150;
            sprite.setX(x);
        }
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health <= 0) destroyed = true;
    }

    @Override
    public void reset() {
        destroyed = false;
    }

    public boolean isDestroyed() { return destroyed; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public Sprite getSprite() { return sprite; }
    public Rectangle getBounds() { return sprite.getBoundingRectangle(); }
    public float getX() { return sprite.getX(); }
    public float getY() { return sprite.getY(); }
    public boolean canShoot() {
        if (shootTimer >= 1.0f) {
            shootTimer = 0;
            return true;
        }
        return false;
    }
}
