package com.hsr.datalogger;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class FeedPage extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedpage);
	}
	
	public static class FPFragment extends ListFragment {
		
		private static final int ADD_DATASTREAM = 1;
		private static final int SHARE_FEED = 2;
		private static final int UPDATE_FEED = 3;
		
		Context context;
		Helper helper;
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setEmptyText("No Data Stream");
			setHasOptionsMenu(true);
			context = getActivity().getApplicationContext();
			helper = new Helper(context);
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
			int title = getArguments().getInt("title");
			final View mDialog = getUpdateFeedView();
			
			final EditText interval = (EditText)mDialog.findViewById(R.id.update_feed_interval);
			final EditText total = (EditText) mDialog.findViewById(R.id.update_feed_totaltime);
			
			final Context forDialog = getActivity();
			
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
}
