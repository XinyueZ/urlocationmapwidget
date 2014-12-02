package widget.map.com.urlocationmapwidget.appwidgets;

import java.lang.ref.WeakReference;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chopping.net.TaskHelper;
import com.google.android.gms.maps.model.LatLng;

import widget.map.com.urlocationmapwidget.R;
import widget.map.com.urlocationmapwidget.app.activities.FBCheckInActivity;
import widget.map.com.urlocationmapwidget.app.activities.MainActivity;
import widget.map.com.urlocationmapwidget.app.activities.ProgressBarActivity;
import widget.map.com.urlocationmapwidget.app.activities.QuickSettingActivity;
import widget.map.com.urlocationmapwidget.app.services.BaseService;
import widget.map.com.urlocationmapwidget.utils.Prefs;
import widget.map.com.urlocationmapwidget.utils.Utils;

/**
 * The basic provider for this widget.
 *
 * @author Xinyue Zhao
 */
public abstract class BaseUrLocationWidgetProvider extends AppWidgetProvider {
	/**
	 * Make url to place short.
	 */
	private static final String TINY = "http://tinyurl.com/api-create.php?url=";
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
	static final String ACTION_CLICK_MAP = "widget.map.com.urlocationmapwidget.CLICK_MAP";
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
	public static final String ACTION_ENABLE_LOCATING = "widget.map.com.urlocationmapwidget.ENABLE_LOCATING";

	/**
	 * CheckIn on Facebook.
	 */
	private static final String ACTION_FB_CHECK_IN = "widget.map.com.urlocationmapwidget.FB_CHECK_IN";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		ComponentName thisWidget = new ComponentName(context, getClass());
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		RemoteViews views;
		Prefs prefs = Prefs.getInstance(context.getApplicationContext());
		for (int i = 0; i < allWidgetIds.length; i++) {
			views = new RemoteViews(context.getPackageName(), getLayoutResId());
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
			views.setOnClickPendingIntent(R.id.fb_check_in_btn, buildViewClickIntent(context, ACTION_FB_CHECK_IN));

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
			context.sendBroadcast(new Intent(BaseService.ACTION_UPDATE));
		} else if (ACTION_ZOOM_OUT.equals(intent.getAction())) {
			int curZoom = prefs.getZoomLevel();
			prefs.setZoomLevel(--curZoom);
			context.sendBroadcast(new Intent(BaseService.ACTION_UPDATE));
		} else if (ACTION_UPDATE.equals(intent.getAction())) {
			context.sendBroadcast(new Intent(BaseService.ACTION_UPDATE));
		} else if (ACTION_ENABLE_LOCATING.equals(intent.getAction())) {
			//Enable locate button now.
			RemoteViews views = new RemoteViews(context.getPackageName(), getLayoutResId());
			views.setOnClickPendingIntent(R.id.locate_btn, buildViewClickIntent(context,
					ACTION_LOCATE));
			ComponentName thisWidget = new ComponentName(context, getClass());
			AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, views);
		} else if (ACTION_CLICK_MAP.equals(intent.getAction())) {
			Utils.toggleLocating(context);
		} else if (ACTION_QUICK_SETTING.equals(intent.getAction())) {
			QuickSettingActivity.showInstance(context);
		} else if(ACTION_SHARE_LOCATION.equals(intent.getAction())) {
			final WeakReference<Context> appRef = new WeakReference<Context>(context.getApplicationContext());
			String latlng =  prefs.getLastLocation();
			if(!TextUtils.isEmpty(latlng)) {
				ProgressBarActivity.showInstance(context);
				String[] latlngs = latlng.split(",");
				LatLng ll = new LatLng(
						Double.parseDouble(latlngs[0]),
						Double.parseDouble(latlngs[1])
				);
				StringRequest request = new StringRequest(Request.Method.GET, TINY + prefs.getUrlPlace(ll), new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						if(appRef != null && appRef.get() != null) {
							Context cxt = appRef.get();
							Prefs p = Prefs.getInstance(cxt);
							ProgressBarActivity.closeInstance(cxt);
							Utils.getDefaultShareIntent(cxt, "", cxt.getString(R.string.lbl_share_your_location, response, p.getLastLocationName()));
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(appRef != null && appRef.get() != null) {
							Context cxt = appRef.get();
							Prefs p = Prefs.getInstance(cxt);
							ProgressBarActivity.closeInstance(cxt);
							String myLastLocation = cxt.getString(R.string.lbl_share_your_location, p.getLastLocation(), p.getLastLocationName());
							Utils.getDefaultShareIntent(cxt, "", myLastLocation);
						}
					}
				});
				TaskHelper.getRequestQueue().add(request);
			}
		} else if(ACTION_FB_CHECK_IN.equals(intent.getAction())) {
			FBCheckInActivity.showInstance(context);
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
	protected  PendingIntent buildViewClickIntent(Context context, String action) {
		Intent intent = new Intent(context, getClass());
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}


	/**
	 * Get layout id.
	 * @return {@link android.support.annotation.LayoutRes}.
	 */
	protected abstract int getLayoutResId();
}
