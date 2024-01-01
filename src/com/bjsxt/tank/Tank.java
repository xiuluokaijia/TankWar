package com.bjsxt.tank;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Random;

public class Tank {
    public static final int SPEED = 1, RSPEED = 2;
    private int angle = 0;
    private boolean live = true;
    private final BloodBar bb = new BloodBar();

    private int life = 100;

    TankClient tc;
    private Thread aiThread = null;
    private AI ai = null;
    private final boolean player;
    //表示是否是玩家操作的玩家坦克
    private float x, y;
    private float oldX, oldY;

    private static final Random r = new Random();

    private boolean bL = false, bU = false, bR = false, bD = false;

    private Direction dir = Direction.STOP;
    private Direction ptDir = Direction.L;

    private static final Toolkit tk = Toolkit.getDefaultToolkit(); //初始化awt工具包
    // 加载tank图像，并等待加载完成
    private static final Image tankImage;

    static {
        tankImage = tk.getImage(Tank.class.getClassLoader()
                                          .getResource("images/TankU.png"));
        Tools.waitForImageLoad(tankImage);
    }

    public Image getTankImg() { //旋转图像
        return Tools.rotateImg(tankImage, this.angle);
    }
    //键盘映射

    public static final int WIDTH = 40;
    public static final int HEIGHT = 45;

    public Tank(int x, int y, boolean player) {
        this.x = x;
        this.y = y;
        this.oldX = x;
        this.oldY = y;
        this.player = player;
        if (!player) {
            this.ai = new AI(this);
            this.aiThread = new Thread(this.ai);
            aiThread.start();
        }
    }

    public Tank(int x, int y, boolean player, Direction dir, TankClient tc) {
        this(x, y, player);
        this.dir = dir;
        this.tc = tc;
    }

    public Tank(boolean player,TankClient tc,Direction dir){
        this.player=player;
        this.tc=tc;
        this.dir=dir;
        Random random = new Random();
        // 生成随机坐标
        int randomX = random.nextInt(TankClient.GAME_WIDTH);
        int randomY = random.nextInt(TankClient.GAME_HEIGHT);
        this.x=randomX;
        this.oldX=x;
        this.y=randomY;
        this.oldY=y;
        //随机获得坐标
        while (this.collidesWithWalls(tc.walls) || this.collidesWithTanks(tc.tanks)||this.waitForguarding(tc.myTank)) {
            // 重新生成随机坐标
            randomX = random.nextInt(tc.GAME_WIDTH);
            randomY = random.nextInt(tc.GAME_HEIGHT);
            this.x = randomX;
            this.oldX=x;
            this.y = randomY;
            this.oldY=y;
        }
        if (!player) {
            this.ai = new AI(this);

            this.aiThread = new Thread(this.ai);
            aiThread.start();
        }
    }

    public boolean waitForguarding(Tank mytank){


            float distance=Tools.getDistanceofMytank(mytank,(int)this.x,(int)this.y);
            if(distance<=this.tc.ENEMY_DETECTION_RANGE){
                return true;
            }



        return false;
    }



    public void draw(Graphics g) {
        if (!live) {
            if (!player) {
                tc.tanks.remove(this);
            }
            return;
        }            //如果！live且！good则从tanks数组中移除



         if(player) bb.draw(g);
        g.drawImage(this.getTankImg(), (int) x, (int) y, null);
    }

