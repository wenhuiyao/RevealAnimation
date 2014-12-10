package demo.reveal.wenhui.com.revealanimation.reveal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by wyao on 12/6/14.
 */
public class CircularDrawable extends Drawable {

    private Paint mPaint;
    private int mBackgroundColor;
    private int mAlpha = 255;
    private Drawable mDrawable;

    public CircularDrawable(Context context, int resId){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBackgroundColor = Color.WHITE;
        mDrawable = context.getResources().getDrawable(resId);
    }

    public void setBackgroundColor(int backgroundColor){
        mBackgroundColor = backgroundColor;
    }

    @Override
    public void draw(Canvas canvas) {

        Rect bound = getBounds();
        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        float radius = Math.min(bound.width(), bound.height()) / 2;
        canvas.drawCircle(bound.exactCenterX(), bound.exactCenterY(), radius, mPaint);

        mDrawable.setAlpha(mAlpha);
        int centerX = bound.centerX();
        int centerY = bound.centerY();
        int width = mDrawable.getIntrinsicWidth() / 2;
        int height = mDrawable.getIntrinsicHeight() / 2;
        mDrawable.setBounds(centerX - width, centerY - height, width + centerX, height + centerY);
        mDrawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
