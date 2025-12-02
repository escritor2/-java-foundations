package senai.projeto.gabriel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private Music music;
    private Sound shootSound;

    public void load() {
        shootSound = Gdx.audio.newSound(Gdx.files.internal("tiro.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(0.5f);
    }

    public void playShoot() {
        if (shootSound != null) shootSound.play(1.0f);
    }

    public void playMusic() {
        if (music != null && !music.isPlaying()) {
            music.play();
        }
    }

    public void stopMusic() {
        if (music != null) music.stop();
    }

    public void dispose() {
        if (shootSound != null) shootSound.dispose();
        if (music != null) music.dispose();
    }
}
