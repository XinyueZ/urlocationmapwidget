package widget.map.com.urlocationmapwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;

/**
 * The provider for this widget.
 *
 * @author Xinyue Zhao
 */
public final class UrLocationWidgetProvider extends AppWidgetProvider {
	private static final String ACTION_CLICK = "ACTION_CLICK";
	public static final String API_GOOGLE = "http://maps.google.com/maps/api/staticmap?center=%s,%s&size=%s&format=png&sensor=true&zoom=%s&maptype=roadmap&markers=color:blue|label:%s|%s,%s";
	public static final String API_BAIDU = "http://api.map.baidu.com/staticimage?center=116.403874,39.914888&width=300&height=200&zoom=11";
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// Get all ids
		ComponentName thisWidget = new ComponentName(context,
				UrLocationWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
//		for (int widgetId : allWidgetIds) {
//			// create some random data
//			int number = (new Random().nextInt(100));
//
//			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
//					R.layout.widget_urlocation);
//			Log.w("WidgetExample", String.valueOf(number));
//
//			// Register an onClickListener
//			Intent intent = new Intent(context, UrLocationWidgetProvider.class);
//
//			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//
//			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//					0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//			remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
//			appWidgetManager.updateAppWidget(widgetId, remoteViews);
//		}
	}
}
