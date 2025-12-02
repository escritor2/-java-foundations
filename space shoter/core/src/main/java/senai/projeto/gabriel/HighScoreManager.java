package senai.projeto.gabriel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

public class HighScoreManager {
    private static final String PREFS_NAME = "SpaceShooterHighScores";
    private static final int MAX_SCORES = 3;

    private Preferences prefs;

    public HighScoreManager() {
        this.prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    public Array<Integer> getHighScores() {
        Array<Integer> scores = new Array<>();
        for (int i = 0; i < MAX_SCORES; i++) {
            scores.add(prefs.getInteger("score_" + i, 0));
        }
        scores.sort();
        scores.reverse();
        return scores;
    }

    public boolean isHighScore(int newScore) {
        Array<Integer> scores = getHighScores();
        return scores.size < MAX_SCORES || newScore > scores.get(scores.size - 1);
    }

    public void addScore(int newScore) {
        Array<Integer> scores = getHighScores();
        scores.reverse(); // voltar para crescente
        scores.add(newScore);
        scores.sort(); // crescente de novo


        while (scores.size > MAX_SCORES) {
            scores.removeIndex(0);
        }

        for (int i = 0; i < MAX_SCORES && i < scores.size; i++) {
            prefs.putInteger("score_" + i, scores.get(i));
        }
        prefs.flush();
    }
}
