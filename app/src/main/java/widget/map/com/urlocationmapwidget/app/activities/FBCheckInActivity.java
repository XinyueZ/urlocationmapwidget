package widget.map.com.urlocationmapwidget.app.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chopping.net.TaskHelper;
import com.chopping.utils.Utils;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.maps.model.LatLng;

import widget.map.com.urlocationmapwidget.R;
import widget.map.com.urlocationmapwidget.app.App;
import widget.map.com.urlocationmapwidget.utils.Prefs;

/**
 * Show a dialog like activity before sharing on Facebook Inc.
 *
 * @author Xinyue Zhao
 */
public class FBCheckInActivity extends Activity {

	/**
	 * Layout.
	 */
	private static final int LAYOUT = R.layout.activity_fbcheck_in;
	/**
	 * The FB component to keep Android live-cycle better.
	 */
	private UiLifecycleHelper mUiLifecycleHelper;
	/**
	 * The activity-code for returning from auth-activity of FB-SDK.
	 */
	private static final int REAUTH_ACTIVITY_CODE = 100;
	/**
	 * Some content of information of checkIn.
	 */
	private TextView mContentTv;
	/**
	 * Info shows that there's no location-info.
	 */
	private View mNoLocationV;
	/**
	 * Permissions for this app-FB-usage.
	 */
	private static final List<String> PERMISSIONS = new ArrayList<String>() {
		{
			add("public_profile");
			add("publish_actions");
		}
	};
	/**
	 * Show user's picture on Facebook Inc.
	 */
	private ProfilePictureView mUserPicIv;
	/**
	 * FB-Session listener.
	 */
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			if (session != null && session.isOpened()) {
				makeUserInfoRequest(session);
			}
		}
	};

	/**
	 * Show single instance of {@link FBCheckInActivity}
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, FBCheckInActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		mContentTv = (TextView) findViewById(R.id.check_in_content_tv);
		mNoLocationV = findViewById(R.id.on_location_tv);
		mUserPicIv = (ProfilePictureView) findViewById(R.id.selection_profile_pic);
		mUserPicIv.setCropped(true);
		mUiLifecycleHelper = new UiLifecycleHelper(this, callback);
		mUiLifecycleHelper.onCreate(savedInstanceState);

		//Show a user name to confirm that FB connection has been established.
		Session session = Session.getActiveSession();
		if (session == null || !session.isOpened()) {
			Session.openActiveSession(this, true, PERMISSIONS, new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					if (state == SessionState.CLOSED_LOGIN_FAILED) {
						finish();
					} else {
						makeUserInfoRequest(session);
					}
				}
			});
		} else {
			makeUserInfoRequest(session);
		}
	}

	/**
	 * Cancel sharing location on the Facebook Inc.
	 *
	 * @param view
	 * 		No used.
	 */
	public void cancel(View view) {
		finish();
	}

	/**
	 * OK, confirm to share location on the Facebook Inc.
	 *
	 * @param view
	 * 		No used.
	 */
	public void confirm(View view) {
		//TODO share and checkIn on the Facebook Inc.
		Prefs prefs = Prefs.getInstance(getApplication());
		String latlng = prefs.getLastLocation();

		if (TextUtils.isEmpty(latlng)) {
			Utils.showLongToast(this, R.string.msg_refresh_plz);
			return;
		}
		String[] latlngs = latlng.split(",");
		//Do checkIn.
		Session session = Session.getActiveSession();
		makePostWallRequest(session, latlngs[0], latlngs[1], prefs.getLastLocationName());
	}

	/**
	 * Locating.
	 *
	 * @param view
	 * 		No used.
	 */
	public void locating(View view) {
		mNoLocationV.setVisibility(View.GONE);
		widget.map.com.urlocationmapwidget.utils.Utils.startOrRefreshLocating(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mUiLifecycleHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		mUiLifecycleHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
		super.onPause();
		mUiLifecycleHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mUiLifecycleHelper.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * A FB-Request to get user information.
	 *
	 * @param session
	 * 		A FB-session.
	 */
	private void makeUserInfoRequest(final Session session) {
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				// If the response is successful
				if (session == Session.getActiveSession()) {
					if (user != null) {
						String info = String.format(getString(R.string.lbl_confirm_check_in), user.getName());
						mContentTv.setVisibility(View.VISIBLE);
						mContentTv.setText(info);
						mUserPicIv.setProfileId(user.getId());

						Prefs prefs = Prefs.getInstance(getApplication());
						String latlng = prefs.getLastLocation();
						if (TextUtils.isEmpty(latlng)) {
							mNoLocationV.setVisibility(View.VISIBLE);
						}
					}
				}
			}
		});
		request.executeAsync();
	}

	/**
	 * Do checkIn on the Facebook Inc.
	 *
	 * @param session
	 * 		Current FB-session.
	 * @param lat
	 * 		The latitude.
	 * @param lng
	 * 		The longitude.
	 * @param locationName
	 * 		The name of current location.
	 */
	private void makePostWallRequest(final Session session, String lat, String lng, final String locationName) {
		LatLng ll = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
		StringRequest tinyReq = new StringRequest(com.android.volley.Request.Method.GET, App.TINY + Prefs.getInstance(
				getApplication()).getUrlPlace(ll), new com.android.volley.Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Request request = Request.newStatusUpdateRequest(session, response + locationName, new Callback() {
					@Override
					public void onCompleted(Response response) {
						if (response != null) {
							finish();
						}
					}
				});
				request.executeAsync();
			}
		}, new com.android.volley.Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
		TaskHelper.getRequestQueue().add(tinyReq);
	}


}
