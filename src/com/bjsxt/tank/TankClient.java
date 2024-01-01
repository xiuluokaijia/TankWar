package com.bjsxt.tank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    public static final int GAME_WIDTH = 1024;
    public static final int GAME_HEIGHT = 768;
    public boolean isPaused = false;
    private float intensity = 1;
    public  float ENEMY_DETECTION_RANGE = 200*getIntensity();        //索敌半径也可以根据等级提升

    int score;          //得分，对应等级
    // 游戏强度


    public float getIntensity() {
        return this.intensity;
    }

    Tank myTank = new Tank(50, 50, true, Direction.STOP, this);

    Wall w1 = new Wall(100, 200, 20, 150, this), w2 = new Wall(300, 500, 300, 20, this);

    List<Explode> explodes = new CopyOnWriteArrayList<>();
    List<Wall> walls = new CopyOnWriteArrayList<>();
    List<Missile> missiles = new CopyOnWriteArrayList<>();
    List<Tank> tanks = new CopyOnWriteArrayList<>();
    List<Warning> warnings = new CopyOnWriteArrayList<>();
    List<Warning> bloods = new CopyOnWriteArrayList<>();
    Image offScreenImage = null;
    private static final Toolkit tk = Toolkit.getDefaultToolkit();
    static Image backGround;
    //Blood b = new Blood();

    //搞个blood却没有使用，所以整个blood类都是不必要看的
    public class Move implements Runnable { // 刷新各渲染对象的位置，单独线程
        @Override
        public void run() {
            while (!isPaused) {
                tanks.forEach(Tank::move);
                for(Tank t:tanks){
                       // t.collidesWithWall(w1);
                        //t.collidesWithWall(w2);
                    t.collidesWithWalls(walls);                           //墙体集合
                    t.collidesWithTanks(tanks);//
                }
                missiles.forEach(Missile::move);
                myTank.move();
                myTank.collidesWithWalls(walls);
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
        // 保存原始字体
        Font originalFont = g.getFont();
        // 创建新的字体，例如，设置字体为宋体，大小为16
        Font newFont = new Font("SimSun", Font.BOLD, 20);
        // 设置 Graphics 对象的字体为新字体
        g.setFont(newFont);

        g.setColor(Color.white);
        // 绘制字符串
        g.drawString("missiles count:" + missiles.size(), 10, 40);
        g.drawString("explodes count:" + explodes.size(), 10, 60);
        g.drawString("tanks    count:" + tanks.size(), 10, 80);
        g.drawString("tanks     life:" + myTank.getLife(), 10, 100);
        g.drawString("player's    score:" + score, 10, 120);

        // 恢复原始字体
        g.setFont(originalFont);
        //绘制计数板，这个动作是需要反复刷新进行的

        if (tanks.size() <= 0) {

            for (int i = 0; i < Integer.parseInt(PropertyMgr.getProperty("reProduceTankCount")); i++) {
                tanks.add(new Tank(false,  this, Direction.L));
            }
        }               //控制坦克再次生成的部分，坦克的数量过少则会触发add添加新的坦克进入
                        //这个动作同样需要刷新验证数量上是否符合要求


        for (int i = 0; i < missiles.size(); i++) {
            Missile m = missiles.get(i);
                       //检测子弹是否击中敌方坦克
            m.hitTanks(tanks);
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
            if(!t.isLive()){
                score++;
                                //随机生成血包
            }
            t.draw(g);
        }

        for(int i=0;i<warnings.size();i++){
            Warning wa=warnings.get(i);
            wa.draw(g);
        }
        this.intensity = score/10 + 1;
        myTank.draw(g);
        //myTank.eat(b);
        w1.draw(g);
        w2.draw(g);
        //b.draw(g);
    }
    public static Image groupPhoto;

    static {
        backGround = tk.getImage(TankClient.class.getClassLoader()
                .getResource("images/160.jpg"));
        groupPhoto =tk.getImage(TankClient.class.getClassLoader()
                .getResource("images/groupphoto.jpg"));
        Tools.waitForImageLoad(groupPhoto);

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


    public void lauchFrame() {

        int initTankCount = Integer.parseInt(PropertyMgr.getProperty("initTankCount"));
        for (int i = 0; i < initTankCount; i++) {
            tanks.add(new Tank(false,  this, Direction.L));
        }
        walls.add( w1);
        walls.add(w2);
        score=0;
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
                float interval = (r.nextInt(3000) + 1000) / (this.intensity);
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
        Frame frame = new Frame("坦克大战");
        frame.setVisible(true);  // 设置窗口可见
        frame.setBounds(300, 300, 500, 500);  // 设置窗口位置和大小
        frame.setBackground(new Color(255, 150, 100));  // 设置窗口背景颜色
        frame.setResizable(true);  // 允许窗口大小可调整
        frame.setLayout(null);  // 关闭布局管理器

        // 创建 Panel（面板）
        Panel panel = new Panel(); // 使用 FlowLayout，垂直排列
        panel.setBounds(50, 50, 400, 400);  // 设置面板位置和大小
        panel.setBackground(new Color(193, 15, 60));  // 设置面板背景颜色

        // 创建“开始”按钮
        JButton startButton = new JButton("开始");
        startButton.setBounds(50, 50, 280, 120);  // 设置按钮位置和大小
        panel.add(startButton);  // 将按钮添加到面板
        //开始添加监听事件
        MybuttonStart mybuttonStart=new MybuttonStart();
        startButton.addActionListener(mybuttonStart);
        //如果你点击开始按钮就可以开始执行坦克大战


        // 创建“小组展示”按钮
        JButton resetButton = new JButton("小组展示");
        resetButton.setBounds(50, 250, 280, 120);  // 设置按钮位置和大小
        panel.add(resetButton);  // 将按钮添加到面板
        //开始添加监听事件
        MybuttonInformation mybuttonInformation=new MybuttonInformation();
        resetButton.addActionListener(mybuttonInformation);
        //如果你点击开始按钮就可以开始展示小组信息


        // 将面板添加到窗口
        frame.add(panel);


        //设置一个窗口的关闭
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //点击关闭的时候程序结束
                System.exit(0);
            }
        });//一会给主窗口去补一个


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

class Myframe extends Frame {
    static int id = 0;//设一个计数器

    public Myframe(int x, int y, int w, int h) {
        super("界面+" + (++id));
        setBounds(x, y, w, h);
        setVisible(true);
    }

};

//设置按钮监听事件
class MybuttonStart implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        //如果点击开始按钮就会进入坦克大战
        TankClient tc = new TankClient();
        tc.lauchFrame();
    }

}
class MybuttonInformation implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        //如果点击开始按钮就会进入信息展示
        Frame frame = new Frame("小组信息展示");
        frame.setVisible(true);  // 设置窗口可见
        frame.setBounds(300, 300, 500, 500);  // 设置窗口位置和大小
        frame.setBackground(new Color(3, 47, 222));  // 设置窗口背景颜色
        frame.setResizable(true);  // 允许窗口大小可调整
        frame.setLayout(null);  // 关闭布局管理器




        // 创建 Panel（面板）
        Panel panel = new Panel(); // 使用 FlowLayout，垂直排列
        panel.setBounds(50, 50, 400, 400);  // 设置面板位置和大小
        panel.setBackground(new Color(122, 217, 207));  // 设置面板背景颜色
        panel.setLayout(new GridLayout(9, 1));//文字信息是竖直排列

        //添加小组信息
        JLabel label0 = new JLabel("小组名称：原神");
        label0.setHorizontalAlignment(SwingConstants.CENTER); // 居中对齐
        panel.add(label0);

        JLabel label00 = new JLabel("  小组口号：jvm启动 ");
        label00.setHorizontalAlignment(SwingConstants.CENTER); // 居中对齐
        panel.add(label00);

        // 添加小组照片
        ImageIcon imageIcon = new ImageIcon(TankClient.groupPhoto); // 替换为实际图片路径
        JLabel imageLabel = new JLabel(imageIcon);
       imageLabel.setHorizontalAlignment(JLabel.CENTER);
       imageLabel.setVerticalAlignment(JLabel.CENTER);
