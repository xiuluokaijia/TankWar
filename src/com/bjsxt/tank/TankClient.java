package com.bjsxt.tank;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 这个类的作用是坦克游戏的主窗口
 *
 * @author mashibing
 */

public class TankClient extends Frame {
    /**
     * 整个坦克游戏的宽度
     */
    //public static final int GAME_WIDTH = 1600;
    //public static final int GAME_HEIGHT = 900;
    public static final int GAME_WIDTH = 3840;
    public static final int GAME_HEIGHT = 2160;
    public boolean isPaused = false;
    public static final int ENEMY_DETECTION_RANGE = 100;
    // 游戏强度
    private float intensity = 5;

    public float getIntensity() {
        return this.intensity;
    }

    public void addIntensity() {
        this.intensity += 0.5F;
    }

    public void resetIntensity() {
        this.intensity = 1;
    }

    Tank myTank = new Tank(50, 50, true, Direction.STOP, this);

    Wall w1 = new Wall(100, 200, 20, 150, this), w2 = new Wall(300, 100, 300, 20, this);

    List<Explode> explodes = new CopyOnWriteArrayList<>();
    List<Missile> missiles = new CopyOnWriteArrayList<>();
    List<Tank> tanks = new CopyOnWriteArrayList<>();
    Image offScreenImage = null;
    private static final Toolkit tk = Toolkit.getDefaultToolkit();
    static Image backGround;
    Blood b = new Blood();

    //搞个blood却没有使用，所以整个blood类都是不必要看的
    public class Move implements Runnable { // 刷新各渲染对象的位置，单独线程
        @Override
        public void run() {
            while (!isPaused) {
                tanks.forEach(Tank::move);
                missiles.forEach(Missile::move);
                myTank.move();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void paint(Graphics g) {
        /*
         * 指明子弹-爆炸-坦克的数量
         * 以及坦克的生命值
         */
        g.drawString("missiles count:" + missiles.size(), 10, 50);
        g.drawString("explodes count:" + explodes.size(), 10, 70);
        g.drawString("tanks    count:" + tanks.size(), 10, 90);
        g.drawString("tanks     life:" + myTank.getLife(), 10, 110);
        //绘制计数板，这个动作是需要反复刷新进行的

        if (tanks.size() <= 0) {
            for (int i = 0; i < Integer.parseInt(PropertyMgr.getProperty("reProduceTankCount")); i++) {
                tanks.add(new Tank(50, 50, false, Direction.L, this));
            }
        }            //控制坦克再次生成的部分，坦克的数量过少则会触发add添加新的坦克进入
        //这个动作同样需要刷新验证数量上是否符合要求
        for (int i = 0; i < missiles.size(); i++) {
            Missile m = missiles.get(i);
            m.hitTanks(tanks);            //检测子弹是否击中敌方坦克
            m.hitTank(myTank);            //检测己方坦克是否被击中
            m.hitWall(w1);
            m.hitWall(w2);
            m.draw(g);
            //if(!m.isLive()) missiles.remove(m);
            //else m.draw(g);
        }

        for (int i = 0; i < explodes.size(); i++) {
            Explode e = explodes.get(i);
            e.draw(g);
        }                    //每一次刷新界面都会对每一个爆炸

        for (int i = 0; i < tanks.size(); i++) {
            Tank t = tanks.get(i);
            t.collidesWithWall(w1);    //
            t.collidesWithWall(w2);    //
            t.collidesWithTanks(tanks);//
            t.draw(g);
        }

        myTank.draw(g);
        //myTank.eat(b);
        w1.draw(g);
        w2.draw(g);
        //b.draw(g);
    }

    public void update(Graphics g) {
        //被线程循环调用
        //update的参数是由系统自动创建的

        if (offScreenImage == null) {
            offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
        }
        //屏外画面，创造影像
        Graphics gOffScreen = offScreenImage.getGraphics();
        //创建graphic型取自屏外影像
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.BLACK);

        //gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);


        gOffScreen.drawImage(backGround, 0, 0, null);
        //在屏外画布上添加background组件
        gOffScreen.setColor(c);
        paint(gOffScreen);
        //在屏外画布上绘画其他组件
        g.drawImage(offScreenImage, 0, 0, null);
    }

    /**
     * 本方法显示坦克主窗口
     *
     */
    static {
        backGround = tk.getImage(TankClient.class.getClassLoader()
                                                 .getResource("images/background.jpg"));
    }

    public void lauchFrame() {

        int initTankCount = Integer.parseInt(PropertyMgr.getProperty("initTankCount"));
        for (int i = 0; i < initTankCount; i++) {
            tanks.add(new Tank(50 + 40 * (i + 1), 50, false, Direction.D, this));
        }

        //this.setLocation(400, 300);
        this.setSize(GAME_WIDTH, GAME_HEIGHT);
        this.setTitle("TankWar");
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        //this.setResizable(false);

        this.setUndecorated(true);

        this.setBackground(Color.GREEN);

        this.addKeyListener(new KeyMonitor());

        setVisible(true);

        new Thread(new PaintThread()).start();
        new Thread(new Move()).start();
        new Thread(()->{
            Random r = new Random();
            while(!isPaused){
                float interval = (r.nextInt(3000) + 1000) / this.intensity;
                try {
                    Thread.sleep((int) interval);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                tanks.forEach(Tank::AIFire);
            }
        }).start();
    }

    public static void main(String[] args) {

        TankClient tc = new TankClient();
        tc.lauchFrame();                //游戏的入口
    }

    private class PaintThread implements Runnable {
        //线程绘制，实现了runnable接口，可以作为线程的任务执行
        public void run() {
            while (true) {
                repaint();
                //会向paint和update发送请求运行这两个方法。
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }                //runnable接口所要实现的方法
    }

    private class KeyMonitor extends KeyAdapter {

        public void keyReleased(KeyEvent e) {
            myTank.keyReleased(e);
        }

        public void keyPressed(KeyEvent e) {
            myTank.keyPressed(e);
        }

    }
}













