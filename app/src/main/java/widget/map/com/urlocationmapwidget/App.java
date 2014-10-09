package widget.map.com.urlocationmapwidget;

import android.app.Application;

import com.chopping.net.TaskHelper;

/**
 * Application object.
 *
 *@author Xinyue Zhao
 */
public final class App extends Application {
	@Override
	public void onCreate() {
		TaskHelper.init(getApplicationContext());
	}

}
