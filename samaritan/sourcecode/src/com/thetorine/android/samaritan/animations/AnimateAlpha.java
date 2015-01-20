package com.thetorine.android.samaritan.animations;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

public class AnimateAlpha extends Animation {

    private float sAlpha;
    private boolean startPoint;
    private ImageView imageView;

    public AnimateAlpha(ImageView view) {
        sAlpha = view.getAlpha();
        if(sAlpha >= 1) {
            startPoint = true;
        } else if(sAlpha <= 0) {
            startPoint = false;
        }
        this.imageView = view;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if(interpolatedTime > 0) {
            float nAlpha = 0f;
            if(!startPoint) {
                nAlpha = sAlpha + (1*interpolatedTime);
            } else {
                nAlpha = sAlpha - (1*interpolatedTime);
            }
            imageView.setAlpha(nAlpha);
            imageView.requestLayout();
        }
    }
}
