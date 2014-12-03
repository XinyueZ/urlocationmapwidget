package widget.map.com.urlocationmapwidget.app.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chopping.application.LL;
import com.chopping.net.TaskHelper;
import com.chopping.utils.Utils;
import com.facebook.AppEventsLogger;
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
	 * Some content of information of checkIn.
	 */
	private TextView mContentTv;
	/**
	 * Info shows that there's no location-info.
	 */
	private View mNoLocationV;
	/**
	 * Permissions for user-info of this app-FB-usage.
	 */
	private static final List<String> PROFILE_PERMISSIONS = new ArrayList<String>() {
		{
			add("public_profile");
		}
	};
	/**
	 * Permissions for publish of this app-FB-usage.
	 */
	private static final List<String> PUBLISH_PERMISSIONS = new ArrayList<String>() {
		{
			add("publish_actions");
		}
	};
	/**
	 * Show user's picture on Facebook Inc.
	 */
	private ProfilePictureView mUserPicIv;
	/**
	 * A switch for sign-in-out.
	 */
	private SwitchCompat mSignSw;
	/**
	 * FB-Session listener.
	 */
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			if (session != null && session.isOpened()) {
				switch (mStatus) {
				case Login:
					makeUserInfoRequest(session);mStatus = null;
				break;
				case Publish:
					makePostWallRequest(session); mStatus = null;
					break;
				}
			}
			LL.d(session.toString());
		}
	};
	/**
	 * Progress indicator.
	 */
	private ProgressDialog mPb;
	/**
	 * A view that can be clicked to send info on Facebook Inc.
	 */
	private View mConfirmV;

	private enum Status {
		Login, Publish
	}

	private Status mStatus;
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
		mConfirmV = findViewById(R.id.confirm_btn);

		fbLogin();
		mSignSw = (SwitchCompat) findViewById(R.id.sign_sw);
		mSignSw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!isChecked) {
					fbLogout();
				} else {
					fbLogin();
				}
			}
		});
	}

	/**
	 * Logout.
	 */
	private void fbLogout() {
		Session session = Session.getActiveSession();
		if (session != null) {
			session.closeAndClearTokenInformation();
		}
		Session.setActiveSession(null);
		mConfirmV.setEnabled(false);
	}

	/**
	 * Login. Show a user name to confirm that FB connection has been established.
	 */
	private void fbLogin() {
		mStatus = Status.Login;
		Session session = Session.getActiveSession();
		if (session == null || !session.isOpened()) {
			Session.openActiveSession(this, true, PROFILE_PERMISSIONS, new Session.StatusCallback() {
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
		mStatus = Status.Publish;
		Session session = Session.getActiveSession();
		if (session != null) {
			if (hasPublishPermission()) {
				makePostWallRequest(session);
			} else if (session.isOpened()) {
				session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PUBLISH_PERMISSIONS));
			}
		}
	}

	/**
	 * Publish location to Facebook Inc.
	 * @param session Current FB-session.
	 */
	private void makePostWallRequest(Session session) {
		Prefs prefs = Prefs.getInstance(getApplication());
		String latlng = prefs.getLastLocation();
		if (TextUtils.isEmpty(latlng)) {
			Utils.showLongToast(this, R.string.msg_refresh_plz);
		}  else {
			String[] latlngs = latlng.split(",");
			makePostWallRequest(session, latlngs[0], latlngs[1], prefs.getLastLocationName());
//			makePostWallRequest(session, 37.42291810 + "", -122.08542120 + "", "1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA");
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mUiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
	}


	/**
	 * To know whether the permission for publishing available or not.
	 *
	 * @return {@code true} if you can publish.
	 */
	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null && session.getPermissions().contains("publish_actions");
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
		AppEventsLogger.activateApp(this);
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
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mUiLifecycleHelper.onDestroy();
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
				// If the response is successful.
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
						mSignSw.setVisibility(View.VISIBLE);
						mSignSw.setEnabled(true);
						mConfirmV.setEnabled(true);
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
		mPb = ProgressDialog.show(this, null, getString(R.string.msg_fb_post));
		StringRequest tinyReq = new StringRequest(com.android.volley.Request.Method.GET, App.TINY + Prefs.getInstance(
				getApplication()).getUrlPlace(ll), new com.android.volley.Response.Listener<String>() {
			@Override
			public void onResponse(String link) {
				if (session != null && session.isOpened()) {
					String fmt = getString(R.string.lbl_here);
					String desc = String.format(fmt, locationName);
					Request request = Request.newStatusUpdateRequest(session, desc, new Callback() {
						@Override
						public void onCompleted(Response response) {
							if (response != null) {
								if (mPb != null && mPb.isShowing()) {
									mPb.dismiss();
								}
								if (response.getError() != null) {
									Utils.showLongToast(getApplication(), R.string.msg_fb_post_error);
								}
								finish();
							}
						}
					});
					Bundle postParams = new Bundle();
					postParams.putString("description", desc);
					postParams.putString("link", link);
					request.setParameters(postParams);
					request.executeAsync();
				}
			}
		}, new com.android.volley.Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				//Tiny-Url error. We do not close view. User can still post.
				Utils.showLongToast(getApplication(), R.string.msg_fb_post_error);
			}
		});
		TaskHelper.getRequestQueue().add(tinyReq);
	}
}
