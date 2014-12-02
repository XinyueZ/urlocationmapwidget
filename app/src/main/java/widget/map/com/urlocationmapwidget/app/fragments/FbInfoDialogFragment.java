package widget.map.com.urlocationmapwidget.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import widget.map.com.urlocationmapwidget.R;
import widget.map.com.urlocationmapwidget.utils.Prefs;


/**
 * Dialog to show some information about push
 *
 * @author Xinyue Zhao
 */
public final class FbInfoDialogFragment extends DialogFragment implements OnClickListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.dialog_fragment_fb_info;


	public static FbInfoDialogFragment newInstance(Context context) {
		return (FbInfoDialogFragment) FbInfoDialogFragment.instantiate(context, FbInfoDialogFragment.class.getName());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(false);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_Dialog);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.close_confirm_btn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Prefs prefs = Prefs.getInstance(getActivity().getApplication());
		prefs.setHasShownFBInfo(true);
		dismiss();
	}
}
