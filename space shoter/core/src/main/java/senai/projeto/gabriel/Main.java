package senai.projeto.gabriel;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Iterator;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private FitViewport viewport;

    // Texturas
    private Texture backgroundTexture;
    private Texture playerTexture;
    private Texture enemyTexture;
    private Texture bulletTexture;
    private Texture enemyBulletTexture;
    private Texture explosionTexture;
    private Texture bulletSpecialTexture;
    private Texture powerUpTripleTexture; // NOVO: Textura do power-up (apontará para bulletTexture)

    // Entidades
    private Player player;
    private Array<PlayerBullet> playerBullets;
    private Array<Enemy> enemies;
    private Array<EnemyBullet> enemyBullets;
    private Array<Explosion> explosions;
    private Array<PowerUp> powerUps; // NOVO: Array de power-ups

    // Sistemas
    private AudioManager audioManager;
    private HighScoreManager highScoreManager;

    // UI & Timers
    private BitmapFont font;
    private int score = 0;
    private int spBar = 0;
    private float bullTimer = 0f;
    private float enemySpawnTimer = 0f;
    private float enemyShootTimer = 0f;

    // NOVO: Variáveis para o Scaler de Dificuldade
    private float gameTime = 0f;
    private float enemySpeed = 150f;
    private float baseSpawnInterval = 1.0f;
    private final float minSpawnInterval = 0.3f;
    private final float difficultyScaler = 0.95f;
    private float difficultyTimer = 0f;
    private final float DIFFICULTY_CHECK_INTERVAL = 30.0f;

    private Vector2 touchPos;
    float speed = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(800, 600);

        // Carregar texturas
        backgroundTexture = new Texture("tema.png");
        playerTexture = new Texture("nave.png");
        enemyTexture = new Texture("inimigo.png");
        bulletTexture = new Texture("tiro.png");
        enemyBulletTexture = new Texture("tiro_inimigo.png");
        explosionTexture = new Texture("explosao.png");
        bulletSpecialTexture = new Texture("tiro_fodao.png");

        // CORREÇÃO: Reutiliza a textura do tiro normal para o Power-up para evitar erro de File Not Found.
        // Se você adicionar o arquivo 'powerup_triple.png' à sua pasta de assets, pode descomentar a linha abaixo.
        // powerUpTripleTexture = new Texture("powerup_triple.png");
        powerUpTripleTexture = bulletTexture;

        // Inicializar sistemas
        audioManager = new AudioManager();
        audioManager.load();
        highScoreManager = new HighScoreManager();

        // Inicializar entidades
        player = new Player(playerTexture, 400 - 30, 50);
        playerBullets = new Array<>();
        enemies = new Array<>();
        enemyBullets = new Array<>();
        explosions = new Array<>();
        powerUps = new Array<>();

        touchPos = new Vector2();
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        audioManager.playMusic();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (!player.isGameOver()) {
            handleInput(deltaTime);
            update(deltaTime);
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                restartGame();
            }
        }

        draw();
    }

    private void handleInput(float deltaTime) {
        float moveDelta = 400f * deltaTime;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) player.move(-moveDelta, 0, viewport.getWorldWidth(), viewport.getWorldHeight() * 0.3f);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player.move(moveDelta, 0, viewport.getWorldWidth(), viewport.getWorldHeight() * 0.3f);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) player.move(0, moveDelta, viewport.getWorldWidth(), viewport.getWorldHeight() * 0.3f);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) player.move(0, -moveDelta, viewport.getWorldWidth(), viewport.getWorldHeight() * 0.3f);

        // Tiro normal
        float fireRate = player.isTripleShotActive() ? 0.15f : 0.25f;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && bullTimer > fireRate && player.canAttack()) {
            spawnPlayerBullet(false);
            bullTimer = 0;
        }

        // Tiro especial
        if (Gdx.input.isKeyJustPressed(Input.Keys.N) && spBar >= 100) {
            spBar -= 100;
            spawnPlayerBullet(true);
            player.setCanAttack(false);
        }

        // Toque na tela
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            player.setPosition(touchPos.x - 30, player.getSprite().getY(), viewport.getWorldWidth());
        }
    }

    private void spawnPlayerBullet(boolean special) {
        float x = player.getSprite().getX() + player.getSprite().getWidth() / 2 - 5;
        float y = player.getSprite().getY() + player.getSprite().getHeight();
        int dur = special ? 100 : 2;
        String type = special ? "especial": "normal";

        Texture tex = special ? bulletSpecialTexture : bulletTexture;
        float lifeTime = type.equals("especial")?3f: 10f;
        // Tiro central
        playerBullets.add(new PlayerBullet( tex, type, x, y, dur, lifeTime));
        audioManager.playShoot();

        // Lógica para Tiro Triplo
        if (player.isTripleShotActive() && !special) {
            // Tiro da esquerda
            playerBullets.add(new PlayerBullet(tex, type, x - 15, y, dur, lifeTime));
            // Tiro da direita
            playerBullets.add(new PlayerBullet(tex, type, x + 15, y, dur, lifeTime));
        }
    }

    private void update(float deltaTime) {
        bullTimer += deltaTime;
        player.update(deltaTime, viewport.getWorldWidth(), viewport.getWorldHeight() * 0.3f);

        // Lógica do Scaler de Dificuldade
        gameTime += deltaTime;
        difficultyTimer += deltaTime;
        if (difficultyTimer >= DIFFICULTY_CHECK_INTERVAL) {
            difficultyTimer = 0;

            // Aumenta a velocidade do inimigo (ex: 10% a cada 30s)
            enemySpeed *= 1.10f;

            // Reduz o intervalo de spawn (mínimo de minSpawnInterval)
            baseSpawnInterval = Math.max(minSpawnInterval, baseSpawnInterval * difficultyScaler);

            Gdx.app.log("Difficulty", "Speed: " + enemySpeed + ", Interval: " + baseSpawnInterval);
        }

        // Update bullets
        Iterator<PlayerBullet> pbIter = playerBullets.iterator();

        while (pbIter.hasNext()) {
            PlayerBullet b = pbIter.next();

            if(b.getType().equals( "especial")){
                speed = 0f;
            }else{
                speed = 600f;
            }
            b.update(deltaTime, speed, player);
        }

        // Spawn enemies
        enemySpawnTimer += deltaTime;
        if (enemySpawnTimer >= baseSpawnInterval) {
            enemySpawnTimer = 0;
            float x = MathUtils.random(0, viewport.getWorldWidth() - 50);
            enemies.add(new Enemy(enemyTexture, x, viewport.getWorldHeight()));
        }

        // Update enemies
        Iterator<Enemy> eIter = enemies.iterator();
        while (eIter.hasNext()) {
            Enemy e = eIter.next();
            e.update(deltaTime, enemySpeed);
            if (e.isOffScreen()) eIter.remove();
        }

        // Enemy shooting
        enemyShootTimer += deltaTime;
        if (enemyShootTimer >= 1.5f && !enemies.isEmpty()) {
            enemyShootTimer = 0;
            Enemy shooter = enemies.random();
            Vector2 dir = new Vector2(
                player.getSprite().getX() + 30,
                player.getSprite().getY() + 30
            ).sub(shooter.getX() + 25, shooter.getY()).nor();
            float x = shooter.getX() + 25 - 7.5f;
            float y = shooter.getY();
            enemyBullets.add(new EnemyBullet(enemyBulletTexture, x, y, dir));
        }

        // Update enemy bullets
        Iterator<EnemyBullet> ebIter = enemyBullets.iterator();
        while (ebIter.hasNext()) {
            EnemyBullet b = ebIter.next();
            b.update(deltaTime, 200f);
            if (b.isOffScreen(viewport.getWorldWidth())) ebIter.remove();
        }

        // Colisões: tiros do jogador x inimigos
        pbIter = playerBullets.iterator();
        while (pbIter.hasNext()) {
            PlayerBullet bullet = pbIter.next();
            boolean bulletRemoved = false;

            // 1. Colisão do Tiro do Jogador com o Inimigo (Vida do Inimigo)
            eIter = enemies.iterator();
            while (eIter.hasNext()) {
                Enemy enemy = eIter.next();
                if (bullet.getBounds().overlaps(enemy.getBounds())) {

                    enemy.takeDamage(1);
                    boolean cu = bullet.getType().equals("especial");
                    boolean bulletShouldBeRemoved = bullet.hitEnemy() ;
                    if(bullet.getLifeTime() <= 0){ bulletShouldBeRemoved = true; player.setCanAttack(true);}
                    if (enemy.isDestroyed()){
                        // Explosão
                        float expX = enemy.getX() + enemy.getWidth() / 2 - 40;
                        float expY = enemy.getY() + enemy.getHeight() / 2 - 40;
                        explosions.add(new Explosion(explosionTexture, expX, expY));

                        eIter.remove();

                        score += 10;
                        if (spBar < 300 && !cu) spBar += 20;

                        // Chance de spawnar Power-up (10% de chance)
                        if (MathUtils.random(0, 100) < 10) {
                            powerUps.add(new PowerUp(powerUpTripleTexture, enemy.getX() + 10, enemy.getY(), "triple_shot"));
                        }
                    }

                    if (bulletShouldBeRemoved) {
                        pbIter.remove();
                        bulletRemoved = true;
                        break;
                    }
                    if(bullet.getType().equals("especial")){
                        break;
                    }
                }
            }

            if (bulletRemoved) continue;

            // 2. Colisões: Tiro Especial x Balas Inimigas (Escudo)
            if(bullet.getType().equals("especial")){
                Iterator<EnemyBullet> ebIterSpecial = enemyBullets.iterator();
                while(ebIterSpecial.hasNext()){
                    EnemyBullet eBul = ebIterSpecial.next();
                    if(bullet.getBounds().overlaps(eBul.getBounds()) ){
                        ebIterSpecial.remove(); // Destroi a bala inimiga
                    }
                }
            }

            // Remoção de balas do jogador fora da tela
            if (bullet.isOffScreen(viewport.getWorldHeight())) {
                pbIter.remove();
            }
        }

        // Atualizar Power-ups e Colisão com Jogador
        Iterator<PowerUp> puIter = powerUps.iterator();
        while (puIter.hasNext()) {
            PowerUp pu = puIter.next();
            pu.update(deltaTime);

            // Colisão do jogador com o Power-up
            if (player.getBounds().overlaps(pu.getBounds())) {
                if (pu.getType().equals("triple_shot")) {
                    player.activateTripleShot();
                }
                puIter.remove();
                audioManager.playShoot();
                continue;
            }

            if (pu.isOffScreen()) {
                puIter.remove();
            }
        }


        // Colisões: jogador x inimigos e tiros inimigos
        if (!player.isInvulnerable()) {
            // Inimigos
            for (Enemy enemy : enemies) {
                if (player.getBounds().overlaps(enemy.getBounds())) {
                    player.takeHit();
                    enemies.removeValue(enemy, true);
                    break;
                }
            }
            // Tiros inimigos
            ebIter = enemyBullets.iterator();
            while (ebIter.hasNext()) {
                EnemyBullet b = ebIter.next();
                if (player.getBounds().overlaps(b.getBounds())) {
                    player.takeHit();
                    ebIter.remove();
                    break;
                }
            }
        }

        // Atualizar explosões
        Iterator<Explosion> expIter = explosions.iterator();
        while (expIter.hasNext()) {
            Explosion e = expIter.next();
            e.update(deltaTime);
            if (e.isFinished()) expIter.remove();
        }

        // Game over?
        if (player.isGameOver()) {
            if (highScoreManager.isHighScore(score)) {
                highScoreManager.addScore(score);
            }
            audioManager.stopMusic();
        }
    }

    private void draw() {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        // Jogador
        if (!player.isGameOver() && (!player.isInvulnerable() || player.shouldBlink())) {
            player.getSprite().draw(batch);
        }


        for (PlayerBullet b : playerBullets) b.getSprite().draw(batch);
        for (EnemyBullet b : enemyBullets) b.getSprite().draw(batch);


        for (Enemy e : enemies) e.getSprite().draw(batch);


        for (PowerUp pu : powerUps) pu.getSprite().draw(batch);


        for (Explosion e : explosions) e.getSprite().draw(batch);

        font.setColor(1, 1, 1, 1);
        font.draw(batch, "Score: " + score, 10, viewport.getWorldHeight() - 10);
        font.draw(batch, "Lives: " + player.getLives(), 10, viewport.getWorldHeight() - 35);
        font.draw(batch, "Sp: " + spBar + "/300", 10, viewport.getWorldHeight() - 60);

        if (player.isTripleShotActive()) {
            font.setColor(0, 1, 0, 1);
            font.draw(batch, "Triple Shot ON!", viewport.getWorldWidth() - 150, viewport.getWorldHeight() - 10);
        }

        if (player.isGameOver()) {
            String msg = "GAME OVER! Final Score: " + score + ". Press ENTER to restart.";
            font.setColor(1, 0, 0, 1);
            font.draw(batch, msg, viewport.getWorldWidth()/2 - 150, viewport.getWorldHeight()/2 + 30);

            font.setColor(1, 1, 0, 1);
            font.draw(batch, "TOP 3 SCORES:", viewport.getWorldWidth()/2 - 100, viewport.getWorldHeight()/2 - 20);

            Array<Integer> scores = highScoreManager.getHighScores();
            for (int i = 0; i < Math.min(3, scores.size); i++) {
                String line = (i+1) + ". " + scores.get(i);
                font.draw(batch, line, viewport.getWorldWidth()/2 - 50, viewport.getWorldHeight()/2 - 45 - i*25);
            }
        }

        batch.end();
    }

    private void restartGame() {
        player = new Player(playerTexture, 400 - 30, 50);
        playerBullets.clear();
        enemies.clear();
        enemyBullets.clear();
        explosions.clear();
        powerUps.clear();
        score = 0;
        spBar = 0;
        enemySpawnTimer = 0;
        enemyShootTimer = 0;

        // Resetar variáveis do Scaler
        gameTime = 0f;
        enemySpeed = 150f;
        baseSpawnInterval = 1.0f;
        difficultyTimer = 0f;

        audioManager.playMusic();
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
        bulletSpecialTexture.dispose();
        // powerUpTripleTexture.dispose(); <--- REMOVIDO: A textura é compartilhada e já é liberada como bulletTexture
        font.dispose();
        audioManager.dispose();
    }
}
