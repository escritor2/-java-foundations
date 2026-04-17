package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class PowerUp implements Pool.Poolable {
    private final Sprite sprite;
    private Enums.PowerUpType type;

    public PowerUp(Texture texture) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(30, 30);
    }

    public void init(Enums.PowerUpType type, float x, float y) {
        this.type = type;
        this.sprite.setPosition(x, y);
    }

    public void update(float deltaTime) {
        sprite.translateY(-100f * deltaTime);
    }

    public boolean isOffScreen() {
        return sprite.getY() + sprite.getHeight() < 0;
    }

    @Override
    public void reset() {
        this.type = null;
    }

    public Enums.PowerUpType getType() { return type; }
    public Sprite getSprite() { return sprite; }
    public Rectangle getBounds() { return sprite.getBoundingRectangle(); }
}
