package com.hsr.datalogger;

import java.util.ArrayList;
import java.util.List;

import com.hsr.datalogger.Homepage.TabListener;

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
import android.content.pm.ActivityInfo;
import android.content.Loader;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
	
	@Override
	protected void onResume() {
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public static class FeedItem {
		
		public final static int PUBLIC = 10;
		public final static int PRIVATE = 11;
		public final static int NONE = 12;
		
		public final static int VIEW = 0;
		public final static int FULL = 1;
		
		private final String feedName;
		private final String feedID;
		private final String feedDataCount;
		private final int feedOwnership;
		private final int feedPermissionLevel;
				
		public FeedItem(Helper helper, String ID) {
			String[] info = helper.getFeedListItem(ID);
			feedID = ID;
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

		@Override
		public String toString() {
			return feedName;
		}
		
		public String getFeedName(){
			return feedName;
		}
		
		public String getFeedID(){
			return feedID;
		}
		
		public String getDataCount(){
			return feedDataCount;
		}
		
		public int getFeedOwnerShip(){
			return feedOwnership;
		}
		
		public int getFeedPermissionLevel(){
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
			if(feeds == null) return null;

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
            forceLoad();
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

            // At this point we can release the resources associated if needed.
            if (mList != null) {
                mList = null;
            }
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
			TextView feedID = (TextView) view.findViewById(R.id.list_feed_id);
			TextView feedDataCount = (TextView) view.findViewById(R.id.list_feed_data_count);
			TextView feedOwnership = (TextView) view.findViewById(R.id.list_feed_ownership);
			ImageView feedPermissionLevel = (ImageView) view.findViewById(R.id.list_feed_permission);
			
			feedID.setText(item.getFeedID());
			feedName.setText(item.getFeedName());
			feedDataCount.setText(item.getDataCount());
			
			if(item.getFeedOwnerShip()==FeedItem.PUBLIC){
				feedOwnership.setText("Public");
			} else if (item.getFeedOwnerShip()==FeedItem.PRIVATE){
				feedOwnership.setText("Private");
			} else {
				feedOwnership.setText("None");
			}

			if(item.getFeedPermissionLevel()==FeedItem.VIEW){
				feedPermissionLevel.setImageResource(R.drawable.feed_pre_view);
			} else if (item.getFeedPermissionLevel()==FeedItem.FULL){
				feedPermissionLevel.setImageResource(R.drawable.feed_pre_all);
			} else {
				feedPermissionLevel.setImageResource(R.drawable.feed_pre_error);
			}
			
			return view;
		}
	}
	
	public static class FLFragment extends ListFragment implements OnQueryTextListener, LoaderManager.LoaderCallbacks<List<FeedItem>> {
		
		private static final int ADD_FEED = 1;
		private static final int SEARCH_LIST = 2;
		
		FeedListAdapter mAdapter;
		String mCurFilter;
		Helper helper;
		Context context;
				
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			context = getActivity().getApplicationContext();
			helper = new Helper(context);
			
			setEmptyText("No Feed");
			setHasOptionsMenu(true);
			
			mAdapter = new FeedListAdapter(getActivity());
			setListAdapter(mAdapter);
			setListShown(false);
			//helper.initFeedListLoader(getLoaderManager(), this);
			//getLoaderManager().initLoader(0, null, this);
			
			getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					DialogFragment editDeletedialog = EditDeleteFeedDialog.newInstance(R.string.edit_delete_dialog_title, getActivity().getApplicationContext(), helper, view);
					editDeletedialog.show(getFragmentManager(), "dialog");
					return false;
				}
			});
		}
		@Override
		public void onResume() {
			super.onResume();
			helper.initFeedListLoader(getLoaderManager(), this);
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
			TextView mid = (TextView) v.findViewById(R.id.list_feed_id);
			TextView mname = (TextView) v.findViewById(R.id.list_feed_name);
			helper.clickOneFeed(mid.getText().toString(), mname.getText().toString());
			helper.reloadFeedList();
			getActivity().getActionBar().setSelectedNavigationItem(Homepage.FEED_PAGE);
		}
		
		
		@Override
		public Loader<List<FeedItem>> onCreateLoader(int id, Bundle args) {
			return new FeedListLoader(getActivity(), helper);
		}

		@Override
		public void onLoadFinished(Loader<List<FeedItem>> loader, List<FeedItem> data) {
			ActionBar bar = getActivity().getActionBar();
			if(data==null||data.size()==0){
				bar.removeTabAt(2);
				bar.removeTabAt(1);
			} else {
				if(bar.getTabCount()==2){
			        bar.addTab(bar.newTab().setText("Feed Data").setTabListener(new TabListener<FeedData.FDFragment>(getActivity(), FeedData.FDFragment.class, helper, "FeedData")));
				} else if(bar.getTabCount()==1){
			        bar.addTab(bar.newTab().setText("Feed Page").setTabListener(new TabListener<FeedPage.FPFragment>(getActivity(), FeedPage.FPFragment.class, helper, "FeedPage")));
			        bar.addTab(bar.newTab().setText("Feed Data").setTabListener(new TabListener<FeedData.FDFragment>(getActivity(), FeedData.FDFragment.class, helper, "FeedData")));
				}
			}
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

	public static class EditDeleteFeedDialog extends DialogFragment {
		private static Context mContext;
		private static Helper helper;
		private static View view;
		
		public static EditDeleteFeedDialog newInstance(int title, Context context, Helper h, View v){
			EditDeleteFeedDialog frag = new EditDeleteFeedDialog();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			mContext = context;
			helper = h;
			view = v;
			return frag;
		}
		
		public static View getEditDeleteFeedView(){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View editDeleteFeeddailog = inflater.inflate(R.layout.edit_delete_feed_dialog, null);
			return editDeleteFeeddailog;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int title = getArguments().getInt("title");
			final View mDialog = getEditDeleteFeedView();
			final EditText newTitle = (EditText) mDialog.findViewById(R.id.edit_feed_new_name);
			final Switch newOwnership = (Switch) mDialog.findViewById(R.id.edit_feed_new_status);
			
			final TextView currentID = (TextView) view.findViewById(R.id.list_feed_id);
			final ImageView permission = (ImageView) view.findViewById(R.id.list_feed_permission);
			final String prem = permission.getId()==R.drawable.feed_pre_view?"View":"Full";

			final Button updateLocation = (Button) mDialog.findViewById(R.id.edit_feed_update_loc);
			updateLocation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(helper.updateLocation(currentID.getText().toString())){
						Toast.makeText(mContext, "Location is updated", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(mContext, "Fail to update location", Toast.LENGTH_LONG).show();
					}
				}
			});
			
			newTitle.setText(helper.getFeedListItem(currentID.getText().toString())[0]);
			
			if(prem.equals("View")){
				newOwnership.setEnabled(false);
				newTitle.setEnabled(false);
			}
			
			final Button delete = (Button) mDialog.findViewById(R.id.delete_feed_delete);
			delete.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog confirm = new AlertDialog.Builder(getActivity())
					   .setMessage(R.string.delete_feed_confirm_title)
					   .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String id = currentID.getText().toString();
								boolean local = false;
								if(prem.equals("View")){
									local = true;
								}
								if(helper.feedDelete(id, local)){
									Toast.makeText(mContext, "You successfully delete the feed", Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(mContext, "Error from pachube server, fail to delete on server side", Toast.LENGTH_LONG).show();
								}
								helper.reloadFeedList();
							}
					   	})
					   .setNegativeButton(R.string.dialog_cancel, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
					   }).create();
					getDialog().cancel();
					confirm.show();
				}
			});
			return new AlertDialog.Builder(getActivity())
								  .setIcon(android.R.drawable.ic_menu_edit)
								  .setView(mDialog)
								  .setTitle(title)
								  .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											String id = currentID.getText().toString();
											String nTitle = newTitle.getText().toString();
											boolean titleOnly = true;
											String nOwn = null;
											if(newOwnership.isEnabled()){
												titleOnly = false;
												nOwn = newOwnership.isChecked()?"Private":"Public";
											}
											if(helper.feedEdit(id, nTitle, titleOnly, nOwn)){
												Toast.makeText(mContext, "You successfully edit the feed", Toast.LENGTH_LONG).show();
											} else {
												Toast.makeText(mContext, "Error from pachube server, fail to edit on server side", Toast.LENGTH_LONG).show();
											}
											helper.reloadFeedList();
										}
								  })
								  .setNegativeButton(R.string.dialog_cancel, new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel();
										}
								  })
								  .create();
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
			
			if(helper.notGuest()){
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
			} else {
				newfeedtitle.setEnabled(false);
				newfeedtype.setEnabled(false);
				newfeedOwnership.setEnabled(false);
			}
			
			
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
									helper.reloadFeedList();
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
