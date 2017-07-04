package com.istarshine.zhxg.togglebuttonview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.istarshine.zhxg.togglebuttonview.R;

/**
 * Created by 王笔锋 on 2017/7/3.
 * Description:自定义控件ToggleButton
 */

public class ToggleButtonView extends View implements View.OnClickListener {
    private Bitmap switchBackgroundBitmap;
    private Bitmap switchForegroundBitmap;
    private Paint paint;
    private int slidLeftMax;
    private int slidLeft;
    private boolean switchOn;
    private float startX;
    private float endX;
    private float distanceX;
    private float initialX;
    private boolean isClickEnabled;

    public ToggleButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        paint = new Paint();
        //设置反锯齿
        paint.setAntiAlias(true);
        switchOn = false;
        switchBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.swith_background);
        switchForegroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.switch_foreground);
        //设置按钮最大的移动距离，即两个图片宽度相减
        slidLeftMax = switchBackgroundBitmap.getWidth() - switchForegroundBitmap.getWidth();
        //设置点击事件
        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置测量的宽高
        setMeasuredDimension(switchBackgroundBitmap.getWidth(), switchBackgroundBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //在这里绘制两个图片
        canvas.drawBitmap(switchBackgroundBitmap, 0, 0, paint);
        canvas.drawBitmap(switchForegroundBitmap, slidLeft, 0, paint);
    }

    @Override
    public void onClick(View view) {
        if (isClickEnabled) {
            //每一次点击事件后都要改变按钮的开关状态
            switchOn = !switchOn;
            //刷新视图
            refreshView();
        }
    }

    /**
     * 根据开关的状态来重新绘制视图
     */
    private void refreshView() {
        if (switchOn) {
            slidLeft = slidLeftMax;
        } else {
            slidLeft = 0;
        }
        //该方法会导致onDraw()方法重新执行  
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //设置按钮是否可点击
                isClickEnabled = true;
                //定义一个变量记录初始位置
                initialX = event.getX();
                //按钮在滑动的过程中的位置状态信息
                startX = event.getX();
                break;            
            case MotionEvent.ACTION_MOVE:
                //滑动结束是按钮的位置
                endX = event.getX();
                distanceX = endX - startX;
                slidLeft += distanceX;
                //屏蔽滑动距离的非法值
                if (slidLeft < 0) {
                    slidLeft = 0;
                } else if (slidLeft > slidLeftMax) {
                    slidLeft = slidLeftMax;
                }
                //刷新
                invalidate();
                //需要改变滑动后的位置
                startX = event.getX();
                break;            
            case MotionEvent.ACTION_UP:
                //滑动到一半的时候处理逻辑
                if (slidLeft < slidLeftMax / 2) {
                    switchOn = false;
                } else if(slidLeft >= slidLeftMax / 2) {
                    switchOn = true;
                }
                //这里之所以用绝对值是因为按钮如果在右边的时候，点击获取的endX - initialX为负值，5是以5个像素为界定值
                isClickEnabled = Math.abs(endX - initialX) <= 5;
                //这个时候也需要刷新状态
                refreshView();
                break;
            
        }
        //这里返回true是表明在事件传递过程中该自定义按钮消费了这个事件
        return true;
    }
}
