package spaceshooter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Player {

    private int x;
    private int y;

    private final int width = 50;
    private final int height = 40;

    private final int speed = 6;

    private boolean leftPressed;
    private boolean rightPressed;

    private boolean spacePressed;

    private final List<Bullet> bullets;

    public Player(int x, int y) {

        this.x = x;
        this.y = y;

        bullets = new ArrayList<>();
    }

    public void update() {

        if (leftPressed) {
            x -= speed;
        }

        if (rightPressed) {
            x += speed;
        }

        if (x < 0) {
            x = 0;
        }

        if (x + width > GamePanel.SCREEN_WIDTH) {
            x = GamePanel.SCREEN_WIDTH - width;
        }

        if (spacePressed) {
            shoot();
            spacePressed = false;
        }

        for (Bullet bullet : bullets) {
            bullet.update();
        }

        bullets.removeIf(bullet -> !bullet.isActive());
    }

    private void shoot() {

        int bulletX = x + width / 2 - 2;
        int bulletY = y - 15;

        bullets.add(
                new Bullet(bulletX, bulletY)
        );
    }

    public void draw(Graphics2D g2) {

        // Ship body
        g2.setColor(Color.WHITE);

        int[] xPoints = {
            x + width / 2,
            x,
            x + width
        };

        int[] yPoints = {
            y,
            y + height,
            y + height
        };

        g2.fillPolygon(
                xPoints,
                yPoints,
                3
        );

        // Engine
        g2.setColor(Color.ORANGE);

        g2.fillRect(
                x + width / 2 - 5,
                y + height,
                10,
                10
        );

        // Bullets
        for (Bullet bullet : bullets) {
            bullet.draw(g2);
        }
    }

    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spacePressed = true;
        }
    }

    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
    }
    public List<Bullet> getBullets() {
        return bullets;
}
}