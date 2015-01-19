package com.thetorine.android.samaritan.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimateLine extends Animation {

    private View animatingView;
    private int startWidth;
    private int maxWidth;

    public AnimateLine(View view, int width) {
        this.animatingView = view;
        startWidth = animatingView.getWidth();
        this.maxWidth = width;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        int newWidth = 0;
        int lineWidth = animatingView.getLayoutParams().width;

        if(lineWidth < maxWidth) {
            newWidth = (int) (startWidth + (Math.abs(maxWidth - startWidth)*interpolatedTime));
        } else if(lineWidth > maxWidth) {
            newWidth = (int) (startWidth - (Math.abs(maxWidth - startWidth)*interpolatedTime));
        }

        if(newWidth != 0) {
            animatingView.getLayoutParams().width = newWidth;
            animatingView.requestLayout();
        }
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
