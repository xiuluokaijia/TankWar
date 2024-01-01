package com.bjsxt.tank;

import java.awt.*;
import java.util.List;

public class Missile {
    public static final int SPEED = 2;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    private final int angle;
    float x, y;

    private boolean isPlayer;
    private boolean live = true;

    private TankClient tc;

    private static final Toolkit tk = Toolkit.getDefaultToolkit();
    private static final Image missileImage;

    static {
        missileImage =
                tk.getImage(Missile.class.getClassLoader()
                                         .getResource("images/MissileD.png"));
        Tools.waitForImageLoad(missileImage);
    }

    public Missile(int x, int y, int angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public Missile(int x, int y, int angle, boolean isPlayer, TankClient tc) {
        this(x, y, angle);
        this.isPlayer = isPlayer;
        this.tc = tc;
    }

    public Image getMissileImage() {
        return Tools.rotateImg(missileImage, this.angle+90);
    }

    public void draw(Graphics g) {
        if (!live) {
            tc.missiles.remove(this);
            //tc是各类的一个属性，但tc本身却存在很多这些类的方法
            return;
        }                    //不移动则移除
        g.drawImage(this.getMissileImage(), (int) x, (int) y, null);
    }

    public void move() {
        if (live) {
            double radians = Math.toRadians(this.angle); // 将角度转换为弧度
            x += (float) (SPEED * Math.cos(radians));
            y += (float) (SPEED * Math.sin(radians));
        }
            if (x < 0 || y < 0 || x > TankClient.GAME_WIDTH || y > TankClient.GAME_HEIGHT) {
                live = false;
            }                    //如果出界则不再继续存在}
        }

    public boolean isLive() {
        return live;
    }

    public Rectangle getRect() {
        return new Rectangle((int) x, (int) y, WIDTH, HEIGHT);
    }

    //外部得到private属性的public方法
    public boolean hitTank(Tank t) {
        if (this.live && this.getRect()
                             .intersects(t.getRect()) && t.isLive() && this.isPlayer != t.isPlayer()) {
                                            //关闭了友伤
            if (t.isPlayer()) {
                t.setLife(t.getLife() - 10);
                if (t.getLife() <= 0) t.setLive(false);
            } else {
                if(tc.getIntensity()>=2){
                    t.setLife(t.getLife() - 50);
                    if (t.getLife() <= 0) t.setLive(false);
                }
                if(tc.getIntensity()>=3){
                    t.setLife(t.getLife() - 34);
                    if (t.getLife() <= 0) t.setLive(false);
                }
                else{
                    t.setLive(false);
                }
            }
            this.live = false;
            Explode e = new Explode((int) x, (int) y, tc);
            //命中时创造explode对象并将其添加进tc内的数组当中
            tc.explodes.add(e);
            return true;
        }
        return false;
    }

    public boolean hitTanks(List<Tank> tanks) {
        for (int i = 0; i < tanks.size(); i++) {
            if (hitTank(tanks.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean hitWall(Wall w) {
        if (this.live && this.getRect()
                             .intersects(w.getRect())) {
            this.live = false;
            Explode e = new Explode((int) x, (int) y, tc);
            //命中时创造explode对象并将其添加进tc内的数组当中
            tc.explodes.add(e);
            return true;
        }
        return false;
    }

}
