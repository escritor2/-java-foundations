package senai.projeto.gabriel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import java.util.Iterator;

public class EntityManager {
    private final Main game;
    private final Player player;
    private final ParticleManager particleManager;
    private final Array<Boss> bosses;
    private final Pool<Boss> bossPool;
    
    // Arrays for active entities
    private final Array<PlayerBullet> playerBullets;
    private final Array<Enemy> enemies;
    private final Array<EnemyBullet> enemyBullets;
    private final Array<PowerUp> powerUps;
    private final Array<Explosion> explosions;

    // Pools
    private final Pool<PlayerBullet> playerBulletPool;
    private final Pool<Enemy> enemyPool;
    private final Pool<EnemyBullet> enemyBulletPool;
    private final Pool<PowerUp> powerUpPool;
    // Explosion doesn't have a specific Pool yet, we'll use Pools.get or just stick to simple for now
    
    public EntityManager(Main game, Player player, ParticleManager particleManager) {
        this.game = game;
        this.player = player;
        this.particleManager = particleManager;
        
        playerBullets = new Array<>();
        enemies = new Array<>();
        enemyBullets = new Array<>();
        powerUps = new Array<>();
        explosions = new Array<>();
        bosses = new Array<>();

        bossPool = new Pool<Boss>() {
            @Override
            protected Boss newObject() {
                return new Boss(game.assets.get(Assets.ENEMY, Texture.class));
            }
        };

        playerBulletPool = new Pool<PlayerBullet>() {
            @Override
            protected PlayerBullet newObject() {
                return new PlayerBullet(game.assets.get(Assets.BULLET, Texture.class));
            }
        };
        
        // Special bullet needs bulletSpecialTexture, we'll handle this in spawn or use separate pools
        // To keep it simple, we'll use one pool and change texture if needed, but textures are final in Bullet usually.
        // Actually, let's just use two pools if they have different textures.
        
        enemyPool = new Pool<Enemy>() {
            @Override
            protected Enemy newObject() {
                return new Enemy(game.assets.get(Assets.ENEMY, Texture.class));
            }
        };
        
        enemyBulletPool = new Pool<EnemyBullet>() {
            @Override
            protected EnemyBullet newObject() {
                return new EnemyBullet(game.assets.get(Assets.ENEMY_BULLET, Texture.class));
            }
        };
        
        powerUpPool = new Pool<PowerUp>() {
            @Override
            protected PowerUp newObject() {
                return new PowerUp(game.assets.get(Assets.BULLET, Texture.class)); // Using bullet texture as placeholder
            }
        };
    }

    public void spawnPlayerBullet(Enums.BulletType type) {
        float x = player.getSprite().getX() + player.getSprite().getWidth() / 2 - 5;
        float y = player.getSprite().getY() + player.getSprite().getHeight();
        
        if (type == Enums.BulletType.SPECIAL) {
            PlayerBullet bullet = playerBulletPool.obtain();
            bullet.init(type, x, y);
            playerBullets.add(bullet);
            return;
        }

        int level = player.getWeaponLevel();
        
        // Pattern based on weapon level
        switch (level) {
            case 1:
                createBullet(type, x, y);
                break;
            case 2:
                createBullet(type, x - 10, y);
                createBullet(type, x + 10, y);
                break;
            case 3:
                createBullet(type, x, y);
                createBullet(type, x - 15, y - 5);
                createBullet(type, x + 15, y - 5);
                break;
            case 4:
                createBullet(type, x - 5, y);
                createBullet(type, x + 5, y);
                createBullet(type, x - 20, y - 10);
                createBullet(type, x + 20, y - 10);
                break;
        }
        
        // Triple shot power-up is an ADDITION to the current weapon level
        if (player.isTripleShotActive()) {
            createBullet(type, x - 30, y - 10);
            createBullet(type, x + 30, y - 10);
        }
    }

    private void createBullet(Enums.BulletType type, float x, float y) {
        PlayerBullet bullet = playerBulletPool.obtain();
        bullet.init(type, x, y);
        playerBullets.add(bullet);
    }

    public void spawnEnemy(float x, float y) {
        Enemy enemy = enemyPool.obtain();
        enemy.init(x, y);
        enemies.add(enemy);
    }

    public void spawnEnemyBullet(float x, float y, Vector2 dir) {
        EnemyBullet bullet = enemyBulletPool.obtain();
        bullet.init(x, y, dir);
        enemyBullets.add(bullet);
    }
    
