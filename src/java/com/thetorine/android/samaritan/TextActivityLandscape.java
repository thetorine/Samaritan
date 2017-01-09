package com.thetorine.android.samaritan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.thetorine.android.samaritan.animations.AnimateAlpha;
import com.thetorine.android.samaritan.animations.AnimateLineMovement;
import com.thetorine.android.samaritan.animations.AnimateLineSize;
import com.thetorine.android.samaritan.animations.AnimateScale;
import com.thetorine.android.samaritan.animations.AnimateText;
import com.thetorine.android.samaritan.utilities.DynamicTextView;
import com.thetorine.samaritan.R;

import java.util.ArrayList;
import java.util.List;

public class TextActivityLandscape extends Activity implements Runnable {
    private List<List<String>> mWords = new ArrayList<List<String>>();
    public int mWordIndex = -1;
    private int loopIndex = -1;
    private Handler mHandler = new Handler();
    private boolean mDestroyed;
    private boolean mRun;
    public static DynamicTextView textView;
    private ImageView triangleView;
    private View lineView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme(InputActivity.sharedPref.getBoolean("pref_theme", false), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        parseText(getIntent().getStringExtra("text"));
        
        this.textView = (DynamicTextView) this.findViewById(R.id.displayText);
        this.triangleView = (ImageView) this.findViewById(R.id.imageView);
        this.lineView = (View) this.findViewById(R.id.black_line);
        
        applyTheme(InputActivity.sharedPref.getBoolean("pref_theme", false), true);
        
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/magdacleanmono-regular.otf");
        ((DynamicTextView)findViewById(R.id.displayText)).setTypeface(font);
        
        mHandler.postDelayed(this, 100);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        	getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDestroyed = true;
        finish();
    } 

    @Override
    public void run() {
        if(mRun) {
            if(mWordIndex < mWords.get(loopIndex).size()-1) {
               changeText(textView);
               animateLine();
            } else {
                mRun = false;
            }
            blinkTriangle(triangleView);
        } else {
            blinkTriangle(triangleView);
            if(checkLastWord() || checkLastWordMovement()) {
            	 animateLine();
            } 
        }
        if(!mDestroyed) {
            mHandler.postDelayed(this, 10);
        }
    }
    
    /**
     * Runs animation according to supplied id.
     * @param animation the animation to run.
     */
    private void runAnimation(int animation) {
        switch(animation) {
            case 0: {
            	mWordIndex++;
                String word = mWords.get(loopIndex).get(mWordIndex);

                AnimateText animateText = new AnimateText(textView, word, mWordIndex, getResources().getDisplayMetrics().widthPixels, true);
                animateText.setDuration(getDisplayTime());
                textView.startAnimation(animateText);
                
                break;
            }
            case 1: {
                AnimateAlpha animateTriangle = new AnimateAlpha(triangleView);
                animateTriangle.setDuration(getDisplayTime());
                triangleView.startAnimation(animateTriangle);
                break;
            }
            case 2: {
                AnimateScale animateScale = new AnimateScale(triangleView, getResources().getDisplayMetrics().density);
                animateScale.setDuration(200);
                triangleView.startAnimation(animateScale);
                break;
            }
            case 3: {
            	Animation animateLine = null;
            	if(checkLastWord()) {
            		animateLine = new AnimateLineSize(lineView, TextActivity.storage.mWidth, false, true);
            	} else if(checkLastWordMovement()) {
            		animateLine = new AnimateLineMovement(lineView);
            	} else {
            		animateLine = new AnimateLineSize(lineView, TextActivity.storage.mWidth, true, false);
            	}
                animateLine.setDuration((long) Math.ceil(getDisplayTime() * 0.3D));
                lineView.startAnimation(animateLine);
                break;
            }
        }
    }
    
