package demo.reveal.wenhui.com.revealanimation.reveal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import demo.reveal.wenhui.com.revealanimation.R;

/**
 * Created by wyao on 12/7/14.
 */
public class RectRevealActivity extends Activity {

    private RevealFrameLayout mRevealFrameLayout;

    private static final String EXTRA_INFO = "EXTRA_INFO";

    private boolean mCloseAnimationFinished = false;

    private ImageView imageView;
    private RevealAnimationInfo info;

    public final static Intent getLaunchIntent(Context context, View targetView){
        RevealAnimationInfo.Builder builder = new RevealAnimationInfo.Builder(context);
        builder.backgroundColor(Color.BLACK);
        builder.buttonColor(0x33000000);
        builder.targetRectView(targetView);

        Intent intent = new Intent(context, RectRevealActivity.class);
        intent.putExtra(EXTRA_INFO, builder.create());

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rect_reveal);

        imageView = (ImageView)findViewById(R.id.activity_rect_reveal_image);
        mRevealFrameLayout = (RevealFrameLayout)findViewById(R.id.activity_rect_revealContainer);
        mRevealFrameLayout.setOnRevealAnimationListener(mOnRevealAnimationListener);
        mRevealFrameLayout.setBackgroundColor(Color.BLACK);

        Picasso.with(this).load(LauncherActivity.IMAGE_URL).into(imageView);
        Intent intent = getIntent();
        info = intent.getParcelableExtra(EXTRA_INFO);
        mRevealFrameLayout.setRevealAnimationInfo(info);
    }


    @Override
    public void finish (){
        if( mCloseAnimationFinished ) {
            super.finish();
        } else {
            if (!mRevealFrameLayout.isAnimationRunning()) {
                mRevealFrameLayout.closeView();
            }
        }
    }

    private RevealFrameLayout.OnRevealAnimationListener mOnRevealAnimationListener = new RevealFrameLayout.OnRevealAnimationListener() {

        @Override
        public void onRevealStart() {
//            float initialScaleX = 1f - (float) info.mStartWidth / (float)imageView.getMeasuredWidth();
//            float initialScaleY = 1f - (float) info.mStartHeight / (float)imageView.getMeasuredHeight();
//
//            int[] locations = new int[2];
//            imageView.getLocationInWindow(locations);
//
//            int mStartX = locations[0] + imageView.getMeasuredWidth() / 2;
//            int mStartY = locations[1] + imageView.getMeasuredHeight() / 2 - RevealAnimationInfo.getStatusBarHeight(getResources());
//            float translationX = info.mStartX - mStartX ;
//            float translationY = info.mStartY - mStartY;
//
//            ViewCompat.animate(imageView).scaleX(initialScaleX).scaleY(initialScaleY)
//                    .translationX(translationX).translationY(translationY).setDuration(0).start();
//
//            ViewCompat.animate(imageView).scaleX(1).scaleY(1)
//                    .translationX(0).translationY(0).setDuration(500l).setStartDelay(500l);
        }

        @Override
        public void onRevealEnd() {
        }

        @Override
        public void onStartClose() {
        }

        @Override
        public void onEndClose() {
            mCloseAnimationFinished = true;
            finish();
            overridePendingTransition(0, 0);
        }
    };

}
