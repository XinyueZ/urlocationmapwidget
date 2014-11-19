package widget.map.com.urlocationmapwidget.app.services;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.chopping.net.TaskHelper;
import com.chopping.utils.DeviceUtils;
import com.chopping.utils.DeviceUtils.ScreenSize;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLng;

import widget.map.com.urlocationmapwidget.utils.Prefs;
import widget.map.com.urlocationmapwidget.R;
import widget.map.com.urlocationmapwidget.appwidgets.BaseUrLocationWidgetProvider;
import widget.map.com.urlocationmapwidget.appwidgets.UrLocationWidgetProvider;
import widget.map.com.urlocationmapwidget.utils.Utils;

/**
 * Service background(main thread) to provide current location of user for widget.
 *
 * @author Xinyue Zhao
 */
public final class UrLocationWidgetService extends BaseService {
	/**
	 * Layout of the widget.
	 */
	private static final int LAYOUT_WIDGET = R.layout.widget_urlocation;

	/**
	 * Get screen's size for widget's max resize.
	 */
	private ScreenSize mScreenSize;


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mScreenSize = DeviceUtils.getScreenSize(this);
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			Prefs prefs = Prefs.getInstance(getApplication());
			//Baidu can not accept size more than 1024! We needs width == height.
			String url = prefs.getUrlMap(new LatLng(location.getLatitude(), location.getLongitude()),
					prefs.getCurrentMap() == Prefs.BAIDU_MAP ? (mScreenSize.Width > 1000 ? 1000 : mScreenSize.Width) :
							mScreenSize.Width,
					prefs.getCurrentMap() == Prefs.BAIDU_MAP ? (mScreenSize.Width > 1000 ? 1000 : mScreenSize.Width) :
							mScreenSize.Height, prefs.getZoomLevel());
			//		LL.d(String.format("Map from :%s", url));
			TaskHelper.getImageLoader().get(url, this);
			prefs.setLastLocation(location);
			setAddress(location);
		}
	}


	/**
	 * Listens for non-error changes to the loading of the image request.
	 *
	 * @param response
	 * 		Holds all information pertaining to the request, as well as the bitmap (if it is loaded).
	 * @param isImmediate
	 * 		True if this was called during ImageLoader.get() variants. This can be used to differentiate between a cached
	 * 		image loading and a network image loading in order to, for example, run an animation to fade in network loaded
	 */
	@Override
	public void onResponse(ImageContainer response, boolean isImmediate) {
		RemoteViews views = new RemoteViews(getPackageName(), LAYOUT_WIDGET);
		views.setImageViewBitmap(R.id.urlocation_iv, response.getBitmap());
		views.setTextViewText(R.id.last_update_tv,
				Utils.convertTimestamps2DateString(this, System.currentTimeMillis()));
		ComponentName thisWidget = new ComponentName(this, UrLocationWidgetProvider.class);
		AppWidgetManager.getInstance(this).updateAppWidget(thisWidget, views);
	}

	@Override
	protected int getLayoutResId() {
		return LAYOUT_WIDGET;
	}


	@Override
	protected Class<? extends BaseUrLocationWidgetProvider> getWidgetProvider() {
		return UrLocationWidgetProvider.class;
	}

	/**
	 * Constructor of {@link UrLocationWidgetService}, no usage.
	 */
	public UrLocationWidgetService() {
		//Not used.
	}


	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}


	@Override
	public void onDisconnected() {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}


	/**
	 * Callback method that an error has been occurred with the provided error code and optional user-readable message.
	 *
	 * @param error
	 */
	@Override
	public void onErrorResponse(VolleyError error) {

	}

}
