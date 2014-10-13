package widget.map.com.urlocationmapwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import de.greenrobot.event.EventBus;

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
	/**
	 * Click event action for zoom-in.
	 */
	private static final String ACTION_ZOOM_IN = "widget.map.com.urlocationmapwidget.ZOOM_IN";
	/**
	 * Click event action for zoom-out.
	 */
	private static final String ACTION_ZOOM_OUT = "widget.map.com.urlocationmapwidget.ZOOM_OUT";
	/**
	 * Click event action for update map.
	 */
	private static final String ACTION_UPDATE = "widget.map.com.urlocationmapwidget.UPDATE";
	/**
	 * Click event action for click map.
	 */
	private static final String ACTION_CLICK_MAP = "widget.map.com.urlocationmapwidget.CLICK_MAP";
	/**
	 * Click event action for quick-setting: update interval, battery saving etc.
	 */
	private static final String ACTION_QUICK_SETTING = "widget.map.com.urlocationmapwidget.QUICK_SETTING";
	/**
	 * Click event action for sharing your location.
	 */
	private static final String ACTION_SHARE_LOCATION = "widget.map.com.urlocationmapwidget.SHARE_LOCATION";

	/**
	 * Click event action for enable locating.
	 */
	static final String ACTION_ENABLE_LOCATING = "widget.map.com.urlocationmapwidget.ENABLE_LOCATING";


	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		ComponentName thisWidget = new ComponentName(context, UrLocationWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		RemoteViews views;
		Prefs prefs = Prefs.getInstance(context.getApplicationContext());
		for (int i = 0; i < allWidgetIds.length; i++) {
			views = new RemoteViews(context.getPackageName(), R.layout.widget_urlocation);
			views.setOnClickPendingIntent(R.id.setting_btn, buildViewClickIntent(context, ACTION_SETTING));
			// Locate-button can't be used directly.
			if (prefs.isInit()) {
				views.setOnClickPendingIntent(R.id.locate_btn, buildViewClickIntent(context, ACTION_LOCATE));
			}

			views.setImageViewResource(R.id.locate_btn,
					prefs.isLocationUpdating() ? R.drawable.ic_no_locate_btn : R.drawable.ic_locate_btn);
			views.setOnClickPendingIntent(R.id.zoom_in_btn, buildViewClickIntent(context, ACTION_ZOOM_IN));
			views.setOnClickPendingIntent(R.id.zoom_out_btn, buildViewClickIntent(context, ACTION_ZOOM_OUT));
			views.setOnClickPendingIntent(R.id.update_btn, buildViewClickIntent(context, ACTION_UPDATE));
			views.setOnClickPendingIntent(R.id.urlocation_iv, buildViewClickIntent(context, ACTION_CLICK_MAP));
			views.setOnClickPendingIntent(R.id.quick_setting_btn, buildViewClickIntent(context, ACTION_QUICK_SETTING));
			views.setOnClickPendingIntent(R.id.share_your_location_btn, buildViewClickIntent(context, ACTION_SHARE_LOCATION));
			appWidgetManager.updateAppWidget(thisWidget, views);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Prefs prefs = Prefs.getInstance(context.getApplicationContext());
		if (!prefs.isInit()) {
			MainActivity.showInstance(context);
			return;
		}
		if (ACTION_SETTING.equals(intent.getAction())) {
			MainActivity.showInstance(context);
		} else if (ACTION_LOCATE.equals(intent.getAction())) {
			Utils.toggleLocating(context);
		} else if (ACTION_ZOOM_IN.equals(intent.getAction())) {
			int curZoom = prefs.getZoomLevel();
			prefs.setZoomLevel(++curZoom);
			EventBus.getDefault().post(new UpdateEvent());
		} else if (ACTION_ZOOM_OUT.equals(intent.getAction())) {
			int curZoom = prefs.getZoomLevel();
			prefs.setZoomLevel(--curZoom);
			EventBus.getDefault().post(new UpdateEvent());
		} else if (ACTION_UPDATE.equals(intent.getAction())) {
			EventBus.getDefault().post(new UpdateEvent());
		} else if (ACTION_ENABLE_LOCATING.equals(intent.getAction())) {
			//Enable locate button now.
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_urlocation);
			views.setOnClickPendingIntent(R.id.locate_btn, UrLocationWidgetProvider.buildViewClickIntent(context,
					ACTION_LOCATE));
			ComponentName thisWidget = new ComponentName(context, UrLocationWidgetProvider.class);
			AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, views);
		} else if (ACTION_CLICK_MAP.equals(intent.getAction())) {
			Utils.toggleLocating(context);
		} else if (ACTION_QUICK_SETTING.equals(intent.getAction())) {
			QuickSettingActivity.showInstance(context);
		} else if(ACTION_SHARE_LOCATION.equals(intent.getAction())) {
			String myLastLocation = context.getString(R.string.lbl_share_your_location, prefs.getLastLocation(), prefs.getLastLocationName());
			Utils.getDefaultShareIntent(context, "", myLastLocation );
		}
	}


	/**
	 * Make click event handler.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param action
	 * 		Action name.
	 *
	 * @return {@link android.app.PendingIntent} for the click event.
	 */
	private static PendingIntent buildViewClickIntent(Context context, String action) {
		Intent intent = new Intent(context, UrLocationWidgetProvider.class);
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}


}
