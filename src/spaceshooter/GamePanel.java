package spaceshooter;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

        player.update();

        spawnEnemies();

        updateEnemies();

        checkCollisions();
    }

    private void spawnEnemies() {

        enemySpawnTimer++;

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

            enemySpawnTimer = 0;
        }
    }

    private void updateEnemies() {

        for (Enemy enemy : enemies) {

            enemy.update();
        }

        enemies.removeIf(
                enemy -> !enemy.isActive()
        );
    }

    private void checkCollisions() {

    List<Bullet> bullets =
            player.getBullets();

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

                // Remove both objects
                bullet.deactivate();
                enemy.deactivate();
            }
        }
    }
}

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 =
                (Graphics2D) g;

        player.draw(g2);

        for (Enemy enemy : enemies) {

            enemy.draw(g2);
        }

        g2.dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {

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