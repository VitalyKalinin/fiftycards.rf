package shenhengbin.practice;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
        .memoryCacheSize(41943040)
        .discCacheSize(1048576000)
        .threadPoolSize(16)
        .build();		        
        imageLoader.init(config);

        for(int i=1;i<36;++i)
        	vf.addView(addImageView(i));
		//vf.addView(addImageView(R.drawable.ricardo));
	}

	View addImageView(int resId) {
		final ImageView iv = new ImageView(this);
		//imageLoader.displayImage("http://localhost/phpmyadmin/deck01/"+resId+".png", iv, options);
		final String url="http://192.168.0.101/phpmyadmin/deck01/"+resId+".png";
        new Thread(new Runnable(){
            @Override
            public void run() 
            {
        		final Bitmap bitmap;
        		URL newurl;
        		try {
                    newurl = new URL( url );
                    bitmap = BitmapFactory.decodeStream( newurl.openConnection( ).getInputStream( ) );
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(bitmap);
                        }
                    });*/
                    iv.setImageBitmap(bitmap);
                } catch ( MalformedURLException e ) {
                    e.printStackTrace( );
                } catch ( IOException e ) {

                    e.printStackTrace( );
                }
            }
        }).start();

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