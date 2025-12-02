package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private Sprite sprite;
    private int lives = 3;
    private float invulnerabilityTimer = 0f;
    private boolean gameOver = false;
    private boolean canAttack = true;

    // NOVO: Variáveis para o Power-up de Tiro Triplo
    private boolean tripleShotActive = false;
    private float tripleShotTimer = 0f;
    private final float MAX_TRIPLE_SHOT_TIME = 8.0f; // Duração de 8 segundos

    public Player(Texture texture, float x, float y) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(60, 60);
        this.sprite.setPosition(x, y);
    }

    public void update(float deltaTime, float worldWidth, float maxHeight) {
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= deltaTime;
        }

        // NOVO: Decrementa o timer do tiro triplo
        if (tripleShotActive) {
            tripleShotTimer -= deltaTime;
            if (tripleShotTimer <= 0) {
                tripleShotActive = false;
            }
        }

        sprite.setX(MathUtils.clamp(sprite.getX(), 0, worldWidth - sprite.getWidth()));
        sprite.setY(MathUtils.clamp(sprite.getY(), 0, maxHeight));
    }

    public void move(float deltaX, float deltaY, float worldWidth, float maxHeight) {
        sprite.translateX(deltaX);
        sprite.translateY(deltaY);
        sprite.setX(MathUtils.clamp(sprite.getX(), 0, worldWidth - sprite.getWidth()));
        sprite.setY(MathUtils.clamp(sprite.getY(), 0, maxHeight));
    }

    public void setPosition(float x, float y, float worldWidth) {
        sprite.setX(MathUtils.clamp(x, 0, worldWidth - sprite.getWidth()));
        sprite.setY(y);
    }
    public void setCanAttack(boolean canAttack){
        this.canAttack = canAttack;
    }

    public void takeHit() {
        lives--;
        if (lives <= 0) {
            gameOver = true;
        } else {
            invulnerabilityTimer = 1.5f;
        }
    }

    // NOVO: Método para ativar o Power-up
    public void activateTripleShot() {
        tripleShotActive = true;
        tripleShotTimer = MAX_TRIPLE_SHOT_TIME;
    }

    // NOVO: Getter
    public boolean isTripleShotActive() {
        return tripleShotActive;
    }
    public boolean canAttack() {return canAttack;}
    public Sprite getSprite() { return sprite; }
    public Rectangle getBounds() { return sprite.getBoundingRectangle(); }
    public int getLives() { return lives; }
    public boolean isGameOver() { return gameOver; }
    public boolean isInvulnerable() { return invulnerabilityTimer > 0; }
    public boolean shouldBlink() {
        return isInvulnerable() && ((int)(invulnerabilityTimer * 10) % 2 == 0);
    }
}
