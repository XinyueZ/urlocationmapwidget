package widget.map.com.urlocationmapwidget.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * List of {@link widget.map.com.urlocationmapwidget.data.GeoResult}.
 *
 * @author Xinyue Zhao
 */
public final class GeoResultList {
	@SerializedName("results")
	private List<GeoResult> mGeoResults;
	@SerializedName("status")
	private String mStatus;

	public GeoResultList(List<GeoResult> geoResults, String status) {
		mGeoResults = geoResults;
		mStatus = status;
	}

	public List<GeoResult> getGeoResults() {
		return mGeoResults;
	}

	public void setGeoResults(List<GeoResult> geoResults) {
		mGeoResults = geoResults;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		mStatus = status;
	}
}
