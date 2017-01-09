package com.thetorine.android.samaritan.animations;

import com.thetorine.android.samaritan.TextActivity;
import com.thetorine.android.samaritan.TextActivityLandscape;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimateLineSize extends Animation {

    private View animatingView;
    private int startWidth;
    private int maxWidth;
    
    private boolean landscape;
    private boolean modifyX;

    public AnimateLineSize(View view, int width, boolean landscape, boolean modifyX) {
        this.animatingView = view;
        startWidth = animatingView.getWidth();
        this.maxWidth = width;
        this.landscape = landscape;
        if(landscape) {
        	 animatingView.setAlpha(0);
        }
        this.modifyX = modifyX;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        if(!landscape) {
        	int newWidth = 0;
            int lineWidth = animatingView.getLayoutParams().width;

            if(lineWidth < maxWidth) {
                newWidth = (int) (startWidth + (Math.abs(maxWidth - startWidth)*interpolatedTime));
            } else if(lineWidth > maxWidth) {
                newWidth = (int) (startWidth - (Math.abs(maxWidth - startWidth)*interpolatedTime));
            }
            
            if(newWidth != 0) {
                animatingView.getLayoutParams().width = newWidth;
                if(modifyX) {
                	int screenWidth = TextActivity.storage.screenWidth;
                    animatingView.setX((screenWidth-newWidth)/2f);
                }
                animatingView.requestLayout();
            }
        } else {
        	int currentTime = (int) (getDuration() * interpolatedTime);
            if(currentTime < 2) {
            	float alphaLevel = currentTime / 2f;
            	animatingView.setAlpha(alphaLevel);
            }  else {
            	animatingView.setAlpha(1);
            } 
        	
        	animatingView.getLayoutParams().width = maxWidth;
        	int index = TextActivity.storage.currentIndex;
        	int screenWidth = TextActivity.storage.screenWidth;
        	int currentWidth = TextActivity.storage.currentWidth;
        	if (index > 0) {
				switch(TextActivity.storage.wordLength) {
					case 1: animatingView.setX((screenWidth - currentWidth) / 2); break;
					case 2:
					case 3: animatingView.setX((screenWidth / 2) - (TextActivityLandscape.textView.getPaint().measureText(" ")/2)); break;
					default: animatingView.setX((screenWidth / 2) - (TextActivityLandscape.textView.getPaint().measureText("   ")/2)); break;
				}
			} else {
				animatingView.setX((screenWidth - currentWidth) / 2);
			}
        }
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
