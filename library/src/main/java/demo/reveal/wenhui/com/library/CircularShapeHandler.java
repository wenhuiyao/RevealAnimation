package demo.reveal.wenhui.com.library;

import android.graphics.Path;
import android.view.View;

/**
 * Created by wyao on 12/7/14.
 */
public class CircularShapeHandler extends ShapeHandler {

    private float maxSize;

    public CircularShapeHandler(RevealAnimationInfo info, View view) {
        super(info, view);
    }

    @Override
    protected void calculateMaxSize(View view){
        final int width = Math.max(view.getMeasuredWidth() - mInfo.mStartX, mInfo.mStartX);
        final int height = Math.max(view.getMeasuredHeight() - mInfo.mStartY, mInfo.mStartY);
        maxSize = (float) Math.sqrt(width * width + height * height);
    }

    @Override
    public void handleRevealing(Path path, float animatedFraction){
        path.reset();
        float startRadius = mInfo.mStartRadius - mInfo.mStartRadiusOffset;
        float radius = startRadius + ( maxSize - startRadius ) * animatedFraction;
        path.addCircle(mInfo.mStartX, mInfo.mStartY, radius, Path.Direction.CCW);
    }

    @Override
    public void handleClosing(Path path, float animatedFraction){
        path.reset();
        float endRadius = mInfo.mStartRadius - mInfo.mStartRadiusOffset;
        float radius = maxSize - ( maxSize - endRadius ) * animatedFraction;
        path.addCircle(mInfo.mStartX, mInfo.mStartY, radius, Path.Direction.CCW);
    }
}
