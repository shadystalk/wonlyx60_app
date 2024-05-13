package com.rockchip.gpadc.demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class ResizableRectangleView extends View {
    private Rect rect;
    private Paint paint;
    private int startX, startY, endX, endY;
    private int oneLeft;
    private int oneTop;
    private int screenWidth;
    private int screenHeight;
    private int oneRight;
    private int oneBottom;
    private int horizontalStartY;
    private int horizontalEndY;
    private int verticalStartX;
    private int verticalEndX;
    private int horizontalStartX;
    private int horizontalEndX;
    private int verticalStartY;
    private int verticalEndY;

    public ResizableRectangleView(Context context) {
        super(context);
        init();
    }

    // 第二个构造函数
    public ResizableRectangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // 第三个构造函数
    public ResizableRectangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    // API 21 或以上版本需要的第四个构造函数
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ResizableRectangleView(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle, defStyleRes);
        init();
    }

    private void init() {
        screenWidth = 1280;
        screenHeight = 800;
        // 假设屏幕宽度和高度可以通过某种方式获取，或者您可以直接使用具体的数值
//        int boxWidth = 800;  // 例如，框宽度为屏幕宽度的一半
//        int boxHeight = 800;  // 同样，框高度也是

        // 计算起始点，使得框位于屏幕中心
        oneLeft = 100;
        oneTop = 100;
        oneRight = screenWidth - oneLeft;
        oneBottom = screenHeight - oneTop;

        rect = new Rect(oneLeft, oneTop, oneRight, oneBottom);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);


        verticalStartX = screenWidth / 2 - 100;
        verticalStartY = 0;
        verticalEndX = screenWidth / 2 + 100;
        ;
        verticalEndY = screenHeight;


        horizontalStartX = 0;
        horizontalStartY = screenHeight / 2 - 100;
        horizontalEndX = screenWidth;
        horizontalEndY = screenHeight / 2 + 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//         绘制用户调整的矩形
        canvas.drawRect(rect, paint);
        // 绘制十字形的竖直部分
        canvas.drawRect(verticalStartX, verticalStartY, verticalEndX, verticalEndY, paint);
        // 绘制十字形的水平部分
        canvas.drawRect(horizontalStartX, horizontalStartY, horizontalEndX, horizontalEndY, paint);
    }

//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                startX = (int) event.getX();
//                startY = (int) event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                endX = (int) event.getX();
//                endY = (int) event.getY();
//                rect.set(startX, startY, endX, endY);
//                invalidate(); // 重绘视图
//                break;
//        }
//        return true;
//    }

    public Rect getRect() {
        return rect;
    }

    public void setView(View up, View left, View down, View right, View up1, View left1, View down1, View right1) {
        left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    if (oneLeft > 10) {
                        oneLeft = oneLeft - 10;
                        rect.set(oneLeft, oneTop, oneRight, oneBottom);
                        invalidate();
                    }
                } else if (flag == 1) {
                    if (horizontalStartX > 10) {
                        horizontalStartX = horizontalStartX - 10;
                        invalidate();
                    }
                } else {
                    if (verticalStartX > 10) {
                        verticalStartX = verticalStartX - 10;
                        invalidate();
                    }
                }
            }
        });
        left1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    int i = screenWidth / 2 - 100;
                    if (oneLeft < i) {
                        oneLeft = oneLeft + 10;
                        rect.set(oneLeft, oneTop, oneRight, oneBottom);
                        invalidate();
                    }
                } else if (flag == 1) {
                    int i = screenWidth / 2 - 100;
                    if (horizontalStartX < i) {
                        horizontalStartX = horizontalStartX + 10;
                        invalidate();
                    }
                } else {
                    int i = screenWidth / 2 - 100;
                    if (verticalStartX < i) {
                        verticalStartX = verticalStartX + 10;
                        invalidate();
                    }
                }
            }
        });


        right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    if (oneRight < (screenWidth - 10)) {
                        oneRight = oneRight + 10;
                        rect.set(oneLeft, oneTop, oneRight, oneBottom);
                        invalidate();
                    }
                } else if (flag == 1) {
                    if (horizontalEndX < (screenWidth - 10)) {
                        horizontalEndX = horizontalEndX + 10;
                        invalidate();
                    }
                } else {
                    if (verticalEndX < (screenWidth - 10)) {
                        verticalEndX = verticalEndX + 10;
                        invalidate();
                    }
                }
            }
        });
        right1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    int i = screenWidth / 2 + 100;
                    if (oneRight > i) {
                        oneRight = oneRight - 10;
                        rect.set(oneLeft, oneTop, oneRight, oneBottom);
                        invalidate();
                    }
                } else if (flag == 1) {
                    int i = screenWidth / 2 + 100;
                    if (horizontalEndX > i) {
                        horizontalEndX = horizontalEndX - 10;
                        invalidate();
                    }
                } else {
                    int i = screenWidth / 2 + 100;
                    if (verticalEndX > i) {
                        verticalEndX = verticalEndX - 10;
                        invalidate();
                    }
                }
            }
        });


        up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    if (oneTop > 10) {
                        oneTop = oneTop - 10;
                        rect.set(oneLeft, oneTop, oneRight, oneBottom);
                        invalidate();
                    }
                } else if (flag == 1) {
                    if (horizontalStartY > 10) {
                        horizontalStartY = horizontalStartY - 10;
                        invalidate();
                    }
                } else {
                    if (verticalStartY > 10) {
                        verticalStartY = verticalStartY - 10;
                        invalidate();
                    }
                }
            }
        });
        up1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    int i = screenHeight / 2 - 100;
                    if (oneTop < i) {
                        oneTop = oneTop + 10;
                        rect.set(oneLeft, oneTop, oneRight, oneBottom);
                        invalidate();
                    }
                } else if (flag == 1) {
                    int i = screenHeight / 2 - 100;
                    if (horizontalStartY < i) {
                        horizontalStartY = horizontalStartY + 10;
                        invalidate();
                    }
                } else {
                    int i = screenHeight / 2 - 100;
                    if (verticalStartY < i) {
                        verticalStartY = verticalStartY + 10;
                        invalidate();
                    }
                }
            }
        });


        down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    if (oneBottom < (screenHeight - 10)) {
                        oneBottom = oneBottom + 10;
                        rect.set(oneLeft, oneTop, oneRight, oneBottom);
                        invalidate();
                    }
                } else if (flag == 1) {
                    if (horizontalEndY < (screenHeight - 10)) {
                        horizontalEndY = horizontalEndY + 10;
                        invalidate();
                    }
                } else {
                    if (verticalEndY < (screenHeight - 10)) {
                        verticalEndY = verticalEndY + 10;
                        invalidate();
                    }
                }
            }
        });

        down1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    int i = screenHeight / 2 + 100;
                    if (oneBottom > i) {
                        oneBottom = oneBottom - 10;
                        rect.set(oneLeft, oneTop, oneRight, oneBottom);
                        invalidate();
                    }
                } else if (flag == 1) {
                    int i = screenHeight / 2 + 100;
                    if (horizontalEndY > i) {
                        horizontalEndY = horizontalEndY - 10;
                        invalidate();
                    }
                } else {
                    int i = screenHeight / 2 + 100;
                    if (verticalEndY > i) {
                        verticalEndY = verticalEndY - 10;
                        invalidate();
                    }
                }
            }
        });
    }

    int flag = 0;

    public void setSwitchRect(int flag) {
        this.flag = flag;
    }
}

