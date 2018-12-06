package xzy.myrecoder.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import xzy.myrecoder.Model.RecoderItem;
import xzy.myrecoder.R;

public class RecoderAdapter extends BaseAdapter {

    private ArrayList<RecoderItem> items;
    private LayoutInflater layoutInflater;

    public RecoderAdapter(Context context, ArrayList<RecoderItem> items) {
        this.items = items;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.fragment_item, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.itemImage);
        TextView itemNameView = (TextView) view.findViewById(R.id.itemName);
        TextView itemSizeView = (TextView) view.findViewById(R.id.itemSize);
        TextView itemLengthView = (TextView) view.findViewById(R.id.itemLength);
        TextView itemDateView = (TextView) view.findViewById(R.id.itemDate);
        ImageView flagImage = view.findViewById(R.id.flagImage);
        RecoderItem item = items.get(position);

        boolean flag=item.getFlag();
        if(flag){
            flagImage.setImageResource(R.drawable.flag);
        }
        itemNameView.setText(item.getItemName());
        itemSizeView.setText(item.getItemSize());
        itemLengthView.setText(item.getItemLength());
        itemDateView.setText(item.getDate());
        //    Log.d("fortest",item.getDate()+"");
        return view;
    }
}
