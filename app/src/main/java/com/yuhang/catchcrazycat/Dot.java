package com.yuhang.catchcrazycat;

/**
 * Created by 宇航 on 2017/5/16.
 * Dot类表示一个地图上的圆圈，传入的x,y为这个圆圈的坐标值
 * 即这个圆圈在圆圈矩阵的第几行第几列。默认初始化会将这个
 * 圆圈的初始值设置为STATUS_OFF。
 */

public class Dot {
    private int  x,y;
    private int status;

    public static final int STATUS_ON = 1;      //表示已经设置路障的点
    public static final int STATUS_OFF = 0;     //表示未设置路障的点
    public static final int STATUS_IN = 9;      //表示猫所在的点

    public Dot(int x, int y) {
        this.x = x;
        this.y = y;
        status = STATUS_OFF;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
