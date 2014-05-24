package ie.jobfinder.app;

import helpers.LogHelper;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DrawerItemAdapter extends ArrayAdapter<String> {
	private static final String TAG = "DrawerItemAdapter";
	private String[] mItems;
	
	public DrawerItemAdapter(Context context, int resource, String[] drawerList) {
		super(context, resource, drawerList);
		mItems = drawerList;
	}

	public class ViewHolder{
		public TextView hText;  
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LogHelper.logDebug(TAG, "getView()");
		View v = convertView;
		final ViewHolder holder;
		if(v == null) 
		{  
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
			v = vi.inflate(R.layout.drawer_list_item, null);
			holder = new ViewHolder();
			holder.hText = (TextView) v.findViewById(R.id.txt_drawer_list);
			v.setTag(holder);
		}  
		else{
			holder =(ViewHolder) v.getTag();
		}
		
		//Set the text
		String text = this.mItems[position];
		holder.hText.setText(text);
		switch(position) {
		case(0):{ //Saved Jobs
			holder.hText.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_action_important), null, null, null);
			break;
		}
		case(1): { //About
			holder.hText.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_action_about), null, null, null);
			break;
		}
		case(2):{ //Rate
			holder.hText.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_action_good), null, null, null);
			break;
		}
		case(3):{ //Help
			holder.hText.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_action_help), null, null, null);
			break;
		}
		}
		return v;
	}
}
