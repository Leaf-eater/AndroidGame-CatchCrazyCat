package com.yuhang.catchcrazycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by 宇航 on 2017/5/15.
 *
 */

public class PlayGround extends SurfaceView implements View.OnTouchListener{

    private static int WIDTH = 80;  //一个圈的直径
    private static int ROW = 10;    //默认的行数
    private static int COL = 10;    //默认的列数
    private static int BLOCKS = 10;     //默认的路障值
    private static int OFFSET_X = 0;    //图像矩阵的X偏移量
    private static int OFFSET_Y = 0;    //图像矩阵的Y偏移量

    private Dot[][] matrix;
    private Dot cat;

    public PlayGround(Context context) {
        super(context);
        getHolder().addCallback(callback);
        matrix = new Dot[ROW][COL];
//        向矩阵赋值
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j] = new Dot(j, i);
            }
        }
        setOnTouchListener(this);
        initGame();
    }

    /**
     * 实现界面的绘制
     */
    private void reDraw() {
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(Color.LTGRAY);
        Paint paint = new Paint();
        for (int i = 0; i < ROW; i++) {
            if (i % 2 != 0) {   //奇数行
                OFFSET_X = WIDTH/2;
            }
            for (int j = 0; j < COL; j++) {
                Dot one = getDot(j, i);
                switch (one.getStatus()) {
                    case Dot.STATUS_OFF:
                        paint.setColor(0xFFEEEEEE);
                        break;
                    case Dot.STATUS_ON:
                        paint.setColor(0xFFFFAA00);
                        break;
                    case Dot.STATUS_IN:
                        paint.setColor(0xFFFF0000);
                        break;
                    default:
                        break;
                }
                canvas.drawOval(new RectF(one.getX()*WIDTH + OFFSET_X,one.getY()*WIDTH+OFFSET_Y,
                        (one.getX()+1)*WIDTH + OFFSET_X,(one.getY()+1)*WIDTH+OFFSET_Y), paint);
            }
            OFFSET_X = 0;
        }
        getHolder().unlockCanvasAndPost(canvas);
    }

    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
