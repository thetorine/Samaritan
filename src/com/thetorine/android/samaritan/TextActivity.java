package com.thetorine.android.samaritan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.thetorine.android.samaritan.animations.AnimateAlpha;
import com.thetorine.android.samaritan.animations.AnimateLine;
import com.thetorine.android.samaritan.animations.AnimateScale;
import com.thetorine.android.samaritan.animations.AnimateText;
import com.thetorine.android.samaritan.utilities.DynamicTextView;
import com.thetorine.android.samaritan.utilities.Storage;
import com.thetorine.samaritan.R;

import java.util.ArrayList;
import java.util.List;

public class TextActivity extends Activity implements Runnable {
    private List<String> mWords = new ArrayList<String>();
    private int mWordIndex;
    private Handler mHandler = new Handler();
    private boolean mDestroyed;
    private boolean mRun;
    private boolean mTriangleStatus;
    
    public static Storage storage;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme(InputActivity.sharedPref.getBoolean("pref_theme", false), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        parseText(getIntent().getStringExtra("text"));
        applyTheme(InputActivity.sharedPref.getBoolean("pref_theme", false), true);
        
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/magdacleanmono-regular.otf");
        ((DynamicTextView)findViewById(R.id.displayText)).setTypeface(font);
        
        storage = new Storage();
        mHandler.postDelayed(this, 100);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDestroyed = true;
        finish();
    } 

    @Override
    public void run() {
        DynamicTextView tv = (DynamicTextView) findViewById(R.id.displayText);
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        if(mRun) {
            if(mWordIndex < mWords.size()) {
               changeText(tv);
               animateLine();
            } else {
                mRun = false;
            }
            openCloseTriangle(iv, 0);
            if(showTriangle()) {
            	blinkTriangle(iv);
            }
        } else {
        	openCloseTriangle(iv, 1);
            blinkTriangle(iv);
            animateLine();
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
                DynamicTextView tv = (DynamicTextView) findViewById(R.id.displayText);
                String word = mWords.get(mWordIndex);

                AnimateText animateText = new AnimateText(tv, word);
                animateText.setDuration(getDisplayTime());
                tv.startAnimation(animateText);
                mWordIndex++;
                break;
            }
            case 1: {
                ImageView iv = (ImageView)findViewById(R.id.imageView);

                AnimateAlpha animateTriangle = new AnimateAlpha(iv);
                animateTriangle.setDuration(getDisplayTime());
                iv.startAnimation(animateTriangle);
                break;
            }
            case 2: {
                ImageView iv = (ImageView)findViewById(R.id.imageView);

                AnimateScale animateScale = new AnimateScale(iv, getResources().getDisplayMetrics().density);
                animateScale.setDuration(200);
                iv.startAnimation(animateScale);
                break;
            }
            case 3: {
            	View view = findViewById(R.id.black_line);
            	
            	AnimateLine animateLine = new AnimateLine(view, storage.mWidth);
                animateLine.setDuration((long) Math.ceil(getDisplayTime() * 0.3D));
                view.startAnimation(animateLine);
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
    	if(!(storage.mLastWidth == storage.mWidth)) {
    		storage.mLastWidth = storage.mWidth;
    		runAnimation(3);
    	}
    }
    
    /**
     * Checks for any animations and either opens or closes the triangle.
     * @param iv the ImageView that corresponds to the triangle on screen.
     * @param method decides on whether to open or close the triangle. 
     */
    private void openCloseTriangle(ImageView iv, int method) {
    	if(!showTriangle()) {
    		if(method == 0) {
    			if(!mTriangleStatus) {
    				runAnimation(2);
                	mTriangleStatus = true;
                	iv.setAlpha(1f);
    			}
    		} else {
    			if(mTriangleStatus) {
    				runAnimation(2);
                	mTriangleStatus = false;
                	iv.setAlpha(1f);
                	mWordIndex = 0;
    			}
    		}
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
                	View line = findViewById(R.id.black_line);
                	if(line.getAnimation() != null) {
                		if(line.getAnimation().hasEnded()) {
                			mRun = true;
                		} 
                	} else {
                		mRun = true;
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
        for(String s : text.toUpperCase().split(" ")) {
            s = " " + s + " ";
            if(s.contains("?")) {
                mWords.add(s.replace("?", ""));
                mWords.add(" ? ");
            } else if(s.contains("!")) {
                mWords.add(s.replace("!", ""));
                mWords.add(" ! ");
            } else {
                mWords.add(s);
            }
        }
        mWords.add("   ");
    }
    
    /**
     * Reads the preference file and returns whether the triangle should disappear 
     * when Samaritan is running.
     * @return
     */
    private boolean showTriangle() {
    	return !InputActivity.sharedPref.getBoolean("pref_triangleStatus", false);
    }
    
    private int getDisplayTime() {
    	int time = 500;
    	try {
    		time = Integer.parseInt(InputActivity.sharedPref.getString("pref_textDisplayLength", "500"));
    	} catch(NumberFormatException e) {}
    	return time;
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
                View view = findViewById(R.id.black_line);
                view.setBackgroundColor(getResources().getColor(android.R.color.black));

                DynamicTextView textView = (DynamicTextView) findViewById(R.id.displayText);
                textView.setTextColor(getResources().getColor(android.R.color.black));
            }
        } else {
            this.setTheme(R.style.TextBlackTheme);
            if(line) {
                View view = findViewById(R.id.black_line);
                view.setBackgroundColor(getResources().getColor(android.R.color.white));

                DynamicTextView textView = (DynamicTextView) findViewById(R.id.displayText);
                textView.setTextColor(getResources().getColor(android.R.color.white));
            }
        }
    }
}
