package widget.map.com.urlocationmapwidget;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * Abstract basic {@link android.app.Service}.
 *
 * @author Xinyue Zhao
 */
public abstract class BaseService extends Service implements ConnectionCallbacks, OnConnectionFailedListener,
		LocationListener, ImageListener {
	public static final String ACTION_UPDATE = "widget.map.com.urlocationmapwidget.action.UPDATE";
	/**
	 * Location provider.
	 */
	private LocationClient mLocationClient;
	/**
	 * Query for a location.
	 */
	private LocationRequest mLocationRequest;
	/**
	 * The {@link android.content.IntentFilter} for {@link #ACTION_UPDATE}.
	 */
	private IntentFilter mIntentFilter = new IntentFilter(ACTION_UPDATE);
	/**
	 * The {@link android.content.BroadcastReceiver} for {@link #ACTION_UPDATE}.
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LocationClient lc = getLocationClient();
			LocationRequest lr = getLocationRequest();
			if (lc != null && lc.isConnected() && lc != null && lr != null) {
				lc.requestLocationUpdates(lr, BaseService.this);
			}
		}
	};
	/**
	 * Location provider.
	 */
	protected LocationClient getLocationClient() {
		return mLocationClient;
	}

	/**
	 * Query for a location.
	 */
	protected LocationRequest getLocationRequest() {
		return mLocationRequest;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // no gps
			Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		}

		Prefs prefs = Prefs.getInstance(getApplicationContext());
		mLocationClient = new LocationClient(this, this, this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(prefs.getPriority());
		mLocationRequest.setInterval(TimeUnit.MINUTES.toMillis(prefs.getInterval()));
		mLocationRequest.setFastestInterval(TimeUnit.MINUTES.toMillis(3));
		mLocationClient.connect();
		prefs.setLocationUpdating(true);
		setLocateButton(R.drawable.ic_no_locate_btn);
		com.chopping.utils.Utils.showLongToast(this, R.string.msg_locate);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(mReceiver, mIntentFilter);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		com.chopping.utils.Utils.showLongToast(this, R.string.msg_stop_locate);
		Prefs.getInstance(getApplication()).setLocationUpdating(false);
		setLocateButton(R.drawable.ic_locate_btn);
		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();
		super.onDestroy();
	}


	@Override
	public void onConnected(Bundle bundle) {
		LocationClient lc = getLocationClient();
		LocationRequest lr = getLocationRequest();

		Location location = mLocationClient.getLastLocation();
		if(location != null) {
			setAddress(location);
		}

		if (lc != null && lc.isConnected() && lc != null && lr != null) {
			lc.requestLocationUpdates(lr, this);
		}
	}


	/**
	 * Set icon on the locate-button on the widget.
	 *
	 * @param drawableResId
	 * 		The resId for the icon.
	 */
	private void setLocateButton(int drawableResId) {
		RemoteViews views = new RemoteViews(getPackageName(), getLayoutResId());
		views.setImageViewResource(R.id.locate_btn, drawableResId);
		ComponentName thisWidget = new ComponentName(this, getWidgetProvider());
		AppWidgetManager.getInstance(this).updateAppWidget(thisWidget, views);
	}


	/**
	 * Get layout id.
	 * @return {@link android.support.annotation.LayoutRes}.
	 */
	protected abstract int getLayoutResId();


	/**
	 * Get and show address.
	 * @param location Current location data.
	 */
	protected void setAddress(Location location) {
		new GetAddressTask(this).executeParallel(location);
	}


	/**
	 * A subclass of AsyncTask that calls getFromLocation() in the
	 * background. The class definition has these generic types:
	 * Location - A Location object containing
	 * the current location.
	 * Void     - indicates that progress units are not used
	 * String   - An address passed to onPostExecute()
	 */
	 class GetAddressTask extends ParallelTask<Location, Void, String> {
		private Context mContext;
		public GetAddressTask(Context context) {
			super();
			mContext = context;
		}

		/**
		 * Get context.
		 * @return {@link android.content.Context}.
		 */
		public Context getContext() {
			return mContext;
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
				Log.e("LocationSampleActivity", "IO Exception in getFromLocation()");
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
			Prefs prefs = Prefs.getInstance(getApplication());
			if (!TextUtils.isEmpty(result)) {
				RemoteViews views = new RemoteViews(getApplication().getPackageName(),
						getLayoutResId());
				views.setTextViewText(R.id.location_name_tv, result);
				views.setTextViewText(R.id.last_update_tv,
						widget.map.com.urlocationmapwidget.Utils.convertTimestamps2DateString(getApplication(),
								System.currentTimeMillis()));
				ComponentName thisWidget = new ComponentName(getApplication(), getWidgetProvider());
				AppWidgetManager.getInstance(getApplication()).updateAppWidget(thisWidget, views);
				prefs.setLastLocationName(result);
			} else {
				prefs.setLastLocationName("");
			}
		}
	}

	/**
	 * Class info of widget's provider.
	 * @return {@link Class<? extends BaseUrLocationWidgetProvider>}.
	 */
	protected abstract Class<? extends BaseUrLocationWidgetProvider> getWidgetProvider();
}
