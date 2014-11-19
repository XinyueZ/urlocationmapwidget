package widget.map.com.urlocationmapwidget.app.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import widget.map.com.urlocationmapwidget.R;

/**
 * Show a progressbar.
 */
public final class ProgressBarActivity extends Activity {
	/**
	 * Action to close this view.
	 */
	private static final String ACTION_CLOSE = "widget.map.com.urlocationmapwidget.CLOSE";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_progressbar;
	/**
	 * {@link android.content.IntentFilter} for close this view.
	 */
	private IntentFilter mIntentFilter = new IntentFilter(ACTION_CLOSE);
	/**
	 * {@link android.content.BroadcastReceiver} for close this view.
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			ProgressBarActivity.this.finish();
		}
	};

	/**
	 * Show single instance of {@link}
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, ProgressBarActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	/**
	 * Close this view.
	 * @param cxt {@link android.content.Context}.
	 */
	public static void closeInstance(Context cxt) {
		cxt.sendBroadcast(new Intent(ACTION_CLOSE));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		registerReceiver(mReceiver, mIntentFilter);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
}
