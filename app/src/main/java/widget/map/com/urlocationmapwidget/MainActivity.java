package widget.map.com.urlocationmapwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;

import widget.map.com.urlocationmapwidget.AnimImageButton.OnAnimImageButtonClickedListener;

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
	 * Show some message.
	 */
	private TextView msgTv;

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
		((RadioButton) findViewById(R.id.google_radio)).setChecked(Prefs.getInstance(getApplication())
				.getCurrentMap() == 0);
		((RadioButton) findViewById(R.id.baidu_radio)).setChecked(Prefs.getInstance(getApplication()).getCurrentMap() ==
				1);

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


	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		Prefs.getInstance(getApplication()).setInit(true);
		msgTv.setText(R.string.lbl_app_init_done);
		sendBroadcast(new Intent(ACTION_ENABLE_LOCATING));
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		msgTv.setText(R.string.lbl_app_init_done);
		Prefs.getInstance(getApplication()).setInit(true);
		sendBroadcast(new Intent(ACTION_ENABLE_LOCATING));
	}
}
