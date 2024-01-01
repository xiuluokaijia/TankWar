package com.bjsxt.tank;

import java.util.Random;

public class AI implements Runnable {
    private Tank tank;
    private AIActions action;
    private int rotationAngle;
    private final Random random = new Random();
    public AI(Tank tank) {
        this.action = generateAction();
        this.tank = tank;
    }

    public AIActions generateAction() {
        AIActions[] actions = AIActions.values();
        return actions[this.random.nextInt(actions.length - 1)];
    }                           //随机给与一个状态

    public AIActions getAction() {
        return this.action;
    }
                                //返回状态
    public void changeActions(AIActions action) {
        this.action = action;
    } // 在Tank.move中调用用于解除瞄准状态至其他状态
                                //改变状态
    public int getRotationAngle() {
        return this.rotationAngle;
    }

    @Override
    public void run() {
        while (tank.isLive()) {
            int interval = this.random.nextInt(3000) + 1000;
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.action = generateAction();
            // 旋转状态，随机生成目标度数
            if (this.action == AIActions.ROTATING) {
                this.rotationAngle = this.random.nextInt(361);
            }
            // 瞄准状态，在Tank.move中进行进一步实现
            // 停留状态与前进状态同样在Tank.move中实现
        }
    }
}


