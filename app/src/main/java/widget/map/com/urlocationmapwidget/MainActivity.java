package widget.map.com.urlocationmapwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.Switch;

import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;

import widget.map.com.urlocationmapwidget.AnimImageButton.OnAnimImageButtonClickedListener;

/**
 * Main view for the application, you can select different map types.
 *
 * @author Xinyue Zhao
 */
public final class MainActivity extends BaseActivity implements OnCheckedChangeListener {
	/**
	 * Open/close locating.
	 */
	private Switch mLocateSw;

	/**
	 * Show single instance of {@link}
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((RadioButton) findViewById(R.id.google_radio)).setChecked(Prefs.getInstance(getApplication())
				.getCurrentMap() == 0);
		((RadioButton) findViewById(R.id.baidu_radio)).setChecked(Prefs.getInstance(getApplication()).getCurrentMap() ==
				1);
		mLocateSw = (Switch) findViewById(R.id.locate_sw);
		mLocateSw.setEnabled(false);
		mLocateSw.setChecked(Prefs.getInstance(getApplication()).isLocationUpdating());
		mLocateSw.setOnCheckedChangeListener(this);

		findViewById(R.id.apply_widget_btn).setOnClickListener(new OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
				pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, new ComponentName(MainActivity.this,
						UrLocationWidgetProvider.class));
				startActivityForResult(pickIntent, 0x90);
			}
		});
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
		Prefs.getInstance(getApplication()).setCurrentMap(0);
	}

	/**
	 * Click event to select baidu map.
	 *
	 * @param view
	 * 		No usage.
	 */
	public void selectBaidu(View view) {
		Prefs.getInstance(getApplication()).setCurrentMap(1);
	}


	/**
	 * Called when the checked state of a compound button has changed.
	 *
	 * @param buttonView
	 * 		The compound button view whose state has changed.
	 * @param isChecked
	 * 		The new checked state of buttonView.
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			startService(new Intent(this, UrLocationWidgetService.class));
		} else {
			stopService(new Intent(this, UrLocationWidgetService.class));
		}
	}

	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		mLocateSw.setEnabled(true);
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		mLocateSw.setEnabled(true);
	}
}
