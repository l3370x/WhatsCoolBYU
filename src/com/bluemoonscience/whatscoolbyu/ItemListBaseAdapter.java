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
			holder.txt_itemShortDesc = (TextView) convertView.findViewById(R.id.tvShortDesc);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txt_itemTitle.setText(itemDetailsrrayList.get(position).title);
		holder.txt_itemShortDesc.setText(itemDetailsrrayList.get(position).sDescription);

		return convertView;
	}

	static class ViewHolder {
		TextView txt_itemTitle;
		TextView txt_itemShortDesc;
		
		//TextView txt_itemPrice;
		//ImageView itemImage;
	}
}
