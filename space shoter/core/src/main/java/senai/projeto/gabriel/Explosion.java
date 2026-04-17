package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Explosion {
    private final Sprite sprite;
    private float lifetime;
    private final float maxLifetime = 0.5f;

    public Explosion(Texture texture, float x, float y) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(80, 80);
        this.sprite.setPosition(x, y);
        this.lifetime = 0;
    }

    public void update(float deltaTime) {
        lifetime += deltaTime;
        float progress = lifetime / maxLifetime;
        float scale = 1.0f + progress;
        sprite.setScale(scale);
        sprite.setAlpha(1.0f - progress);
    }

    public boolean isFinished() {
        return lifetime >= maxLifetime;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
