package widget.map.com.urlocationmapwidget.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import widget.map.com.urlocationmapwidget.R;

/**
 * Show a dialog like activity before sharing on Facebook Inc.
 *
 * @author Xinyue Zhao
 */
public class FBCheckInActivity extends Activity {

	/**
	 * Layout.
	 */
	private static final int LAYOUT = R.layout.activity_fbcheck_in;

	/**
	 * Show single instance of {@link FBCheckInActivity}
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, FBCheckInActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
	}

	/**
	 * Cancel sharing location on the Facebook Inc.
	 *
	 * @param view
	 * 		No used.
	 */
	public void cancel(View view) {
		finish();
	}

	/**
	 * OK, confirm to share location on the Facebook Inc.
	 *
	 * @param view
	 * 		No used.
	 */
	public void confirm(View view) {
		//TODO share and checkIn on the Facebook Inc.
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
