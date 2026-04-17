package senai.projeto.gabriel;

public class Constants {
    // Screen Dimensions
    public static final float WORLD_WIDTH = 800;
    public static final float WORLD_HEIGHT = 600;
    
    // Player Configuration
    public static final float PLAYER_SPEED = 400f;
    public static final float PLAYER_SIZE = 60f;
    public static final float PLAYER_MAX_Y = WORLD_HEIGHT * 0.3f;
    public static final float PLAYER_INVULNERABILITY_TIME = 1.5f;
    public static final int PLAYER_MAX_LIVES = 3;
    
    // Combat
    public static final float NORMAL_FIRE_RATE = 0.25f;
    public static final float TRIPLE_SHOT_FIRE_RATE = 0.15f;
    public static final float BULLET_SPEED = 600f;
    public static final int SPECIAL_BULLET_SP_COST = 100;
    public static final int MAX_SP = 300;
    public static final float TRIPLE_SHOT_DURATION = 8.0f;
    
    // Enemies
    public static final float BASE_ENEMY_SPEED = 150f;
    public static final float BASE_SPAWN_INTERVAL = 1.0f;
    public static final float MIN_SPAWN_INTERVAL = 0.3f;
    public static final float DIFFICULTY_SCALER = 0.95f;
    public static final float DIFFICULTY_CHECK_INTERVAL = 30.0f;
    
    // Effects
    public static final float SCREEN_SHAKE_DURATION = 0.3f;
    public static final float SCREEN_SHAKE_INTENSITY = 5f;
}
