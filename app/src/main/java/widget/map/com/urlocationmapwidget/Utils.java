package widget.map.com.urlocationmapwidget;

import android.content.Context;
import android.content.Intent;

/**
 * Utils for some global methods.
 *
 * @author Xinyue Zhao
 */
public final class Utils {
	/**
	 * Start or stop the {@link widget.map.com.urlocationmapwidget.UrLocationWidgetService} to require current location
	 * with different priorities.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 */
	public static void toggleLocating(Context context) {
		Prefs prefs = Prefs.getInstance(context.getApplicationContext());
		if (!prefs.isLocationUpdating()) {
			context.startService(new Intent(context, UrLocationWidgetService.class));
		} else {
			context.stopService(new Intent(context, UrLocationWidgetService.class));
		}
	}

	/**
	 * Restart the {@link UrLocationWidgetService}.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 */
	public static void restart(Context context) {
		context.stopService(new Intent(context, UrLocationWidgetService.class));
		context.startService(new Intent(context, UrLocationWidgetService.class));
	}

}
