package com.bjsxt.tank;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Tank {
    public static final int SPEED = 1, RSPEED = 3;
    private int angle = 0;

    private boolean live = true;
    private final BloodBar bb = new BloodBar();

    private int life = 100;

    TankClient tc;

    private final boolean player;
    //表示是否是玩家操作的玩家坦克

    private double x, y;
    private double oldX, oldY;

    private static final Random r = new Random();

    private boolean bL = false, bU = false, bR = false, bD = false;

    private Direction dir = Direction.STOP;
    private Direction ptDir = Direction.L;

    private int step = r.nextInt(12) + 3;

    private static final Toolkit tk = Toolkit.getDefaultToolkit(); //初始化awt工具包
    // 加载tank图像，并等待加载完成
    private static final Image tankImage;

    static {
        tankImage = tk.getImage(Tank.class.getClassLoader()
                                          .getResource("images/TankU.png"));
        Tools.waitForImageLoad(tankImage);
    }

    public Image getTankImg() { //旋转图像
        return Tools.rotateImg(tankImage, this.angle+90);
    }
    //键盘映射

    public static final int WIDTH = 30;
    public static final int HEIGHT = 30;

    public Tank(int x, int y, boolean player) {
        this.x = x;
        this.y = y;
        this.oldX = x;
        this.oldY = y;
        this.player = player;
    }

    public Tank(int x, int y, boolean player, Direction dir, TankClient tc) {
        this(x, y, player);
        this.dir = dir;
        this.tc = tc;
    }

    public void draw(Graphics g) {
        if (!live) {
            if (!player) {
                tc.tanks.remove(this);
            }
            return;
        }            //如果！live且！good则从tanks数组中移除

        if (player) bb.draw(g);
        g.drawImage(this.getTankImg(), (int) x, (int) y, null);
    }

    public void move() {

        this.oldX = x;
        this.oldY = y;

        Tools.rotateTo(this, dir);
        if (this.dir != Direction.STOP) {
            double radians = Math.toRadians(this.angle); // 将角度转换为弧度
            x += SPEED * Math.cos(radians);
            y += SPEED * Math.sin(radians);
        }

        if (this.dir != Direction.STOP) {
            this.ptDir = this.dir;
        }

        if (x < 0) x = 0;
        if (y < 30) y = 30;
        if (x + Tank.WIDTH > TankClient.GAME_WIDTH) x = TankClient.GAME_WIDTH - Tank.WIDTH;
        if (y + Tank.HEIGHT > TankClient.GAME_HEIGHT) y = TankClient.GAME_HEIGHT - Tank.HEIGHT;

        if (!player) {
            Direction[] dirs = Direction.values();
            if (step == 0) {
                step = r.nextInt(12) + 3;
                int rn = r.nextInt(dirs.length);
                dir = dirs[rn];
            }
            step--;

            if (r.nextInt(40) > 38) this.fire();
        }
    }

    private void stay() {
        x = oldX;
        y = oldY;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_F2:
                if (!this.live) {
                    this.live = true;
                    this.life = 100;
                }
                break;
            case KeyEvent.VK_LEFT:
                bL = true;
                break;
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            case KeyEvent.VK_UP:
                bU = true;
                break;
            case KeyEvent.VK_RIGHT:
                bR = true;
                break;
            case KeyEvent.VK_DOWN:
                bD = true;
                break;
        }
        locateDirection();
    }

    void locateDirection() {
        if (bL && !bU && !bR && !bD) dir = Direction.L;
        else if (bL && bU && !bR && !bD) dir = Direction.GO;
        else if (!bL && bU && !bR && !bD) dir = Direction.U;
        else if (!bL && bU && bR && !bD) dir = Direction.GO;
        else if (!bL && !bU && bR && !bD) dir = Direction.R;
        else if (!bL && !bU && bR && bD) dir = Direction.GO;
        else if (!bL && !bU && !bR && bD) dir = Direction.D;
        else if (bL && !bU && !bR && bD) dir = Direction.GO;
        else if (!bL && !bU && !bR && !bD) dir = Direction.STOP;
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_CONTROL:
                fire();
                break;
            case KeyEvent.VK_LEFT:
                bL = false;
                break;
            case KeyEvent.VK_UP:
                bU = false;
                break;
            case KeyEvent.VK_RIGHT:
                bR = false;
                break;
            case KeyEvent.VK_DOWN:
                bD = false;
                break;
            case KeyEvent.VK_A:
                superFire();
                break;
        }
        locateDirection();
    }

    public Missile fire() {
        if (!live) return null;
        int x = (int) (this.x + Tank.WIDTH / 2 - Missile.WIDTH / 2);
        int y = (int) (this.y + Tank.HEIGHT / 2 - Missile.HEIGHT / 2);
        Missile m = new Missile(x, y,this.angle, player, this.tc);
        tc.missiles.add(m);
        return m;
    }

    public Missile fire(int angle) {
        if (!live) return null;
        int x = (int) (this.x + Tank.WIDTH / 2 - Missile.WIDTH / 2);
        int y = (int) (this.y + Tank.HEIGHT / 2 - Missile.HEIGHT / 2);
        Missile m = new Missile(x, y,this.angle, player, this.tc);
        tc.missiles.add(m);
        return m;
    }

    public Rectangle getRect() {
        return new Rectangle((int) x, (int) y, WIDTH, HEIGHT);
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public boolean isPlayer() {
        return player;
    }

    /**
     * 撞墙
     *
     * @param w 被撞的墙
     * @return 撞上了返回true，否则false
     */
    public boolean collidesWithWall(Wall w) {
        if (this.live && this.getRect()
                             .intersects(w.getRect())) {
            this.stay();
            return true;
        }
        return false;
    }

    public boolean collidesWithTanks(java.util.List<Tank> tanks) {
        for (int i = 0; i < tanks.size(); i++) {
            Tank t = tanks.get(i);
            if (this != t) {
                if (this.live && t.isLive() && this.getRect()
                                                   .intersects(t.getRect())) {
                    this.stay();
                    t.stay();
                    return true;
                }
            }
        }
        return false;
    }

    private void superFire() {
        fire(this.angle);
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    private class BloodBar {
        public void draw(Graphics g) {
            Color c = g.getColor();
            g.setColor(Color.RED);
            g.drawRect((int) x, (int) (y - 10), WIDTH, 10);
            int w = WIDTH * life / 100;
            g.fillRect((int) x, (int) (y - 10), w, 10);
            g.setColor(c);
        }
    }

    public boolean eat(Blood b) {
        if (this.live && b.isLive() && this.getRect()
                                           .intersects(b.getRect())) {
            this.life = 100;
            b.setLive(false);
            return true;
        }
        return false;
    }

    public int getAngle() {
        return this.angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}