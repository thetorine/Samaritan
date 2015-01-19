package com.thetorine.android.samaritan.animations;

import com.thetorine.android.samaritan.InputActivity;

import android.os.Handler;
import android.view.animation.*;
import android.widget.TextView;

public class AnimateText extends Animation {
    private TextView textView;
    private String animatingWord;

    public AnimateText(TextView view, String word) {
        this.textView = view;
        this.animatingWord = word;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if(!textView.getText().toString().equals(animatingWord)) {
        	if(InputActivity.sharedPref.getBoolean("pref_transitionText", false)) {
        		textView.setAlpha(0);
        	}
			Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					textView.setText(animatingWord);
				}
			});
        }
        
        if(InputActivity.sharedPref.getBoolean("pref_transitionText", false)) {
        	int currentTime = (int) (getDuration() * interpolatedTime);
            if(currentTime < 50) {
            	float alphaLevel = currentTime / 50f;
            	textView.setAlpha(alphaLevel);
            }  else {
            	textView.setAlpha(1);
            } 
    	}
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}

