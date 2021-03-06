package widget.map.com.urlocationmapwidget.utils;

import java.util.Locale;

import android.content.Context;
import android.location.Location;

import com.chopping.application.BasicPrefs;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

/**
 * Application's properties.
 *
 * @author Xinyue Zhao
 */
public final class Prefs extends BasicPrefs {
	/**
	 * Map by Google Inc.
	 */
	public static final int GOOGLE_MAP = 0;
	/**
	 * Map by Baidu Inc.
	 */
	public static final int BAIDU_MAP = 1;
	/**
	 * Priority 0: accuracy.
	 */
	public static final int PRIORITY_HIGH_ACCURACY = 0;
	/**
	 * Priority 1: balanced power.
	 */
	public static final int PRIORITY_BALANCED_POWER_ACCURACY = 1;
	/**
	 * Priority 2: low accuracy.
	 */
	public static final int PRIORITY_LOW_POWER = 2;
	/**
	 * Max zoom-level.
	 */
	private static final int MAX_ZOOM_LEVEL = 19;
	/**
	 * Min zoom-level.
	 */
	private static final int MIN_ZOOM_LEVEL = 10;
	/**
	 * Max update interval.
	 */
	public static final int MAX_INTERVAL = 30;
	/**
	 * Min update interval.
	 */
	public static final int MIN_INTERVAL = 3;
	/**
	 * Default zoom.
	 */
	private static final int DEFAULT_ZOOM_LEVEL = MAX_ZOOM_LEVEL;
	/**
	 * Default interval.
	 */
	private static final int DEFAULT_INTERVAL = 20;
	/**
	 * Impl singleton pattern.
	 */
	private static Prefs sInstance;
	/**
	 * Url to the Google Inc. map.
	 */
	private static final String KEY_GOOGLE_MAP = "google_map";
	/**
	 * Url to the Baidu Inc. map.
	 */
	private static final String KEY_BAIDU_MAP = "baidu_map";
	/**
	 * Status of current selected map type, {@code 0=google map}, {@code 0=baidu map}.
	 */
	private static final String KEY_CURRENT_MAP = "current_map";
	/**
	 * Flag, whether location updating or not.
	 */
	private static final String KEY_LOCATION_UPDATES_ON = "location_update_on";
	/**
	 * Current zoom-level.
	 */
	private static final String KEY_ZOOM_LEVEL = "current_zoom_level";
	/**
	 * Location update interval.
	 */
	private static final String KEY_INTERVAL = "update_interval";
	/**
	 * Flag, whether app has been inited or not.
	 */
	private static final String KEY_INIT = "app_init";
	/**
	 * Locating priority.
	 */
	private static final String KEY_PRIORITY = "priority";
	/**
	 * Storage. Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 * {@code true} if EULA has been shown and agreed.
	 */
	private static final String KEY_EULA_SHOWN = "key_eula_shown";
	/**
	 * Last location data that has been caught.
	 */
	private static final String KEY_LAST_LATLNG = "key_last_latlng";
	/**
	 * Last location name that has been caught.
	 */
	private static final String KEY_LAST_LOCATION_NAME = "key_last_location_name";
	/**
	 * Url for geocode of Google Inc.
	 */
	private static final String KEY_GOOGLE_GEOCODE="google_geocode";
	/**
	 * Url for geocode of Baidu Inc.
	 */
	private static final String KEY_BAIDU_GEOCODE = "baidu_geocode";
	/**
	 * Url for place-api of Google Inc.
	 */
	private static final String KEY_GOOGLE_PLACE="google_place";
	/**
	 * Url for place-api of Baidu Inc.
	 */
	private static final String KEY_BAIDU_PLACE = "baidu_place";
	/**
	 * Did the app show the FB-info.
	 */
	private static final String KEY_FB_INFO_SHOWN = "fb_info_shown";

	/**
	 * Created a DeviceData storage.
	 *
	 * @param context
	 * 		A context object.
	 */
	private Prefs(Context context) {
		super(context);
	}

