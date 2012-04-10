package com.hsr.datalogger;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.widget.Toast;

public class Homepage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar bar = getActionBar();

        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        
        bar.addTab(bar.newTab()
        			  .setText("Feed List")
        			  .setTabListener(new TabListener<FeedList.FLFragment>(this, "FeedList", FeedList.FLFragment.class)));
        
        bar.addTab(bar.newTab()
        			  .setText("Feed Page")
        			  .setTabListener(new TabListener<FeedPage.FPFragment>(this, "FeedPage", FeedPage.FPFragment.class)));
        
        bar.addTab(bar.newTab()
        			  .setText("Feed Data")
        			  .setTabListener(new TabListener<FeedData.fragment>(this, "FeedData", FeedData.fragment.class)));
        
        // [TO BE ADDED] get from cache
        bar.setSelectedNavigationItem(0);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	// [TO BE ADDED] put into cache
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
        // [TO BE ADDED] get from cache
    }

    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;
        private final Bundle mArgs;
        private Fragment mFragment;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            this(activity, tag, clz, null);
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
            Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
        }
        

    }

    
}
