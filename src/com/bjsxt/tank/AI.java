package com.bjsxt.tank;

import java.util.Random;

public class AI implements Runnable {
    private Tank tank;
    private TankClient client;
    private AIActions action;
    private int rotationAngle;
    private final Random random = new Random();
    private final Thread shottingThread = new Thread(() -> {
        while (tank.isLive()) {
            float interval = (this.random.nextInt(3000) + 1000) / client.getIntensity();
            try {
                Thread.sleep((int) interval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.tank.fire();
        }
    });

    public AI(Tank tank) {
        this.action = generateAction();
        this.tank = tank;
    }

    public AIActions generateAction() {
        AIActions[] actions = AIActions.values();
        return actions[this.random.nextInt(actions.length - 1)];
    }

    public AIActions getAction() {
        return this.action;
    }

    public void changeActions(AIActions action) {
        this.action = action;
    } // 在Tank.move中调用用于解除瞄准状态至其他状态

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


