package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Particle implements Pool.Poolable {
    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();
    public final Color color = new Color();
    public float lifetime;
    public float maxLifetime;
    public float size;

    @Override
    public void reset() {
        position.set(0, 0);
        velocity.set(0, 0);
        color.set(1, 1, 1, 1);
        lifetime = 0;
        maxLifetime = 0;
        size = 0;
    }

    public void update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
        lifetime += delta;
    }

    public boolean isDead() {
        return lifetime >= maxLifetime;
    }
}
