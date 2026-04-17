package senai.projeto.gabriel;

import com.badlogic.gdx.math.MathUtils;

public class WaveManager {
    private int currentWave;
    private int enemiesToSpawn;
    private int enemiesSpawned;
    private float spawnTimer;
    private float spawnInterval;
    
    private float waveTimer;
    private final float WAVE_COUNTDOWN_TIME = 5.0f;
    private boolean isCountingDown;
    
    public WaveManager() {
        this.currentWave = 0;
        startNextWave();
    }

    public void startNextWave() {
        currentWave++;
        enemiesSpawned = 0;
        enemiesToSpawn = 5 + (currentWave * 2);
        spawnInterval = Math.max(0.5f, 2.0f - (currentWave * 0.1f));
        spawnTimer = 0;
        isCountingDown = false;
        waveTimer = 0;
    }

    public void update(float delta, EntityManager entityManager, float enemySpeed) {
        if (isCountingDown) {
            waveTimer -= delta;
            if (waveTimer <= 0) {
                startNextWave();
            }
            return;
        }

        // Only spawn if we still have enemies to spawn for this wave
        if (enemiesSpawned < enemiesToSpawn) {
            spawnTimer += delta;
            if (spawnTimer >= spawnInterval) {
                spawnTimer = 0;
                float x = MathUtils.random(0, Constants.WORLD_WIDTH - 50);
                entityManager.spawnEnemy(x, Constants.WORLD_HEIGHT);
                enemiesSpawned++;
            }
        } else if (!entityManager.hasEnemies()) {
            // Wave cleared! Start countdown for next wave
            isCountingDown = true;
            waveTimer = WAVE_COUNTDOWN_TIME;
        }
    }

    public int getCurrentWave() { return currentWave; }
    public boolean isCountingDown() { return isCountingDown; }
    public float getCountdownTime() { return waveTimer; }
    public int getEnemiesRemainingToSpawn() { return enemiesToSpawn - enemiesSpawned; }
}
