package widget.map.com.urlocationmapwidget.appwidgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import widget.map.com.urlocationmapwidget.R;

/**
 * The provider for this widget.
 *
 * @author Xinyue Zhao
 */
public final class UrLocationSmallWidgetProvider extends BaseUrLocationWidgetProvider {
	/**
	 * Layout of the widget.
	 */
	private static final int LAYOUT_WIDGET = R.layout.small_widget_urlocation;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		ComponentName thisWidget = new ComponentName(context, getClass());
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		RemoteViews views;
		for (int i = 0; i < allWidgetIds.length; i++) {
			views = new RemoteViews(context.getPackageName(), getLayoutResId());
			views.setOnClickPendingIntent(R.id.buttons_ll, buildViewClickIntent(context, ACTION_CLICK_MAP));
			appWidgetManager.updateAppWidget(thisWidget, views);
		}
	}


	@Override
	protected int getLayoutResId() {
		return LAYOUT_WIDGET;
	}
}
