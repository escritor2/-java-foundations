package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ParticleManager {
    private final Array<Particle> activeParticles = new Array<>();
    private final Pool<Particle> particlePool = new Pool<Particle>() {
        @Override
        protected Particle newObject() {
            return new Particle();
        }
    };
    private final Assets assets;

    public ParticleManager(Assets assets) {
        this.assets = assets;
    }

    public void spawnExplosion(float x, float y, Color color) {
        for (int i = 0; i < 20; i++) {
            Particle p = particlePool.obtain();
            p.position.set(x, y);
            float angle = MathUtils.random(0, MathUtils.PI2);
            float speed = MathUtils.random(50, 200);
            p.velocity.set(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed);
            p.color.set(color);
            p.maxLifetime = MathUtils.random(0.5f, 1.0f);
            p.size = MathUtils.random(4, 8);
            activeParticles.add(p);
        }
    }

    public void spawnEngineTrail(float x, float y) {
        Particle p = particlePool.obtain();
        p.position.set(x + MathUtils.random(-5, 5), y);
        p.velocity.set(MathUtils.random(-20, 20), -MathUtils.random(100, 200));
        p.color.set(1, 0.5f, 0.2f, 0.8f); // Orange-ish
        p.maxLifetime = MathUtils.random(0.2f, 0.4f);
        p.size = MathUtils.random(2, 5);
        activeParticles.add(p);
    }

    public void update(float delta) {
        for (int i = activeParticles.size - 1; i >= 0; i--) {
            Particle p = activeParticles.get(i);
            p.update(delta);
            if (p.isDead()) {
                activeParticles.removeIndex(i);
                particlePool.free(p);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (Particle p : activeParticles) {
            batch.setColor(p.color.r, p.color.g, p.color.b, 1.0f - (p.lifetime / p.maxLifetime));
            batch.draw(assets.whitePixel, p.position.x, p.position.y, p.size, p.size);
        }
        batch.setColor(Color.WHITE);
    }
    
    public void clear() {
        particlePool.freeAll(activeParticles);
        activeParticles.clear();
    }
}
