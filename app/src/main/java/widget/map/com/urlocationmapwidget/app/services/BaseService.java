package widget.map.com.urlocationmapwidget.app.services;

import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.chopping.net.GsonRequestTask;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import de.greenrobot.event.EventBus;
import widget.map.com.urlocationmapwidget.R;
import widget.map.com.urlocationmapwidget.appwidgets.BaseUrLocationWidgetProvider;
import widget.map.com.urlocationmapwidget.data.GeoResult;
import widget.map.com.urlocationmapwidget.data.GeoResultList;
import widget.map.com.urlocationmapwidget.utils.Prefs;
import widget.map.com.urlocationmapwidget.utils.Utils;

/**
 * Abstract basic {@link android.app.Service}.
 *
 * @author Xinyue Zhao
 */
public abstract class BaseService extends Service implements ConnectionCallbacks, OnConnectionFailedListener,
		LocationListener, ImageListener {
	public static final String ACTION_UPDATE = "widget.map.com.urlocationmapwidget.action.UPDATE";
	/**
	 * OK status of geocode.
	 */
	private static final String STATUS_OK = "OK";
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

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link GeoResultList}.
	 *
	 * @param list
	 * 		Event {@link GeoResultList}.
	 */
	public void onEvent(GeoResultList list) {
		if(TextUtils.equals(list.getStatus(), STATUS_OK) ) {
			GeoResult gr = list.getGeoResults().get(0);
			Prefs prefs = Prefs.getInstance(getApplication());
			String adr = gr.getAddress();
			if (!TextUtils.isEmpty(adr)) {
				RemoteViews views = new RemoteViews(getApplication().getPackageName(),
						getLayoutResId());
				views.setTextViewText(R.id.location_name_tv, adr);
				views.setTextViewText(R.id.last_update_tv,
						Utils.convertTimestamps2DateString(getApplication(), System.currentTimeMillis()));
				ComponentName thisWidget = new ComponentName(getApplication(), getWidgetProvider());
				AppWidgetManager.getInstance(getApplication()).updateAppWidget(thisWidget, views);
				prefs.setLastLocationName(adr);
			} else {
				prefs.setLastLocationName("");
			}
		}
	}
	//------------------------------------------------

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
		EventBus.getDefault().register(this);
		registerReceiver(mReceiver, mIntentFilter);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
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
		try {
			LocationClient lc = getLocationClient();
			LocationRequest lr = getLocationRequest();

			Location location = mLocationClient.getLastLocation();
			if (location != null) {
				setAddress(location);
			}

			if (lc != null && lc.isConnected() && lc != null && lr != null) {
				lc.requestLocationUpdates(lr, this);
			}
		} catch (IllegalStateException e) {
			//Ignore the case.
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
		LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
		String url = Prefs.getInstance(getApplication()).getUrlGeocode(ll);
		new GsonRequestTask<GeoResultList>(getApplication(), Method.GET, url, GeoResultList.class).execute();
	}




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



	/**
	 * Class info of widget's provider.
	 * @return {@link Class<? extends  widget.map.com.urlocationmapwidget.appwidgets.BaseUrLocationWidgetProvider >}.
	 */
	protected abstract Class<? extends BaseUrLocationWidgetProvider> getWidgetProvider();
}