	/**
	 * Get instance of  {@link  Prefs} singleton.
	 *
	 * @param context
	 * 		{@link android.app.Application}.
	 *
	 * @return The {@link  Prefs} singleton.
	 */
	public static Prefs getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new Prefs(context);
		}
		return sInstance;
	}

	/**
	 * Set current selected map type.
	 *
	 * @param currentMap
	 * 		{@link #GOOGLE_MAP} or {@link #BAIDU_MAP}.
	 */
	public void setCurrentMap(int currentMap) {
		setInt(KEY_CURRENT_MAP, currentMap);
	}

	/**
	 * Get current selected map type.
	 *
	 * @return {@link #GOOGLE_MAP} or {@link #BAIDU_MAP}.
	 */
	public int getCurrentMap() {
		return getInt(KEY_CURRENT_MAP, GOOGLE_MAP);
	}

	/**
	 * Set whether updating location or not.
	 *
	 * @param on
	 * 		{@code true} if updating.
	 */
	public void setLocationUpdating(boolean on) {
		setBoolean(KEY_LOCATION_UPDATES_ON, on);
	}

	/**
	 * Get whether updating location or not.
	 *
	 * @return {@code true} if updating.
	 */
	public boolean isLocationUpdating() {
		return getBoolean(KEY_LOCATION_UPDATES_ON, false);
	}

	/**
	 * Url to the Google Inc. map.
	 * <p/>
	 * <a href="https://developers.google.com/maps/documentation/staticmaps/?hl=en">API</a>
	 * <p/>
	 * Example:
	 * <p/>
	 * {@code https://maps.googleapis.com/maps/api/staticmap?center=Brooklyn+Bridge,New+York,NY&zoom=13&size=600x300&maptype=roadmap
	 * &markers=color:blue%7Clabel:S%7C40.702147,-74.015794&markers=color:green%7Clabel:G%7C40.711614,-74.012318
	 * &markers=color:red%7Clabel:C%7C40.718217,-73.998284}
	 */
	private String getUrlGoogle() {
		return getString(KEY_GOOGLE_MAP, null);
	}

	/**
	 * Url to the Baidu Inc. map.
	 * <p/>
	 * <a href="http://developer.baidu.com/map/index.php?title=static/static-1">API</a>
	 * <p/>
	 * Example:
	 * <p/>
	 * {@code http://api.map.baidu.com/staticimage?center=116.403874,39.914888&width=768&height=1000&zoom=19&markers=116.403874,39.914888&markerStyles=m,A}
	 */
	private String getUrlBaidu() {
		return getString(KEY_BAIDU_MAP, null);
	}

	/**
	 * Get map url with {@code zoom=}{@link #DEFAULT_ZOOM_LEVEL}.
	 *
	 * @param latlng
	 * 		{@link com.google.android.gms.maps.model.LatLng} for center.
	 * 		<p/>
	 * 		<b>The API call for getting map center is different by different provider. Google: latitude,longitude, Baidu:
	 * 		longitude, latitude</b>
	 * @param width
	 * 		width for the map.
	 * @param height
	 * 		height for the map.
	 *
	 * @return The final url to the static map.
	 */
	public String getMap(LatLng latlng, int width, int height) {
		return getUrlMap(latlng, width, height, DEFAULT_ZOOM_LEVEL);
	}

	/**
	 * Get API to get map of different providers.
	 *
	 * @param latlng
	 * 		{@link com.google.android.gms.maps.model.LatLng} for center.
	 * 		<p/>
	 * 		<b>The API call for getting map center is different by different provider. Google: latitude,longitude, Baidu:
	 * 		longitude, latitude</b>
	 * @param width
	 * 		The width for the map.
	 * @param height
	 * 		The height for the map.
	 * @param zoom
	 * 		The zoom level for the map.
	 *
	 * @return The final url to the static map.
	 */
	public String getUrlMap(LatLng latlng, int width, int height, int zoom) {
		if (getCurrentMap() == GOOGLE_MAP) {
			return String.format(getUrlGoogle(), latlng.latitude + "", latlng.longitude + "", width + "", height + "",
					zoom, latlng.latitude + "", latlng.longitude + "");
		} else {
			return String.format(getUrlBaidu(), latlng.longitude + "", latlng.latitude + "", width + "", height + "",
					zoom, latlng.longitude + "", latlng.latitude + "");
		}
	}

	/**
	 * Set current map zoom-level.
	 *
	 * @param zoomLevel
	 * 		The zoom-level is ({@link #MIN_ZOOM_LEVEL}-{@link #MAX_ZOOM_LEVEL}).
	 * 		<p/>
	 * 		When larger or less than the limit value, the limit value should be accepted.
	 */
	public void setZoomLevel(int zoomLevel) {
		if (zoomLevel > MAX_ZOOM_LEVEL) {
			zoomLevel = MAX_ZOOM_LEVEL;
		} else if (zoomLevel < MIN_ZOOM_LEVEL) {
			zoomLevel = MIN_ZOOM_LEVEL;
		}
		setInt(KEY_ZOOM_LEVEL, zoomLevel);
	}

	/**
	 * Set map update interval in minute.
	 *
	 * @param interval
	 * 		The interval is ({@link #MIN_INTERVAL}-{@link #MAX_INTERVAL})minutes.
	 */
	public void setInterval(int interval) {
		setInt(KEY_INTERVAL, interval);
	}

	/**
	 * Get map current zoom-level
	 *
	 * @return The zoom-level is ({@link #MIN_ZOOM_LEVEL}-{@link #MAX_ZOOM_LEVEL}). Default is {@link
	 * #DEFAULT_ZOOM_LEVEL}.
	 */
	public int getZoomLevel() {
		return getInt(KEY_ZOOM_LEVEL, DEFAULT_ZOOM_LEVEL);
	}

	/**
	 * Get map update interval.
	 *
	 * @return The interval ({@link #MIN_INTERVAL}-{@link #MAX_INTERVAL})minutes. Default is {@link #DEFAULT_INTERVAL}.
	 */
	public int getInterval() {
		return getInt(KEY_INTERVAL, DEFAULT_INTERVAL);
	}

	/**
	 * Set whether system has been initialized or not, configuration to load etc.
	 *
	 * @param init
	 * 		{@code true} if initialized.
	 */
	public void setInit(boolean init) {
		setBoolean(KEY_INIT, init);
	}

	/**
	 * Get whether system has been initialized or not, configuration to load etc.
	 *
	 * @return {@code true} if initialized.
	 */
	public boolean isInit() {
		return getBoolean(KEY_INIT, false);
	}

	/**
	 * Set selection of locating priority.
	 *
	 * @param priority
	 * 		{@link #PRIORITY_HIGH_ACCURACY}, {@link #PRIORITY_BALANCED_POWER_ACCURACY}, {@link #PRIORITY_LOW_POWER}.
	 */
	public void setPrioritySelection(int priority) {
		setInt(KEY_PRIORITY, priority);
	}

	/**
	 * Get locating priority.
	 *
	 * @return priority  {@link com.google.android.gms.location.LocationRequest#PRIORITY_HIGH_ACCURACY}, {@link
	 * com.google.android.gms.location.LocationRequest#PRIORITY_BALANCED_POWER_ACCURACY}, {@link
	 * com.google.android.gms.location.LocationRequest#PRIORITY_LOW_POWER}.
	 */
	public int getPriority() {
		switch (getInt(KEY_PRIORITY, PRIORITY_BALANCED_POWER_ACCURACY)) {
		case PRIORITY_HIGH_ACCURACY:
			return LocationRequest.PRIORITY_HIGH_ACCURACY;
		case PRIORITY_LOW_POWER:
			return LocationRequest.PRIORITY_LOW_POWER;
		default:
			return LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
		}
	}

	/**
	 * Get selection of locating priority.
	 *
	 * @return selection of priority.
	 */
	public int getPrioritySelection() {
		return getInt(KEY_PRIORITY, PRIORITY_BALANCED_POWER_ACCURACY);
	}


	/**
	 * Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @return {@code true} if EULA has been shown and agreed.
	 */
	public boolean isEULAOnceConfirmed() {
		return getBoolean(KEY_EULA_SHOWN, false);
	}

	/**
	 * Set whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @param isConfirmed
	 * 		{@code true} if EULA has been shown and agreed.
	 */
	public void setEULAOnceConfirmed(boolean isConfirmed) {
		setBoolean(KEY_EULA_SHOWN, isConfirmed);
	}

	/**
	 * Set last location data that has been caught.
	 *
	 * @param l
	 * 		Last {@link android.location.Location}.
	 */
	public   void setLastLocation(Location l) {
		setString(KEY_LAST_LATLNG, String.format("%s,%s", l.getLatitude() + "", l.getLongitude() + ""));
	}

	/**
	 * Get last location data that has been caught.
	 *
	 * @return Last location in format: Latitude,Longitude.
	 */
	public   String getLastLocation() {
		return getString(KEY_LAST_LATLNG, null);
	}

	/**
	 * Set last location name that has been caught.
	 *
	 * @param name
	 * 		Location name, it might be null to replace the last one.
	 */
	public   void setLastLocationName(String name) {
		setString(KEY_LAST_LOCATION_NAME, name);
	}

	/**
	 * Get last location name that has been caught.
	 *
	 * @return Location name, it might be null to replace the last one.
	 */
	public   String getLastLocationName() {
		return getString(KEY_LAST_LOCATION_NAME, null);
	}

	/**
	 * Get API of Google Inc. geocode.
	 * @return Url of API.
	 */
	private String getUrlGoogleGeocode() {
		return getString(KEY_GOOGLE_GEOCODE, null);
	}

	/**
	 * Get API of Baidu Inc. geocode.
	 * @return Url of API.
	 */
	private String getUrlBaiduGeocode() {
		return getString(KEY_BAIDU_GEOCODE, null);
	}

	/**
	 * Get API of geocode of different providers.
	 * @param latlng  Latitude and Longitude.
	 * @return Url of API.
	 */
	public String getUrlGeocode(LatLng latlng) {
		String lang = Locale.getDefault().getDisplayLanguage();
		if (getCurrentMap() == GOOGLE_MAP) {
			return String.format(getUrlGoogleGeocode(), latlng.latitude + "", latlng.longitude + "", lang);
		} else {
			return String.format(getUrlBaiduGeocode(), latlng.latitude + "", latlng.longitude + "", lang);
		}
	}


	/**
	 * Get API of Google Inc. place.
	 * @return Url of API.
	 */
	private String getUrlGooglePlace() {
		return getString(KEY_GOOGLE_PLACE, null);
	}

	/**
	 * Get API of Baidu Inc. place.
	 * @return Url of API.
	 */
	private String getUrlBaiduPlace() {
		return getString(KEY_BAIDU_PLACE, null);
	}

	/**
	 * Get API of place of different providers.
	 * @param latlng  Latitude and Longitude.
	 * @return Url of API.
	 */
	public String getUrlPlace(LatLng latlng) {
		if (getCurrentMap() == GOOGLE_MAP) {
			return String.format(getUrlGooglePlace(), latlng.latitude + "", latlng.longitude + ""  );
		} else {
			return String.format(getUrlBaiduPlace(), latlng.latitude + "", latlng.longitude + ""  );
		}
	}

	/**
	 * Set shown FB-info or not.
	 * @param shown {@code true} if has shown.
	 */
	public void setHasShownFBInfo(boolean shown) {
		setBoolean(KEY_FB_INFO_SHOWN, shown);
	}

	/**
	 * Shown FB-info or not?
	 * @return {@code true} if has shown.
	 */
	public boolean hasShownFBInfo() {
		return getBoolean(KEY_FB_INFO_SHOWN, false);
	}
}
