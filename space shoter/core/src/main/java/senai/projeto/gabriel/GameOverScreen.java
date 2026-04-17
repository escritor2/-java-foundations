package senai.projeto.gabriel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOverScreen extends ScreenAdapter {
    private final Main game;
    private final FitViewport viewport;
    private final BitmapFont font;
    private final int score;

    public GameOverScreen(Main game, int score) {
        this.game = game;
        this.score = score;
        this.viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        this.font = new BitmapFont();
        this.font.getData().setScale(1.5f);
        
        if (game.highScoreManager.isHighScore(score)) {
            game.highScoreManager.addScore(score);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.justTouched()) {
            game.setScreen(new MenuScreen(game));
        }

        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        
        game.batch.draw(game.assets.get(Assets.BACKGROUND, Texture.class), 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        
        font.setColor(1, 0, 0, 1);
        font.draw(game.batch, "GAME OVER!", Constants.WORLD_WIDTH / 2 - 80, Constants.WORLD_HEIGHT / 2 + 100);
        
        font.setColor(1, 1, 1, 1);
        font.draw(game.batch, "Final Score: " + score, Constants.WORLD_WIDTH / 2 - 80, Constants.WORLD_HEIGHT / 2 + 60);
        
        font.setColor(1, 1, 0, 1);
        font.draw(game.batch, "TOP SCORES:", Constants.WORLD_WIDTH / 2 - 80, Constants.WORLD_HEIGHT / 2 + 20);
        
        Array<Integer> scores = game.highScoreManager.getHighScores();
        for (int i = 0; i < Math.min(5, scores.size); i++) {
            font.draw(game.batch, (i + 1) + ". " + scores.get(i), Constants.WORLD_WIDTH / 2 - 40, Constants.WORLD_HEIGHT / 2 - 20 - (i * 30));
        }
        
        font.setColor(0, 1, 1, 1);
        font.draw(game.batch, "PRESS ENTER TO RESTART", Constants.WORLD_WIDTH / 2 - 150, 50);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
