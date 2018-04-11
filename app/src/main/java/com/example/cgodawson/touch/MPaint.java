package com.example.cgodawson.touch;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by CG_Dawson on 2017/12/21.
 */

public class MPaint extends View {
    private Paint paint;
    private Canvas mCanvas;
    private Bitmap bitmap;
    private StringBuilder sb;
    private int index;
    public MPaint(Context context) {
        super(context);
    }
    public void clearMap()
    {
        mCanvas.drawColor(Color.BLACK);
        drawCircle();
        invalidate();
    }
    public void clearLog()
    {
        sb = new StringBuilder();
        index = 0;
    }
    public String getSB()
    {
        return sb.toString();
    }
    public MPaint(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(Color.RED);
        bitmap = Bitmap.createBitmap(TouchAct.width, TouchAct.height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
        mCanvas.drawColor(Color.BLACK);
        drawCircle();
        sb = new StringBuilder();
    }

    public MPaint(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap,0,0,paint);
    }
    long evenTime,old_evntTime;
    float gas_x,gas_y;
    String s;
    int count = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x_ = event.getRawX();
        float y_ = event.getRawY();
        //Log.i("MTouch",event.getAction()+"   "+x_+"   "+y_+"   "+event.getActionIndex());


        try {
            Field field =  MotionEvent.class.getDeclaredField("mNativePtr");
            field.setAccessible(true);
            Log.i("MTOUCH","mNativePtr:"+field.getLong(event)+"      "+event.toString());
        }catch (Exception e)
        {e.printStackTrace();}
        //Log.i("MTouch",event.toString());

        evenTime = event.getEventTime();
        mCanvas.drawPoint(x_,y_,paint);

        drawCircle();


        invalidate();


        if(event.getAction()==MotionEvent.ACTION_DOWN)
        {
            sb.append(  "<font color='#bcbcbc'>"+"["+(++index)+"]---------------------------"+"</font><Br/>");
        }
        else if(event.getAction()==MotionEvent.ACTION_MOVE)
        {
            count++;
        }


        s = "<font color='#ffff00'>"+event.getAction()+"</font>"+

                "<font color='#9B30FF'>"+"    x="+"</font>"+

                "<font color='#ccbc00'>"+(int)x_+"</font>"+

                "<font color='#9B30FF'>"+"  y="+"</font>"+

                "<font color='#ccbc00'>"+(int)y_+"</font>"+

                "<font color='#9B30FF'>"+"  step_x="+"</font>"+

                "<font color='#ccbc00'>"+(int)Math.abs(gas_x-x_)+"</font>"+

                "<font color='#9B30FF'>"+"  step_y="+"</font>"+

                "<font color='#ccbc00'>"+(int)Math.abs(gas_y-y_)+"</font>"+

                "<font color='#9B30FF'>"+"  step_time="+"</font>"+

                "<font color='#ccbc00'>"+(evenTime - old_evntTime)+"</font>"+

                "<Br/>";



        sb.append(s);

        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            sb.append("<font color='#00ffff'>"+"move count:"+"</font>"+
                    "<font color='#ffffff'>"+count+"</font>"+
                    "<Br/>");
            count = 0;
        }


        old_evntTime = evenTime;
        gas_x = x_;
        gas_y  = y_;
        return true;
    }

    private void drawCircle()
    {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        mCanvas.drawCircle(320,780,110,paint);
        Rect rect = new Rect(255,674,442,843);
        paint.setColor(Color.GREEN);
        mCanvas.drawRect(rect,paint);
        paint.setColor(Color.RED);
    }


}
