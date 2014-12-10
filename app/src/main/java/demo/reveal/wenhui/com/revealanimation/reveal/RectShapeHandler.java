package demo.reveal.wenhui.com.revealanimation.reveal;

import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by wyao on 12/7/14.
 */
public class RectShapeHandler extends ShapeHandler {

    private int mMaxWidth, mMaxHeight;
    private RectF mRectF;

    public RectShapeHandler(RevealAnimationInfo info, View view) {
        super(info, view);
        mRectF = new RectF();
    }

    @Override
    protected void calculateMaxSize(View view){
        mMaxWidth = Math.max(view.getMeasuredWidth() - mInfo.mStartX, mInfo.mStartX);
        mMaxHeight = Math.max(view.getMeasuredHeight() - mInfo.mStartY, mInfo.mStartY);
    }

    @Override
    public void handleRevealing(Path path, float animatedFraction){
        path.reset();
        float startWidth = mInfo.mStartWidth - mInfo.mStartRadiusOffset;
        float startHeight = mInfo.mStartHeight - mInfo.mStartRadiusOffset;

        float widthDelta = startWidth + ( mMaxWidth - startWidth ) * animatedFraction;
        float heightDelta = startHeight + ( mMaxHeight - startHeight ) * animatedFraction;

        mRectF.left = mInfo.mStartX - widthDelta;
        mRectF.top = mInfo.mStartY - heightDelta;
        mRectF.right = mInfo.mStartX + widthDelta;
        mRectF.bottom = mInfo.mStartY + heightDelta;
        path.addRect(mRectF, Path.Direction.CCW);
    }

    @Override
    public void handleClosing(Path path, float animatedFraction){
        path.reset();
        float endWidth = mInfo.mStartWidth - mInfo.mStartRadiusOffset;
        float endHeight = mInfo.mStartHeight - mInfo.mStartRadiusOffset;

        float widthDelta = mMaxWidth - ( mMaxWidth - endWidth ) * animatedFraction;
        float heightDelta = mMaxHeight - ( mMaxHeight - endHeight ) * animatedFraction;

        mRectF.left = mInfo.mStartX - widthDelta;
        mRectF.top = mInfo.mStartY - heightDelta;
        mRectF.right = mInfo.mStartX + widthDelta;
        mRectF.bottom = mInfo.mStartY + heightDelta;
        path.addRect(mRectF, Path.Direction.CCW);
    }
}