    /**
     * Changes the word of the DynamicTextView in the current view.
     * @param tv the DynamicTextView to change the word of.
     */
    private void changeText(DynamicTextView tv) {
    	 Animation a = tv.getAnimation();
         if(a != null) {
             if(a.hasEnded()) {
                 runAnimation(0);
             }
         } else {
             runAnimation(0);
         }
    }
    
    private void animateLine() {
    	if(TextActivity.storage.textChanged) {
    		runAnimation(3);
    		TextActivity.storage.textChanged = false;
    	}
    }
    
    /**
     * Blinks the triangle associated with this image.
     * @param iv the ImageView to blink.
     */
    private void blinkTriangle(ImageView iv) {
    	Animation animation = iv.getAnimation();
        if(animation != null) {
            if(animation.hasEnded()) {
                runAnimation(1);
            }
        } else {
            runAnimation(1);
        }
    }
    
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.displayText_layout: {
                if(!mRun) {
                	if(lineView.getAnimation() != null) {
                		if(lineView.getAnimation().hasEnded()) {
                			mRun = true;
                			loopIndex++;
                			if(loopIndex == mWords.size()) {
                				loopIndex = 0;
                			}
                			mWordIndex = -1;
                		} 
                	} else {
                		mRun = true;
                		loopIndex++;
            			if(loopIndex == mWords.size()) {
            				loopIndex = 0;
            			}
            			mWordIndex = -1;
                	}
                }
            }
        }
    }

    public String getRotation(Context context){
        @SuppressWarnings("deprecation")
		final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return "portrait";
            case Surface.ROTATION_90:
                return "landscape";
            case Surface.ROTATION_180:
                return "portrait";
            default:
                return "landscape";
        }
    }

    /**
     * Parses the raw text received from InputActivity.
     * @param text the text to parse.
     */
    public void parseText(String text) {
        String[] loops = text.split("-");
        for(String s : loops) {
        	List<String> allWords = new ArrayList<String>();
        	for(String w : s.toUpperCase().split(" ")) {
        		if(w.contains("?")) {
        			allWords.add(w.replace("?", ""));
        			allWords.add("?");
        		} else if(w.contains("!")) {
        			allWords.add(w.replace("!", ""));
        			allWords.add("!");
        		} else {
        			allWords.add(w);
        		}
        	}
        	allWords.add("     ");
    		mWords.add(allWords);
        }
    }
    
    private int getDisplayTime() {
    	int time = 500;
    	try {
    		time = Integer.parseInt(InputActivity.sharedPref.getString("pref_textDisplayLength", "500"));
    	} catch(NumberFormatException e) {}
    	return time;
    }
    
    private boolean checkLastWord() {
    	if(loopIndex > -1) {
    		if(mWordIndex == mWords.get(loopIndex).size()-1) {
        		String prevWord = mWords.get(loopIndex).get(mWordIndex-1);
            	if(prevWord.length() == 1) {
            		return true;
            	}
        	}
    	}
    	return false;
    }
    
    private boolean checkLastWordMovement() {
    	if(loopIndex > -1) {
    		if(mWordIndex == mWords.get(loopIndex).size()-1) {
        		String prevWord = mWords.get(loopIndex).get(mWordIndex-1);
            	if(prevWord.length() > 1) {
            		return true;
            	}
        	}
    	}
    	return false;
    }

    /**
     * Changes the theme of the current view according the settings activity. 
     * @param b the colour of the view that should be set.
     * @param line whether to change the individual elements on screen according to the supplied colour.
     */
    public void applyTheme(boolean b, boolean line) {
        if(!b) {
            this.setTheme(R.style.TextWhiteTheme);
            if(line) {
                lineView.setBackgroundColor(getResources().getColor(android.R.color.black));
                textView.setTextColor(getResources().getColor(android.R.color.black));
            }
        } else {
            this.setTheme(R.style.TextBlackTheme);
            if(line) {
                lineView.setBackgroundColor(getResources().getColor(android.R.color.white));
                textView.setTextColor(getResources().getColor(android.R.color.white));
            }
        }
    }
}
