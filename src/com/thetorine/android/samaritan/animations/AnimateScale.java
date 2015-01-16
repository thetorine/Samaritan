package com.thetorine.android.samaritan.animations;

import java.lang.reflect.Field;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

public class AnimateScale extends Animation {

    private ImageView imageView;
    private int sMeasurement;
    private boolean sPoint;
    private int density;
    private int textSP;

    public AnimateScale(ImageView view, float d) {
        this.imageView = view;
        this.density = (int)d;
        this.textSP = (int) (getMaxWidth(imageView) / d);
        if(view.getWidth() <= 0) {
            sPoint = false;
        } else if(view.getWidth() >= 0) {
            sPoint = true;
            sMeasurement = textSP*density;
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        int mApply = 0;
        if(!sPoint) {
            mApply = (int) (sMeasurement + (textSP*density*interpolatedTime));
        } else {
            mApply = (int) (sMeasurement - (textSP*density*interpolatedTime));
        }
        imageView.getLayoutParams().width = mApply;
        imageView.getLayoutParams().height = mApply;
        imageView.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
    
    private int getMaxWidth(ImageView view) {
    	try {
    		Field maxWidthField = ImageView.class.getDeclaredField("mMaxWidth");
    		maxWidthField.setAccessible(true);
    		int maxWidth = (Integer) maxWidthField.get(view);
    		return maxWidth;
    	} catch(Exception e) {}
    	return 0;
    }
}
