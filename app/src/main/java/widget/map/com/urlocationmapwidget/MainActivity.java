package widget.map.com.urlocationmapwidget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		msgTv.setText(R.string.lbl_app_init_done);
		msgPb.setVisibility(View.GONE);
		Prefs.getInstance(getApplication()).setInit(true);
		sendBroadcast(new Intent(ACTION_ENABLE_LOCATING));
	}
}
