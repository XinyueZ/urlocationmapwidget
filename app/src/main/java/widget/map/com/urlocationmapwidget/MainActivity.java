package widget.map.com.urlocationmapwidget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import static widget.map.com.urlocationmapwidget.UrLocationWidgetProvider.ACTION_ENABLE_LOCATING;

/**
 * Main view for the application, you can select different map types.
 *
 * @author Xinyue Zhao
 */
public final class MainActivity extends BaseActivity {
	/**
	 * Main menu.
	 */
	private static final int MENU = R.menu.main;
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
		Crashlytics.start(this);
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

		final int isFound = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (isFound == ConnectionResult.SUCCESS ||
				isFound == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {//Ignore update.
			//The "End User License Agreement" must be confirmed before you use this application.
			if (!Prefs.getInstance(getApplication()).isEULAOnceConfirmed()) {
				showDialogFragment(AboutDialogFragment.EulaConfirmationDialog.newInstance(this), null);
			}
		} else {
			new AlertDialog.Builder(this).setTitle(R.string.app_name).setMessage(R.string.lbl_play_service)
					.setCancelable(false).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(getString(R.string.play_service_url)));
					startActivity(intent);
					finish();
				}
			}).create().show();
		}

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


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(MENU, menu);
		MenuItem menuShare = menu.findItem(R.id.action_share_app);

		android.support.v7.widget.ShareActionProvider provider =
				(android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);

		String subject = getString(R.string.lbl_introduce_app);
		String text = getString(R.string.lbl_share_app_content);
		provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_about:
			showDialogFragment(AboutDialogFragment.newInstance(this), null);
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	/**
	 * Show  {@link android.support.v4.app.DialogFragment}.
	 *
	 * @param dlgFrg
	 * 		An instance of {@link android.support.v4.app.DialogFragment}.
	 * @param tagName
	 * 		Tag name for dialog, default is "dlg". To grantee that only one instance of {@link
	 * 		android.support.v4.app.DialogFragment} can been seen.
	 */
	protected void showDialogFragment(DialogFragment dlgFrg, String tagName) {
		try {
			if (dlgFrg != null) {
				DialogFragment dialogFragment = dlgFrg;
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				// Ensure that there's only one dialog to the user.
				Fragment prev = getSupportFragmentManager().findFragmentByTag("dlg");
				if (prev != null) {
					ft.remove(prev);
				}
				try {
					if (TextUtils.isEmpty(tagName)) {
						dialogFragment.show(ft, "dlg");
					} else {
						dialogFragment.show(ft, tagName);
					}
				} catch (Exception _e) {
				}
			}
		} catch (Exception _e) {
		}
	}

}
