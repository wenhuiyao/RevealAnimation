package demo.reveal.wenhui.com.revealanimation.reveal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import demo.reveal.wenhui.com.library.RevealAnimationInfo;
import demo.reveal.wenhui.com.library.RevealFrameLayout;
import demo.reveal.wenhui.com.revealanimation.R;

/**
 * Created by wyao on 12/5/14.
 */
public class RevealActivity extends Activity {

    private RevealFrameLayout mRevealFrameLayout;

    private static final String EXTRA_INFO = "EXTRA_INFO";

    private boolean mCloseAnimationFinished = false;

    private final static float SHADOW_SIZE = 9F;

    public final static Intent getLaunchIntent(Context context, View targetView, int viewColor){
        RevealAnimationInfo.Builder builder = new RevealAnimationInfo.Builder(context);
        builder.backgroundColor(Color.BLACK);
        builder.buttonColor(viewColor);
        builder.targetCircularView(targetView);
        builder.offsetStartRadius(SHADOW_SIZE);

        Intent intent = new Intent(context, RevealActivity.class);
        intent.putExtra(EXTRA_INFO, builder.create());

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveal);

        ImageView imageView = (ImageView)findViewById(R.id.image);
        mRevealFrameLayout = (RevealFrameLayout)findViewById(R.id.revealContainer);
        mRevealFrameLayout.setOnRevealAnimationListener(mOnRevealAnimationListener);
        mRevealFrameLayout.setBackgroundColor(Color.BLACK);

        Picasso.with(this).load(LauncherActivity.IMAGE_URL).into(imageView);

        Intent intent = getIntent();
        RevealAnimationInfo info = intent.getParcelableExtra(EXTRA_INFO);
        if( info != null ) {
            // Create the drawable for the reveal animation
            CircularDrawable circularDrawable = new CircularDrawable(this, R.drawable.ic_editor_mode_edit);
            circularDrawable.setBackgroundColor(Color.TRANSPARENT);

            int radius = (int)info.mStartRadius;
            int left = info.mStartX - radius;
            int right = info.mStartX + radius;
            int top = info.mStartY - radius;
            int bottom = info.mStartY + radius;
            circularDrawable.setBounds(left, top, right, bottom);

            mRevealFrameLayout.setTargetDrawable(circularDrawable);
            mRevealFrameLayout.setRevealAnimationInfo(info);
        }
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
