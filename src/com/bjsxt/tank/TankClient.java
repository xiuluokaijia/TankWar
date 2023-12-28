package com.bjsxt.tank;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类的作用是坦克游戏的主窗口
 * @author mashibing
 *
 */

public class TankClient extends Frame {
	/**
	 * 整个坦克游戏的宽度
	 */
	//public static final int GAME_WIDTH = 1600;
	//public static final int GAME_HEIGHT = 900;
	public static final int GAME_WIDTH = 3840;
	public static final int GAME_HEIGHT = 2160;

	Tank myTank = new Tank(50, 50, true, Direction.STOP, this);

	Wall w1 = new Wall(100, 200, 20, 150, this), w2 = new Wall(300, 100, 300, 20, this);

	List<Explode> explodes = new ArrayList<Explode>();
	List<Missile> missiles = new ArrayList<Missile>();
	List<Tank> tanks = new ArrayList<Tank>();
	Image offScreenImage = null;
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	static Image backGround=null;
	Blood b = new Blood();

	public void paint(Graphics g) {
		/*
		 * 指明子弹-爆炸-坦克的数量
		 * 以及坦克的生命值
		 */
		g.drawString("missiles count:" + missiles.size(), 10, 50);
		g.drawString("explodes count:" + explodes.size(), 10, 70);
		g.drawString("tanks    count:" + tanks.size(), 10, 90);
		g.drawString("tanks     life:" + myTank.getLife(), 10, 110);

		if(tanks.size() <= 0) {
			for(int i=0; i<Integer.parseInt(PropertyMgr.getProperty("reProduceTankCount")); i++) {
				tanks.add(new Tank(50 , 50, false, Direction.L, this));
			}
		}

		for(int i=0; i<missiles.size(); i++) {
			Missile m = missiles.get(i);
			m.hitTanks(tanks);
			m.hitTank(myTank);
			m.hitWall(w1);
			m.hitWall(w2);
			m.draw(g);
			//if(!m.isLive()) missiles.remove(m);
			//else m.draw(g);
		}

		for(int i=0; i<explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}

		for(int i=0; i<tanks.size(); i++) {
			Tank t = tanks.get(i);
			//t.collidesWithWall(w1);
			//t.collidesWithWall(w2);
			//t.collidesWithTanks(tanks);
			t.draw(g);
		}

		myTank.draw(g);
		//myTank.eat(b);
		w1.draw(g);
		w2.draw(g);
		//b.draw(g);
	}

	public void update(Graphics g) {
		if(offScreenImage == null) {
			offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.BLACK);

		//gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);


		gOffScreen.drawImage(backGround, 0, 0, null);

		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	/**
	 * 本方法显示坦克主窗口
	 *
	 */
	static {
		backGround=tk.getImage(TankClient.class.getClassLoader().getResource("images/background.jpg"));
	}

	public void lauchFrame() {

		int initTankCount = Integer.parseInt(PropertyMgr.getProperty("initTankCount"));
		for(int i=0; i<initTankCount; i++) {
			tanks.add(new Tank(50 + 40*(i+1), 50, false, Direction.D, this));
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
	}

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.lauchFrame();
	}

	private class PaintThread implements Runnable {

		public void run() {
			while(true) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
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