//            reDraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            WIDTH = width/(COL+1);
            OFFSET_Y = (height - (WIDTH * ROW))/2;
            reDraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private void  initGame() {
//        初始化所有点
        for (Dot[] a : matrix) {
            for (Dot dot : a) {
                dot.setStatus(Dot.STATUS_OFF);
            }
        }
//        初始化猫
        cat = getDot(4, 5);
//     todo   cat.setStatus(Dot.STATUS_IN);
        getDot(4, 5).setStatus(Dot.STATUS_IN);
//      生成路障
        Random random = new Random();
        for (int i = 0; i < BLOCKS;) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);
            if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {
                getDot(x,y).setStatus(Dot.STATUS_ON);
                i++;
            }
        }
    }

    private Dot getDot(int x, int y) {
        return matrix[y][x];
    }

    /**
     * 获取一个点的邻居点，一个点的邻居一共有留个1-6
     * 左侧为1，左上为2，右上为3，右侧为4，右下为5，左下为6
     * @param source 起始点
     * @param dir 起始点的某个方向
     * @return 起始点对应方向的值
     */
    private Dot getNeighbor(Dot source, int dir) {
        switch (dir) {
            case 1:     //左侧
                return getDot(source.getX() - 1, source.getY());
            case 2:     //左上
                if (source.getY() % 2 == 0) {
                    return getDot(source.getX()-1, source.getY()-1);
                } else {
                    return getDot(source.getX(), source.getY()-1);
                }
            case 3:     //右上
                if (source.getY() % 2 == 0) {
                    return getDot(source.getX(), source.getY()-1);
                } else {
                    return getDot(source.getX()+1, source.getY()-1);
                }
            case 4:     //右侧
                return getDot(source.getX() + 1, source.getY());
            case 5:     //右下
                if (source.getY() % 2 == 0) {
                    return getDot(source.getX(), source.getY()+1);
                } else {
                    return getDot(source.getX()+1, source.getY()+1);
                }
            case 6:     //左下
                if (source.getY() % 2 == 0) {
                    return getDot(source.getX()-1, source.getY()+1);
                } else {
                    return getDot(source.getX(), source.getY()+1);
                }
            default:
                break;
        }
        return null;
    }

    private boolean isAtEdge(Dot dot) {
        return dot.getX() * dot.getY() == 0 || dot.getX() + 1 == COL || dot.getY() + 1 == ROW;
    }

    /**
     * 返回dot到在dir方向最远能走多远<br>
     * 注意：当点位于边缘时所有方向的返回值都为0，因为高边缘时需要进行胜利条件的判断
     * @param dot 原点
     * @param dir 方向
     * @return 返回一个距离，正数代表最后到达了边界，负数代表了受到了阻碍
     */
    private int getDistance(Dot dot, int dir) {
        Dot source = dot;
        Dot next;
        int distance = 0;
        while (true) {
            if (isAtEdge(source)) return distance;
            next = getNeighbor(source, dir);
            if (next.getStatus() == Dot.STATUS_ON) {
                return distance*-1;
            } else {
                source = next;
                distance++;
            }
        }
    }

    /**
     * 将cat从一个点移动到dot
     * @param dot 需要移动到的目标点
     */
    private void moveTo(Dot dot) {
        dot.setStatus(Dot.STATUS_IN);
        cat.setStatus(Dot.STATUS_OFF);
        cat = dot;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x, y;
            y = (int) ((event.getY()-OFFSET_Y) / WIDTH);
            if (y % 2 == 0) {
                x = (int) (event.getX() / WIDTH);
            } else {
                x = (int) ((event.getX() - WIDTH / 2) / WIDTH);
            }
            if (x + 1 > COL || y + 1 > ROW || x < 0 || y < 0) {
                initGame();
            } else if (getDot(x,y).getStatus()==Dot.STATUS_OFF){
                getDot(x, y).setStatus(Dot.STATUS_ON);
                move();
            }
            reDraw();
        }
        return true;
    }

    /**
     *  寻找下一个可用的最优路径：<br>
     *  当有可以直接到达边缘的路径时：选择最近的一条路径
     *  当没有可以直接到达边缘的路径时：选择到障碍点最长的路径
     */
    private void move() {
//        失败条件的判断
        if (isAtEdge(cat)) {
            lose();
            return;
        }
//        成功条件的判断：available中没有可用的点
        ArrayList<Dot> available = new ArrayList();
        HashMap<Dot,Integer> toEdge = new HashMap();
        HashMap<Dot, Integer> toBlock = new HashMap<>();
        for (int i = 1; i < 7; i++) {
            Dot neighbor = getNeighbor(cat, i);
            if (neighbor.getStatus() == Dot.STATUS_OFF) {
                available.add(neighbor);
            }
            int distance = getDistance(cat, i);
            if (distance > 0) {
                toEdge.put(neighbor,distance);
            } else if (distance < 0) {
                toBlock.put(neighbor, distance);
            }
        }
        if (available.size() == 0) {
            win();
            return;
        }
//        移动路径的判断
        Dot nextDot = null;
        if (toEdge.size() != 0) {   //有可以直接到边缘的路径
            Log.i("test", "移动到边缘");
            int minLength = ROW + 1;    //初始赋值，赋值为该图最大路径+1
            Iterator<Map.Entry<Dot, Integer>> it = toEdge.entrySet().iterator();
//            当距离小于当前最小距离时，将新的点赋值给nextDot
            while (it.hasNext()) {
                Map.Entry<Dot,Integer> entry= it.next();
                if (entry.getValue() < minLength) {
                    nextDot = entry.getKey();
                    minLength = entry.getValue();
                }
            }
        } else {    //没有可以直接到边缘的路径
            Log.i("test", "移动到障碍");
            int minLength = 0;    //初始赋值，赋值为该图最大路径+1
            Iterator<Map.Entry<Dot, Integer>> it = toBlock.entrySet().iterator();
//            当距离小于当前最小距离时，将新的点赋值给nextDot
            while (it.hasNext()) {
                Map.Entry<Dot,Integer> entry= it.next();
                if (entry.getValue() < minLength) {
                    nextDot = entry.getKey();
                    minLength = entry.getValue();
                }
            }
        }
        moveTo(nextDot);
    }



    private void lose() {
        Toast.makeText(getContext(),"you lose",Toast.LENGTH_SHORT).show();
    }

    private void win() {
        Toast.makeText(getContext(), "you win", Toast.LENGTH_SHORT).show();
    }

}
