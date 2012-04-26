package com.hsr.datalogger;

import java.util.List;

import com.hsr.datalogger.FeedList.EditDeleteFeedDialog;
import com.hsr.datalogger.FeedList.FeedItem;
import com.hsr.datalogger.FeedPage.DataItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FeedPage extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedpage);
	}
	
	public static class DataItem {
		private String dataName = "";
		private String tags = "";
		
		
		public String getDataName(){
			return dataName;
		}
		
		public String getTags(){
			return tags;
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
			
			final DataItem item = getItem(position);
			
			View view = null;
			if(convertView == null){
				view = mInflater.inflate(R.layout.list_data_item, parent, false);
			} else {
				view = convertView;
			}
			
			CheckBox check = (CheckBox) view.findViewById(R.id.data_item_check);
			check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					helper.checkData(item.getDataName(), isChecked); 
				}
			});
			
			return super.getView(position, convertView, parent);
		}
	}
	
	public static class FPFragment extends ListFragment {
		
		private static final int ADD_DATASTREAM = 1;
		private static final int SHARE_FEED = 2;
		private static final int UPDATE_FEED = 3;
		
		Context context;
		Helper helper;
		DataListAdapter mAdapter;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			context = getActivity().getApplicationContext();
			helper = new Helper(context);

			setEmptyText("No Data Stream");
			setHasOptionsMenu(true);
			
			mAdapter = new DataListAdapter(context, helper);
			setListAdapter(mAdapter);
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
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			
			// check whether user has premission to modify this feed
			if(helper.notFullLevel()){
				Toast.makeText(context, "You have no premission to control this feed", Toast.LENGTH_LONG).show();
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
				default:
					return super.onOptionsItemSelected(item);
			}
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
									
									if(helper.dataCreate(name, sensor)){
										// reload the data list
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
									Toast.makeText(mContext, "Fail to create premission, or there are no email apps", Toast.LENGTH_LONG).show();
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
								helper.startUpdateData(inter, tota);
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
					AlertDialog confirm = new AlertDialog.Builder(mContext)
					   .setMessage(R.string.delete_feed_confirm_title)
					   .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								String dataID = dataName.getText().toString();
								
								// FIXME reload the list (delete data)
								if(helper.dataDelete(dataID)){
									Toast.makeText(mContext, "You successfully delete the data", Toast.LENGTH_LONG).show();
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
			
			return new AlertDialog.Builder(mContext)
			  .setIcon(android.R.drawable.ic_menu_edit)
			  .setView(mDialog)
			  .setTitle(title)
			  .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						String dataID = dataName.getText().toString();
						
						// FIXME reload the list (edit data)
						String nTags = newTags.getText().toString();

						if(helper.dataEdit(dataID, nTags)){
							Toast.makeText(mContext, "You successfully edit the data", Toast.LENGTH_LONG).show();
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
