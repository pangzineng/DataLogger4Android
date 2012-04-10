package com.hsr.datalogger;

import java.util.List;

import com.hsr.datalogger.database.DatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Loader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Switch;
import android.widget.Toast;

public class FeedList extends Activity {
	
    int mStackLevel = 1;
    // Helper helper
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	public static class MFeedItem {
		private final String feedName;
		private final String feedType;
		private final String feedStatus;
		
		public MFeedItem(String username, int feedIndex) {
			feedName = DatabaseHelper.getFeedName(username, feedIndex);
			feedType = DatabaseHelper.getFeedType(username, feedIndex);
			feedStatus = DatabaseHelper.getFeedStatus(username, feedIndex);
			// feedName = Helper.getFeedList();
			// [TO BE ADDED] get from database component the list of feeds
		}
		
		public String getFeedName(){
			return feedName;
		}
		
		public String getFeedType(){
			return feedType;
		}
		
		public String getFeedStatus(){
			return feedStatus;
		}
	}
	
	public static class MFeedListAdapter extends ArrayAdapter<MFeedItem>{
		
		private final LayoutInflater mInflater;
		
		public MFeedListAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_2);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setData(List<MFeedItem> data){
			clear();
			if(data!=null) addAll(data);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			
			return view;
		}
	}
	
	public static class FLFragment extends ListFragment implements OnQueryTextListener, LoaderManager.LoaderCallbacks<List<MFeedItem>> {
		
		private static final int ADD_FEED = 1;
		private static final int SEARCH_LIST = 2;
		
		ArrayAdapter<String> mAdapter;
		String mCurFilter;
		
				
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setEmptyText("No Feed");
			setHasOptionsMenu(true);
			
			mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_2);
			setListAdapter(mAdapter);
			
			setListShown(false);
			
			getLoaderManager().initLoader(0, null, this);
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			menu.add(Menu.NONE, ADD_FEED , Menu.NONE, "Add Feed")
				.setIcon(android.R.drawable.ic_menu_add)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			
			SearchView search = new SearchView(getActivity());
			search.setOnQueryTextListener(this);
			menu.add(Menu.NONE, SEARCH_LIST, Menu.NONE, "Search")
				.setActionView(search)
				.setIcon(android.R.drawable.ic_menu_search)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch(item.getItemId()){
				case ADD_FEED:
					DialogFragment addfeedDialog = AddFeedDialog.newInstance(R.string.add_feed_dialog_title, getActivity().getApplicationContext());
					addfeedDialog.show(getFragmentManager(), "dialog");
					return true;
				case SEARCH_LIST:
					
				default:
					return super.onOptionsItemSelected(item);
			}
		}

		public boolean onQueryTextChange(String newText) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean onQueryTextSubmit(String query) {
			return true;
		}

		public Loader<List<MFeedItem>> onCreateLoader(int id, Bundle args) {
			// TODO Auto-generated method stub
			return null;
		}


		public void onLoadFinished(Loader<List<MFeedItem>> loader,	List<MFeedItem> data) {
			// TODO Auto-generated method stub
			
		}

		public void onLoaderReset(Loader<List<MFeedItem>> loader) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public static class AddFeedDialog extends DialogFragment {

		private static Context mContext;
		
		public static AddFeedDialog newInstance(int title, Context context) {
			AddFeedDialog frag = new AddFeedDialog();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			
			mContext = context;
			
			return frag;
		}
		
		public static View getAddFeedView(){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View addFeeddialog = inflater.inflate(R.layout.add_feed_dialog, null);
			return addFeeddialog;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			int title = getArguments().getInt("title");
			final View mDialog = getAddFeedView();
			
			final EditText feedid = (EditText)mDialog.findViewById(R.id.add_feed_e_id);
			final EditText feedkey = (EditText)mDialog.findViewById(R.id.add_feed_e_key);
			final EditText newfeedtitle = (EditText)mDialog.findViewById(R.id.add_feed_n_title);
			final Switch newfeedtype = (Switch) mDialog.findViewById(R.id.add_feed_n_type);
			final Switch newfeedstatus = (Switch) mDialog.findViewById(R.id.add_feed_n_status);
			
			feedid.addTextChangedListener(new TextWatcher() {
								
				public void afterTextChanged(Editable s) {
					newfeedtitle.setEnabled((s.length()<=0)?true:false);					
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				public void onTextChanged(CharSequence s, int start, int before, int count) {}
			});
			
			newfeedtitle.addTextChangedListener(new TextWatcher() {
				
				public void afterTextChanged(Editable s) {
					feedid.setEnabled((s.length()<=0)?true:false);
				}
				
				public void onTextChanged(CharSequence s, int start, int before, int count) {}
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			});
			
			return new AlertDialog.Builder(getActivity())
					   .setIcon(android.R.drawable.ic_menu_add)
					   .setTitle(title)
					   .setView(mDialog)
					   .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
						
							public void onClick(DialogInterface dialog, int which) {
								if(feedid.length()<=0 && newfeedtitle.length()>0){
									// Helper.createFeed(newfeedtitle.getText().toString(), newfeedtype.isChecked(), newfeedstatus.isChecked());
									// [TO BE ADDED] send the request to Pachube Component to create one feed
								} else if (feedid.length()>0 && newfeedtitle.length()<=0){
									// Helper.getFeedInfo(feedid.getText().toString(), feedkey.getText().toString());
									// [TO BE ADDED] or send request to get the existing feed info
								} else {
									Toast.makeText(mContext, "Please enter feed id or new feed title", Toast.LENGTH_LONG).show();
								}
							}
						})
					   .setNegativeButton(R.string.dialog_cancel, new OnClickListener() {
						
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();								
							}
						})
					   .create();
		}
	}
}
