package com.hsr.datalogger;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Loader;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FeedPage extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("pachube101", "FeedPage: onCreate");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("pachube101", "FeedPage: onResume");
	}
	@Override
	protected void onPause() {
    	super.onPause();
		Log.d("pachube101", "FeedPage: onPause");
	};
	@Override
	protected void onDestroy() {
    	super.onDestroy();
		Log.d("pachube101", "FeedPage: onDestroy");
	};
	
	public static class DataItem {
		
		private String dataName;
		private String tags;
		private boolean checked;
		
		public DataItem(Helper helper, String dataName) {
			String[] info = helper.getDataListItem(dataName);
			tags = info[0];
			checked = (info[1]==null||info[1].equals("0"))?false:true;
			this.dataName = dataName;
		}

		@Override
		public String toString() {
			return dataName;
		}
		
		public String getDataName(){
			return dataName;
		}
		
		public String getTags(){
			return tags;
		}
		
		public boolean getChecked(){
			return checked;
		}
	}
	
	public static class DataListLoader extends AsyncTaskLoader<List<DataItem>>{

		private Helper helper;
		List<DataItem> mList;
		
		public DataListLoader(Context context, Helper h) {
			super(context);
			helper = h;
		}

		@Override
		public List<DataItem> loadInBackground() {
			Log.d("pachube101", "DataListLoader: loadInBackground");
			List<String> datas = helper.getDataList();
			if(datas == null) return null;
			// TODO Testing off (load data list)
			// List<DataItem> entries = new ArrayList<DataItem>(0);
			List<DataItem> entries = new ArrayList<DataItem>(datas.size());
			for(int i=0; i<datas.size(); i++){
				DataItem item = new DataItem(helper, datas.get(i));
				entries.add(item);
			}
			return entries;
		}
		
		@Override
		public void deliverResult(List<DataItem> data) {
			Log.d("pachube101", "DataListLoader: deliverResult");
            mList = data;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately deliver its results.
                super.deliverResult(data);
            }
		}
		
		@Override
		protected void onStartLoading() {
			Log.d("pachube101", "DataListLoader: onStartLoading");
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
	
	public static class DataListAdapter extends ArrayAdapter<DataItem>{

		private final LayoutInflater mInflater;
		private Helper helper;

		public DataListAdapter(Context context, Helper h) {
			super(context, android.R.layout.simple_list_item_2);
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			helper = h;
		}
		
		public void setData(List<DataItem> data){
			clear();
			if(data!=null) addAll(data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = null;
			if(convertView == null){
				view = mInflater.inflate(R.layout.list_data_item, parent, false);
			} else {
				view = convertView;
			}

			final DataItem item = getItem(position);
			
			TextView dataName = (TextView) view.findViewById(R.id.data_item_name);
			TextView tags = (TextView) view.findViewById(R.id.data_item_tags);
			
			dataName.setText(item.getDataName());
			tags.setText(item.getTags());
			
			CheckBox check = (CheckBox) view.findViewById(R.id.data_item_check);
			check.setChecked(item.getChecked());
			
//			check.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Log.d("pachube debug", "click");
//					helper.checkData(item.getDataName(), item.getChecked()?false:true);
//				}
//			});
			
			check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					helper.checkData(item.getDataName(), isChecked);
				}
			});
			
			return view;
		}
	}
	
	public static class FPFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<DataItem>>{
		
		private static final int ADD_DATASTREAM = 1;
		private static final int SHARE_FEED = 2;
		private static final int UPDATE_FEED = 3;
		private static final int FEED_INFO = 4;
		
		Context context;
		Helper helper;
		DataListAdapter mAdapter;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			Log.d("pachube101", "FPFragment: onActivityCreated");
			context = getActivity().getApplicationContext();
			helper = new Helper(context);
			
			setEmptyText("No Data Stream");
			setHasOptionsMenu(true);
			
			mAdapter = new DataListAdapter(context, helper);
			setListAdapter(mAdapter);
			
			setListShown(false);

			helper.initFeedPageLoader(getLoaderManager(), this);
			//getLoaderManager().initLoader(0, null, this);

			getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					DialogFragment editDeletedialog = EditDeleteDataDialog.newInstance(R.string.edit_data_dialog_title, getActivity().getApplicationContext(), helper, view);
					editDeletedialog.show(getFragmentManager(), "dialog");
					return false;
				}
			});
		}
		
		@Override
		public void onResume() {
			super.onResume();
			Log.d("pachube101", "FPFragment: onResume");
		}
		
		@Override
		public void onPause() {
			super.onPause();
			Log.d("pachube101", "FPFragment: onPause");
		}
		
		@Override
		public void onDestroy() {
			super.onDestroy();
			Log.d("pachube101", "FPFragment: onDestroy");
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			menu.add(Menu.NONE, ADD_DATASTREAM , Menu.NONE, "Add Datastream")
				.setIcon(android.R.drawable.ic_menu_add)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
			menu.add(Menu.NONE, SHARE_FEED, Menu.NONE, "Share")
				.setIcon(android.R.drawable.ic_menu_share)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			menu.add(Menu.NONE, UPDATE_FEED, Menu.NONE, "Update")
				.setIcon(android.R.drawable.ic_menu_upload)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			
			menu.add(Menu.NONE, FEED_INFO, Menu.NONE, "Info")
				.setIcon(android.R.drawable.ic_menu_info_details)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			
			// check whether user has permission to modify this feed
			// TODO Testing off (switch off functions with paH)
			if(helper.notFullLevel()){
				Toast.makeText(context, "You have no permission to control this feed", Toast.LENGTH_LONG).show();
				return super.onOptionsItemSelected(item);
			}
			
			switch(item.getItemId()){
				case ADD_DATASTREAM:
					DialogFragment addDatastreamDialog = AddDatastreamDialog.newInstance(R.string.add_datastream, context, helper);
					addDatastreamDialog.show(getFragmentManager(), "dialog");
					return true;
				case SHARE_FEED:
					DialogFragment shareFeedDialog = ShareFeedDialog.newInstance(R.string.share_feed_title, context, helper);
					shareFeedDialog.show(getFragmentManager(), "dialog");
					return true;
				case UPDATE_FEED:
					DialogFragment updateFeedDialog = UpdateFeedDialog.newInstance(R.string.update_feed, context, helper);
					updateFeedDialog.show(getFragmentManager(), "dialog");
					return true;
				case FEED_INFO:
					DialogFragment feedInfoDialog = FeedInfoDialog.newInstance(R.string.feed_info_title, context, helper);
					feedInfoDialog.show(getFragmentManager(), "dialog");
				default:
					return super.onOptionsItemSelected(item);
			}
		}
		
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			TextView dataName = (TextView) v.findViewById(R.id.data_item_name);
			helper.clickOneData(dataName.getText().toString());
			getActivity().getActionBar().setSelectedNavigationItem(Homepage.FEED_DATA);
			Log.d("pachube101", "FPFragment: onListItemClick (click one data)");
		}

		
		@Override
		public Loader<List<DataItem>> onCreateLoader(int id, Bundle args) {
			Log.d("pachube101", "FPFragment: onCreateLoader");
			return new DataListLoader(getActivity(), helper);
		}

		@Override
		public void onLoadFinished(Loader<List<DataItem>> loader, List<DataItem> data) {
			Log.d("pachube101", "FPFragment: onLoadFinished");
			mAdapter.setData(data);
			if(isResumed()){
				setListShown(true);
			} else {
				setListShownNoAnimation(true);
			}
		}

		@Override
		public void onLoaderReset(Loader<List<DataItem>> arg0) {
			mAdapter.setData(null);
		}

	}
	
	public static class FeedInfoDialog extends DialogFragment{

		private static Context mContext;
		private static Helper helper;

		public static DialogFragment newInstance(int title, Context context, Helper h) {
			FeedInfoDialog frag = new FeedInfoDialog();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			
			mContext = context;
			helper = h;
			
			return frag;
		}
		
		public static View getFeedInfoView(){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View feedInfodialog = inflater.inflate(R.layout.feed_info_dialog, null);
			return feedInfodialog;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int title = getArguments().getInt("title");
			final View mDialog = getFeedInfoView();

			// title, id, owned(Public, Private, None), access(View, Full), location("lon, lat, alt"), datacount
			String[] info = helper.getFeedInfo();
			
			TextView Ftitle = (TextView) mDialog.findViewById(R.id.feed_info_feedTitle);
			TextView Fid = (TextView) mDialog.findViewById(R.id.feed_info_feedID);
			TextView Fown = (TextView) mDialog.findViewById(R.id.feed_info_owned);
			TextView Faccess = (TextView) mDialog.findViewById(R.id.feed_info_premiLevel);
			TextView FdataC = (TextView) mDialog.findViewById(R.id.feed_info_dataCount);
			TextView Flocation = (TextView) mDialog.findViewById(R.id.feed_info_location);
			
			Ftitle.setText(info[0]);
			Fid.setText(info[1]);
			Fown.setText(info[2]);
			Faccess.setText(info[3]);
			Flocation.setText(info[4]);
			FdataC.setText(info[5]);
			
			return new AlertDialog.Builder(getActivity())
						.setIcon(android.R.drawable.ic_menu_info_details)
						.setTitle(title)
						.setView(mDialog)
						.setNeutralButton(R.string.dialog_confirm, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						})
						.create();
		}
	}
	
	public static class AddDatastreamDialog extends DialogFragment {

		private static Context mContext;
		private static Helper helper;

		public static AddDatastreamDialog newInstance(int title, Context context, Helper h) {
			AddDatastreamDialog frag = new AddDatastreamDialog();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			
			mContext = context;
			helper = h;
			
			return frag;
		}
		
		public static View getAddDatastreamView(){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View addFeeddialog = inflater.inflate(R.layout.add_datastream_dialog, null);
			return addFeeddialog;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			int title = getArguments().getInt("title");
			final View mDialog = getAddDatastreamView();
			
			final EditText dataName = (EditText)mDialog.findViewById(R.id.add_datastream_new);

			final Spinner sp = (Spinner) mDialog.findViewById(R.id.add_datastream_sensor_select);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, helper.setSensorForDevice());
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp.setAdapter(adapter);
						
			return new AlertDialog.Builder(getActivity())
					   .setIcon(android.R.drawable.ic_menu_add)
					   .setTitle(title)
					   .setView(mDialog)
					   .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
						
							public void onClick(DialogInterface dialog, int which) {
								if(dataName.length() > 0){
									
									String name = dataName.getText().toString();
									String sensor = sp.getSelectedItem().toString();
									int sensorID = sp.getSelectedItemPosition();
									
									if(helper.dataCreate(name, sensor, sensorID)){
										// reload the data list
										Log.d("pachube101", "AddDatastreamDialog: onClick (add one data)");
										getActivity().getActionBar().setSelectedNavigationItem(Homepage.FEED_PAGE);
									} else {
										Toast.makeText(mContext, "Fail to create new data", Toast.LENGTH_SHORT).show();
									}
									
								} else {
									Toast.makeText(mContext, "Please enter a name for datastream", Toast.LENGTH_LONG).show();
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
	
	public static class ShareFeedDialog extends DialogFragment {

		private static Context mContext;
		private static Helper helper;
		
		public static ShareFeedDialog newInstance(int title, Context context, Helper h) {
			ShareFeedDialog frag = new ShareFeedDialog();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			
			mContext = context;
			helper = h;
			
			return frag;
		}
		
		public static View getShareFeedView(){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View shareFeedDialog = inflater.inflate(R.layout.share_feed_dialog, null);
			return shareFeedDialog;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			int title = getArguments().getInt("title");
			final View mDialog = getShareFeedView();
			
			final EditText emailAddress = (EditText)mDialog.findViewById(R.id.share_friend_email);
			final RadioGroup level = (RadioGroup) mDialog.findViewById(R.id.share_permission);
			final RadioButton selected = (RadioButton) mDialog.findViewById(level.getCheckedRadioButtonId());
			
			final Context forDialog = getActivity();
			
			return new AlertDialog.Builder(forDialog)
					   .setIcon(android.R.drawable.ic_menu_share)
					   .setTitle(title)
					   .setView(mDialog)
					   .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
						
							public void onClick(DialogInterface dialog, int which) {
								
								if(helper.sendEmail(emailAddress.getText().toString(), selected.getText().toString(), forDialog)){
									Toast.makeText(mContext, "Email is sent successfully", Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(mContext, "Fail to create permission, or there are no email apps", Toast.LENGTH_LONG).show();
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

	public static class UpdateFeedDialog extends DialogFragment {

		private static Context mContext;
		private static Helper helper;

		public static DialogFragment newInstance(int title, Context context, Helper h) {
			UpdateFeedDialog frag = new UpdateFeedDialog();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			
			mContext = context;
			helper = h;

			return frag;
		}
		
		public static View getUpdateFeedView(){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View updateFeedDialog = inflater.inflate(R.layout.update_feed_dialog, null);
			return updateFeedDialog;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Context forDialog = getActivity();

			int title = getArguments().getInt("title");
			final View mDialog = getUpdateFeedView();
			
			TextView selected = (TextView) mDialog.findViewById(R.id.update_selected_num);
			// TODO Testing off (switch off functions with paH)
			int num = helper.getSelectedDataNum();
			if(num<=0){
				return new AlertDialog.Builder(forDialog)
									  .setMessage("You did not select any data")
									  .setNeutralButton(R.string.dialog_cancel, new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel();
										}
									  }).create();
			} else {
				selected.setText(String.valueOf(num));
			}
			
			final EditText interval = (EditText)mDialog.findViewById(R.id.update_feed_interval);
			final EditText total = (EditText) mDialog.findViewById(R.id.update_feed_totaltime);

			return new AlertDialog.Builder(forDialog)
					   .setIcon(android.R.drawable.ic_menu_upload)
					   .setTitle(title)
					   .setView(mDialog)
					   .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
						
							public void onClick(DialogInterface dialog, int which) {
								int inter = Integer.parseInt(interval.getText().toString());
								int tota = Integer.parseInt(total.getText().toString());
								helper.startBackgroundUpdate(inter, tota);
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

	public static class EditDeleteDataDialog extends DialogFragment {

		private static Context mContext;
		private static Helper helper;
		private static View view;

		public static EditDeleteDataDialog newInstance(int title, Context context, Helper h, View v){
			EditDeleteDataDialog frag = new EditDeleteDataDialog();
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
			View editDeleteFeeddailog = inflater.inflate(R.layout.edit_delete_data_dialog, null);
			return editDeleteFeeddailog;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			int title = getArguments().getInt("title");
			final View mDialog = getEditDeleteFeedView();
			
			final TextView dataName = (TextView) view.findViewById(R.id.data_item_name);
			
			final EditText newTags = (EditText) mDialog.findViewById(R.id.edit_data_new_tags);
			TextView oldTags = (TextView) view.findViewById(R.id.data_item_tags);
			newTags.setText(oldTags.getText());
			
			Button deleteData = (Button) mDialog.findViewById(R.id.delete_data_delete);
			deleteData.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog confirm = new AlertDialog.Builder(getActivity())
					   .setMessage(R.string.delete_feed_confirm_title)
					   .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								String dataID = dataName.getText().toString();
								
								if(helper.dataDelete(dataID)){
									Toast.makeText(mContext, "You successfully delete the data", Toast.LENGTH_LONG).show();
									// SOS reload the list (delete data)
									helper.reloadAllList();
									getActivity().getActionBar().setSelectedNavigationItem(Homepage.FEED_PAGE);
								} else {
									Toast.makeText(mContext, "Fail to delete the data, error occurs", Toast.LENGTH_LONG).show();
								}
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

						String dataID = dataName.getText().toString();
						
						String nTags = newTags.getText().toString();

						if(helper.dataEdit(dataID, nTags)){
							Toast.makeText(mContext, "You successfully edit the data", Toast.LENGTH_LONG).show();
							// SOS reload the list (edit data)
							helper.reloadAllList();
							getActivity().getActionBar().setSelectedNavigationItem(Homepage.FEED_PAGE);
						} else {
							Toast.makeText(mContext, "Fail to edit, error with pachube", Toast.LENGTH_LONG).show();
						}
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
}
