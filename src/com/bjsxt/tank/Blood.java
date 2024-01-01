package com.bjsxt.tank;
import java.awt.*;

public class Blood {
	int x, y;
	int w=20, h=20;			//血块的大小和位置，xy表示坐标位置，wh表示宽高
	TankClient tc;			//生成一个tankclient对象用来与游戏中的其它元素及性能交互


	private boolean live = true;	//表示血块是否存活，这个存活实际上也相当抽象，就是存在的意思

	                        //指明血块运动的轨迹，由pos中各个点构成
						//血的不同的位置

	public Blood(int x,int y,TankClient tc) {
		x=x;
		y=y;
		tc=tc;
	}						//构造函数

	public void draw(Graphics g) {
		if (!live) {
			tc.bloods.remove(this);

			Color c = g.getColor();
			g.setColor(Color.red);
			g.fillRect(x, y, w, h);
			g.setColor(c);
		}                //有关与在gui中绘制自己，值得研究
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



