package widget.map.com.urlocationmapwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * The provider for this widget.
 *
 * @author Xinyue Zhao
 */
public final class UrLocationWidgetProvider extends AppWidgetProvider {
	/**
	 * Click event action for open setting.
	 */
	private static final String ACTION_SETTING = "widget.map.com.urlocationmapwidget.SETTING";
	/**
	 * Click event action for open/close locating.
	 */
	private static final String ACTION_LOCATE = "widget.map.com.urlocationmapwidget.LOCATE";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		ComponentName thisWidget = new ComponentName(context, UrLocationWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		RemoteViews views;
		for (int i = 0; i < allWidgetIds.length; i++) {
			views = new RemoteViews(context.getPackageName(), R.layout.widget_urlocation);
			views.setOnClickPendingIntent(R.id.setting_btn, getSettingIntent(context));
			views.setOnClickPendingIntent(R.id.locate_btn, getLocateIntent(context));
			appWidgetManager.updateAppWidget(thisWidget, views);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (ACTION_SETTING.equals(intent.getAction())) {
			MainActivity.showInstance(context);
		} else if(ACTION_LOCATE.equals(intent.getAction())) {
			if (!Prefs.getInstance(context.getApplicationContext()).isLocationUpdating()) {
				context.startService(new Intent(context, UrLocationWidgetService.class));
			} else {
				context.stopService(new Intent(context, UrLocationWidgetService.class));
			}
		}
	}

	/**
	 * Make click event setting for widget.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 *
	 * @return {@link android.app.PendingIntent} for the click event.
	 */
	private PendingIntent getSettingIntent(Context context) {
		Intent intent = new Intent(context, getClass());
		intent.setAction(ACTION_SETTING);
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	/**
	 * Make click event open/close locate for widget.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 *
	 * @return {@link android.app.PendingIntent} for the click event.
	 */
	private PendingIntent getLocateIntent(Context context) {
		Intent intent = new Intent(context, getClass());
		intent.setAction(ACTION_LOCATE);
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

}
