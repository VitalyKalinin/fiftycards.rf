package shenhengbin.practice;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.GridView;
import android.util.Log;

public class DeckAdapter extends BaseAdapter implements OnClickListener {
    private List<Deck> list;
    View mRoot;
    LayoutInflater inflater;
    GridView grid;
    
    class DeckViewHolder 
    {
        TextView deckName;
    	ImageView img;
    }
    
    ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

    public DeckAdapter(Context context, List<Deck> list) {
        this.list = list;
		inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mRoot=inflater.inflate(R.layout.deck_adapter, null);
        grid = (GridView) mRoot.findViewById(R.id.gridview);
        
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .memoryCacheSize(41943040)
        .discCacheSize(1048576000)
        .threadPoolSize(16)
        .build();		        
        imageLoader.init(config);

    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
        	convertView = inflater.inflate(R.layout.deck_adapter, null);
        	DeckViewHolder viewHolder = new DeckViewHolder();
            viewHolder.deckName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        }
        DeckViewHolder holder = (DeckViewHolder) convertView.getTag();
    	Deck entry = list.get(position);
        holder.deckName.setText(entry.name);
    	//holder.img.setImageResource(R.drawable.child01);
		imageLoader.displayImage("http://fitcards.ru/preview/images_golovolomki_so_slovami_869x1172_200.jpg", holder.img, options);
        return convertView;
    }

    @Override
    public void onClick(View view) 
    {
    }
}
