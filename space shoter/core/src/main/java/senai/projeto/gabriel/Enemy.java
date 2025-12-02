package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    private Sprite sprite;
    private int health; // NOVO: Campo de vida

    public Enemy(Texture texture, float x, float y) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(50, 50);
        this.sprite.setPosition(x, y);
        this.health = 2; // NOVO: Inimigos básicos têm 2 de vida
    }

    public void update(float deltaTime, float speed) {
        sprite.translateY(-speed * deltaTime);
    }

    public boolean isOffScreen() {
        return sprite.getY() + sprite.getHeight() < 0;
    }

    // NOVO: Método para receber dano
    public void takeDamage(int damage) {
        this.health -= damage;
    }

    // NOVO: Método para checar se o inimigo foi destruído
    public boolean isDestroyed() {
        return health <= 0;
    }

    public Sprite getSprite() { return sprite; }
    public Rectangle getBounds() { return sprite.getBoundingRectangle(); }
    public float getX() { return sprite.getX(); }
    public float getY() { return sprite.getY(); }
    public float getWidth() { return sprite.getWidth(); }
    public float getHeight() { return sprite.getHeight(); }
}
