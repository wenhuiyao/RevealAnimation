package demo.reveal.wenhui.com.revealanimation.reveal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import demo.reveal.wenhui.com.revealanimation.R;

/**
 * Created by wyao on 7/10/14.
 */
public class CircularView extends View {
    private Paint paint;

    private int circleColor;

    private Rect bound = new Rect();

    private CircularDrawable mCircularDrawable;

    private boolean pressed = false;

    public CircularView(Context context) {
        super(context);
        init();
    }

    public CircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        circleColor = getResources().getColor(R.color.green);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0x33000000);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bound.left = getPaddingLeft();
        bound.top = getPaddingTop();
        bound.right = w - getPaddingRight();
        bound.bottom = h - getPaddingBottom();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if( mCircularDrawable == null ){
            mCircularDrawable = new CircularDrawable(getContext(), R.drawable.ic_editor_mode_edit);
            mCircularDrawable.setBackgroundColor(circleColor);
            mCircularDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        }
        mCircularDrawable.draw(canvas);

        if( pressed ){
            canvas.drawCircle(bound.centerX(), bound.centerY(), bound.width() / 2, paint);
        }

    }

    @Override
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if( !isClickable() ){
            return false;
        }

        int action = MotionEventCompat.getActionMasked(event);

        switch(action){
            case MotionEvent.ACTION_DOWN:
                pressed = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                int x =(int) event.getX();
                int y =(int) event.getY();

                if( !bound.contains(x, y)){
                    pressed = false;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if( pressed ){
                    callOnClick();
                }
            case MotionEvent.ACTION_CANCEL:
                pressed = false;
                invalidate();
        }

        return true;
    }

}