package widget.map.com.urlocationmapwidget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;

import static widget.map.com.urlocationmapwidget.UrLocationWidgetProvider.ACTION_ENABLE_LOCATING;

/**
 * Main view for the application, you can select different map types.
 *
 * @author Xinyue Zhao
 */
public final class MainActivity extends BaseActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;
	/**
	 * Show some messages.
	 */
	private TextView msgTv;
	/**
	 * Progress indicator for some messages.
	 */
	private View msgPb;
	/**
	 * Use navigation-drawer for this fork.
	 */
	private ActionBarDrawerToggle mDrawerToggle;
	/**
	 * Navigation drawer.
	 */
	private DrawerLayout mDrawerLayout;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------
	/**
	 * Handler for {@link CloseDrawerEvent}.
	 *
	 * @param e
	 * 		Event {@link  CloseDrawerEvent}.
	 */
	public void onEvent(CloseDrawerEvent e) {
		mDrawerLayout.closeDrawers();
	}
	/**
	 * Event, open an external app that has been installed.
	 *
	 * @param e
	 * 		{@link  LinkToExternalAppEvent}.
	 */
	public void onEvent(LinkToExternalAppEvent e) {
		com.chopping.utils.Utils.linkToExternalApp(this, e.getAppListItem());
	}

	//------------------------------------------------
	/**
	 * Show single instance of {@link MainActivity}
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		msgTv = (TextView) findViewById(R.id.msg_tv);
		msgPb = findViewById(R.id.msg_pb);
		((RadioButton) findViewById(R.id.google_radio)).setChecked(Prefs.getInstance(getApplication())
				.getCurrentMap() == 0);
		((RadioButton) findViewById(R.id.baidu_radio)).setChecked(Prefs.getInstance(getApplication()).getCurrentMap() ==
				1);
		initDrawer();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setErrorHandlerAvailable(false);
	}

	/**
	 * App that use this Chopping should know the preference-storage.
	 *
	 * @return An instance of {@link com.chopping.application.BasicPrefs}.
	 */
	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance(getApplication());
	}

	/**
	 * Click event to select google map.
	 *
	 * @param view
	 * 		No usage.
	 */
	public void selectGoogle(View view) {
		Prefs.getInstance(getApplication()).setCurrentMap(Prefs.GOOGLE_MAP);
	}

	/**
	 * Click event to select baidu map.
	 *
	 * @param view
	 * 		No usage.
	 */
	public void selectBaidu(View view) {
		Prefs.getInstance(getApplication()).setCurrentMap(Prefs.BAIDU_MAP);
	}


	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		Prefs.getInstance(getApplication()).setInit(true);
		msgTv.setText(R.string.lbl_app_init_done);
		msgPb.setVisibility(View.GONE);
		sendBroadcast(new Intent(ACTION_ENABLE_LOCATING));
		showAppList();
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		msgTv.setText(R.string.lbl_app_init_done);
		msgPb.setVisibility(View.GONE);
		Prefs.getInstance(getApplication()).setInit(true);
		sendBroadcast(new Intent(ACTION_ENABLE_LOCATING));
		showAppList();
	}

	/**
	 * Initialize the navigation drawer.
	 */
	private void initDrawer() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name,
					R.string.app_name) {
				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {
					super.onDrawerSlide(drawerView, slideOffset);
					if (!getSupportActionBar().isShowing()) {
						getSupportActionBar().show();
					}
				}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);
		}
	}


	/**
	 * Show all external applications links.
	 */
	private void showAppList() {
		getSupportFragmentManager().beginTransaction().replace(R.id.app_list_fl, AppListFragment.newInstance(this))
				.commit();
	}
}
