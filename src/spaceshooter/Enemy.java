package spaceshooter;

import java.awt.Color;
import java.awt.Graphics2D;

public class Enemy {

    private int x;
    private int y;

    private final int width = 40;
    private final int height = 40;

    private final int speed = 3;

    private boolean active;
    private boolean reachedBottom;

    public Enemy(int x, int y) {

        this.x = x;
        this.y = y;

        active = true;
        reachedBottom = false;
    }

    public void update() {

        y += speed;

        if (y > GamePanel.SCREEN_HEIGHT) {

            active = false;
            reachedBottom = true;
        }
    }

    public void draw(Graphics2D g2) {

        g2.setColor(Color.RED);

        g2.fillOval(
                x,
                y,
                width,
                height
        );

        // Enemy eyes
        g2.setColor(Color.WHITE);

        g2.fillOval(
                x + 8,
                y + 10,
                8,
                8
        );

        g2.fillOval(
                x + 24,
                y + 10,
                8,
                8
        );
    }

    public boolean isActive() {
        return active;
    }

    public boolean reachedBottom() {
        return reachedBottom;
    }

    public void deactivate() {

        active = false;
        reachedBottom = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}