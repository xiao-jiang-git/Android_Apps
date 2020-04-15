package com.xiaojiang.sbu.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wangke on 2017/2/20.
 * 自定义折线图
 */

public class ChartView extends View {

    private int mViewHeight; //当前View的高
    private int mViewWidth; //当前View的宽

    private Paint mPaintCdt;// 绘制坐标系的画笔
    private Paint mPaintSysPoint; //绘制坐标系上刻度点
    private Paint mPaintLinePoint; //绘制折线上的点
    private Paint mPaintText; //绘制文字
    private Paint mPaintLine; //绘制折线
    private Paint mPaintDash; //绘制虚线
    private Paint mPaintSys; //x,y轴

    private Rect mXBound;
    private Rect mYBound;

    private ArrayList<Point> pointList = null;
    private int X_MAX; //传入点的X的最大坐标
    private int Y_MAX; //传入点的Y的最大坐标
    private float mScreenXdistance; //x轴刻度在屏幕上的间隔
    private float mScreenYdistance; //y轴刻度在屏幕上的间隔

    //折线图距离四周的像素大小
    private int Margin = 80;

    private int coordinateSystemColor;
    private float coordinateSystemSize;
    private int lineColor;
    private float lineSize;
    private int lineColorPoint;
    private float lineColorPointRadius;
    private int scalePointColor;
    private float scalePointRadius;
    private boolean isShowDash;
    private int xScale;
    private int yScale;
    private float dashSize;
    private int dashColor;


    public ChartView(Context context) {
        super(context);
        InitPaint();
    }

    //设置点的数据
    public void setPoint(ArrayList<Point> points) {

        pointList = new ArrayList();

        pointList = points;


        int []xPointArray = new int[100];
        int []yPointArray = new int[100];

        //遍历传入的点的坐标，获取最大的x,y点的坐标，用来计算刻度
        for(int i=0;i<pointList.size();i++){

            Point point = pointList.get(i);
            xPointArray[i] = point.x;
            yPointArray[i] = point.y;

        }

        Arrays.sort(xPointArray);
        Arrays.sort(yPointArray);

        X_MAX = xPointArray[xPointArray.length-1];
        Y_MAX = yPointArray[yPointArray.length-1];

        Log.i("wk","X的最大坐标："+xPointArray[xPointArray.length-1]);
        Log.i("wk","y的最大坐标："+yPointArray[yPointArray.length-1]);

        //调用绘制
        invalidate();


    }

    //初始化画笔
    private void InitPaint() {

        mPaintCdt = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置画线
        mPaintCdt.setStyle(Paint.Style.STROKE);
        //设置线的宽度
        mPaintCdt.setStrokeWidth(lineSize);
        mPaintCdt.setColor(lineColor);
        mPaintSysPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置填充
        mPaintSysPoint.setStyle(Paint.Style.FILL);
        mPaintSysPoint.setColor(scalePointColor);
        mPaintLinePoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置填充
        mPaintLinePoint.setStyle(Paint.Style.FILL);
        Log.i("wk","线上点的颜色："+lineColor);
        mPaintLinePoint.setColor(lineColorPoint);
        //绘制xy轴
        mPaintSys = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置画线
        mPaintSys.setStyle(Paint.Style.STROKE);
        //设置线的宽度
        mPaintSys.setStrokeWidth(coordinateSystemSize);
        mPaintSys.setColor(coordinateSystemColor);
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(30);
        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置画线
        mPaintLine.setStyle(Paint.Style.STROKE);
        //设置线的宽度
        mPaintLine.setStrokeWidth(lineSize);
        //设置画笔的颜色
        mPaintLine.setColor(lineColor);
        mPaintDash = new Paint();
        mPaintDash.setStyle(Paint.Style.STROKE);
        mPaintDash.setStrokeWidth(dashSize);
        mPaintDash.setColor(dashColor);
        mPaintDash.setPathEffect(new DashPathEffect(new float[]{10,10},0));

        mXBound = new Rect();
        mYBound = new Rect();

        invalidate();

    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //获取属性值
        getAttrs(context,attrs);
        InitPaint();
    }
    //获取设置的属性
    private void getAttrs(Context context,AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChartView);


