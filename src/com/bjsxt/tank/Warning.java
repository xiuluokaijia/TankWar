package com.bjsxt.tank;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

//控制爆炸效果部分

public class Warning {
    float x, y;
    private boolean live = true;

    private TankClient tc ;

    private static Toolkit tk = Toolkit.getDefaultToolkit();


    private static final Image warningimage;

    static {
        warningimage =
                tk.getImage(Warning.class.getClassLoader()
                        .getResource("images/warning.png"));
        Tools.waitForImageLoad(warningimage);
    }
    int step = 0;		//表示爆炸图片应有的步数

    //private static boolean init = false;

    public Warning(float x, float y, TankClient tc) {
        this.x = x;
        this.y = y;
        this.tc = tc;
    }

    public void draw(Graphics g) {



        if(!live) {
            tc.explodes.remove(this);
            return;
        }           //要改

        if(step == 5) {
            live = false;
            step = 0;
            return;
        }

        g.drawImage(warningimage, (int)x,(int) y, null);

        step ++;
    }
}