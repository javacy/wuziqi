package com.cheng.wuziqi;

import android.graphics.Point;

import java.util.List;

public class Utils {
    private int MAX_COUNT_IN_LINE = 5;

    //输赢逻辑判断 相邻5个是否是同颜色
    public boolean checkFiveINLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;

            boolean win = checkHorizontail(x, y, points);
            if(win) return true;
            win = checkVertial(x, y, points);
            if(win) return true;
            win = checkleftDaigonal(x, y, points);
            if(win) return true;
            win = checkRDaigonal(x, y, points);
            if(win) return true;
        }
        return false;
    }

    //判断x,y的棋子，横向是否有相邻的五个棋子
    private boolean checkHorizontail(int x, int y, List<Point> points) {
        int count = 1;
        //ZUO
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {//左边第一个棋子 x-i
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;//如果左边够，就不用在执行下面
        //YOU
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {//右边第一个棋子 x-i
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    //纵向
    private boolean checkVertial(int x, int y, List<Point> points) {
        int count = 1;
        //上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        //下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y +i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return false;
    }

    //左斜
    private boolean checkleftDaigonal(int x, int y, List<Point> points) {
        int count = 1;
        //左下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        //左上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return false;
    }
    //右斜
    private boolean checkRDaigonal(int x, int y, List<Point> points) {
        int count = 1;
        //右下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        //右上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return false;
    }
}
