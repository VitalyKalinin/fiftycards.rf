package fiftycards.rg;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class ChooseDeckActivity extends Activity implements OnTouchListener {

	private Context mContext;
    public final static String EXTRA_INDEX = "AGE_INDEX";
    GridView glv;
    DeckAdapter deckListAdapter;
    List<Deck> deckList = new ArrayList<Deck>();    
    int ageId=0;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_deck);
		setTitle("50карточек.рф");
		mContext = this;
		glv = (GridView) findViewById( R.id.gridview );
		glv.setNumColumns(2);
		glv.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v,
	                int position, long id) 
	        {
	            final Intent intent = new Intent(getApplicationContext(), ViewDeckActivity.class);
	            //intent.putExtra(TestConstants.SELCTED_SCENE_KEY, position);
	            startActivity(intent);
	        }
		});
		for(int i=0;i<20;++i)
			deckList.add(new Deck("1","Name#"+i,"bmp"));
		deckListAdapter = new DeckAdapter(this, deckList);
		getDecks();
		glv.setAdapter(deckListAdapter);
		
        ageId=getIntent().getExtras().getInt("ITEM_ID");
        
        Log.v("BUG","ageId="+ageId);
        Toast.makeText(this, "AgeId="+ageId, Toast.LENGTH_LONG).show();
        
        if(android.os.Build.VERSION.SDK_INT >= 14)
        {
            setHomeButton();
        }
	}

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) 
    { 
    	return true;
    } 


	void getDecks()
	{
		HttpAsyncClient sad = new HttpAsyncClient(mContext, "Получение информации о совершенных покупках...");
			sad.showProgressDialog=true;
			sad.callBack=new HttpCallback() {
				@Override public void callBack(String result, boolean gettedFromCache) {
						try {
							ArrayList<Deck> newDeckList=getDecksFromXML(result);
							for(int i=0;i<newDeckList.size();++i)
								deckList.add(newDeckList.get(i));

							Log.v("BUG","Count="+deckList.size());	
							Log.v("BUG","Name="+deckList.get(1).name);
							//glv.setAdapter(deckListAdapter);
							deckListAdapter.notifyDataSetChanged();							
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
				@Override public void Prepare() {}
			};
			deckList.clear();
			sad.execute("http://192.168.0.101/phpmyadmin/decks.xml?id="+ageId);
	}

	
	private ArrayList<Deck> getDecksFromXML(String xml) throws XmlPullParserException, IOException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
    	factory.setNamespaceAware(true);
    	XmlPullParser xpp = factory.newPullParser();
    	String buf = "";
    	Deck deck = null;
    	boolean inGenres=false;
    	boolean incast=false;
    	ArrayList<Deck> decks=new ArrayList<Deck>();
    	
    	boolean inposter=false;
    	boolean firstposter=true;
    	xpp.setInput( new StringReader ( xml ) );
    	int eventType = xpp.getEventType();
    	while (eventType != XmlPullParser.END_DOCUMENT) 
    	{
    		if(eventType == XmlPullParser.START_DOCUMENT) 
    		{
    		}
    		else if(eventType == XmlPullParser.START_TAG) {
    	        buf = "";
    	        String tag = xpp.getName();
    	        if (tag.equals("deck")) {
    	        	deck = new Deck();
    	        }
    		} 
    		else if(eventType == XmlPullParser.END_TAG) {
    			String tag = xpp.getName();    	        
    			if (tag.equals("deck")) 
    			{
    	        	decks.add(deck);
    	        } 
    			else if (tag.equals("deck_id")) 
    			{
    	        	deck.id = buf;
    	        } 
    			else if (tag.equals("deck_name")) 
    			{
    	        	deck.name = buf;    	        	
    			}
    			else if (tag.equals("deck_thumb")) 
    			{
    	        	deck.image = buf;    	        	
    			} 
    		} else if(eventType == XmlPullParser.TEXT) {
    			buf = xpp.getText();
    		}
    		eventType = xpp.next();
    	}
    	return decks;
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		
	      // Inflate the menu; this adds items to the action bar if it is present.
	      getMenuInflater().inflate(R.menu.main, menu);
	      return true;
	}
	
	 @Override
	 public boolean onOptionsItemSelected(MenuItem menu)
	 {
	     switch(menu.getItemId())
	     {
	         case R.id.go_to_favs:
	             finish();
	             break;
	         case android.R.id.home:
	             finish();
	             break;
	     };
	     return true;
	 }
	 
	private void setHomeButton()
	 {
	     getActionBar().setHomeButtonEnabled(true);
	     getActionBar().setDisplayHomeAsUpEnabled(true);
	 }
}