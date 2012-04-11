package com.hsr.datalogger;

import com.hsr.datalogger.FeedPage.AddDatastreamDialog;
import com.hsr.datalogger.FeedPage.ShareFeedDialog;
import com.hsr.datalogger.FeedPage.UpdateFeedDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class FeedData extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feeddata);

	}
	
	public static class FDFragment extends Fragment {
		private static final int EDIT_DIAGRAM = 1;
		private static final int SHARE_DIAGRAM = 2;

		Context context;
		Helper helper;

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setHasOptionsMenu(true);
			context = getActivity().getApplicationContext();
			helper = new Helper(context);
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			menu.add(Menu.NONE, EDIT_DIAGRAM , Menu.NONE, "Edit Diagram")
				.setIcon(android.R.drawable.ic_menu_edit)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
			menu.add(Menu.NONE, SHARE_DIAGRAM, Menu.NONE, "Share Diagram")
				.setIcon(android.R.drawable.ic_menu_share)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch(item.getItemId()){
				case EDIT_DIAGRAM:
					DialogFragment editDiagramDialog = EditDiagramDialog.newInstance(R.string.add_datastream, context, helper);
					editDiagramDialog.show(getFragmentManager(), "dialog");
					return true;
				case SHARE_DIAGRAM:
					DialogFragment shareDiagramDialog = ShareDiagramDialog.newInstance(R.string.share_diagram_title, context, helper);
					shareDiagramDialog.show(getFragmentManager(), "dialog");
					return true;
				default:
					return super.onOptionsItemSelected(item);
			}
		}
	}

	public static class EditDiagramDialog extends DialogFragment {

		public static DialogFragment newInstance(int addDatastream, Context context, Helper helper) {
			EditDiagramDialog frag = new EditDiagramDialog();
			
			return frag;
		}
		
	}
	
	public static class ShareDiagramDialog extends DialogFragment {

		private static Context mContext;
		private static Helper helper;

		
		public static DialogFragment newInstance(int shareFeedTitle, Context context, Helper h) {
			
			ShareDiagramDialog frag = new ShareDiagramDialog();
			Bundle args = new Bundle();
			args.putInt("title", shareFeedTitle);
			frag.setArguments(args);

			mContext = context;
			helper = h;
			
			return frag;
		}
		
		public static View getShareDiagramView(){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View shareFeedDialog = inflater.inflate(R.layout.share_diagram_dialog, null);
			return shareFeedDialog;
		}
		
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			int title = getArguments().getInt("title");
			final View mDialog = getShareDiagramView();
			
			final EditText emailAddress = (EditText)mDialog.findViewById(R.id.share_friend_email);
			final EditText description = (EditText) mDialog.findViewById(R.id.share_diagram_des);
			
			final Context forDialog = getActivity();
			
			return new AlertDialog.Builder(forDialog)
					   .setIcon(android.R.drawable.ic_menu_share)
					   .setTitle(title)
					   .setView(mDialog)
					   .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
						
							public void onClick(DialogInterface dialog, int which) {
								
								if(helper.sendDiagramEmail(emailAddress.getText().toString(), description.getText().toString(), forDialog)){
									Toast.makeText(mContext, "Email is sent successfully", Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(mContext, "There are no email apps installed", Toast.LENGTH_LONG).show();
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