//        imageLabel.setHorizontalAlignment(SwingConstants.CENTER); // 图片居中显示
        panel.add(imageLabel);

        //添加第一个人的介绍
        JLabel label1 = new JLabel("左：张宪冲 ");
        label1.setHorizontalAlignment(SwingConstants.CENTER); // 居中对齐
        panel.add(label1);

        JLabel label2 = new JLabel("学号：2225060781 ");
        label2.setHorizontalAlignment(SwingConstants.CENTER); // 居中对齐
        panel.add(label2);
        //添加第二个人的介绍
        JLabel label3 = new JLabel("中：王一衡 ");
        label3.setHorizontalAlignment(SwingConstants.CENTER); // 居中对齐
        panel.add(label3);

        JLabel label4 = new JLabel("学号：2225060762 ");
        label4.setHorizontalAlignment(SwingConstants.CENTER); // 居中对齐
        panel.add(label4);

        //添加第三个人的介绍
        JLabel label5 = new JLabel("右：李卓兴");
        label5.setHorizontalAlignment(SwingConstants.CENTER); // 居中对齐
        panel.add(label5);

        JLabel label6 = new JLabel("学号：2225060841 ");
        label6.setHorizontalAlignment(SwingConstants.CENTER); // 居中对齐
        panel.add(label6);
        frame.add(panel);
        //设置一个窗口的关闭
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 点击关闭时只关闭当前窗口
                frame.dispose();
            }
        });
    }

}
    /*public static void main(String[] args) {

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
}*/













