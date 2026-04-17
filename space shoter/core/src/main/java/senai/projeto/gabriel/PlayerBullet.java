package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

public class PlayerBullet implements Pool.Poolable {
    private final Sprite sprite;
    private int durability;
    private Enums.BulletType type;
    private float lifeTime;

    public PlayerBullet(Texture texture) {
        this.sprite = new Sprite(texture);
    }

    public void init(Enums.BulletType type, float x, float y) {
        this.type = type;
        this.lifeTime = (type == Enums.BulletType.SPECIAL) ? 3f : 10f;
        this.durability = (type == Enums.BulletType.SPECIAL) ? 100 : 2;

        int width, height;
        if (this.type == Enums.BulletType.SPECIAL) {
            width = 2;
            height = 10000;
        } else {
            width = 10;
            height = 30;
        }
        this.sprite.setSize(width, height);
        this.sprite.setPosition(x, y);
    }

    public void update(float deltaTime, float speed, Player player) {
        if (this.type == Enums.BulletType.SPECIAL) {
            this.lifeTime -= deltaTime;
            sprite.setPosition(player.getSprite().getX() + player.getSprite().getWidth() / 2, player.getSprite().getY() + 15);
        } else {
            sprite.translateY(speed * deltaTime);
        }
    }

    public boolean isOffScreen() {
        return sprite.getY() > Constants.WORLD_HEIGHT;
    }

    public boolean hitEnemy() {
        durability--;
        return durability <= 0;
    }

    @Override
    public void reset() {
        this.durability = 0;
        this.lifeTime = 0;
    }

    public Enums.BulletType getType() { return type; }
    public float getLifeTime() { return lifeTime; }
    public Sprite getSprite() { return sprite; }
    public Rectangle getBounds() { return sprite.getBoundingRectangle(); }
}
