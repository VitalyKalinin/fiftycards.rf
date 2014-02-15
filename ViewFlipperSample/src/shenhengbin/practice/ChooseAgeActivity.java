package shenhengbin.practice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class ChooseAgeActivity extends Activity implements OnTouchListener {

	private Context mContext;
    public final static String EXTRA_INDEX = "AGE_INDEX";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.age);
		setTitle("50карточек.рф");
		mContext = this;
	}

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) 
    { 
    	return true;
    } 
}