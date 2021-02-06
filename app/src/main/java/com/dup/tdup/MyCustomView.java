package com.dup.tdup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

public class MyCustomView extends View
{
    private Bitmap sourceBitmap;
    private Canvas sourceCanvas = new Canvas();
    private Paint destPaint = new Paint();
    private Path destPath = new Path();

    public Bitmap getSourceBitmap() {
        return sourceBitmap;
    }

    public MyCustomView(Context context, Bitmap rawBitmap)
    {
        super(context);

        //converting bitmap into mutable bitmap
        sourceBitmap = Bitmap.createBitmap(rawBitmap.getWidth(), rawBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        sourceCanvas.setBitmap(sourceBitmap);
        sourceCanvas.drawBitmap(rawBitmap, 0, 0, null);

        destPaint.setAlpha(0);
        destPaint.setAntiAlias(true);
        destPaint.setStyle(Paint.Style.STROKE);
        destPaint.setStrokeJoin(Paint.Join.ROUND);
        destPaint.setStrokeCap(Paint.Cap.ROUND);
		//change this value as per your need
        destPaint.setStrokeWidth(50);
        destPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        sourceCanvas.drawPath(destPath, destPaint);
        canvas.drawBitmap(sourceBitmap, 0, 0, null);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float xPos = event.getX();
        float yPos = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                destPath.moveTo(xPos, yPos);
                break;

            case MotionEvent.ACTION_MOVE:
                destPath.lineTo(xPos, yPos);
                break;

            default:
                return false;
        }

        invalidate();
        return true;
    }
}