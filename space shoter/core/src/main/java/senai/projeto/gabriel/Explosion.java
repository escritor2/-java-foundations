package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Explosion {
    private Sprite sprite;
    private float lifetime = 0f;
    private final float maxLifetime = 0.5f;

    public Explosion(Texture texture, float x, float y) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(80, 80);
        this.sprite.setPosition(x, y);
    }

    public void update(float deltaTime) {
        lifetime += deltaTime;
    }

    public boolean isFinished() {
        return lifetime >= maxLifetime;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
