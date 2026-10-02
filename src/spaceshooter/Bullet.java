package spaceshooter;

import java.awt.Color;
import java.awt.Graphics2D;

public class Bullet {

    private int x;
    private int y;

    private final int width = 5;
    private final int height = 15;

    private final int speed = 10;

    private boolean active;

    public Bullet(int x, int y) {

        this.x = x;
        this.y = y;
        this.active = true;
    }

    public void update() {

        y -= speed;

        if (y + height < 0) {
            active = false;
        }
    }

    public void draw(Graphics2D g2) {

        g2.setColor(Color.YELLOW);

        g2.fillRect(
                x,
                y,
                width,
                height
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