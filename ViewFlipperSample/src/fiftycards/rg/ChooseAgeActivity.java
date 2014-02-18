package fiftycards.rg;

import shenhengbin.practice.R;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class ChooseAgeActivity extends Activity implements android.view.View.OnClickListener {

	private Context mContext;
    public final static String EXTRA_INDEX = "AGE_INDEX";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.age);
		setTitle("50карточек.рф");
		mContext = this;
		
		LinearLayout item1=(LinearLayout)findViewById(R.id.item1);
		item1.setOnClickListener(this);
		LinearLayout item2=(LinearLayout)findViewById(R.id.item2);
		item2.setOnClickListener(this);
		LinearLayout item3=(LinearLayout)findViewById(R.id.item3);
		item3.setOnClickListener(this);
		LinearLayout item4=(LinearLayout)findViewById(R.id.item4);
		item4.setOnClickListener(this);
		LinearLayout item5=(LinearLayout)findViewById(R.id.item5);
		item5.setOnClickListener(this);
		LinearLayout item6=(LinearLayout)findViewById(R.id.item6);
		item6.setOnClickListener(this);

	}

    public void onClick(View v)
    {
    	Log.v("BUG","Click");
    	int itemId=0;
    	switch(v.getId())
        {
            case R.id.item1:
                itemId=1;
                break;
            case R.id.item2:
                itemId=2;
                break;
            case R.id.item3:
                itemId=3;
                break;
            case R.id.item4:
                itemId=4;
                break;
            case R.id.item5:
                itemId=5;
                break;
            case R.id.item6:
                itemId=6;
                break;
        }
        final Intent intent = new Intent(getApplicationContext(), ChooseDeckActivity.class);
        Log.v("BUG","put itemId="+itemId);
        intent.putExtra("ITEM_ID", itemId);
        startActivity(intent);

    }
}