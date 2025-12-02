package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class PowerUp {
    private Sprite sprite;
    private final float speed = 100f; // Velocidade de queda
    private String type;

    public PowerUp(Texture texture, float x, float y, String type) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(30, 30);
        this.sprite.setPosition(x, y);
        this.type = type; // Ex: "triple_shot"
    }

    public void update(float deltaTime) {
        sprite.translateY(-speed * deltaTime); // Move para baixo
    }

    public boolean isOffScreen() {
        return sprite.getY() + sprite.getHeight() < 0;
    }

    // Getters
    public Sprite getSprite() { return sprite; }
    public Rectangle getBounds() { return sprite.getBoundingRectangle(); }
    public String getType() { return type; }
}
