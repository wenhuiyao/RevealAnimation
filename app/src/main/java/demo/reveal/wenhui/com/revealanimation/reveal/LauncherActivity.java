package demo.reveal.wenhui.com.revealanimation.reveal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import demo.reveal.wenhui.com.revealanimation.R;

/**
 * Created by wyao on 7/27/14.
 */
public class LauncherActivity extends FragmentActivity {

    public static final String IMAGE_URL = "https://lh6.googleusercontent.com/-kO2B2nLrgbw/VITfc8xrFEI/AAAAAAAAAOw/cbup-djw588/w2236-h1264-no/IMAG1711.jpg";

    private View mRevealButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mRevealButton = findViewById(R.id.revealButton);
        mRevealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RevealActivity.getLaunchIntent(LauncherActivity.this, v,
                        getResources().getColor(R.color.green));

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        ImageView iv = (ImageView)findViewById(R.id.activity_intro_image);
        Picasso.with(this).load(IMAGE_URL).into(iv);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RectRevealActivity.getLaunchIntent(LauncherActivity.this, v);

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

    }

}
