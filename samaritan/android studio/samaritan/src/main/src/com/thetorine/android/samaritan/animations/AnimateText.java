package com.thetorine.android.samaritan.animations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.thetorine.android.samaritan.InputActivity;
import com.thetorine.android.samaritan.TextActivity;
import com.thetorine.android.samaritan.TextActivityLandscape;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.animation.*;
import android.widget.TextView;

public class AnimateText extends Animation {
    private TextView textView;
    private String animatingWord;
    private int wordIndex;
    private int screenWidth;
    private boolean landscape;

    public AnimateText(TextView view, String word, int wordIndex, int screenWidth, boolean landscape) {
        this.textView = view;
        this.animatingWord = word;
        this.wordIndex = wordIndex;
        this.screenWidth = 1920;
        this.landscape = landscape;
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
					TextActivity.storage.textChanged = true;
					TextActivity.storage.screenWidth = determineHeight();
				}
			});
        }
        
        if(InputActivity.sharedPref.getBoolean("pref_transitionText", false) || landscape) {
        	int currentTime = (int) (getDuration() * interpolatedTime);
            if(currentTime < 50) {
            	float alphaLevel = currentTime / 50f;
            	textView.setAlpha(alphaLevel);
            }  else {
            	textView.setAlpha(1);
            } 
    	}
        
		if (landscape) {
			if (wordIndex > 0) { 
				switch(TextActivity.storage.wordLength) {
				case 1: textView.setX((screenWidth - TextActivity.storage.mWidth) / 2); break;
				case 2:
				case 3: textView.setX((screenWidth / 2) - (TextActivityLandscape.textView.getPaint().measureText(" ")/2)); break;
				default: textView.setX((screenWidth / 2) - (TextActivityLandscape.textView.getPaint().measureText("   ")/2)); break;
			}
			} else {
				textView.setX((screenWidth - TextActivity.storage.mWidth) / 2);
			}
			
			TextActivity.storage.currentIndex = wordIndex;
			TextActivity.storage.currentWidth = TextActivity.storage.mWidth;
			TextActivity.storage.wordLength = animatingWord.length();
			
			Log.d("Samaritan", Integer.toString(determineHeight()));
		}
    }
    
    @SuppressLint("NewApi")
	private int determineHeight() {
    	int width = 0, height = 0;
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((Activity)textView.getContext()).getWindowManager().getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(metrics);

                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e3) {  
            e3.printStackTrace();
        }
        return width > height ? width : height;
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}