    public void spawnBoss(int health) {
        Boss boss = bossPool.obtain();
        boss.init(health);
        bosses.add(boss);
    }
    
    public void spawnPowerUp(Enums.PowerUpType type, float x, float y) {
        PowerUp pu = powerUpPool.obtain();
        pu.init(type, x, y);
        powerUps.add(pu);
    }

    public void update(float delta, float enemySpeed) {
        // Update Player Bullets
        Iterator<PlayerBullet> pbIter = playerBullets.iterator();
        while (pbIter.hasNext()) {
            PlayerBullet b = pbIter.next();
            float speed = (b.getType() == Enums.BulletType.SPECIAL) ? 0 : Constants.BULLET_SPEED;
            b.update(delta, speed, player);
            if (b.isOffScreen()) {
                pbIter.remove();
                playerBulletPool.free(b);
            }
        }

        // Update Enemies
        Iterator<Enemy> eIter = enemies.iterator();
        while (eIter.hasNext()) {
            Enemy e = eIter.next();
            e.update(delta, enemySpeed);
            if (e.isOffScreen()) {
                eIter.remove();
                enemyPool.free(e);
            }
        }

        // Update Bosses
        Iterator<Boss> bIter = bosses.iterator();
        while (bIter.hasNext()) {
            Boss b = bIter.next();
            b.update(delta);
            if (b.canShoot()) {
                // Boss shoots 3 bullets
                spawnEnemyBullet(b.getX() + 100, b.getY(), new Vector2(0, -1));
                spawnEnemyBullet(b.getX() + 50, b.getY(), new Vector2(-0.5f, -1).nor());
                spawnEnemyBullet(b.getX() + 150, b.getY(), new Vector2(0.5f, -1).nor());
            }
            if (b.isDestroyed()) {
                particleManager.spawnExplosion(b.getX() + 100, b.getY() + 75, com.badlogic.gdx.graphics.Color.GOLD);
                bIter.remove();
                bossPool.free(b);
            }
        }

        // Update Enemy Bullets
        Iterator<EnemyBullet> ebIter = enemyBullets.iterator();
        while (ebIter.hasNext()) {
            EnemyBullet b = ebIter.next();
            b.update(delta, 200f);
            if (b.isOffScreen()) {
                ebIter.remove();
                enemyBulletPool.free(b);
            }
        }

        // Update PowerUps
        Iterator<PowerUp> puIter = powerUps.iterator();
        while (puIter.hasNext()) {
            PowerUp pu = puIter.next();
            pu.update(delta);
            if (pu.isOffScreen()) {
                puIter.remove();
                powerUpPool.free(pu);
            }
        }

        // Update Explosions
        Iterator<Explosion> exIter = explosions.iterator();
        while (exIter.hasNext()) {
            Explosion ex = exIter.next();
            ex.update(delta);
            if (ex.isFinished()) exIter.remove();
        }

        checkCollisions();
    }

