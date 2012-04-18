package com.hsr.datalogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hsr.datalogger.FeedList.FeedItem;
import com.hsr.datalogger.database.DatabaseHelper;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Loader;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class FeedList extends Activity {
	
    int mStackLevel = 1;
    // Helper helper
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        FragmentManager fm = getFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
        	FLFragment list = new FLFragment();
            fm.beginTransaction().add(android.R.id.content, list).commit();
        }

	}
	
	// FIXME this one accept the resource from database and sort it to different attribute for the list to load
	// FIXME need another loader class to load the list from database and store individually into the FeedItem
	public static class FeedItem {
		
		public final static int PUBLIC = 10;
		public final static int PRIVATE = 11;
		public final static int NONE = 12;
		
		public final static int VIEW = 0;
		public final static int FULL = 1;
		
		private final String feedName;
		private final String feedDataCount;
		private final int feedOwnership;
		private final int feedPermissionLevel;
				
		public FeedItem(Helper helper, String feedID) {
			String[] info = helper.getFeedListItem(feedID);
			feedName = info[0];
			feedDataCount = info[1];
			if(info[2].compareTo("None")==0){
				feedOwnership = NONE;
			} else if(info[2].compareTo("Private")==0){
				feedOwnership = PRIVATE;
			} else {
				feedOwnership = PUBLIC;
			}
			
			if(info[3].compareTo("View")==0){
				feedPermissionLevel = VIEW;
			} else {
				feedPermissionLevel = FULL;
			}
		}

		public String getFeedName(){
			return feedName;
		}
		
		public String getDataCount(){
			return feedDataCount;
		}
		
		public int getFeedOwnerShip(){
			return feedOwnership;
		}
		
		public int getFeedPremissionLevel(){
			return feedPermissionLevel;
		}
	}
	
	public static class FeedListLoader extends AsyncTaskLoader<List<FeedItem>> {

		
		private Helper helper;
		List<FeedItem> mList;
		
		public FeedListLoader(Context context, Helper h) {
			super(context);
			helper = h;
		}

		@Override
		public List<FeedItem> loadInBackground() {
			List<String> feeds = helper.getFeedList(); 
			// SOS for testing only
			//List<FeedItem> entries = new ArrayList<FeedItem>(0);
			List<FeedItem> entries = new ArrayList<FeedItem>(feeds.size());
			for(int i=0; i<feeds.size(); i++){
				FeedItem item = new FeedItem(helper, feeds.get(i));
				entries.add(item);
			}
			return entries;
		}
		
		@Override
		public void deliverResult(List<FeedItem> data) {
			
            mList = data;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately deliver its results.
                super.deliverResult(data);
            }
		}

		@Override
		protected void onStartLoading() {
            if (mList != null) {
                // If we currently have a result available, deliver it immediately.
                deliverResult(mList);
            }

            // FIXME Start watching for changes in the data.
//            if (mPackageObserver == null) {
//                mPackageObserver = new PackageIntentReceiver(this);
//            }

            if (takeContentChanged() || mList == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
		}
		
		@Override
		protected void onStopLoading() {
			cancelLoad();
		}
		
		@Override
		protected void onReset() {
			super.onReset();
			
            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (mList != null) {
                mList = null;
            }

            // FIXME Stop monitoring for changes.
//            if (mPackageObserver != null) {
//                getContext().unregisterReceiver(mPackageObserver);
//                mPackageObserver = null;
//            }

		}
	}
	
	public static class FeedListAdapter extends ArrayAdapter<FeedItem>{
		
		private final LayoutInflater mInflater;
		
		public FeedListAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_2);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setData(List<FeedItem> data){
			clear();
			if(data!=null) addAll(data);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if(convertView == null){
				view = mInflater.inflate(R.layout.list_feed_item, parent, false);
			} else {
				view = convertView;
			}
			
			FeedItem item = getItem(position);
			
			TextView feedName = (TextView) view.findViewById(R.id.list_feed_name);
			TextView feedDataCount = (TextView) view.findViewById(R.id.list_feed_data_count);
			TextView feedOwnership = (TextView) view.findViewById(R.id.list_feed_ownership);
			ImageView feedPremissionLevel = (ImageView) view.findViewById(R.id.list_feed_premission);
			
			feedName.setText(item.getFeedName());
			feedDataCount.setText(item.getDataCount());
			
			if(item.getFeedOwnerShip()==FeedItem.PUBLIC){
				feedOwnership.setText("Public");
			} else if (item.getFeedOwnerShip()==FeedItem.PRIVATE){
				feedOwnership.setText("Private");
			} else {
				feedOwnership.setText("None");
			}

			if(item.getFeedPremissionLevel()==FeedItem.VIEW){
				feedPremissionLevel.setImageResource(R.drawable.feed_pre_view);
			} else if (item.getFeedPremissionLevel()==FeedItem.FULL){
				feedPremissionLevel.setImageResource(R.drawable.feed_pre_all);
			} else {
				feedPremissionLevel.setImageResource(R.drawable.feed_pre_error);
			}

			// SOS this should be done in ListFragment below
//			view.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// bring the tab to the next tab
//					Log.d("pang", "Click on feed list item");
//					
//				}
//			});
//			view.setOnLongClickListener(new View.OnLongClickListener() {
//				
//				@Override
//				public boolean onLongClick(View v) {
//					// bring out the edit_delete dialog
//					Log.d("pang", "Long press on feed list item");
//					return false;
//				}
//			});
			
			return view;
		}
	}
	
	public static class FLFragment extends ListFragment implements OnQueryTextListener, LoaderManager.LoaderCallbacks<List<FeedItem>> {
		
		private static final int ADD_FEED = 1;
		private static final int SEARCH_LIST = 2;
		
		FeedListAdapter mAdapter;
		String mCurFilter;
		Helper helper;
				
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			helper = new Helper(getActivity().getApplicationContext());
			
			setEmptyText("No Feed");
			setHasOptionsMenu(true);
			
			mAdapter = new FeedListAdapter(getActivity());
			setListAdapter(mAdapter);
			
			setListShown(false);
			
			getLoaderManager().initLoader(0, null, this);
			
			getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// FIXME do the list item long click here
					return false;
				}
			});
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
					DialogFragment addfeedDialog = AddFeedDialog.newInstance(R.string.add_feed_dialog_title, getActivity().getApplicationContext(), helper);
					addfeedDialog.show(getFragmentManager(), "dialog");
					return true;
				case SEARCH_LIST:
					
				default:
					return super.onOptionsItemSelected(item);
			}
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			mCurFilter = !TextUtils.isEmpty(newText)?newText:null;
			mAdapter.getFilter().filter(mCurFilter);
			return true;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			return true;
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			Log.d("pang", "Click on feed list item, listener in fragment");
			// FIXME do the list item click here
		}
		
		
		@Override
		public Loader<List<FeedItem>> onCreateLoader(int id, Bundle args) {
			return new FeedListLoader(getActivity(), helper);
		}

		@Override
		public void onLoadFinished(Loader<List<FeedItem>> loader,	List<FeedItem> data) {
			mAdapter.setData(data);
			if(isResumed()){
				setListShown(true);
			} else {
				setListShownNoAnimation(true);
			}
		}
		
		@Override
		public void onLoaderReset(Loader<List<FeedItem>> loader) {
			mAdapter.setData(null);
		}
	}

	public static class AddFeedDialog extends DialogFragment {

		private static Context mContext;
		private static Helper helper;
		
		public static AddFeedDialog newInstance(int title, Context context, Helper h) {
			AddFeedDialog frag = new AddFeedDialog();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			
			mContext = context;
			helper = h;
			
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
			final Switch newfeedOwnership = (Switch) mDialog.findViewById(R.id.add_feed_n_status);
			
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
								
								String title = newfeedtitle.getText().toString();
								String type = newfeedtype.isChecked()?"Custome":"Sensor";
								String ownership = newfeedOwnership.isChecked()?"Private":"Public";
								
								String importID = feedid.getText().toString();
								String importPermission  = feedkey.getText().toString();
								
								boolean result = false;
								if(feedid.length()<=0 && newfeedtitle.length()>0){
									// For create new feed
									if(helper.feedCreate(title, type, ownership)){
										Toast.makeText(mContext, "You just create a new feed", Toast.LENGTH_LONG).show();
										result = true;
									} else {
										Toast.makeText(mContext, "Fail to create new feed", Toast.LENGTH_SHORT).show();
									}
								} else if (feedid.length()>0 && newfeedtitle.length()<=0){
									// For import existed feed
									if(helper.feedImport(importID, importPermission)){
										Toast.makeText(mContext, "You just import a feed", Toast.LENGTH_LONG).show();
										result = true;
									} else {
										Toast.makeText(mContext, "Fail to import feed", Toast.LENGTH_SHORT).show();
									}
								} else {
									Toast.makeText(mContext, "Please enter feed id or new feed title", Toast.LENGTH_LONG).show();
								}
								
								if(result){
									getActivity().getActionBar().setSelectedNavigationItem(Homepage.FEED_PAGE);
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
