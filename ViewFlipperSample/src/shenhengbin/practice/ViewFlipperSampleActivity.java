package shenhengbin.practice;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

public class ViewFlipperSampleActivity extends Activity {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private ViewFlipper vf;
	private Context mContext;
	private final GestureDetector detector = new GestureDetector(
			new MyGestureDetector());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext = this;
		vf = (ViewFlipper) this.findViewById(R.id.vfShow);
		vf.setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(final View view, final MotionEvent event) {
	        	detector.onTouchEvent(event);
	            return true;
	        }
	    });

		vf.addView(addImageView(R.drawable.scott));
		vf.addView(addImageView(R.drawable.ricardo));

	}

	View addImageView(int resId) {
		ImageView iv = new ImageView(this);
		iv.setImageResource(resId);

		return iv;
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {

				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
							R.anim.left_in));
					vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
							R.anim.left_out));
					vf.showNext();
					return true;
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					vf.setInAnimation(AnimationUtils.loadAnimation(mContext,
							R.anim.right_in));
					vf.setOutAnimation(AnimationUtils.loadAnimation(mContext,
							R.anim.right_out));
					vf.showPrevious();
					return true;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return false;
		}
	}
}