package widget.map.com.urlocationmapwidget;

/**
 * The provider for this widget.
 *
 * @author Xinyue Zhao
 */
public final class UrLocationWidgetProvider extends BaseUrLocationWidgetProvider {
	/**
	 * Layout of the widget.
	 */
	private static final int LAYOUT_WIDGET = R.layout.widget_urlocation;

	@Override
	protected int getLayoutResId() {
		return LAYOUT_WIDGET;
	}
}
