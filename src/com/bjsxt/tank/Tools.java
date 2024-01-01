package com.bjsxt.tank;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Tools {
    // 通过反正切计算相对位置的度数
    public static int calcAngle(float x1, float y1, float x2, float y2) {
        double dx = (double) x1 - x2;
        double dy = (double) y1 - y2;
        double angle = Math.atan2(dy, dx);
        double degrees = Math.toDegrees(angle);
        degrees = (degrees + 360) % 360;
        return (int) degrees+90;
    }

    // 平方根快速算法
    private static float sqrt(float x) {
        int i = Float.floatToRawIntBits(x);
        i = 0x1fbcf800 + (i >> 1);
        float y = Float.intBitsToFloat(i);
        return 0.5f * (y + x / y);
    }

    public static float getDistance(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return sqrt(dx * dx + dy * dy);
    }

    public static  float getDistanceofMytank(Tank player,int x,int y){
        Rectangle playerPos = player.getRect();
        int playerX = playerPos.x + playerPos.width / 2-15, playerY = playerPos.y + playerPos.height / 2-10;
        return Tools.getDistance(playerX, playerY, (int) x, (int) y);
    }
    //求玩家坦克距离




    // 等待图像加载
    public static void waitForImageLoad(Image img) {
        MediaTracker tracker = new MediaTracker(new Container());
        tracker.addImage(img, 0);
        try {
            tracker.waitForAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 旋转图像
    public static Image rotateImg(Image img, int angle) {
        int width = img.getWidth(null);
        int height = img.getHeight(null);

        BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.rotate(Math.toRadians(angle), (double) width / 2, (double) height / 2);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotatedImage;
    }

    // 计算旋转后角度
    public static int rotateTo(int angle, int targetAngle, int rSpeed) {
        int expectationAngle = angle - targetAngle;
        // 规范化
        if (expectationAngle > 180) expectationAngle -= 360;
        if (expectationAngle < -180) expectationAngle += 360;
        // 期望偏移量过小直接吸附至目标角度
        if (expectationAngle < rSpeed && expectationAngle > -rSpeed) return targetAngle;

        // 偏移方向flag
        int dirFlag = 1;
        if (expectationAngle > 0) dirFlag = -1;
        // 做最后一步过滤
        int tempAngle = angle + (rSpeed * dirFlag);
        if (tempAngle < 0) tempAngle += 360;
        else if (tempAngle > 360) tempAngle -= 360;
        return tempAngle;
    }

    public static void rotateAction(Tank tank, Direction direction) {
        final int rSpeed = Tank.RSPEED;
        int angle = tank.getAngle();
        switch (direction) {
            case L -> {
                angle = rotateTo(angle, 270, rSpeed);
            }
            case R -> {
                angle = rotateTo(angle, 90, rSpeed);
            }
            case U -> {
                angle = rotateTo(angle, 0, rSpeed);
            }
            case D -> {
                angle = rotateTo(angle, 180, rSpeed);
            }
            case LD -> {
                angle = rotateTo(angle, 225, rSpeed);
            }
            case LU -> {
                angle = rotateTo(angle, 315, rSpeed);
            }
            case RD -> {
                angle = rotateTo(angle, 135, rSpeed);
            }
            case RU -> {
                angle = rotateTo(angle, 45, rSpeed);
            }
        }
        tank.setAngle(angle);
    }
}
