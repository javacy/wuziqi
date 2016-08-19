package com.cheng.wuziqi;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WuziqiPanel extends View {
    private int mPanelWidth;
    private float mlineHeight;//一小格的高度
    private int MAX_LINE = 10;

    //（画笔）类
    private Paint mpaint = new Paint();

    private Bitmap mWhitePice;
    private Bitmap mBlackPice;
    private float rationPieceofLineHigent = 3 * 1.0f / 4;//比例，决定棋子是高的3/4

    //白棋先手，当前轮到白棋
    private boolean misWhite = true;
    private ArrayList<Point> mWhiteArray = new ArrayList<>();//存棋子的坐标位置x和Y
    private ArrayList<Point> mBlackArray = new ArrayList<>();
    //标记输赢
    private boolean misGameOver;
    private boolean misWhiteWinner;

    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(0x44ff0000);
        init();
    }

    //初始化
    private void init() {
        //设置画笔的颜色灰黑
        mpaint.setColor(0x88000000);
        //设置画笔的锯齿效果
        mpaint.setAntiAlias(true);
        //防抖动（让画面更有质感）
        mpaint.setDither(true);
        //设置画笔的风格（实心）
        mpaint.setStyle(Paint.Style.STROKE);
        //初始化棋子图片
        mWhitePice = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPice = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //由于可能设置成Math_Content 如果widthSize为0 视图不会显示 故判断
        int width = Math.min(widthSize, heightSize);
        //MeasureSpec.UNSPECIFIED 源码：public static final int UNSPECIFIED = 0 << MODE_SHIFT;
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        //向父View申请指定宽高
        setMeasuredDimension(width, width);
    }

    //当宽高发生改变调用 (设置的是正方形) 跟尺寸有关调用

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mlineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int piceWidth = (int) (mlineHeight * rationPieceofLineHigent);//棋子尺寸
        //修改图片大小（第一个参数是待修改的Bitmap,第二第三个参数分别为修改后的宽、高，最后一个Boolean参数暂时不知什么作用）
        mWhitePice = Bitmap.createScaledBitmap(mWhitePice, piceWidth, piceWidth, false);
        mBlackPice = Bitmap.createScaledBitmap(mBlackPice, piceWidth, piceWidth, false);
    }

    //手势的监听
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (misGameOver) return false;//表明态度，告诉父类用户手势事件不用交给我
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) { //ACTION_DOWN 往下划，滚动时不能落子，不能写
            int x = (int) event.getX();
            int y = (int) event.getY();

//            Point p =new Point(x,y);
            /*这里存在一个问题：p代表需要绘制棋子的位置，
             但白棋已在那个，再次点击如何去重，如何不再绘制
             还有棋子落在顶角位置，因为x,y是mlineHeight/2得到，所以他每次的点击都有落差，
             该怎么避免—— 方法是 将位置记成数组下标形式
            */
            Point p = getValidPoint(x, y);//contains new Point 能返回true
            //验证我们是否已经绘制棋子
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }
            if (misWhite) {
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            //请求重绘
            invalidate();
            misWhite = !misWhite;
            // return true;//ACTION_DOWN 告诉父类view，如果有TOUCH事件，交给我来处理
        }
        return true;
    }

    //将位置记成数组下标形式
    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mlineHeight), (int) (y / mlineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*canvas （画布）类 画笔属性设置好之后，还需要将图像绘制到画布上。
        Canvas类可以用来实现各种图形的绘制工作，如绘制直线、矩形、圆等等。*/
        drawBoard(canvas);
        drawPice(canvas);
        checkGameOver();
    }

    //判断输赢 横竖斜5个
    private void checkGameOver() {
        Utils utils = new Utils();
        boolean whitewin = utils.checkFiveINLine(mWhiteArray);
        boolean blackwin = utils.checkFiveINLine(mBlackArray);

        if (whitewin || blackwin) {
            misGameOver = true;
            misWhiteWinner = whitewin;
            String t = misWhiteWinner ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(), t, Toast.LENGTH_LONG).show();
        }
    }

    //绘制棋子
    private void drawPice(Canvas canvas) {
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            //拿到棋子
            Point whitePiont = mWhiteArray.get(i);
            //坐标确定-棋子顶角起点处的位置，0位置左边还有半个棋子，0位置到边框是半个linehight.所以棋子左边开始位置是1/8的位置
            canvas.drawBitmap(mWhitePice,
                    (whitePiont.x + (1 - rationPieceofLineHigent) / 2) * mlineHeight,
                    (whitePiont.y + (1 - rationPieceofLineHigent) / 2) * mlineHeight, null);
        }
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPiont = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPice,
                    (blackPiont.x + (1 - rationPieceofLineHigent) / 2) * mlineHeight,
                    (blackPiont.y + (1 - rationPieceofLineHigent) / 2) * mlineHeight, null);
        }
    }

    //绘制棋盘
    private void drawBoard(Canvas canvas) {
        //棋盘宽度
        int w = mPanelWidth;
        float lineHeight = mlineHeight;
        for (int i = 0; i < MAX_LINE; i++) {
            //横线
            //横着的位置
            int startX = (int) (lineHeight / 2);//开始坐标 半个棋子的开始距离
            int endX = (int) (w - lineHeight / 2);//结束坐标
            //竖着的位置
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mpaint);
            //竖线
            canvas.drawLine(y, startX, y, endX, mpaint);
        }
    }

    //简单的方法 加到Menu_item中标题栏
    public void start() {
        mWhiteArray.clear();
        mBlackArray.clear();
        misGameOver=false;
        misWhiteWinner=false;
        invalidate();//重绘
    }


    /*
    标准的自定义View的存储与恢复的写法

    在这两个方法里，对当前棋子位置存储
     白子和黑子集合需存储*/
    private static final String INSTANCE="instance";
    private static final String INSTANCE_GAME_OVER="instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY="instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY="instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {
        //存储
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());//系统的一些东西
        bundle.putBoolean(INSTANCE_GAME_OVER,misGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        //需要ArrayList的泛型继承Parcelable，“ArrayList<Point> mWhiteArray” ，point中已继承
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof  Bundle) {//state是否是我们设置，是的话取值
            Bundle bundle =(Bundle) state;
            misGameOver=bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray=bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray=bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;

        }
        super.onRestoreInstanceState(state);
    }
}
