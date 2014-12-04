package widget.map.com.urlocationmapwidget.app.receivers;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import widget.map.com.urlocationmapwidget.R;
import widget.map.com.urlocationmapwidget.app.services.UrLocationSmallWidgetService;
import widget.map.com.urlocationmapwidget.app.services.UrLocationWidgetService;
import widget.map.com.urlocationmapwidget.appwidgets.UrLocationSmallWidgetProvider;
import widget.map.com.urlocationmapwidget.appwidgets.UrLocationWidgetProvider;

/**
 * Handling device boot by {@link android.content.BroadcastReceiver}.
 *
 * @author Xinyue Zhao
 */
public final class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, UrLocationWidgetService.class));
		context.startService(new Intent(context, UrLocationSmallWidgetService.class));

		updateWidget(context,UrLocationWidgetProvider.class,  R.layout.widget_urlocation);
		updateWidget(context,UrLocationSmallWidgetProvider.class,  R.layout.small_widget_urlocation);
	}

	/**
	 * Refresh widget and it's UI.
	 * @param cxt {@link android.content.Context}.
	 * @param cls {@link java.lang.Class}.
	 * @param layoutResId layout of the widget.
	 */
	private void updateWidget(Context cxt, Class<?> cls, int layoutResId) {
		RemoteViews views = new RemoteViews(cxt.getPackageName(), layoutResId);
		ComponentName thisWidget = new ComponentName(cxt, cls);
		AppWidgetManager.getInstance(cxt).updateAppWidget(thisWidget, views);
	}
}

