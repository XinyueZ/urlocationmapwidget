package widget.map.com.urlocationmapwidget;

import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.chopping.application.LL;
import com.chopping.net.TaskHelper;
import com.chopping.utils.DeviceUtils;
import com.chopping.utils.DeviceUtils.ScreenSize;
import com.chopping.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import de.greenrobot.event.EventBus;

/**
 * Service background(main thread) to provide current location of user for widget.
 *
 * @author Xinyue Zhao
 */
public final class UrLocationWidgetService extends Service implements ConnectionCallbacks, OnConnectionFailedListener,
		LocationListener, ImageListener {
	/**
	 * Location provider.
	 */
	private LocationClient mLocationClient;
	/**
	 * Query for a location.
	 */
	private LocationRequest mLocationRequest;
	/**
	 * Get screen's size for widget's max resize.
	 */
	private ScreenSize mScreenSize;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link widget.map.com.urlocationmapwidget.UpdateEvent}.
	 *
	 * @param e
	 * 		Event {@link widget.map.com.urlocationmapwidget.UpdateEvent}.
	 */
	public void onEvent(UpdateEvent e) {
		if (mLocationClient != null && mLocationClient.isConnected() && mLocationRequest != null) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
	}

	//------------------------------------------------

	/**
	 * Constructor of {@link UrLocationWidgetService}, no usage.
	 */
	public UrLocationWidgetService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		EventBus.getDefault().register(this);

		Prefs prefs = Prefs.getInstance(getApplicationContext());
		mScreenSize = DeviceUtils.getScreenSize(this);
		mLocationClient = new LocationClient(this, this, this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(prefs.getPriority());
		mLocationRequest.setInterval(TimeUnit.MINUTES.toMillis(prefs.getInterval()));
		mLocationRequest.setFastestInterval(TimeUnit.MINUTES.toMillis(3));
		mLocationClient.connect();

		prefs.setLocationUpdating(true);
		setLocateButton(R.drawable.ic_no_locate_btn);
		Utils.showLongToast(this, R.string.msg_locate);

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * Set icon on the locate-button on the widget.
	 *
	 * @param drawableResId
	 * 		The resId for the icon.
	 */
	private void setLocateButton(int drawableResId) {
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_urlocation);
		views.setImageViewResource(R.id.locate_btn, drawableResId);
		ComponentName thisWidget = new ComponentName(this, UrLocationWidgetProvider.class);
		AppWidgetManager.getInstance(this).updateAppWidget(thisWidget, views);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);

		Utils.showLongToast(this, R.string.msg_stop_locate);
		Prefs.getInstance(getApplication()).setLocationUpdating(false);
		setLocateButton(R.drawable.ic_locate_btn);

		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onConnected(Bundle bundle) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	public void onLocationChanged(Location location) {
		Prefs prefs = Prefs.getInstance(getApplication());
		String url = prefs.getMap(new LatLng(location.getLatitude(), location.getLongitude()), mScreenSize.Width,
				mScreenSize.Height, prefs.getZoomLevel());
		LL.d(String.format("Map from :%s", url));
		TaskHelper.getImageLoader().get(url, this);
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
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_urlocation);
		views.setImageViewBitmap(R.id.urlocation_iv, response.getBitmap());
		ComponentName thisWidget = new ComponentName(this, UrLocationWidgetProvider.class);
		AppWidgetManager.getInstance(this).updateAppWidget(thisWidget, views);
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
