package widget.map.com.urlocationmapwidget.data;

import com.google.gson.annotations.SerializedName;

/**
 * Result of a single geocode.
 *
 * @author Xinyue Zhao
 */
public final class GeoResult {
	@SerializedName("formatted_address")
	private String mAddress;

	public GeoResult(String address) {
		mAddress = address;
	}

	public String getAddress() {
		return mAddress;
	}

	public void setAddress(String address) {
		mAddress = address;
	}
}
