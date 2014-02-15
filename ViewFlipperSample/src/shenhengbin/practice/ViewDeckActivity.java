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

public class ViewDeckActivity extends Activity implements OnTouchListener {

	private Context mContext;
    public final static String EXTRA_INDEX = "CARD_INDEX";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_deck);
		setTitle("50карточек.рф");
		mContext = this;
		ImageView imageView = (ImageView) findViewById(R.id.deck);
		imageView.setOnTouchListener(this);
		try
		{
			Bitmap bmp = BitmapFactory.decodeStream(mContext.getAssets().open("image/deck.jpg"));
			imageView.setImageBitmap(bmp);
		}
	    catch (Exception e)
	    {
	        Log.e("Exception", e.getLocalizedMessage());
	    }
	}

	View addImageView(int resId) {
		ImageView iv = new ImageView(this);
		iv.setImageResource(resId);

		return iv;
	}

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) 
    { 
    	// Detect index, which card clicked
    	if(arg1.getAction()==MotionEvent.ACTION_DOWN)
    	{
    		Log.v("DECK","Touch");
    		ImageView imageView = (ImageView) findViewById(R.id.deck);
    		int totalWidth=imageView.getWidth();
    		Log.v("DECK","w="+totalWidth);
    		int totalHeight=imageView.getHeight();
    		Log.v("DECK","h="+totalHeight);
    		int col=(int) arg1.getX()/(totalWidth/7);
    		Log.v("DECK","col="+col);
    		int row=(int) arg1.getY()/(totalHeight/7);
    		Log.v("DECK","row="+row);
    	    Intent intent = new Intent(this, ViewFlipperSampleActivity.class);
    	    int index=row*7+col;
    	    intent.putExtra(EXTRA_INDEX, index);
    	    startActivity(intent);

    	}

    	return true;
    } 
}