package senai.projeto.gabriel;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Assets {
    private final AssetManager manager;
    
    // Texturas
    public static final String BACKGROUND = "tema.png";
    public Texture whitePixel;
    public static final String PLAYER = "nave.png";
    public static final String ENEMY = "inimigo.png";
    public static final String BULLET = "tiro.png";
    public static final String ENEMY_BULLET = "tiro_inimigo.png";
    public static final String EXPLOSION = "explosao.png";
    public static final String BULLET_SPECIAL = "tiro_fodao.png";
    
    // Sons
    public static final String SHOOT_SOUND = "tiro.mp3";
    public static final String MUSIC = "music.mp3";
    
    // UI
    public com.badlogic.gdx.scenes.scene2d.ui.Skin skin;

    public Assets() {
        manager = new AssetManager();
    }

    public void load() {
        manager.load(BACKGROUND, Texture.class);
        manager.load(PLAYER, Texture.class);
        manager.load(ENEMY, Texture.class);
        manager.load(BULLET, Texture.class);
        manager.load(ENEMY_BULLET, Texture.class);
        manager.load(EXPLOSION, Texture.class);
        manager.load(BULLET_SPECIAL, Texture.class);
        
        manager.load(SHOOT_SOUND, Sound.class);
        manager.load(MUSIC, Music.class);
        
        // Scene2D Skin (Loading synchronously after other assets or managed separately)
        // For simplicity, we'll load it via the AssetManager if possible, but Skins usually need an atlas.
        manager.load("ui/uiskin.json", com.badlogic.gdx.scenes.scene2d.ui.Skin.class);

        // Programmatic texture
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();
    }

    public boolean update() {
        boolean done = manager.update();
        if (done && skin == null) {
            skin = manager.get("ui/uiskin.json", com.badlogic.gdx.scenes.scene2d.ui.Skin.class);
        }
        return done;
    }

    public void finishLoading() {
        manager.finishLoading();
        if (skin == null && manager.isLoaded("ui/uiskin.json")) {
            skin = manager.get("ui/uiskin.json", com.badlogic.gdx.scenes.scene2d.ui.Skin.class);
        }
    }

    public <T> T get(String fileName, Class<T> type) {
        return manager.get(fileName, type);
    }

    public void dispose() {
        manager.dispose();
        if (whitePixel != null) whitePixel.dispose();
    }
}
