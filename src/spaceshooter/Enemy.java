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

    public Enemy(int x, int y) {

        this.x = x;
        this.y = y;

        active = true;
    }

    public void update() {

        y += speed;

        if (y > GamePanel.SCREEN_HEIGHT) {
            active = false;
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
    public void deactivate() {
    active = false;
}
}