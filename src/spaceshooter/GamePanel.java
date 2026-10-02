package spaceshooter;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;

    private Thread gameThread;
    private boolean running;

    private Player player;

    private List<Enemy> enemies;

    private Random random;

    private int enemySpawnTimer;

    // Game statistics
    private int score;
    private int lives;

    // Wave system
    private int wave;
    private int enemiesToSpawn;
    private int enemiesSpawned;
    private int enemiesDefeated;

    private boolean waveComplete;

    // Game over
    private boolean gameOver;

    public GamePanel() {

        setPreferredSize(
                new Dimension(
                        SCREEN_WIDTH,
                        SCREEN_HEIGHT
                )
        );

        setBackground(Color.BLACK);

        setFocusable(true);

        addKeyListener(this);

        player = new Player(
                SCREEN_WIDTH / 2 - 25,
                SCREEN_HEIGHT - 80
        );

        enemies = new ArrayList<>();

        random = new Random();

        enemySpawnTimer = 0;

        // Initial game values
        score = 0;

        lives = 3;

        wave = 1;

        enemiesToSpawn = 5;

        enemiesSpawned = 0;

        enemiesDefeated = 0;

        waveComplete = false;

        gameOver = false;
    }

    public void startGame() {

        if (gameThread == null) {

            gameThread = new Thread(this);

            running = true;

            gameThread.start();
        }

        requestFocusInWindow();
    }

    @Override
    public void run() {

        while (running) {

            update();

            repaint();

            try {

                Thread.sleep(16);

            } catch (InterruptedException ex) {

                Thread.currentThread().interrupt();

                running = false;
            }
        }
    }

    private void update() {

        /*
         * Stop the game logic when Game Over happens.
         */
        if (gameOver) {
            return;
        }

        player.update();

        spawnEnemies();

        updateEnemies();

        checkCollisions();

        checkWaveComplete();
    }

    private void spawnEnemies() {

        /*
         * Do not spawn more enemies than
         * required for the current wave.
         */
        if (enemiesSpawned >= enemiesToSpawn) {
            return;
        }

        enemySpawnTimer++;

        /*
         * Spawn one enemy every 60 frames.
         */
        if (enemySpawnTimer >= 60) {

            int enemyX =
                    random.nextInt(
                            SCREEN_WIDTH - 40
                    );

            enemies.add(
                    new Enemy(
                            enemyX,
                            -40
                    )
            );

            enemiesSpawned++;

            enemySpawnTimer = 0;
        }
    }

    private void updateEnemies() {

        for (Enemy enemy : enemies) {

            if (enemy.isActive()) {

                enemy.update();

                /*
                 * Enemy reached the bottom.
                 *
                 * This costs one life.
                 */
                if (enemy.reachedBottom()) {

                    loseLife();

                    enemiesDefeated++;
                }
            }
        }

        /*
         * Remove inactive enemies.
         */
        enemies.removeIf(
                enemy -> !enemy.isActive()
        );
    }

    private void checkCollisions() {

        List<Bullet> bullets =
                player.getBullets();

        /*
         * =====================================
         * BULLET VS ENEMY
         * =====================================
         */
        for (Bullet bullet : bullets) {

            for (Enemy enemy : enemies) {

                if (!bullet.isActive()
                        || !enemy.isActive()) {

                    continue;
                }

                boolean collision =
                        bullet.getX()
                                < enemy.getX()
                                    + enemy.getWidth()

                        && bullet.getX()
                                + bullet.getWidth()
                                    > enemy.getX()

                        && bullet.getY()
                                < enemy.getY()
                                    + enemy.getHeight()

                        && bullet.getY()
                                + bullet.getHeight()
                                    > enemy.getY();

                if (collision) {

                    /*
                     * Remove bullet.
                     */
                    bullet.deactivate();

                    /*
                     * Remove enemy.
                     */
                    enemy.deactivate();

                    /*
                     * Give player points.
                     */
                    score += 10;

                    /*
                     * Count defeated enemy.
                     */
                    enemiesDefeated++;
                }
            }
        }

        /*
         * =====================================
         * ENEMY VS PLAYER
         * =====================================
         */
        Rectangle playerBounds =
                new Rectangle(
                        player.getX(),
                        player.getY(),
                        player.getWidth(),
                        player.getHeight()
                );

        for (Enemy enemy : enemies) {

            if (!enemy.isActive()) {
                continue;
            }

            Rectangle enemyBounds =
                    new Rectangle(
                            enemy.getX(),
                            enemy.getY(),
                            enemy.getWidth(),
                            enemy.getHeight()
                    );

            if (playerBounds.intersects(enemyBounds)) {

                /*
                 * Remove enemy.
                 */
                enemy.deactivate();

                /*
                 * Player loses one life.
                 */
                loseLife();

                /*
                 * Count this enemy as completed.
                 */
                enemiesDefeated++;
            }
        }
    }

    private void loseLife() {

        /*
         * Never allow lives to become negative.
         */
        if (lives > 0) {

            lives--;

            /*
             * Game Over when lives reach zero.
             */
            if (lives == 0) {

                gameOver = true;
            }
        }
    }

    private void checkWaveComplete() {

        /*
         * The wave is complete when:
         *
         * 1. All enemies have been spawned.
         * 2. All enemies have been dealt with.
         * 3. No active enemies remain.
         */
        if (enemiesSpawned >= enemiesToSpawn
                && enemies.isEmpty()
                && enemiesDefeated >= enemiesToSpawn
                && !waveComplete) {

            waveComplete = true;

            startNextWave();
        }
    }

    private void startNextWave() {

        /*
         * Increase wave number.
         */
        wave++;

        /*
         * Add two more enemies
         * for every new wave.
         *
         * Wave 1 = 5
         * Wave 2 = 7
         * Wave 3 = 9
         * Wave 4 = 11
         */
        enemiesToSpawn =
                5 + (wave - 1) * 2;

        enemiesSpawned = 0;

        enemiesDefeated = 0;

        enemySpawnTimer = 0;

        waveComplete = false;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 =
                (Graphics2D) g;

        /*
         * Draw player.
         */
        player.draw(g2);

        /*
         * Draw enemies.
         */
        for (Enemy enemy : enemies) {

            enemy.draw(g2);
        }

        /*
         * Draw HUD.
         */
        drawHUD(g2);

        /*
         * Draw Game Over screen.
         */
        if (gameOver) {

            drawGameOver(g2);
        }

        g2.dispose();
    }

    private void drawHUD(Graphics2D g2) {

        g2.setColor(Color.WHITE);

        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );

        /*
         * Score.
         */
        g2.drawString(
                "Score: " + score,
                20,
                30
        );

        /*
         * Lives.
         */
        g2.drawString(
                "Lives: " + lives,
                20,
                60
        );

        /*
         * Wave.
         */
        g2.drawString(
                "Wave: " + wave,
                SCREEN_WIDTH - 120,
                30
        );

        /*
         * Enemies remaining.
         */
        int remaining =
                enemiesToSpawn - enemiesDefeated;

        if (remaining < 0) {

            remaining = 0;
        }

        g2.drawString(
                "Enemies: " + remaining,
                SCREEN_WIDTH - 170,
                60
        );
    }

    private void drawGameOver(Graphics2D g2) {

        /*
         * Dark transparent overlay.
         */
        g2.setColor(
                new Color(
                        0,
                        0,
                        0,
                        180
                )
        );

        g2.fillRect(
                0,
                0,
                SCREEN_WIDTH,
                SCREEN_HEIGHT
        );

        /*
         * GAME OVER text.
         */
        g2.setColor(Color.RED);

        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        60
                )
        );

        String gameOverText =
                "GAME OVER";

        int textWidth =
                g2.getFontMetrics()
                        .stringWidth(gameOverText);

        g2.drawString(
                gameOverText,
                (SCREEN_WIDTH - textWidth) / 2,
                250
        );

        /*
         * Final score.
         */
        g2.setColor(Color.WHITE);

        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        25
                )
        );

        String scoreText =
                "Final Score: " + score;

        int scoreWidth =
                g2.getFontMetrics()
                        .stringWidth(scoreText);

        g2.drawString(
                scoreText,
                (SCREEN_WIDTH - scoreWidth) / 2,
                310
        );

        /*
         * Final wave.
         */
        String waveText =
                "Wave: " + wave;

        int waveWidth =
                g2.getFontMetrics()
                        .stringWidth(waveText);

        g2.drawString(
                waveText,
                (SCREEN_WIDTH - waveWidth) / 2,
                350
        );

        /*
         * Restart instruction.
         */
        g2.setColor(Color.YELLOW);

        g2.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        String restartText =
                "Press R to Restart";

        int restartWidth =
                g2.getFontMetrics()
                        .stringWidth(restartText);

        g2.drawString(
                restartText,
                (SCREEN_WIDTH - restartWidth) / 2,
                410
        );
    }

    private void restartGame() {

        /*
         * Create a new player.
         */
        player = new Player(
                SCREEN_WIDTH / 2 - 25,
                SCREEN_HEIGHT - 80
        );

        /*
         * Remove all enemies.
         */
        enemies.clear();

        /*
         * Reset score.
         */
        score = 0;

        /*
         * Reset lives.
         */
        lives = 3;

        /*
         * Reset wave.
         */
        wave = 1;

        /*
         * Reset enemies per wave.
         */
        enemiesToSpawn = 5;

        enemiesSpawned = 0;

        enemiesDefeated = 0;

        /*
         * Reset spawn timer.
         */
        enemySpawnTimer = 0;

        /*
         * Reset game states.
         */
        waveComplete = false;

        gameOver = false;

        /*
         * Return keyboard focus.
         */
        requestFocusInWindow();
    }

    @Override
    public void keyPressed(KeyEvent e) {

        /*
         * Restart after Game Over.
         */
        if (gameOver
                && e.getKeyCode() == KeyEvent.VK_R) {

            restartGame();

            return;
        }

        /*
         * Normal player controls.
         */
        player.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {

        player.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}