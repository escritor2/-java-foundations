package senai.projeto.gabriel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

public class HighScoreManager {
    private static final String PREFS_NAME = "SpaceShooterHighScores";
    private static final int MAX_SCORES = 5;

    private final Preferences prefs;

    public HighScoreManager() {
        this.prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    public Array<Integer> getHighScores() {
        Array<Integer> scores = new Array<>();
        for (int i = 0; i < MAX_SCORES; i++) {
            int val = prefs.getInteger("score_" + i, 0);
            if (val > 0) scores.add(val);
        }
        scores.sort();
        scores.reverse();
        return scores;
    }

    public boolean isHighScore(int newScore) {
        if (newScore <= 0) return false;
        Array<Integer> scores = getHighScores();
        return scores.size < MAX_SCORES || newScore > scores.get(scores.size - 1);
    }

    public void addScore(int newScore) {
        Array<Integer> scores = getHighScores();
        scores.add(newScore);
        scores.sort();
        scores.reverse();

        while (scores.size > MAX_SCORES) {
            scores.removeIndex(scores.size - 1);
        }

        // We need to clear and re-save to keep order consistency in prefs
        for (int i = 0; i < MAX_SCORES; i++) {
            if (i < scores.size) {
                prefs.putInteger("score_" + i, scores.get(i));
            } else {
                prefs.remove("score_" + i);
            }
        }
        prefs.flush();
    }
}