        //坐标系颜色
        coordinateSystemColor = ta.getColor(R.styleable.ChartView_coordinateSystemColor, Color.RED);
        coordinateSystemSize = ta.getDimension(R.styleable.ChartView_coordinateSystemLineSize, 3f);


        //折线颜色
        lineColor = ta.getColor(R.styleable.ChartView_lineColor, Color.BLACK);
        lineSize = ta.getDimension(R.styleable.ChartView_lineSize, 2f);


        //折线上坐标点颜色
        lineColorPoint = ta.getColor(R.styleable.ChartView_linePointColor, Color.RED);
        //折线上坐标点的半径
        lineColorPointRadius = ta.getDimension(R.styleable.ChartView_linePointRadius,6f);


        //刻度点颜色
        scalePointColor = ta.getColor(R.styleable.ChartView_scalePointColor, Color.RED);
        //刻度点半径
        scalePointRadius = ta.getDimension(R.styleable.ChartView_scalePointRadius, 6);

        //设置是否显示虚线
        isShowDash = ta.getBoolean(R.styleable.ChartView_showDash,false);

        dashSize = ta.getDimension(R.styleable.ChartView_setDashSize,2f);

        dashColor = ta.getColor(R.styleable.ChartView_setDashColor, Color.WHITE);

        xScale = ta.getInt(R.styleable.ChartView_setXScale,5);
        yScale = ta.getInt(R.styleable.ChartView_setYScale,5);

        ta.recycle();


