package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class EnemyBullet {
    private Sprite sprite;
    private Vector2 direction;

    public EnemyBullet(Texture texture, float x, float y, Vector2 direction) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(15, 30);
        this.sprite.setPosition(x, y);
        this.direction = direction;
    }

    public void update(float deltaTime, float speed) {
        sprite.translate(direction.x * speed * deltaTime, direction.y * speed * deltaTime);
    }

    public boolean isOffScreen(float worldWidth) {
        return sprite.getY() < -sprite.getHeight() ||
            sprite.getX() < -sprite.getWidth() ||
            sprite.getX() > worldWidth;
    }

    // Getters
    public Sprite getSprite() { return sprite; }
    public Rectangle getBounds() { return sprite.getBoundingRectangle(); }
}
