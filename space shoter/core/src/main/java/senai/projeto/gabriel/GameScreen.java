package senai.projeto.gabriel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameScreen extends ScreenAdapter {
    private final Main game;
    private final FitViewport viewport;
    
    private final Player player;
    private final EntityManager entityManager;
    private final ParticleManager particleManager;
    private final WaveManager waveManager;
    private final AudioManager audioManager;
    
    private int score;
    private int lastBossScore;
    private int spBar;
    private float bulletTimer;
    private float enemySpawnTimer;
    private float enemyShootTimer;
    
    // Difficulty
    private float enemySpeed = Constants.BASE_ENEMY_SPEED;
    private float spawnInterval = Constants.BASE_SPAWN_INTERVAL;
    private float difficultyTimer;
    
    // Combo
    private int combo;
    private float comboTimer;
    private final float MAX_COMBO_TIME = 2.0f;
    
    // Parallax
    private final BackgroundLayer background;
    
    // Effects
    private float shakeTimer;
    private float flashTimer;
    private final Vector2 shakeOffset = new Vector2();
    
    // UI
    private final BitmapFont font;

    public GameScreen(Main game) {
        this.game = game;
        this.viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        
        this.player = new Player(game.assets.get(Assets.PLAYER, Texture.class), Constants.WORLD_WIDTH / 2 - 30, 50);
        this.particleManager = new ParticleManager(game.assets);
        this.entityManager = new EntityManager(game, player, particleManager);
        this.waveManager = new WaveManager();
        this.audioManager = new AudioManager(game.assets);
        this.audioManager.playMusic();
        
        this.background = new BackgroundLayer(game.assets.get(Assets.BACKGROUND, Texture.class), 50f);
        this.shakeTimer = 0;
        this.flashTimer = 0;
        
        this.score = 0;
        this.lastBossScore = 0;
        this.spBar = 0;
        this.combo = 0;
        this.comboTimer = 0;
        this.bulletTimer = 0;
        this.enemySpawnTimer = 0;
        this.enemyShootTimer = 0;
        this.difficultyTimer = 0;
        
        this.font = new BitmapFont();
        this.font.getData().setScale(1.5f);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        
        if (!player.isGameOver()) {
            handleInput(delta);
            update(delta);
        } else {
            game.setScreen(new GameOverScreen(game, score));
        }
        
        draw();
    }

    private void handleInput(float delta) {
        float moveDelta = Constants.PLAYER_SPEED * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) player.move(-moveDelta, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.move(moveDelta, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) player.move(0, moveDelta);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) player.move(0, -moveDelta);

        // Shooting
        bulletTimer += delta;
        float fireRate = player.isTripleShotActive() ? Constants.TRIPLE_SHOT_FIRE_RATE : Constants.NORMAL_FIRE_RATE;
        
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && bulletTimer > fireRate && player.canAttack()) {
            entityManager.spawnPlayerBullet(Enums.BulletType.NORMAL);
            audioManager.playShoot();
            bulletTimer = 0;
        }

        // Special attack
        if (Gdx.input.isKeyJustPressed(Input.Keys.N) && spBar >= Constants.SPECIAL_BULLET_SP_COST) {
            spBar -= Constants.SPECIAL_BULLET_SP_COST;
            entityManager.spawnPlayerBullet(Enums.BulletType.SPECIAL);
            player.setCanAttack(false);
        }
    }

    private void update(float delta) {
        background.update(delta);
        player.update(delta);
        particleManager.update(delta);
        
        // Boss Logic (Spawn every 500 points)
        if (score > 0 && score % 500 == 0 && score > lastBossScore && entityManager.getActiveBoss() == null) {
            lastBossScore = score;
            entityManager.spawnBoss(50 + (score / 10));
        }

        // Wave Logic (Only update if no Boss is active)
        if (entityManager.getActiveBoss() == null) {
            waveManager.update(delta, entityManager, enemySpeed);
        }

        // Engine particles
        particleManager.spawnEngineTrail(player.getSprite().getX() + player.getSprite().getWidth()/2, player.getSprite().getY());

        if (shakeTimer > 0) {
            shakeTimer -= delta;
            shakeOffset.set(MathUtils.random(-1f, 1f) * Constants.SCREEN_SHAKE_INTENSITY,
                           MathUtils.random(-1f, 1f) * Constants.SCREEN_SHAKE_INTENSITY);
        } else {
            shakeOffset.set(0, 0);
        }

        if (flashTimer > 0) flashTimer -= delta;

        if (comboTimer > 0) {
            comboTimer -= delta;
            if (comboTimer <= 0) combo = 0;
        }

        // Enemy Shooting
        enemyShootTimer += delta;
        if (enemyShootTimer >= 1.5f && entityManager.hasEnemies() && entityManager.getActiveBoss() == null) {
            enemyShootTimer = 0;
            Enemy shooter = entityManager.getRandomEnemy();
            if (shooter != null) {
                Vector2 dir = new Vector2(
                    player.getSprite().getX() + player.getSprite().getWidth() / 2,
                    player.getSprite().getY() + player.getSprite().getHeight() / 2
                ).sub(shooter.getX() + 25, shooter.getY()).nor();
                entityManager.spawnEnemyBullet(shooter.getX() + 25, shooter.getY(), dir);
            }
        }

        entityManager.update(delta, enemySpeed);
        
        // Check if player special bullet finished
        // (Actually EntityManager handles the lifetime, but we need to reset player.canAttack if needed)
        // Let's simplify and just allow attack if no special bullet exists.
    }

    public void addScore(int value) {
        combo++;
        comboTimer = MAX_COMBO_TIME;
        
        int bonus = (combo > 1) ? (combo * 2) : 0;
        this.score += value + bonus;
        
        if (spBar < Constants.MAX_SP) {
            spBar += 20 + (combo / 5);
        }
        // Shake on score (small)
        applyShake(0.1f, 2f);
    }

    public void applyShake(float duration, float intensity) {
        this.shakeTimer = duration;
    }

    public void onPlayerHit() {
        if (player.isShieldActive()) {
            applyShake(0.1f, 3f);
            this.flashTimer = 0.05f;
            particleManager.spawnExplosion(player.getSprite().getX() + player.getSprite().getWidth()/2, player.getSprite().getY() + player.getSprite().getHeight()/2, com.badlogic.gdx.graphics.Color.CYAN);
        } else {
            applyShake(0.3f, 5f);
            this.flashTimer = 0.1f;
            this.combo = 0;
            this.comboTimer = 0;
            particleManager.spawnExplosion(player.getSprite().getX() + player.getSprite().getWidth()/2, player.getSprite().getY() + player.getSprite().getHeight()/2, com.badlogic.gdx.graphics.Color.RED);
        }
    }

    private void draw() {
        viewport.getCamera().position.set(Constants.WORLD_WIDTH / 2 + shakeOffset.x, 
                                          Constants.WORLD_HEIGHT / 2 + shakeOffset.y, 0);
        viewport.getCamera().update();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        
        background.draw(game.batch);
        
        if (flashTimer > 0) {
            game.batch.setColor(1, 0, 0, 1); // Red flash for damage
        }

        if (!player.isInvulnerable() || player.shouldBlink()) {
            player.getSprite().draw(game.batch);
            
            // Shield effect
            if (player.isShieldActive()) {
                game.batch.setColor(0, 0.7f, 1, 0.5f);
                float shieldSize = player.getSprite().getWidth() * 1.5f;
                game.batch.draw(game.assets.whitePixel, 
                               player.getSprite().getX() - (shieldSize - player.getSprite().getWidth()) / 2,
                               player.getSprite().getY() - (shieldSize - player.getSprite().getHeight()) / 2,
                               shieldSize, shieldSize);
                game.batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
            }
        }
        
        game.batch.setColor(1, 1, 1, 1);
        entityManager.draw(game.batch);
        particleManager.draw(game.batch);
        
        drawUI();
        
        game.batch.end();
    }

    private void drawUI() {
        font.setColor(1, 1, 1, 1);
        font.draw(game.batch, "Score: " + score, 10, Constants.WORLD_HEIGHT - 10);
        
        // Health Bar
        game.batch.setColor(0.2f, 0.2f, 0.2f, 1); // Background
        game.batch.draw(game.assets.whitePixel, 10, Constants.WORLD_HEIGHT - 45, 100, 15);
        game.batch.setColor(1, 0, 0, 1); // Foreground
        game.batch.draw(game.assets.whitePixel, 10, Constants.WORLD_HEIGHT - 45, (player.getLives() / (float)Constants.PLAYER_MAX_LIVES) * 100, 15);
        
        // SP Bar
        game.batch.setColor(0.2f, 0.2f, 0.2f, 1);
        game.batch.draw(game.assets.whitePixel, 10, Constants.WORLD_HEIGHT - 70, 100, 15);
        game.batch.setColor(0, 0.5f, 1, 1);
        game.batch.draw(game.assets.whitePixel, 10, Constants.WORLD_HEIGHT - 70, (spBar / (float)Constants.MAX_SP) * 100, 15);
        
        // Shield Bar (if active)
        if (player.isShieldActive()) {
            game.batch.setColor(0.2f, 0.2f, 0.2f, 1);
            game.batch.draw(game.assets.whitePixel, 10, Constants.WORLD_HEIGHT - 95, 100, 10);
            game.batch.setColor(0, 0.8f, 1, 1);
            game.batch.draw(game.assets.whitePixel, 10, Constants.WORLD_HEIGHT - 95, (player.getShieldHealth() / 3f) * 100, 10);
            font.setColor(0, 0.8f, 1, 1);
            font.draw(game.batch, "SHIELD", 115, Constants.WORLD_HEIGHT - 85);
        }

        game.batch.setColor(1, 1, 1, 1);
        font.draw(game.batch, "SP", 115, Constants.WORLD_HEIGHT - 58);
        
        // Weapon Level
        font.setColor(1, 1, 0, 1);
        font.draw(game.batch, "WEAPON LVL: " + player.getWeaponLevel(), Constants.WORLD_WIDTH - 200, 30);

        if (combo > 1) {
            font.setColor(1, 1, 0, 1);
            font.draw(game.batch, "COMBO X" + combo, 10, Constants.WORLD_HEIGHT - 85);
        }

        if (player.isTripleShotActive()) {
            font.setColor(0, 1, 0, 1);
            font.draw(game.batch, "TRIPLE SHOT ACTIVE!", Constants.WORLD_WIDTH - 200, Constants.WORLD_HEIGHT - 10);
        }
        
        // Wave Info
        font.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        font.draw(game.batch, "WAVE: " + waveManager.getCurrentWave(), Constants.WORLD_WIDTH / 2 - 40, Constants.WORLD_HEIGHT - 10);
        
        if (waveManager.isCountingDown()) {
            font.setColor(com.badlogic.gdx.graphics.Color.YELLOW);
            font.draw(game.batch, "NEXT WAVE IN: " + (int)waveManager.getCountdownTime(), Constants.WORLD_WIDTH / 2 - 80, Constants.WORLD_HEIGHT / 2);
        }
        
        // Boss Health Bar
        Boss activeBoss = entityManager.getActiveBoss();
        if (activeBoss != null) {
            game.batch.setColor(0.2f, 0.2f, 0.2f, 1);
            game.batch.draw(game.assets.whitePixel, Constants.WORLD_WIDTH / 2 - 150, Constants.WORLD_HEIGHT - 40, 300, 20);
            game.batch.setColor(com.badlogic.gdx.graphics.Color.RED);
            game.batch.draw(game.assets.whitePixel, Constants.WORLD_WIDTH / 2 - 150, Constants.WORLD_HEIGHT - 40, (activeBoss.getHealth() / (float)activeBoss.getMaxHealth()) * 300, 20);
            font.setColor(com.badlogic.gdx.graphics.Color.WHITE);
            font.draw(game.batch, "BOSS", Constants.WORLD_WIDTH / 2 - 20, Constants.WORLD_HEIGHT - 25);
        }
        game.batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        audioManager.dispose();
        font.dispose();
    }
}
