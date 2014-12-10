package demo.reveal.wenhui.com.revealanimation.reveal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by wyao on 12/7/14.
 */
public class RevealAnimationInfo implements Parcelable {

    public static final int SHAPE_CIRCULAR = 1;
    public static final int SHAPE_RECT = 2;

    public int mButtonColor;
    public int mBackgroundColor;

    public int mAnimationDuration;
    public int mStartX;
    public int mStartY;
    public float mStartRadius;
    public float mStartRadiusOffset;
    public int mShape = SHAPE_RECT;
    public int mStartWidth = 0, mStartHeight = 0;


    public static final Parcelable.Creator<RevealAnimationInfo>
            CREATOR = new Parcelable.Creator<RevealAnimationInfo>(){

        @Override
        public RevealAnimationInfo createFromParcel(Parcel source) {
            return new RevealAnimationInfo(source);
        }

        @Override
        public RevealAnimationInfo[] newArray(int size) {
            return new RevealAnimationInfo[size];
        }
    };

    private RevealAnimationInfo(){
    }

    private RevealAnimationInfo(Parcel src){
        mButtonColor = src.readInt();
        mBackgroundColor = src.readInt();
        mAnimationDuration = src.readInt();
        mStartX = src.readInt();
        mStartY = src.readInt();
        mStartRadius = src.readFloat();
        mStartRadiusOffset = src.readFloat();
        mShape = src.readInt();
        mStartWidth = src.readInt();
        mStartHeight = src.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mButtonColor);
        dest.writeInt(mBackgroundColor);
        dest.writeInt(mAnimationDuration);
        dest.writeInt(mStartX);
        dest.writeInt(mStartY);
        dest.writeFloat(mStartRadius);
        dest.writeFloat(mStartRadiusOffset);
        dest.writeInt(mShape);
        dest.writeInt(mStartWidth);
        dest.writeInt(mStartHeight);
    }

    public static class Builder {

        private Context mContext;

        int mButtonColor;
        private int mBackgroundColor;

        int mAnimationDuration;
        int mStartX= Integer.MIN_VALUE;
        int mStartY = Integer.MIN_VALUE;
        float mStartRadius = Float.MIN_VALUE;
        float mStartRadiusOffset=0f;
        int mStartWidth = 0, mStartHeight = 0;
        public int mShape = SHAPE_CIRCULAR;

        public Builder(Context context) {
            this.mContext = context;
            mBackgroundColor = Color.BLACK;
            mButtonColor = Color.WHITE;
            mAnimationDuration = context.getResources().getInteger(android.R.integer.config_longAnimTime);
        }

        public Builder targetCircularView(View targetView) {
            int[] locations = new int[2];
            targetView.getLocationInWindow(locations);

            int x = locations[0];
            int y = locations[1];

            int width = targetView.getMeasuredWidth() - targetView.getPaddingLeft() - targetView
                    .getPaddingRight();
            int height = targetView.getMeasuredHeight() - targetView.getPaddingTop() -
                    targetView.getPaddingBottom();

            mStartRadius = Math.min(width, height) / 2;

            mStartX = x + width / 2;
            mStartY = y + height / 2 - getStatusBarHeight(mContext.getResources());

            mShape = SHAPE_CIRCULAR;

            return this;
        }

        public Builder targetRectView(View targetView) {
            int[] locations = new int[2];
            targetView.getLocationInWindow(locations);

            int x = locations[0];
            int y = locations[1];

            int width = targetView.getMeasuredWidth() - targetView.getPaddingLeft() - targetView
                    .getPaddingRight();
            int height = targetView.getMeasuredHeight() - targetView.getPaddingTop() -
                    targetView.getPaddingBottom();

            mStartWidth = targetView.getMeasuredWidth() / 2 ;
            mStartHeight = targetView.getMeasuredHeight() / 2;

            mStartX = x + width / 2;
            mStartY = y + height / 2 - getStatusBarHeight(mContext.getResources());

            mShape = SHAPE_RECT;

            return this;
        }

        public Builder offsetStartRadius(float offset){
            mStartRadiusOffset = offset;
            return this;
        }

        public Builder backgroundColor(int backgroundColor) {
            mBackgroundColor = backgroundColor;
            return this;
        }

        public Builder buttonColor(int buttonColor) {
            mButtonColor = buttonColor;
            return this;
        }

        public Builder duration(int duration) {
            mAnimationDuration = duration;
            return this;
        }

        public Builder startX(int startX){
            mStartX = startX;
            return this;
        }

        public Builder startY(int startY){
            mStartY = startY;
            return this;
        }

        public Builder startRadius(float startRadius){
            mStartRadius = startRadius;
            return this;
        }

        public RevealAnimationInfo create() {
            RevealAnimationInfo result =  new RevealAnimationInfo();
            result.mButtonColor = mButtonColor;
            result.mBackgroundColor = mBackgroundColor;
            result.mStartX= mStartX;
            result.mStartY= mStartY;
            result.mAnimationDuration = mAnimationDuration;
            result.mStartRadius = mStartRadius;
            result.mStartRadiusOffset = mStartRadiusOffset;
            result.mShape = mShape;
            result.mStartWidth = mStartWidth;
            result.mStartHeight = mStartHeight;
            return result;
        }

    }

    public static int getStatusBarHeight(Resources resources) {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
