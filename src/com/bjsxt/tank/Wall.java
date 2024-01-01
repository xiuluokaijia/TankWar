package com.bjsxt.tank;

import java.awt.*;

public class Wall {
	int x, y, w, h;
	TankClient tc ;			//tankclient类的tc是wall的一个属性，这是否意味着一种归属？
	
	public Wall(int x, int y, int w, int h, TankClient tc) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.tc = tc;
	}						//构造函数
	
	public void draw(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.white);
		g.fillRect(x, y, w, h);
		g.setColor(c);
	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, w, h);
	}
	//返回一个矩形区域，可用于检测碰撞等Rectangle
	//对象提供了用于执行这种碰撞检测的方法，例如intersects(Rectangle r)方法可以用于检测两个矩形是否相交
}
