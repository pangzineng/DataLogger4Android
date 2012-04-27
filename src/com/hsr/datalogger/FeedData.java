package com.hsr.datalogger;

import java.net.HttpURLConnection;

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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FeedData extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public static class FDFragment extends Fragment {
		
		private static final int EDIT_DIAGRAM = 1;
		private static final int SHARE_DIAGRAM = 2;

		Context context;
		Helper helper;

		private ImageView diagram;

		HttpURLConnection urlc = null;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setHasOptionsMenu(true);
			context = getActivity().getApplicationContext();
			helper = new Helper(context);
			
			View view = getView();
			
			diagram = (ImageView) view.findViewById(R.id.data_diagram);

			TextView current = (TextView) view.findViewById(R.id.diagram_stat_current);
			TextView unit = (TextView) view.findViewById(R.id.diagram_stat_unit);
			TextView max = (TextView) view.findViewById(R.id.diagram_stat_max);
			TextView min = (TextView) view.findViewById(R.id.diagram_stat_min);
			
			String[] stat = helper.getDiagramStat();
			
			current.setText(stat[0]);
			unit.setText(stat[1]);
			max.setText(stat[2]);
			min.setText(stat[3]);
			
			helper.tempStore(diagram);
			helper.reDraw();
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			return inflater.inflate(R.layout.feeddata, null);
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
					DialogFragment editDiagramDialog = EditDiagramDialog.newInstance(R.string.edit_diagram_title, context, helper);
					editDiagramDialog.show(getFragmentManager(), "dialog");
					return true;
				case SHARE_DIAGRAM:
					DialogFragment shareDiagramDialog = ShareDiagramDialog.newInstance(R.string.share_diagram_title, context, helper, diagram);
					shareDiagramDialog.show(getFragmentManager(), "dialog");
					return true;
				default:
					return super.onOptionsItemSelected(item);
			}
		}
	}

	public static class EditDiagramDialog extends DialogFragment {

		private static Context mContext;
		private static Helper helper;

		public static DialogFragment newInstance(int addDatastream, Context context, Helper h) {
			EditDiagramDialog frag = new EditDiagramDialog();

			Bundle args = new Bundle();
			args.putInt("title", addDatastream);
			frag.setArguments(args);

			mContext = context;
			helper = h;

			return frag;
		}
		
		public static View getEditDiagramView(){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View editFeedDialog = inflater.inflate(R.layout.edit_diagram_dialog, null);
			return editFeedDialog;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			int title = getArguments().getInt("title");
			final View mDialog = getEditDiagramView();
			
			TextView current = (TextView) mDialog.findViewById(R.id.diagram_current_duration);
			current.setText(helper.getDiagramDuration());
			
			final EditText newDuraion = (EditText)mDialog.findViewById(R.id.diagram_new_duration);

			final Spinner sp = (Spinner) mDialog.findViewById(R.id.diagram_new_duration_unit);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.time_unit_list, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp.setAdapter(adapter);
						
			return new AlertDialog.Builder(getActivity())
					   .setIcon(android.R.drawable.ic_menu_edit)
					   .setTitle(title)
					   .setView(mDialog)
					   .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
						
							public void onClick(DialogInterface dialog, int which) {
								String dura = newDuraion.getText().toString();
								String unit = sp.getSelectedItem().toString();
								
								if((sp.getSelectedItemPosition() == sp.getCount()-1)&&(Integer.parseInt(dura) > 730)){
									Toast.makeText(mContext, "Donnot set > 23months, Y U NO LISTEN !?", Toast.LENGTH_LONG).show();
									dialog.cancel();
								} else if((sp.getSelectedItemPosition() == sp.getCount()-2)&&(Integer.parseInt(dura) > 23)){
									Toast.makeText(mContext, "Donnot set > 730days, Y U NO LISTEN !?", Toast.LENGTH_LONG).show();
									dialog.cancel();
								} else if(dura.length()==0){
									Toast.makeText(mContext, "Please enter a number", Toast.LENGTH_LONG).show();
									dialog.cancel();
								} else{			
									Toast.makeText(mContext, "You just set the duration to " + dura + unit, Toast.LENGTH_LONG).show();
									helper.setDiagramDuration(dura+unit);
									helper.reDraw();
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
	
	public static class ShareDiagramDialog extends DialogFragment {

		private static Context mContext;
		private static Helper helper;
		private static ImageView diagram;

		
		public static DialogFragment newInstance(int shareFeedTitle, Context context, Helper h, ImageView d) {
			
			ShareDiagramDialog frag = new ShareDiagramDialog();
			Bundle args = new Bundle();
			args.putInt("title", shareFeedTitle);
			frag.setArguments(args);

			mContext = context;
			helper = h;
			diagram = d;
			
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
								
								if(helper.sendDiagramEmail(emailAddress.getText().toString(), description.getText().toString(), forDialog, diagram)){
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
