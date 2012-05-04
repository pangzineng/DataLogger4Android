package com.hsr.datalogger;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Homepage extends Activity {
	
	public static final int FEED_LIST = 0;
	public static final int FEED_PAGE = 1;
	public static final int FEED_DATA = 2;
	
	private Helper helper;
	private static ActionBar bar;
	
	public static ActionBar barInstance() {
		return bar;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        helper = new Helper(this);
        bar = getActionBar();

        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        
        bar.addTab(bar.newTab().setText("Feed List").setTabListener(new TabListener<FeedList.FLFragment>(this, FeedList.FLFragment.class, helper, "FeedList")));
        bar.addTab(bar.newTab().setText("Feed Page").setTabListener(new TabListener<FeedPage.FPFragment>(this, FeedPage.FPFragment.class, helper, "FeedPage")));
        bar.addTab(bar.newTab().setText("Feed Data").setTabListener(new TabListener<FeedData.FDFragment>(this, FeedData.FDFragment.class, helper, "FeedData")));
        
        bar.setSelectedNavigationItem(helper.getCurrentTab());
        
        
        bar.setCustomView(getLayoutInflater().inflate(R.layout.user_account_title, null));
        TextView user = (TextView)bar.getCustomView();
        
        String autoCheck = helper.AutoLoginAccount();
        if(autoCheck == null){
        	user.setText("guest");
        	Toast.makeText(this, "You auto login password had change, auto login failed", Toast.LENGTH_LONG).show();
        } else {
            user.setText(autoCheck);
        }
                
        user.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment loginDialog = LoginDialog.newInstance(R.string.login_dialog_title, Homepage.this, helper, v, bar);
				loginDialog.show(getFragmentManager(), "dialog");
			}
		});

		bar.setDisplayOptions(bar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
    }
    
    
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        private Helper helper;
        
        public TabListener(Activity activity, Class<T> clz, Helper h, String tag) {
            this(activity, tag, clz, null);
            helper = h;
        }
        
        public TabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
            mArgs = args;

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
            if (mFragment != null && !mFragment.isDetached()) {
                FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
                ft.detach(mFragment);
                ft.commit();
            }
        }


		public void onTabSelected(Tab tab, FragmentTransaction ft) {
        	helper.setCurrentTab(tab.getPosition());
        	Log.d("pachube debug", "click a tab: " + tab.getPosition());
            if (mFragment == null) {
                mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                ft.attach(mFragment);
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            Toast.makeText(mActivity, "Refresh!", Toast.LENGTH_SHORT).show();
        }
        

    }


	public static class LoginDialog extends DialogFragment {

		private static Context mContext;
		private static TextView username;
		private static Helper helper;
		private static ActionBar bar;
		AlertDialog dialog;
		
		public static LoginDialog newInstance(int title, Context context, Helper h, View name, ActionBar b) {
			LoginDialog frag = new LoginDialog();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			
			bar = b;
			mContext = context;
			helper = h;
			username = (TextView) name;
			
			return frag;
		}
		
		public static View getLoginDialogView(){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View loginDialog = inflater.inflate(R.layout.login_dialog, null);
			return loginDialog;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			int title = getArguments().getInt("title");
			final View mDialog = getLoginDialogView();
			
			/* For the logout section
			 * */
			
			final TextView currentName = (TextView) mDialog.findViewById(R.id.current_account);
			currentName.setText(username.getText());
			final Button currentLogout = (Button) mDialog.findViewById(R.id.current_logout);
			
			if(username.getText().toString().compareTo("guest")==0){
				currentLogout.setEnabled(false);
			}
			
			currentLogout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog confirm = new AlertDialog.Builder(getActivity())
								   .setMessage(R.string.login_logout_confirm)
								   .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											helper.logout();
											// SOS reload the list (logout)
											bar.setSelectedNavigationItem(Homepage.FEED_LIST);
									    	username.setText("guest");
									    	Toast.makeText(mContext, "You just log out and become guest", Toast.LENGTH_LONG).show();
									    	dialog.dismiss();
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
			
			/* For the switch account section
			 * */
			
			final EditText loginName = (EditText)mDialog.findViewById(R.id.login_username);
			final EditText loginPW = (EditText)mDialog.findViewById(R.id.login_pw);
			final CheckBox loginAuto = (CheckBox) mDialog.findViewById(R.id.login_autoCheck);
			final CheckBox loginReg = (CheckBox) mDialog.findViewById(R.id.login_register);
			
			dialog = new AlertDialog.Builder(getActivity())
					   .setIcon(R.drawable.ic_menu_login)
					   .setTitle(title)
					   .setView(mDialog)
					   .setPositiveButton(R.string.dialog_confirm, new OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								String lgName = loginName.getText().toString();
								String lgPW = loginPW.getText().toString();
								String[] account = new String[]{lgName, lgPW};
								
								if(helper.login(account, loginAuto.isChecked(), loginReg.isChecked())){
									// SOS reload the list (login)
									bar.setSelectedNavigationItem(Homepage.FEED_LIST);
									username.setText(lgName);
							    	Toast.makeText(mContext, "You just log in as " + username.getText(), Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(mContext, "You enter wrong name or password", Toast.LENGTH_LONG).show();
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
			
			return dialog;
		}
	}
}
