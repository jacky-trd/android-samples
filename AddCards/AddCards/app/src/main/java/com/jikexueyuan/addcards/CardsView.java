package com.jikexueyuan.addcards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * CardsView类为自定义的View类，用于在MainActivity中画5行4列的20个正方形
 */
public class CardsView extends View {

    //画笔
    private Paint paint = null;

    public CardsView(Context context) {
        super(context);

        paint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        //三个常量分别是：行数，列数，空白间隔
        final int rows = 5, columns = 4, margin = 20;

        //以下变量分别是：屏幕宽度，屏幕高度，正方形边长，左边界，上边界，右边界，下边界
        int screenWidth,screenHeight,sideLength,rectLeft,rectTop,rectRight,rectBottom;

        //分别计算屏幕宽度，屏幕高度，正方形边长
        screenWidth = canvas.getWidth();
        screenHeight = canvas.getHeight();
        sideLength = (screenWidth - columns * margin) / columns;

        //画方框
        paint.setColor(Color.RED);
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                //计算左边界，上边界，右边界，下边界
                rectLeft = margin / 2 + j * (sideLength + margin);
                rectTop = (screenHeight - rows * sideLength - margin * (rows - 1)) / 2 + i * (sideLength + margin);
                rectRight = rectLeft + sideLength;
                rectBottom = rectTop + sideLength;

                canvas.drawRect(rectLeft,rectTop,rectRight,rectBottom,paint);
            }
        }

        //画数字
        paint.setColor(Color.WHITE);
        for(int i = 0; i < rows;i++) {
            for (int j = 0; j < columns; j++) {

                rectLeft = margin / 2 + j * (sideLength + margin);
                rectTop = (screenHeight - rows * sideLength - margin * (rows - 1)) / 2 + i * (sideLength + margin);

                paint.setTextSize(sideLength / 3);
                //字体坐标系居中
                paint.setTextAlign(Paint.Align.CENTER);

                //计算字体高度
                Paint.FontMetrics fontMetrics = paint.getFontMetrics();
                float fontHeight = fontMetrics.bottom - fontMetrics.top;

                //计算文字baseline
                float textBaseY = rectTop + sideLength / 2 + fontHeight / 2 - fontMetrics.bottom;

                canvas.drawText(String.valueOf(i * columns + j + 1), rectLeft+sideLength / 2, textBaseY, paint);
            }
        }
    }
}
