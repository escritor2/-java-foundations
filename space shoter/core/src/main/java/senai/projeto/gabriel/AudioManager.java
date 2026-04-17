package senai.projeto.gabriel;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private final Music music;
    private final Sound shootSound;

    public AudioManager(Assets assets) {
        this.shootSound = assets.get(Assets.SHOOT_SOUND, Sound.class);
        this.music = assets.get(Assets.MUSIC, Music.class);
        
        if (music != null) {
            music.setLooping(true);
            music.setVolume(0.5f);
        }
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
        // Assets are managed by the main Assets class, so we don't dispose them here
    }
}
