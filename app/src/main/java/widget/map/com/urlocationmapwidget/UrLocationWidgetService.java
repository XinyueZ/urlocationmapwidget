package widget.map.com.urlocationmapwidget;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
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
import de.greenrobot.event.EventBusException;

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
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // no gps
			Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		}
		try {
			EventBus.getDefault().register(this);
		}catch (EventBusException e) {
			LL.d(e.getMessage());
		}

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
		try {
			EventBus.getDefault().unregister(this);
		}catch (EventBusException e) {
			LL.d(e.getMessage());
		}

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
		if(location!= null) {
			Prefs prefs = Prefs.getInstance(getApplication());

			//Baidu can not accept size more than 1024! We needs width == height.
			String url = prefs.getMap(new LatLng(location.getLatitude(), location.getLongitude()),
					prefs.getCurrentMap() == Prefs.BAIDU_MAP ? (mScreenSize.Width > 1000 ? 1000 : mScreenSize.Width) :
							mScreenSize.Width,
					prefs.getCurrentMap() == Prefs.BAIDU_MAP ? (mScreenSize.Width > 1000 ? 1000 : mScreenSize.Width) :
							mScreenSize.Height, prefs.getZoomLevel());
			//		LL.d(String.format("Map from :%s", url));
			TaskHelper.getImageLoader().get(url, this);
			prefs.setLastLocation(location);
			new GetAddressTask(this).executeParallel(location);
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
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_urlocation);
		views.setImageViewBitmap(R.id.urlocation_iv, response.getBitmap());
		views.setTextViewText(R.id.last_update_tv, widget.map.com.urlocationmapwidget.Utils.convertTimestamps2DateString(this, System.currentTimeMillis()));
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


	/**
	 * A subclass of AsyncTask that calls getFromLocation() in the
	 * background. The class definition has these generic types:
	 * Location - A Location object containing
	 * the current location.
	 * Void     - indicates that progress units are not used
	 * String   - An address passed to onPostExecute()
	 */
	private static class GetAddressTask extends ParallelTask<Location, Void, String> {
		private Context mContext;
		public GetAddressTask(Context context) {
			super();
			mContext = context;
		}
		/**
		 * Get a Geocoder instance, get the latitude and longitude
		 * look up the address, and return it
		 *
		 * @params params One or more Location objects
		 * @return A string containing the address of the current
		 * location, or an empty string if no address can be found,
		 * or an error message
		 */
		@Override
		protected String doInBackground(Location... params) {
			Geocoder geocoder =
					new Geocoder(mContext, Locale.getDefault());
			// Get the current location from the input parameter list
			Location loc = params[0];
			// Create a list to contain the result address
			List<Address> addresses = null;
			try {
                /*
                 * Return 1 address.
                 */
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e1) {
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return null;
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments " +
						Double.toString(loc.getLatitude()) +
						" , " +
						Double.toString(loc.getLongitude()) +
						" passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return null;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
				String addressText = String.format(
						"%s, %s, %s",
						// If there's a street address, add it
						address.getMaxAddressLineIndex() > 0 ?
								address.getAddressLine(0) : "",
						// Locality is usually a city
						address.getLocality(),
						// The country of the address
						address.getCountryName());
				// Return the text
				return addressText;
			} else {
				return  null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Prefs prefs = Prefs.getInstance(mContext.getApplicationContext());
			if(!TextUtils.isEmpty(result)) {
				RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_urlocation);
				views.setTextViewText(R.id.location_name_tv, result);
				ComponentName thisWidget = new ComponentName(mContext, UrLocationWidgetProvider.class);
				AppWidgetManager.getInstance(mContext).updateAppWidget(thisWidget, views);
				prefs.setLastLocationName(result);
			} else {
				prefs.setLastLocationName("");
			}
		}
	}
}
