package widget.map.com.urlocationmapwidget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * A dialog like {@link android.app.Activity} give user chance to set update interval, battery optimizing etc..
 *
 * @author Xinyue Zhao
 */
public final class QuickSettingActivity extends Activity implements OnSeekBarChangeListener, OnItemSelectedListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_quick_setting;
	/**
	 * Setting update interval.
	 */
	private SeekBar mIntervalSb;
	/**
	 * Show current selected minutes.
	 */
	private TextView mMinutesTv;

	/**
	 * Show single instance of {@link QuickSettingActivity}
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, QuickSettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		mMinutesTv = (TextView) findViewById(R.id.minutes_tv);
		Prefs prefs = Prefs.getInstance(getApplication());
		mIntervalSb = (SeekBar) findViewById(R.id.set_interval_sb);
		mIntervalSb.setMax(Prefs.MAX_INTERVAL - Prefs.MIN_INTERVAL);
		int curInterval = prefs.getInterval();
		showSelectedInterval(curInterval);
		mIntervalSb.setOnSeekBarChangeListener(this);

		/*
	  Select priority on a list.
	 */
		Spinner prioritySp = (Spinner) findViewById(R.id.set_priority_sp);
		prioritySp.setSelection(prefs.getPrioritySelection());
		prioritySp.setOnItemSelectedListener(this);
	}

	@Override
	protected void onStop() {
		Utils.restart(getApplication());
		super.onStop();
	}

	/**
	 * Show selected interval on UI.
	 *
	 * @param curInterval
	 * 		The current selected interval.
	 */
	private void showSelectedInterval(int curInterval) {
		mIntervalSb.setProgress(curInterval - Prefs.MIN_INTERVAL);
		mMinutesTv.setText(String.format(getString(R.string.lbl_minutes), curInterval + ""));
	}

	/**
	 * Notification that the progress level has changed. Clients can use the fromUser parameter to distinguish
	 * user-initiated changes from those that occurred programmatically.
	 *
	 * @param seekBar
	 * 		The SeekBar whose progress has changed
	 * @param progress
	 * 		The current progress level. This will be in the range 0..max where max was set by {@link
	 * 		android.widget.ProgressBar#setMax(int)}. (The default value for max is 100.)
	 * @param fromUser
	 * 		True if the progress change was initiated by the user.
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int correctedValue = Prefs.MIN_INTERVAL + progress;
		Prefs prefs = Prefs.getInstance(getApplication());
		prefs.setInterval(correctedValue);
		showSelectedInterval(prefs.getInterval());
	}

	/**
	 * Notification that the user has started a touch gesture. Clients may want to use this to disable advancing the
	 * seekbar.
	 *
	 * @param seekBar
	 * 		The SeekBar in which the touch gesture began
	 */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	/**
	 * Notification that the user has finished a touch gesture. Clients may want to use this to re-enable advancing the
	 * seekbar.
	 *
	 * @param seekBar
	 * 		The SeekBar in which the touch gesture began
	 */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}


	/**
	 * <p>Callback method to be invoked when an item in this view has been selected. This callback is invoked only when
	 * the newly selected position is different from the previously selected position or if there was no selected
	 * item.</p>
	 * <p/>
	 * Impelmenters can call getItemAtPosition(position) if they need to access the data associated with the selected
	 * item.
	 *
	 * @param parent
	 * 		The AdapterView where the selection happened
	 * @param view
	 * 		The view within the AdapterView that was clicked
	 * @param position
	 * 		The position of the view in the adapter
	 * @param id
	 * 		The row id of the item that is selected
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Prefs.getInstance(getApplication()).setPrioritySelection(position);
	}

	/**
	 * Callback method to be invoked when the selection disappears from this view. The selection can disappear for
	 * instance when touch is activated or when the adapter becomes empty.
	 *
	 * @param parent
	 * 		The AdapterView that now contains no selected item.
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
