package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    private final Sprite sprite;
    private int lives;
    private float invulnerabilityTimer;
    private boolean gameOver;
    private boolean canAttack;

    private boolean tripleShotActive;
    private float tripleShotTimer;
    
    // Phase 2: Progression & Defense
    private int weaponLevel;
    private int shieldHealth;

    public Player(Texture texture, float x, float y) {
        this.sprite = new Sprite(texture);
        this.sprite.setSize(Constants.PLAYER_SIZE, Constants.PLAYER_SIZE);
        this.sprite.setPosition(x, y);
        this.lives = Constants.PLAYER_MAX_LIVES;
        this.invulnerabilityTimer = 0;
        this.gameOver = false;
        this.canAttack = true;
        this.weaponLevel = 1;
        this.shieldHealth = 0;
    }

    public void update(float deltaTime) {
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= deltaTime;
        }

        if (tripleShotActive) {
            tripleShotTimer -= deltaTime;
            if (tripleShotTimer <= 0) {
                tripleShotActive = false;
            }
        }

        sprite.setX(MathUtils.clamp(sprite.getX(), 0, Constants.WORLD_WIDTH - sprite.getWidth()));
        sprite.setY(MathUtils.clamp(sprite.getY(), 0, Constants.PLAYER_MAX_Y));
    }

    public void move(float deltaX, float deltaY) {
        sprite.translateX(deltaX);
        sprite.translateY(deltaY);
        updateBounds();
    }

    private void updateBounds() {
        sprite.setX(MathUtils.clamp(sprite.getX(), 0, Constants.WORLD_WIDTH - sprite.getWidth()));
        sprite.setY(MathUtils.clamp(sprite.getY(), 0, Constants.PLAYER_MAX_Y));
    }

    public void setPosition(float x, float y) {
        sprite.setX(x);
        sprite.setY(y);
        updateBounds();
    }

    public void takeHit() {
        if (isInvulnerable()) return;
        
        if (shieldHealth > 0) {
            shieldHealth--;
            invulnerabilityTimer = 0.5f; // Short invulnerability on shield hit
            return;
        }

        lives--;
        
        // Classic arcade penalty: decrease weapon level on damage
        if (weaponLevel > 1) weaponLevel--;
        
        if (lives <= 0) {
            gameOver = true;
        } else {
            invulnerabilityTimer = Constants.PLAYER_INVULNERABILITY_TIME;
        }
    }

    public void activateTripleShot() {
        tripleShotActive = true;
        tripleShotTimer = Constants.TRIPLE_SHOT_DURATION;
    }
    
    public void upgradeWeapon() {
        if (weaponLevel < 4) weaponLevel++;
    }
    
    public void activateShield() {
        shieldHealth = 3; // 3 hits shield
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public boolean isTripleShotActive() { return tripleShotActive; }
    public boolean canAttack() { return canAttack; }
    public Sprite getSprite() { return sprite; }
    public Rectangle getBounds() { return sprite.getBoundingRectangle(); }
    public int getLives() { return lives; }
    public int getWeaponLevel() { return weaponLevel; }
    public int getShieldHealth() { return shieldHealth; }
    public boolean isShieldActive() { return shieldHealth > 0; }
    public boolean isGameOver() { return gameOver; }
    public boolean isInvulnerable() { return invulnerabilityTimer > 0; }
    public boolean shouldBlink() {
        return isInvulnerable() && ((int)(invulnerabilityTimer * 10) % 2 == 0);
    }
}
