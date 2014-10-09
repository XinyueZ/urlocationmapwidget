package widget.map.com.urlocationmapwidget;

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

/**
 * Provide location for widget.
 *
 * @author Xinyue Zhao
 */
public final class UrLocationWidgetService extends Service implements ConnectionCallbacks, OnConnectionFailedListener,
		LocationListener, ImageListener {
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5 * 60;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

	LocationClient mLocationClient;
	LocationRequest mLocationRequest;

	ScreenSize mScreenSize;

	public UrLocationWidgetService() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mScreenSize = DeviceUtils.getScreenSize(this);
		/*
		 * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
		mLocationClient = new LocationClient(this, this, this);
		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		mLocationClient.connect();


		Prefs.getInstance(getApplication()).setLocationUpdating(true);

		RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_urlocation);
		views.setImageViewResource(R.id.locate_btn, R.drawable.ic_no_locate_btn);
		ComponentName thisWidget = new ComponentName(this, UrLocationWidgetProvider.class);
		AppWidgetManager.getInstance(this).updateAppWidget(thisWidget, views);

		Utils.showLongToast(this, R.string.msg_locate);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Utils.showLongToast(this, R.string.msg_stop_locate);

		Prefs.getInstance(getApplication()).setLocationUpdating(false);

		RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_urlocation);
		views.setImageViewResource(R.id.locate_btn, R.drawable.ic_locate_btn);
		ComponentName thisWidget = new ComponentName(this, UrLocationWidgetProvider.class);
		AppWidgetManager.getInstance(this).updateAppWidget(thisWidget, views);

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
		String url = Prefs.getInstance(getApplication()).getMap(new LatLng(location.getLatitude(),
				location.getLongitude()), mScreenSize.Width, mScreenSize.Height);
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
