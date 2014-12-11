package demo.reveal.wenhui.com.library;

import android.graphics.Path;
import android.view.View;

/**
 * Created by wyao on 12/7/14.
 */
public abstract class ShapeHandler {

    protected RevealAnimationInfo mInfo;

    public ShapeHandler(RevealAnimationInfo info, View view){
        mInfo = info;
        calculateMaxSize(view);
    }

    protected abstract void calculateMaxSize(View view);

    public abstract  void handleRevealing(Path path, float animatedFraction);

    public abstract void handleClosing(Path path, float animatedFraction);



}


