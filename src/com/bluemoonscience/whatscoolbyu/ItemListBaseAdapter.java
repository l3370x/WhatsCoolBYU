package com.bluemoonscience.whatscoolbyu;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
//import android.widget.ImageView;
import android.widget.TextView;

public class ItemListBaseAdapter extends BaseAdapter {
	private static ArrayList<Entry> itemDetailsrrayList;

//	private Integer[] imgid = { R.drawable.p1, R.drawable.bb2, R.drawable.p2, R.drawable.bb5,
//			R.drawable.bb6, R.drawable.d1 };

	private LayoutInflater l_Inflater;

	public ItemListBaseAdapter(Context context, ArrayList<Entry> results) {
		itemDetailsrrayList = results;
		l_Inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return itemDetailsrrayList.size();
	}

	public Object getItem(int position) {
		return itemDetailsrrayList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.list_entry, null);
			holder = new ViewHolder();
			holder.txt_itemTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.txt_itemTimestamp = (TextView) convertView.findViewById(R.id.tvEntry);
			//holder.txt_itemDescription = (TextView) convertView.findViewById(R.id.itemDescription);
			//holder.txt_itemPrice = (TextView) convertView.findViewById(R.id.price);
			//holder.itemImage = (ImageView) convertView.findViewById(R.id.photo);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txt_itemTitle.setText(itemDetailsrrayList.get(position).getTitle());
		holder.txt_itemTimestamp.setText(itemDetailsrrayList.get(position).getTimestamp());
		//holder.txt_itemPrice.setText(itemDetailsrrayList.get(position).getPrice());
		//holder.itemImage.setImageResource(imgid[itemDetailsrrayList.get(position).getImageNumber() - 1]);

		return convertView;
	}

	static class ViewHolder {
		TextView txt_itemTitle;
		TextView txt_itemTimestamp;
		
		//TextView txt_itemPrice;
		//ImageView itemImage;
	}
}
