package widget.map.com.urlocationmapwidget;

import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.google.android.gms.common.ConnectionResult;

/**
 * Service background(main thread) to provide current location of user for widget.
 *
 * @author Xinyue Zhao
 */
public final class UrLocationSmallWidgetService extends BaseService {
	/**
	 * Layout of the widget.
	 */
	private static final int LAYOUT_WIDGET = R.layout.small_widget_urlocation;

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			Prefs prefs = Prefs.getInstance(getApplication());
			prefs.setLastLocation(location);
			setAddress(location);
		}
	}


	@Override
	protected int getLayoutResId() {
		return LAYOUT_WIDGET;
	}

	@Override
	protected  Class<? extends BaseUrLocationWidgetProvider>  getWidgetProvider() {
		return UrLocationSmallWidgetProvider.class;
	}

	/**
	 * Constructor of {@link widget.map.com.urlocationmapwidget.UrLocationSmallWidgetService}, no usage.
	 */
	public UrLocationSmallWidgetService() {
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
