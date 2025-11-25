package senai.projeto.gabriel;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Texture playerTexture;
    private Texture enemyTexture;
    private Texture bulletTexture;
    private Texture enemyBulletTexture;
    private Texture explosionTexture;

    private Sound shootSound;
    private Music music;

    private FitViewport viewport;
    private Sprite playerSprite;
    private Array<Sprite> playerBullets;
    private Array<EnemyBulletData> enemyBullets;
    private Array<Sprite> enemies;
    private Array<ExplosionData> activeExplosions;

    private BitmapFont font;
    private GlyphLayout layout;

    private Vector2 touchPos;
    private float enemySpawnTimer;
    private float enemyShootTimer;
    private float enemySpawnInterval = 1.0f;
    private int score = 0;
    private boolean gameOver = false;

    // Variáveis de Vidas
    private int playerLives = 3;
    private float invulnerabilityTimer = 0f;

    private static class EnemyBulletData {
        Sprite sprite;
        Vector2 direction;
    }

    private static class ExplosionData {
        Sprite sprite;
        float lifetime = 0f;
        final float maxLifetime = 0.5f;
    }

    public void playMusic() {
        if (this.music != null) {
            this.music.setLooping(true);
            this.music.setVolume(0.5f);
            this.music.play();
        }
    }

    public void stopMusic() {
        if (this.music != null && this.music.isPlaying()) {
            this.music.stop();
        }
    }

    public void playShootSound() {
        if (this.shootSound != null) {
            this.shootSound.play(1.0f);
        }
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture("tema.png");
        playerTexture = new Texture("nave.png");
        enemyTexture = new Texture("inimigo.png");
        bulletTexture = new Texture("tiro.png");
        enemyBulletTexture = new Texture("tiro_inimigo.png");
        explosionTexture = new Texture("explosao.png");

        viewport = new FitViewport(800, 600);

        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(60, 60);
        playerSprite.setPosition(400 - playerSprite.getWidth() / 2, 50);

        playerBullets = new Array<>();
        enemyBullets = new Array<>();
        enemies = new Array<>();
        activeExplosions = new Array<>();

        touchPos = new Vector2();

        font = new BitmapFont();
        font.getData().setScale(1.5f);
        layout = new GlyphLayout();

        if (music != null) {
            playMusic();
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        handleInput();

        if (!gameOver) {
            update();
        }

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        if (!gameOver && (invulnerabilityTimer <= 0 || (int)(invulnerabilityTimer * 10) % 2 == 0)) {
            playerSprite.draw(batch);
        }

        for (Sprite bullet : playerBullets) {
            bullet.draw(batch);
        }

        for (EnemyBulletData enemyBullet : enemyBullets) {
            enemyBullet.sprite.draw(batch);
        }

        for (Sprite enemy : enemies) {
            enemy.draw(batch);
        }

        for (ExplosionData exp : activeExplosions) {
            exp.sprite.draw(batch);
        }

        String scoreText = "Score: " + score;
        String livesText = "Lives: " + playerLives;

        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.draw(batch, scoreText, 10, viewport.getWorldHeight() - 10);
        font.draw(batch, livesText, 10, viewport.getWorldHeight() - 35);

        if (gameOver) {
            String gameOverText = "GAME OVER! Final Score: " + score + ". Press ENTER to restart.";
            font.setColor(1.0f, 0.0f, 0.0f, 1.0f);
            layout.setText(font, gameOverText);
            font.draw(batch, gameOverText,
                viewport.getWorldWidth() / 2 - layout.width / 2,
                viewport.getWorldHeight() / 2);
        }

        batch.end();
    }

    private void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= deltaTime;
        }

        playerSprite.setX(
            MathUtils.clamp(playerSprite.getX(),
                0,
                viewport.getWorldWidth() - playerSprite.getWidth())
        );
        playerSprite.setY(
            MathUtils.clamp(playerSprite.getY(),
                0,
                viewport.getWorldHeight() * 0.3f)
        );

        for (int i = playerBullets.size - 1; i >= 0; i--) {
            Sprite bullet = playerBullets.get(i);
            float bulletSpeed = 600f;
            bullet.translateY(bulletSpeed * deltaTime);
            if (bullet.getY() > viewport.getWorldHeight()) {
                playerBullets.removeIndex(i);
            }
        }

        for (int i = enemyBullets.size - 1; i >= 0; i--) {
            EnemyBulletData bulletData = enemyBullets.get(i);
            float enemyBulletSpeed = 200f;
            bulletData.sprite.translate(
                bulletData.direction.x * enemyBulletSpeed * deltaTime,
                bulletData.direction.y * enemyBulletSpeed * deltaTime
            );

            if (bulletData.sprite.getY() < -bulletData.sprite.getHeight() ||
                bulletData.sprite.getX() < -bulletData.sprite.getWidth() ||
                bulletData.sprite.getX() > viewport.getWorldWidth()) {
                enemyBullets.removeIndex(i);
            }
        }

        enemySpawnTimer += deltaTime;
        if (enemySpawnTimer >= enemySpawnInterval) {
            enemySpawnTimer = 0;
            Sprite enemy = new Sprite(enemyTexture);

            enemy.setSize(50, 50);

            enemy.setPosition(
                MathUtils.random(0, viewport.getWorldWidth() - enemy.getWidth()),
                viewport.getWorldHeight()
            );
            enemies.add(enemy);
        }

        for (int i = enemies.size - 1; i >= 0; i--) {
            Sprite enemy = enemies.get(i);
            float enemySpeed = 150f;
            enemy.translateY(-enemySpeed * deltaTime);
            if (enemy.getY() + enemy.getHeight() < 0) {
                enemies.removeIndex(i);
            }
        }

        enemyShootTimer += deltaTime;
        float enemyShootInterval = 1.5f;
        if (enemyShootTimer >= enemyShootInterval && enemies.size > 0) {
            enemyShootTimer = 0;
            Sprite shootingEnemy = enemies.get(MathUtils.random(0, enemies.size - 1));

            EnemyBulletData newBullet = new EnemyBulletData();
            newBullet.sprite = new Sprite(enemyBulletTexture);
            newBullet.sprite.setSize(15, 30);

            newBullet.sprite.setPosition(
                shootingEnemy.getX() + shootingEnemy.getWidth() / 2 - newBullet.sprite.getWidth() / 2,
                shootingEnemy.getY()
            );

            newBullet.direction = new Vector2(
                playerSprite.getX() + playerSprite.getWidth() / 2,
                playerSprite.getY() + playerSprite.getHeight() / 2
            ).sub(
                shootingEnemy.getX() + shootingEnemy.getWidth() / 2,
                shootingEnemy.getY()
            ).nor();

            enemyBullets.add(newBullet);
        }

        // Colisão Tiros do Jogador vs. Inimigos
        for (int i = playerBullets.size - 1; i >= 0; i--) {
            Sprite bullet = playerBullets.get(i);
            Rectangle bulletRect = bullet.getBoundingRectangle();

            for (int j = enemies.size - 1; j >= 0; j--) {
                Sprite enemy = enemies.get(j);
                Rectangle enemyRect = enemy.getBoundingRectangle();
                if (bulletRect.overlaps(enemyRect)) {
                    ExplosionData data = new ExplosionData();
                    data.sprite = new Sprite(explosionTexture);
                    data.sprite.setSize(80, 80);
                    data.sprite.setPosition(
                        enemy.getX() + enemy.getWidth()/2 - data.sprite.getWidth()/2,
                        enemy.getY() + enemy.getHeight()/2 - data.sprite.getHeight()/2
                    );

                    activeExplosions.add(data);
                    playerBullets.removeIndex(i);
                    enemies.removeIndex(j);
                    score += 10;
                    break;
                }
            }
        }

        Rectangle playerRect = playerSprite.getBoundingRectangle();

        if (invulnerabilityTimer <= 0) { 

            for (int i = enemies.size - 1; i >= 0; i--) {
                Sprite enemy = enemies.get(i);
                Rectangle enemyRect = enemy.getBoundingRectangle();
                if (playerRect.overlaps(enemyRect)) {
                    takeHit();
                    enemies.removeIndex(i);
                    break;
                }
            }

            for (int i = enemyBullets.size - 1; i >= 0; i--) {
                EnemyBulletData bulletData = enemyBullets.get(i);
                Rectangle enemyBulletRect = bulletData.sprite.getBoundingRectangle();
                if (playerRect.overlaps(enemyBulletRect)) {
                    takeHit();
                    enemyBullets.removeIndex(i);
                    break;
                }
            }
        }

        for (int k = activeExplosions.size - 1; k >= 0; k--) {
            ExplosionData exp = activeExplosions.get(k);
            exp.lifetime += deltaTime;
            if (exp.lifetime >= exp.maxLifetime) {
                activeExplosions.removeIndex(k);
            }
        }
    }

    private void takeHit() {
        playerLives--;
        if (playerLives <= 0) {
            gameOver = true;
            stopMusic();
        } else {
            float playerInvulnerabilityTime = 1.5f;
            invulnerabilityTimer = playerInvulnerabilityTime;
        }
    }

    private void handleInput() {
        float playerSpeed = 300f;
        float moveDelta = playerSpeed * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerSprite.translateX(-moveDelta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerSprite.translateX(moveDelta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerSprite.translateY(moveDelta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerSprite.translateY(-moveDelta);
        }

        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            playerSprite.setPosition(touchPos.x - playerSprite.getWidth() / 2, playerSprite.getY());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !gameOver) {
            Sprite bullet = new Sprite(bulletTexture);

            bullet.setSize(10, 30);

            bullet.setPosition(
                playerSprite.getX() + playerSprite.getWidth() / 2 - bullet.getWidth() / 2,
                playerSprite.getY() + playerSprite.getHeight()
            );
            playerBullets.add(bullet);

            if (shootSound != null) {
                playShootSound();
            }
        }

        if (gameOver && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            restartGame();
            if (music != null) {
                playMusic();
            }
        }
    }

    private void restartGame() {
        gameOver = false;
        score = 0;
        playerLives = 3;
        invulnerabilityTimer = 0f;
        playerBullets.clear();
        enemyBullets.clear();
        enemies.clear();
        activeExplosions.clear();
        playerSprite.setPosition(400 - playerSprite.getWidth() / 2, 50);
        enemySpawnTimer = 0;
        enemyShootTimer = 0;
        enemySpawnInterval = 1.0f;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        playerTexture.dispose();
        enemyTexture.dispose();
        bulletTexture.dispose();
        enemyBulletTexture.dispose();
        explosionTexture.dispose();
        font.dispose();

        if (music != null) music.dispose();
        if (shootSound != null) shootSound.dispose();
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public void setShootSound(Sound sound) {
        this.shootSound = sound;
    }
}