    private void checkCollisions() {
        // Player Bullets vs Enemies
        for (int i = playerBullets.size - 1; i >= 0; i--) {
            PlayerBullet bullet = playerBullets.get(i);
            for (int j = enemies.size - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (bullet.getBounds().overlaps(enemy.getBounds())) {
                    enemy.takeDamage(1);
                    boolean hitDone = bullet.hitEnemy();
                    
                    if (bullet.getType() == Enums.BulletType.SPECIAL) {
                        // Special bullet destroys enemy bullets too
                        for (int k = enemyBullets.size - 1; k >= 0; k--) {
                            if (bullet.getBounds().overlaps(enemyBullets.get(k).getBounds())) {
                                enemyBulletPool.free(enemyBullets.removeIndex(k));
                            }
                        }
                    }

                    if (enemy.isDestroyed()) {
                        particleManager.spawnExplosion(enemy.getX() + enemy.getWidth()/2, enemy.getY() + enemy.getHeight()/2, com.badlogic.gdx.graphics.Color.ORANGE);
                        explosions.add(new Explosion(game.assets.get(Assets.EXPLOSION, Texture.class), enemy.getX(), enemy.getY()));
                        enemies.removeIndex(j);
                        enemyPool.free(enemy);
                        // Add score in GameScreen
                        onEnemyDestroyed(enemy);
                    }

                    if (hitDone) {
                        playerBullets.removeIndex(i);
                        playerBulletPool.free(bullet);
                        break;
                    }
                }
            }
            
            // Player Bullets vs Bosses
            for (int j = bosses.size - 1; j >= 0; j--) {
                Boss boss = bosses.get(j);
                if (bullet.getBounds().overlaps(boss.getBounds())) {
                    boss.takeDamage(1);
                    boolean hitDone = bullet.hitEnemy();
                    if (hitDone) {
                        playerBullets.removeIndex(i);
                        playerBulletPool.free(bullet);
                        break;
                    }
                }
            }
        }

        // Player vs Enemies, Bosses and Enemy Bullets
        if (!player.isInvulnerable()) {
            for (int i = enemies.size - 1; i >= 0; i--) {
                if (player.getBounds().overlaps(enemies.get(i).getBounds())) {
                    player.takeHit();
                    notifyPlayerHit();
                    enemyPool.free(enemies.removeIndex(i));
                    break;
                }
            }
            for (int i = bosses.size - 1; i >= 0; i--) {
                if (player.getBounds().overlaps(bosses.get(i).getBounds())) {
                    player.takeHit();
                    notifyPlayerHit();
                    // Boss doesn't die on collision, player just takes damage
                    break;
                }
            }
            for (int i = enemyBullets.size - 1; i >= 0; i--) {
                if (player.getBounds().overlaps(enemyBullets.get(i).getBounds())) {
                    player.takeHit();
                    notifyPlayerHit();
                    enemyBulletPool.free(enemyBullets.removeIndex(i));
                    break;
                }
            }
        }

        // Player vs PowerUps
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp pu = powerUps.get(i);
            if (player.getBounds().overlaps(pu.getBounds())) {
                switch (pu.getType()) {
                    case TRIPLE_SHOT:
                        player.activateTripleShot();
                        break;
                    case SHIELD:
                        player.activateShield();
                        break;
                    case WEAPON_UP:
                        player.upgradeWeapon();
                        break;
                }
                powerUpPool.free(powerUps.removeIndex(i));
            }
        }
    }

    private void onEnemyDestroyed(Enemy enemy) {
        // Notification for score
        if (game.getScreen() instanceof GameScreen) {
            ((GameScreen) game.getScreen()).addScore(10);
        }
        
        // Random PowerUp Drop
        float roll = MathUtils.random(0, 100);
        if (roll < 5) { // 5% chance for Weapon Up
            spawnPowerUp(Enums.PowerUpType.WEAPON_UP, enemy.getX(), enemy.getY());
        } else if (roll < 10) { // 5% chance for Shield
            spawnPowerUp(Enums.PowerUpType.SHIELD, enemy.getX(), enemy.getY());
        } else if (roll < 15) { // 5% chance for Triple Shot
            spawnPowerUp(Enums.PowerUpType.TRIPLE_SHOT, enemy.getX(), enemy.getY());
        }
    }

    private void notifyPlayerHit() {
        if (game.getScreen() instanceof GameScreen) {
            ((GameScreen) game.getScreen()).onPlayerHit();
        }
    }

    public void draw(SpriteBatch batch) {
        for (PlayerBullet b : playerBullets) b.getSprite().draw(batch);
        for (Enemy e : enemies) e.getSprite().draw(batch);
        for (Boss b : bosses) b.getSprite().draw(batch);
        for (EnemyBullet b : enemyBullets) b.getSprite().draw(batch);
        for (PowerUp pu : powerUps) pu.getSprite().draw(batch);
        for (Explosion ex : explosions) ex.getSprite().draw(batch);
    }

    public boolean hasEnemies() {
        return enemies.size > 0 || bosses.size > 0;
    }

    public Boss getActiveBoss() {
        return bosses.size > 0 ? bosses.first() : null;
    }

    public Enemy getRandomEnemy() {
        return enemies.random();
    }
    
    public void clear() {
        playerBulletPool.freeAll(playerBullets);
        playerBullets.clear();
        enemyPool.freeAll(enemies);
        enemies.clear();
        bossPool.freeAll(bosses);
        bosses.clear();
        enemyBulletPool.freeAll(enemyBullets);
        enemyBullets.clear();
        powerUpPool.freeAll(powerUps);
        powerUps.clear();
        explosions.clear();
    }
}
