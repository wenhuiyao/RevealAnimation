package demo.reveal.wenhui.com.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by wyao on 11/11/14.
 */
public class RevealFrameLayout extends FrameLayout {

    private static final int STATE_NONE = 0;
    private static final int STATE_NOT_SET = 1;
    private static final int STATE_ANIMATING = 2;
    private static final int STATE_FINISHED = 3;

    private static final float ANIMATION_FULL_RATIO = 0.9f;
    private static final float CHILD_VIEW_ANIMATION_START_RATIO = 0.8F;


    public static interface OnRevealAnimationListener {
        public void onRevealStart();
        public void onRevealEnd();
        public void onStartClose();
        public void onEndClose();
    }

    private int mBackgroundColor = Color.BLACK;
    private RevealAnimationInfo mInfo;

    private Path mPath;
    private OnRevealAnimationListener mRevealListener;
    private int mState = STATE_NONE;
    private Paint mPaint;
    private Drawable mTargetCallerDrawable;
    private Drawable mTargetDrawable;
    private ShapeHandler mShapeHandler;
    private Animator mRevealingAnimator;
    private int mExclusiveAnimatedViewId = 0;

    public RevealFrameLayout(Context context) {
        super(context);
        init();
    }

    public RevealFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RevealFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 ){
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        } else {
            setBackgroundDrawable(null);
            setWillNotDraw(false);
        }

    }

    @Override
    public void draw(Canvas canvas) {
        if( !canvas.isHardwareAccelerated() ){
            mState = STATE_NOT_SET;
            super.draw(canvas);
            return;
        }

        if( mState == STATE_NONE ){
            mPath = new Path();
            mPaint = new Paint();
            mPaint.setAntiAlias(true);

            if( mInfo == null ){
                Log.d(RevealFrameLayout.class.getName(), "Please call startRevealAnimation");
                mInfo = getDefaultRevealInfo();
            }

            if( mInfo.mShape == RevealAnimationInfo.SHAPE_CIRCULAR ){
                mShapeHandler = new CircularShapeHandler(mInfo, this);
            } else {
                mShapeHandler = new RectShapeHandler(mInfo, this);
            }

            mState = STATE_ANIMATING;
            startRevealAnimationInternal();
            return;

        }

        if( mState == STATE_ANIMATING ) {
            if( canvas.isHardwareAccelerated() ) {
                canvas.save(Canvas.CLIP_TO_LAYER_SAVE_FLAG);
                canvas.clipPath(mPath);
                canvas.drawColor(mBackgroundColor);
                if (mTargetCallerDrawable != null) {
                    mTargetCallerDrawable.draw(canvas);
                }
                super.draw(canvas);
                canvas.restore();
            }
        } else {
            canvas.drawColor(mBackgroundColor);
            super.draw(canvas);
        }
    }

    @Override
    public boolean isOpaque() {
        if( isHardwareAccelerated() ){
            return super.isOpaque();
        }
        return Color.alpha(mBackgroundColor) >= 255;
    }

    @Override
    protected void onDetachedFromWindow() {
        if( mRevealingAnimator != null && mRevealingAnimator.isRunning() ){
            mRevealingAnimator.cancel();
        }

        super.onDetachedFromWindow();

    }

    public boolean isAnimationRunning(){
        return mState == STATE_ANIMATING;
    }

    public void setRevealAnimationInfo(RevealAnimationInfo info){
        mInfo = info;
    }

    public void setTargetDrawable(Drawable drawable){
        mTargetDrawable = drawable;
    }

    public void setExclusiveAnimatedViewId(int id){
        mExclusiveAnimatedViewId = id;
    }

    /**
     * This will be used when there is no animation
     *
     * @param backgroundColor
     */
    public void setBackgroundColor(int backgroundColor){
        mBackgroundColor = backgroundColor;
    }

    public void setOnRevealAnimationListener(OnRevealAnimationListener l) {
        mRevealListener = l;
    }

    private void startRevealAnimationInternal(){
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(mInfo.mAnimationDuration);
        animator.setInterpolator(new BakedBezierInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                mBackgroundColor = blendColors(mInfo.mBackgroundColor, mInfo.mButtonColor, animatedFraction);
                mPaint.setColor(mBackgroundColor);

                final float ratio = 1f- ANIMATION_FULL_RATIO;
                if( mTargetDrawable != null && animatedFraction <=  ratio ) {
                    int alpha = (int) (255f * ( 1f -  animatedFraction / ratio ) );
                    mTargetCallerDrawable = mTargetDrawable;
                    mTargetCallerDrawable.setAlpha(alpha);
                } else {
                    mTargetCallerDrawable = null;
                }

                mShapeHandler.handleRevealing(mPath, animatedFraction);
                postInvalidateOnAnimation();
            }
        });

        setChildViewAlpha(0);
        if( isHardwareAccelerated() ) {
            long animationDuration = (long) (mInfo.mAnimationDuration * CHILD_VIEW_ANIMATION_START_RATIO);
            long delayed = mInfo.mAnimationDuration - animationDuration;
            animateChildren(1, animationDuration, delayed);
        }

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mState = STATE_ANIMATING;
                setVisibility(View.VISIBLE);
                if( mRevealListener != null ){
                    mRevealListener.onRevealStart();
                }

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mState = STATE_FINISHED;
                if(mRevealListener != null ){
                    mRevealListener.onRevealEnd();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mState = STATE_FINISHED;
            }
        });

        mRevealingAnimator = animator;
        mRevealingAnimator.start();
    }

    private void setChildViewAlpha(int alpha){
        int size = getChildCount();
        for(int i = 0; i < size; i++ ){
            View child = getChildAt(i);
            if( child.getVisibility() == View.VISIBLE &&
                    mExclusiveAnimatedViewId != child.getId() ){
                ViewCompat.setAlpha(child, alpha);
            }

        }
    }

    private void animateChildren(int alpha, long animationDuration, long delayed){
        int size = getChildCount();
        for(int i = 0; i < size; i++ ){
            View child = getChildAt(i);
            if( child.getVisibility() == View.VISIBLE &&
                    mExclusiveAnimatedViewId != child.getId() ){
                ViewCompat.animate(child).alpha(alpha)
                        .setDuration(animationDuration).setStartDelay(delayed)
                        .withLayer();
            }
        }
    }

    public void closeView(){
        if( mState == STATE_NOT_SET || mState == STATE_NONE ){
            if( mRevealListener != null ){
                mRevealListener.onEndClose();
            }
            return;
        }

        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
        animator.setDuration(mInfo.mAnimationDuration);
        animator.setInterpolator(new BakedBezierInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(View.INVISIBLE);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                mBackgroundColor = blendColors(mInfo.mButtonColor, mInfo.mBackgroundColor, animatedFraction);
                mPaint.setColor(mBackgroundColor);

                if( mTargetDrawable != null && animatedFraction > ANIMATION_FULL_RATIO ) {
                    float ratio = 1f - ANIMATION_FULL_RATIO;
                    int alpha = (int)(255f * ( 1f - ( 1f - animatedFraction ) / ratio )) ;
                    mTargetCallerDrawable = mTargetDrawable;
                    mTargetCallerDrawable.setAlpha(alpha);
                }

                mShapeHandler.handleClosing(mPath, animatedFraction);
                postInvalidateOnAnimation();
            }
        });

        if( isHardwareAccelerated() ) {
            long animationDuration = (long) (mInfo.mAnimationDuration * CHILD_VIEW_ANIMATION_START_RATIO);
            animateChildren(0, animationDuration, 0);
        }

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                if( mRevealListener != null ){
                    mRevealListener.onStartClose();
                }
                mState = STATE_ANIMATING;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if( mRevealListener != null ){
                    mRevealListener.onEndClose();
                }
                mState = STATE_FINISHED;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mState = STATE_FINISHED;
            }
        });
        mRevealingAnimator = animator;
        mRevealingAnimator.start();
    }

    private RevealAnimationInfo getDefaultRevealInfo(){
        RevealAnimationInfo.Builder builder = new RevealAnimationInfo.Builder(getContext());
        builder.startX(getMeasuredWidth() / 2);
        builder.startY(getMeasuredHeight());
        builder.startRadius(0f);
        builder.backgroundColor(mBackgroundColor);
        builder.buttonColor(mBackgroundColor);
        return builder.create();
    }

    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float a = (Color.alpha(color1) * ratio) + (Color.alpha(color2) * inverseRation );
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        ss.mInfo = this.mInfo;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.mInfo = ss.mInfo;
    }

    static class SavedState extends BaseSavedState {
        RevealAnimationInfo mInfo;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mInfo = in.readParcelable(RevealAnimationInfo.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(mInfo, flags);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

}