        Log.i("wk","coordinateSystemColor:"+ coordinateSystemColor +"\n coordinateSystemSize:"+ coordinateSystemSize +"\n"+"lineColor:"+ lineColor +"\n"+"lineSize:"+ lineSize);




    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        getAttrs(context,attrs);
        InitPaint();

    }


    //测量view
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));




    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //获取当前View的宽高
        mViewWidth = w;
        mViewHeight = h;

        Log.i("wk","宽度："+w);
        Log.i("wk","高度："+h);

    }

    //绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制X轴Y轴 以及原点
        canvas.drawLine(Margin,mViewHeight-Margin, Margin,5, mPaintSys);

        canvas.drawLine(Margin,mViewHeight-Margin,mViewWidth-5,mViewHeight-Margin, mPaintSys);

        //绘制原点
        canvas.drawCircle(Margin,mViewHeight-Margin,scalePointRadius,mPaintSysPoint);

        //

        /**
         * 计算两个刻度之间的间距：
         *
         *     1.刻度的个数 = 传入坐标最大的坐标点/坐标轴间距
         *     2.两个刻度之间的间距 = 屏幕的宽或高 /刻度的个数
         *
         */


        int num_x = X_MAX/xScale; //x轴上需要绘制的刻度的个数
        mScreenXdistance = (mViewWidth- Margin *2)/(num_x*1f);

        Log.i("wk","需要绘制的刻度个数==>"+num_x+"两个刻度间间隔："+ mScreenXdistance);

        int num_y = Y_MAX/yScale;
        mScreenYdistance = (mViewHeight-Margin*2)/(num_y*1f);
        Log.i("wk","需要绘制的刻度个数==>"+num_x+"两个刻度间间隔："+ mScreenXdistance);


        //绘制 X,y轴刻度标记
        for(int i=0;i<pointList.size();i++){

            canvas.drawCircle(Margin +(i* mScreenXdistance),mViewHeight-Margin,scalePointRadius,mPaintSysPoint);

            canvas.drawCircle(Margin,mViewHeight-Margin-(i* mScreenYdistance),scalePointRadius,mPaintSysPoint);

            //计算刻度字体的宽高
            String index_x = String.valueOf(i*xScale);
            String index_y = String.valueOf(i*yScale);
            mPaintText.getTextBounds(index_x,0,index_x.length(),mXBound);
            mPaintText.getTextBounds(index_y,0,index_x.length(),mYBound);
            int indexWidth_x = mXBound.width();
            int indexHeight_x = mXBound.height();
            int indexWidth_y = mYBound.width();
            int indexHeight_y = mYBound.height();

            Log.i("wk","字体的宽度："+indexWidth_x+"字体的高度："+indexHeight_x);
            canvas.drawText(index_x, Margin +(i* mScreenXdistance),mViewHeight-Margin+indexHeight_x+indexHeight_x/2,mPaintText);
            if(i!=0) {
                canvas.drawText(index_y, Margin - indexHeight_y-indexWidth_y/2, mViewHeight - Margin - (i * mScreenYdistance), mPaintText);
            }

        }

        /**
         * 绘制折线
         */
        Point LastPoint = new Point(); //记录上一个坐标点
        LastPoint.x = Margin;
        LastPoint.y = mViewHeight-Margin;

        for(int i=1;i<pointList.size();i++){


            /**
             * 计算绘制点的坐标位置
             * 绘制点的坐标 =  (传入点的的最大的xy坐标/坐标轴上的间隔） * 坐标间隔对应的屏幕上的间隔
             */
//            canvas.drawCircle(LastPoint.x,LastPoint.y,4f,mPaintPoint);
            //计算出脱离坐标系的点所处的位置
            float point_x = (pointList.get(i).x/(xScale*1f))* mScreenXdistance;
            float point_y = (pointList.get(i).y/(yScale*1f))* mScreenYdistance;

            //坐标系内的点的位置

            float startX = LastPoint.x;
            float startY = LastPoint.y;

            float endX = Margin +point_x;
            float endY = mViewHeight-Margin-point_y;

            //需要计算此处


            canvas.drawLine(startX,startY,endX,endY,mPaintLine);

            //记录上一个坐标点的位置
            LastPoint.x = (int) endX;
            LastPoint.y = (int) endY;


            if(isShowDash) {
                //绘制横向虚线
                canvas.drawLine(Margin, mViewHeight - Margin - point_y -lineColorPointRadius/2, Margin + point_x - lineColorPointRadius/2, mViewHeight - Margin - point_y -lineColorPointRadius/2, mPaintDash);
                //绘制竖向虚线
                canvas.drawLine(LastPoint.x, LastPoint.y, LastPoint.x, mViewHeight - Margin - lineColorPointRadius, mPaintDash);
            }

            canvas.drawCircle(LastPoint.x, LastPoint.y, lineColorPointRadius, mPaintLinePoint);

        }


    }

    //测量view高度
    private int measureHeight(int heightMeasureSpec) {

        int result = 0;

        int specSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的高度 单位 为px

        int specMode = MeasureSpec.getMode(heightMeasureSpec);//获取测量的模式

        //如果是精确测量，就将获取View的大小设置给将要返回的测量值
        if(specMode == MeasureSpec.EXACTLY){

            Log.i("wk","高度:精确测量 + specSize:==>"+specSize);

            result = specSize;

        }else{

            Log.i("wk","高度:UNSPECIFIED + specSize:==>"+specSize);

            result = 400;

            //如果设置成wrap_content时，给高度指定一个值
            if(specMode == MeasureSpec.AT_MOST){

                Log.i("wk","高度:最大值模式 + specSize:==>"+specSize);

                result = Math.min(result,specSize);

            }
        }



        return result;
    }

    //测量view宽度
    private int  measureWidth(int widthMeasureSpec) {


        int result = 0;

        int specSize = MeasureSpec.getSize(widthMeasureSpec); //获取高的高度

        int specMode = MeasureSpec.getMode(widthMeasureSpec);//获取测量的模式

        //如果是精确测量，就将获取View的大小设置给将要返回的测量值
        if(specMode == MeasureSpec.EXACTLY){

            Log.i("wk","宽度:精确测量 + specSize:==>"+specSize);

            result = specSize;

        }else{

            Log.i("wk","宽度:UNSPECIFIED + specSize:==>"+specSize);

            result = 400;
            //如果设置成wrap_content时，给高度指定一个值
            if(specMode == MeasureSpec.AT_MOST){

                Log.i("wk","宽度:最大值模式 + specSize:==>"+specSize);

                result = Math.min(result,specSize);
            }
        }

        return result;
    }
}