package widget.map.com.urlocationmapwidget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;


/**
 * Extension of {@link android.widget.ImageButton} only with animation.
 *
 * @author Xinyue Zhao
 */
public final class AnimImageButton extends ImageButton {
	public AnimImageButton(Context context) {
		super(context);
		init(context);
	}

	public AnimImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AnimImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * Init.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	private void init(Context cxt) {
	}

	/**
	 * Click listener for {@link AnimImageButton} which enhance an animation when clicking.
	 *
	 * @author Xinyue Zhao
	 */
	public static abstract class OnAnimImageButtonClickedListener implements OnClickListener {
		/**
		 * Impl. Event what user clicks.
		 */
		public abstract void onClick();

		@Override
		public final void onClick(final View v) {
			v.setEnabled(false);
			final float initX = v.getScaleX();
			final float initY = v.getScaleY();
			AnimatorSet animatorSet = new AnimatorSet();
			animatorSet.playTogether(ObjectAnimator.ofFloat(v, "scaleX", initX, 0.5f, initX).setDuration(100),
					ObjectAnimator.ofFloat(v, "scaleY", initY, 0.5f, initY).setDuration(100));
			animatorSet.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					onClick();
					v.setEnabled(true);
				}
			});
			animatorSet.start();
		}
	}
}
