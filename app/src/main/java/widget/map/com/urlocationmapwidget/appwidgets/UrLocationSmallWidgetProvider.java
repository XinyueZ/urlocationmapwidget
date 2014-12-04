package widget.map.com.urlocationmapwidget.appwidgets;

import android.appwidget.AppWidgetManager;
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
	public static final int LAYOUT_WIDGET = R.layout.small_widget_urlocation;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		doUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	protected void doMoreUpdate(Context context, RemoteViews parent) {
		parent.setOnClickPendingIntent(R.id.buttons_ll, buildViewClickIntent(context, ACTION_CLICK_MAP));
	}

	@Override
	protected int getLayoutResId() {
		return LAYOUT_WIDGET;
	}
}
