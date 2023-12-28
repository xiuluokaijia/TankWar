package com.bjsxt.tank;
import java.awt.*;

public class Blood {
	int x, y, w, h;			//血块的大小和位置
	TankClient tc;			//生成一个tankclient对象用来与游戏中的其它元素及性能交互

	int step = 0;			//血块的相对位置，即它的运动到了第几步
	private boolean live = true;	//表示血块是否存活

	//指明血块运动的轨迹，由pos中各个点构成
	private int[][] pos = {
			{350, 300}, {360, 300}, {375, 275}, {400, 200}, {360, 270}, {365, 290}, {340, 280}
	};

	public Blood() {
		x = pos[0][0];
		y = pos[0][1];
		w = h = 15;
	}

	public void draw(Graphics g) {
		if(!live) return;

		Color c = g.getColor();
		g.setColor(Color.MAGENTA);
		g.fillRect(x, y, w, h);
		g.setColor(c);

		move();
	}

	private void move() {
		step ++;
		if(step == pos.length){
			step = 0;
		}
		x = pos[step][0];
		y = pos[step][1];
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, w , h);
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

}
