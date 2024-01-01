package com.bjsxt.tank;
import java.awt.*;

public class Blood {
	int x, y;
	int w=50, h=52;			//血块的大小和位置，xy表示坐标位置，wh表示宽高
	TankClient tc;			//生成一个tankclient对象用来与游戏中的其它元素及性能交互

	private static final Toolkit tk = Toolkit.getDefaultToolkit();
	private boolean live = true;	//表示血块是否存活，这个存活实际上也相当抽象，就是存在的意思

	                        //指明血块运动的轨迹，由pos中各个点构成
						//血的不同的位置

	public Blood(int x,int y,TankClient tc) {
		this.x=x;
		this.y=y;
		this.tc=tc;
	}						//构造函数

	private static final Image bloodImage;

	static {
		bloodImage =
				tk.getImage(Missile.class.getClassLoader()
						.getResource("images/blood.png"));
		Tools.waitForImageLoad(bloodImage);
	}
	public void draw(Graphics g) {
		if (!live) {
			tc.bloods.remove(this);
			return;
		}
		g.drawImage(this.bloodImage, (int) x, (int) y, null);
		             //有关与在gui中绘制自己，值得研究
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

	public boolean beEat(Tank t) {
		if (this.live && t.isLive() && this.getRect()
				.intersects(t.getRect())) {
			t.setLife(100);
			this.live=false;

			return true;
		}
		return false;
	}

	public boolean beEat(java.util.List<Tank> tanks){
		for(int i=0;i<tanks.size();i++){
			Tank t=tanks.get(i);
			if(this.beEat(t)){
				return true;
			}
		}
		return false;
	}

}