    public void move() {
        int playerX = 0, playerY = 0;
        this.oldX = x;
        this.oldY = y;
        if (this.player) {
            Tools.rotateAction(this, dir); // 逐渐旋转至指定角度
        } else {
            // 处理ai索敌以及巡逻逻辑
            if(!tc.myTank.isLive()){
                if (this.ai.getAction() == AIActions.AIMING)
                    this.ai.changeActions(AIActions.HEADING);

            }
            else {
                Rectangle playerPos = tc.myTank.getRect();
                playerX = playerPos.x + playerPos.width / 2-15;
                playerY = playerPos.y + playerPos.height / 2-10;

                float distance = Tools.getDistanceofMytank(this.tc.myTank,(int)this.x,(int)this.y);
                //Tools.getDistance(playerX, playerY, (int) this.x, (int) this.y);

                if (distance <= tc.ENEMY_DETECTION_RANGE) {
                    this.ai.changeActions(AIActions.AIMING);
                    this.tc.warnings.add(new Warning(x,y,tc));          //进入警戒状态时添加警告标志
                } else {
                    if (this.ai.getAction() == AIActions.AIMING)
                        this.ai.changeActions(AIActions.HEADING);
                }
            }
            AIActions act = this.ai.getAction();
            switch (act) {
                case STAY ->{
                    this.dir = Direction.STOP;
                }
                case HEADING -> {
                    this.dir = Direction.GO;
                }
                case ROTATING -> {
                    this.dir = Direction.STOP;
                    this.angle = Tools.rotateTo(this.angle, ai.getRotationAngle(), RSPEED);// 逐渐旋转至指定角度
                }
                case AIMING -> {
                    this.angle = Tools.rotateTo(this.angle, Tools.calcAngle(playerX, playerY, this.x, this.y), RSPEED);
                    this.dir = Direction.STOP;
                }
            }
        }
        if (this.dir != Direction.STOP) {
            this.ptDir = this.dir;
        }
        // 处理位移：
        if (this.dir != Direction.STOP) {
            double radians = Math.toRadians(this.angle - 90); // 将角度转换为弧度
            x += (float) (SPEED * Math.cos(radians));
            y += (float) (SPEED * Math.sin(radians));
        } // 由朝向速度计算x,y的移动距离
        if (x < 0) x = 0 ;
        if (y < 30) y = 30;
        if (x + Tank.WIDTH > TankClient.GAME_WIDTH) x = TankClient.GAME_WIDTH - Tank.WIDTH;
        if (y + Tank.HEIGHT > TankClient.GAME_HEIGHT-10) y = TankClient.GAME_HEIGHT - Tank.HEIGHT-15;
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
                    tc.score=0;
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
        else if (bL && bU && !bR && !bD) dir = Direction.LU;
        else if (!bL && bU && !bR && !bD) dir = Direction.U;
        else if (!bL && bU && bR && !bD) dir = Direction.RU;
        else if (!bL && !bU && bR && !bD) dir = Direction.R;
        else if (!bL && !bU && bR && bD) dir = Direction.RD;
        else if (!bL && !bU && !bR && bD) dir = Direction.D;
        else if (bL && !bU && !bR && bD) dir = Direction.LD;
        else dir = Direction.STOP;
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
        Missile m = new Missile(x + 6, y + 1, this.angle - 90, player, this.tc);
        tc.missiles.add(m);
        return m;
    }

    private Missile fire(int angle) {
        if (!live) return null;
        int x = (int) (this.x + Tank.WIDTH / 2 - Missile.WIDTH / 2);
        int y = (int) (this.y + Tank.HEIGHT / 2 - Missile.HEIGHT / 2);
        Missile m = new Missile(x + 6, y + 1, angle - 90, player, this.tc);
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

    public boolean  collidesWithWalls(java.util.List<Wall> walls){
        for(int i=0;i<walls.size();i++){
            Wall w=walls.get(i);
            if(this.collidesWithWall(w)){
                return true;
            }
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
        if(tc.getIntensity()>3){
            fire(this.angle+30);
            fire(this.angle-30);
        }
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
            g.drawRect((int) x, (int) (y - 10), WIDTH, 7);
            int w = WIDTH * life / 100;
            g.fillRect((int) x, (int) (y - 10), w, 7);
            g.setColor(c);
        }
    }



    public int getAngle() {
        return this.angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }



    public void AIFire() {
        if (this.ai.getAction() == AIActions.AIMING) {
            this.superFire();
        }
    }

    public Blood giveBlood(){

            Blood b=new Blood((int)x,(int)y,tc);

            return b;
    }

}
