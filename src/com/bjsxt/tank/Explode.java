package com.bjsxt.tank;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

//控制爆炸效果部分

public class Explode {
	int x, y;
	private boolean live = true;
	
	private TankClient tc ;
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	
	private static Image[] imgs = {
		tk.getImage(Explode.class.getClassLoader().getResource("images/0.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/1.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/2.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/3.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/4.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/5.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/6.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/7.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/8.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/9.gif")),
		tk.getImage(Explode.class.getClassLoader().getResource("images/10.gif"))
	};					//获取爆炸的图片
	
	int step = 0;		//表示爆炸图片应有的步数
	
	private static boolean init = false;
	
	public Explode(int x, int y, TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	
	public void draw(Graphics g) {
		
		if(!init) {
			for (int i = 0; i < imgs.length; i++) {
				g.drawImage(imgs[i], -100, -100, null);
			}			
			init = true;
		}
		//一开始在画面外绘制所有爆炸图片
		
		if(!live) {
			tc.explodes.remove(this);
			return;
		}
		
		if(step == imgs.length) {
			live = false;
			step = 0;
			return;
		}
		
		g.drawImage(imgs[step], x, y, null);
		
		step ++;
	}
}
