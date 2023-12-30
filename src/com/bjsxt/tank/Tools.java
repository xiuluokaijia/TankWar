package com.bjsxt.tank;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tools {
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
    public static void rotateTo(Tank tank,Direction direction) {
        final int rSpeed = Tank.RSPEED;
        int angle = tank.getAngle();
        switch (direction) {
            case L -> {
                if (angle == 270) break;
                int tempAngle = angle;
                if (angle <= 90 || angle > 270) {
                    tempAngle -= rSpeed;
                    if (tempAngle < 0) {
                        tempAngle += 360;
                    }
                    if (tempAngle >= 270 - rSpeed && tempAngle < 270) tempAngle = 270;
                } else {
                    tempAngle += rSpeed;
                    if (tempAngle > 270) tempAngle = 270;
                }
                angle = tempAngle;
            }
            case R -> {
                if (angle == 90) break;
                int tempAngle = angle;
                if (angle <= 90 || angle > 270) {
                    tempAngle += rSpeed;
                    if (tempAngle > 360) {
                        tempAngle -= 360;
                    }
                    if (tempAngle > 90) tempAngle = 90;
                } else {
                    tempAngle -= rSpeed;
                    if (tempAngle < 90) tempAngle = 90;
                }
                angle = tempAngle;
            }
            case U -> {
                if (angle == 0) break;
                int tempAngle = angle;
                if (angle <= 180) {
                    tempAngle -= rSpeed;
                    if (tempAngle < 0)
                        tempAngle = 0;
                } else {
                    tempAngle += rSpeed;
                    if (tempAngle >= 360)
                        tempAngle = 0;
                }
                angle = tempAngle;
            }
            case D -> {
                if (angle == 180) break;
                int tempAngle = angle;
                if (angle < 180) {
                    tempAngle += rSpeed;
                    if (tempAngle >180)
                        tempAngle = 180;
                } else {
                    tempAngle -= rSpeed;
                    if (tempAngle <180)
                        tempAngle = 180;
                }
                angle = tempAngle;
            }
        }
        tank.setAngle(angle);
    }
}
