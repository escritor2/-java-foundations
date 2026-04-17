package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BackgroundLayer {
    private final Texture texture;
    private final float scrollSpeed;
    private float y1, y2;

    public BackgroundLayer(Texture texture, float scrollSpeed) {
        this.texture = texture;
        this.scrollSpeed = scrollSpeed;
        this.y1 = 0;
        this.y2 = Constants.WORLD_HEIGHT;
    }

    public void update(float delta) {
        y1 -= scrollSpeed * delta;
        y2 -= scrollSpeed * delta;

        if (y1 <= -Constants.WORLD_HEIGHT) y1 = y2 + Constants.WORLD_HEIGHT;
        if (y2 <= -Constants.WORLD_HEIGHT) y2 = y1 + Constants.WORLD_HEIGHT;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, 0, y1, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        batch.draw(texture, 0, y2, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
    }
}
