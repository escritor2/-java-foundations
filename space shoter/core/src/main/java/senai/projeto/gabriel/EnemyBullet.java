package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class EnemyBullet implements Pool.Poolable {
    private final Sprite sprite;
    private final Vector2 direction;

    public EnemyBullet(Texture texture) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(15, 30);
        this.direction = new Vector2();
    }

    public void init(float x, float y, Vector2 dir) {
        this.sprite.setPosition(x, y);
        this.direction.set(dir);
    }

    public void update(float deltaTime, float speed) {
        sprite.translate(direction.x * speed * deltaTime, direction.y * speed * deltaTime);
    }

    public boolean isOffScreen() {
        return sprite.getY() < -sprite.getHeight() ||
               sprite.getX() < -sprite.getWidth() ||
               sprite.getX() > Constants.WORLD_WIDTH;
    }

    @Override
    public void reset() {
        direction.set(0, 0);
    }

    public Sprite getSprite() { return sprite; }
    public Rectangle getBounds() { return sprite.getBoundingRectangle(); }
}
