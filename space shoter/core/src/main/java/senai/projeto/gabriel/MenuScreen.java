package senai.projeto.gabriel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MenuScreen extends ScreenAdapter {
    private final Main game;
    private final Stage stage;
    private final FitViewport viewport;
    private final Texture background;

    public MenuScreen(final Main game) {
        this.game = game;
        this.viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        this.stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);
        this.background = game.assets.get(Assets.BACKGROUND, Texture.class);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Title
        Label titleLabel = new Label("SPACE SHOOTER ULTIMATE", game.assets.skin, "subtitle");
        table.add(titleLabel).padBottom(50).row();

        // Buttons
        TextButton playButton = new TextButton("START MISSION", game.assets.skin);
        TextButton exitButton = new TextButton("EXIT GAME", game.assets.skin);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(playButton).fillX().uniformX().padBottom(10).row();
        table.add(exitButton).fillX().uniformX().padBottom(30).row();

        // High Scores Display
        table.add(new Label("TOP PILOTS:", game.assets.skin, "default")).padBottom(5).row();
        com.badlogic.gdx.utils.Array<Integer> scores = game.highScoreManager.getHighScores();
        for (int i = 0; i < scores.size; i++) {
            table.add(new Label((i + 1) + ". " + scores.get(i), game.assets.skin, "default")).row();
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
