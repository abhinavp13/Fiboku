package com.pabhinav.fiboku.customanimation;

import android.content.Context;
import android.media.Image;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.pabhinav.fiboku.R;

/**
 * @author pabhinav
 */
public class PressEffectAnimation implements View.OnTouchListener{

    private Context context;
    private ImageView[] imageViews;

    public PressEffectAnimation(Context context, ImageView[] imageViews){
        this.context = context;
        this.imageViews = imageViews;
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i].setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        /** If id matches **/
        for (int i = 0; i < imageViews.length; i++) {

            /** If press state is caught, show circular collapse **/
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.circular_collapse);
                imageViews[i].startAnimation(animation);
            } else if(event.getAction() == MotionEvent.ACTION_UP) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.circular_expand);
                imageViews[i].startAnimation(animation);
                imageViews[i].performClick();
            }
        }
        return true;
    }
}
