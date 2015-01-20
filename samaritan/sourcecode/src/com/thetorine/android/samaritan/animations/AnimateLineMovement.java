package com.thetorine.android.samaritan.animations;

import com.thetorine.android.samaritan.TextActivity;
import com.thetorine.android.samaritan.TextActivityLandscape;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class AnimateLineMovement extends Animation {
	private View line;
	
	public AnimateLineMovement(View line) {
		this.line = line;
	}
	
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		
		int currentTime = (int) (getDuration() * interpolatedTime);
        if(currentTime < 2) {
        	float alphaLevel = currentTime / 2f;
        	line.setAlpha(alphaLevel);
        }  else {
        	line.setAlpha(1);
        } 
		
		int start = determineLineStart();
		int screenWidth = TextActivity.storage.screenWidth;
		line.getLayoutParams().width = TextActivity.storage.mWidth;
		float modifier = start - ((start - (screenWidth-TextActivity.storage.mWidth)/2)*interpolatedTime);
		if(modifier > 0) {
			line.setX(modifier);
			line.requestLayout();
		}
	}
	
	private int determineLineStart() {
		int index = TextActivity.storage.currentIndex;
    	int screenWidth = TextActivity.storage.screenWidth;
    	int currentWidth = TextActivity.storage.currentWidth;
    	if (index > 0) {
			switch(TextActivity.storage.wordLength) {
				case 1: return (screenWidth - currentWidth) / 2;
				case 2:
				case 3: return  (int) ((screenWidth / 2) - (TextActivityLandscape.textView.getPaint().measureText(" ")/2));
				default: return (int) ((screenWidth / 2) - (TextActivityLandscape.textView.getPaint().measureText("   ")/2));
			}
		} else {
			return (screenWidth - currentWidth) / 2;
		}
	}
}
