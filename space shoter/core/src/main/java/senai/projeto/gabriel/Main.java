package senai.projeto.gabriel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch batch;
    public Assets assets;
    public HighScoreManager highScoreManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new Assets();
        assets.load();
        assets.finishLoading(); // Pre-loading assets for now, can be improved with a LoadingScreen later
        
        highScoreManager = new HighScoreManager();
        
        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
        if (getScreen() != null) getScreen().dispose();
    }
}
