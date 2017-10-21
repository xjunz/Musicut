package meta.z.musicut.util;
import android.animation.*;
import android.view.animation.*;

import android.content.Context;
import android.util.ArrayMap;
import android.widget.ImageView;
import java.util.ArrayList;
import meta.z.musicut.MusicutApplication;

public class AnimUtils
{
	private static Context context=MusicutApplication.context;
	public static void windmillTrick(final ImageView v,final int image,int rotation){
	v.animate().rotation(rotation).setDuration(300)
	.setInterpolator(getLinearOutSlowInInterpolator())
	.start();
	v.postDelayed(new Runnable(){
				@Override
				public void run()
				{
					v.setImageResource(image);
				}
			}, 50);
		}
	
	
	
		
	private static Interpolator fosi_interpolator;
	public static Interpolator getFastOutSlowInInterpolator(){
		if(fosi_interpolator==null){
			return AnimationUtils.loadInterpolator(context,android.R.interpolator.fast_out_slow_in);
		}
		return fosi_interpolator;
	}
	private static Interpolator losi_interpolator;
	public static Interpolator getLinearOutSlowInInterpolator(){
		if(losi_interpolator==null){
			return AnimationUtils.loadInterpolator(context,android.R.interpolator.linear_out_slow_in);
		}
		return losi_interpolator;
	}
	
	public static Interpolator loadInterpolator(int res){
		return AnimationUtils.loadInterpolator(context,res);
	}


    /**
     * https://halfthought.wordpress.com/2014/11/07/reveal-transition/
     * <p/>
     * Interrupting Activity transitions can yield an OperationNotSupportedException when the
     * transition tries to pause the animator. Yikes! We can fix this by wrapping the Animator:
     */
    public static class NoPauseAnimator extends Animator {
        private final Animator mAnimator;
        private final ArrayMap<AnimatorListener, AnimatorListener> mListeners = new ArrayMap<>();

        public NoPauseAnimator(Animator animator) {
            mAnimator = animator;
        }

        @Override
        public void addListener(AnimatorListener listener) {
            AnimatorListener wrapper = new AnimatorListenerWrapper(this, listener);
            if (!mListeners.containsKey(listener)) {
                mListeners.put(listener, wrapper);
                mAnimator.addListener(wrapper);
            }
        }

        @Override
        public void cancel() {
            mAnimator.cancel();
        }

        @Override
        public void end() {
            mAnimator.end();
        }

        @Override
        public long getDuration() {
            return mAnimator.getDuration();
        }

        @Override
        public TimeInterpolator getInterpolator() {
            return mAnimator.getInterpolator();
        }

        @Override
        public void setInterpolator(TimeInterpolator timeInterpolator) {
            mAnimator.setInterpolator(timeInterpolator);
        }

        @Override
        public ArrayList<AnimatorListener> getListeners() {
            return new ArrayList<>(mListeners.keySet());
        }

        @Override
        public long getStartDelay() {
            return mAnimator.getStartDelay();
        }

        @Override
        public void setStartDelay(long delayMS) {
            mAnimator.setStartDelay(delayMS);
        }

        @Override
        public boolean isPaused() {
            return mAnimator.isPaused();
        }

        @Override
        public boolean isRunning() {
            return mAnimator.isRunning();
        }

        @Override
        public boolean isStarted() {
            return mAnimator.isStarted();
        }

        /* We don't want to override pause or resume methods because we don't want them
         * to affect mAnimator.
        public void pause();

        public void resume();

        public void addPauseListener(AnimatorPauseListener listener);

        public void removePauseListener(AnimatorPauseListener listener);
        */

        @Override
        public void removeAllListeners() {
            mListeners.clear();
            mAnimator.removeAllListeners();
        }

        @Override
        public void removeListener(AnimatorListener listener) {
            AnimatorListener wrapper = mListeners.get(listener);
            if (wrapper != null) {
                mListeners.remove(listener);
                mAnimator.removeListener(wrapper);
            }
        }

        @Override
        public Animator setDuration(long durationMS) {
            mAnimator.setDuration(durationMS);
            return this;
        }

        @Override
        public void setTarget(Object target) {
            mAnimator.setTarget(target);
        }

        @Override
        public void setupEndValues() {
            mAnimator.setupEndValues();
        }

        @Override
        public void setupStartValues() {
            mAnimator.setupStartValues();
        }

        @Override
        public void start() {
            mAnimator.start();
        }
    }

    private static class AnimatorListenerWrapper implements Animator.AnimatorListener {
        private final Animator mAnimator;
        private final Animator.AnimatorListener mListener;

        AnimatorListenerWrapper(Animator animator, Animator.AnimatorListener listener) {
            mAnimator = animator;
            mListener = listener;
        }

        @Override
        public void onAnimationStart(Animator animator) {
            mListener.onAnimationStart(mAnimator);
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mListener.onAnimationEnd(mAnimator);
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            mListener.onAnimationCancel(mAnimator);
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
            mListener.onAnimationRepeat(mAnimator);
        }
    }

}
