package meta.z.musicut.transition;

import android.animation.*;
import android.transition.*;
import android.view.*;
import android.content.Context;
import android.util.AttributeSet;
import meta.z.musicut.util.UiUtils;
import android.content.res.TypedArray;
import meta.z.musicut.R;

public class ElementCircularReveal extends Transition
{

	private static final String PROP_SCREEN_LOCATION="musicut:elementCircularReveal:screenLocation";
	private int centerOnId;

	public ElementCircularReveal(int centerOnId)
	{
        this.centerOnId = centerOnId;
	}

	public ElementCircularReveal(Context context,AttributeSet attrs){
		TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.ElementCircularReveal);
		centerOnId=ta.getResourceId(R.styleable.ElementCircularReveal_centerOn,View.NO_ID);
		if(centerOnId==View.NO_ID){
			throw new IllegalArgumentException("请指定android:centerOn属性");
		}
		ta.recycle();
	}
	
	@Override
	public void captureStartValues(TransitionValues transitionValues)
	{
		int[] loc=new int[2];
		transitionValues.view.getLocationOnScreen(loc);
		transitionValues.values.put(PROP_SCREEN_LOCATION, loc);
	}

	@Override
	public void captureEndValues(TransitionValues transitionValues)
	{
		int[] loc=new int[2];
		transitionValues.view.getLocationOnScreen(loc);
		transitionValues.values.put(PROP_SCREEN_LOCATION, loc);
	}



	@Override
	public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues)
	{
		if (startValues == null || endValues == null)
		{return null;}

		AnimatorSet set=new AnimatorSet();

		int[] startLoc=(int[]) startValues.values.get(PROP_SCREEN_LOCATION);
		int[] endLoc=(int[]) endValues.values.get(PROP_SCREEN_LOCATION);
		ObjectAnimator transAnimator=ObjectAnimator.ofFloat(endValues.view, View.
															TRANSLATION_X,
															View.TRANSLATION_Y, 
															getPathMotion().getPath(startLoc[0], 
															                        startLoc[1],
																					endLoc[0], 
																					endLoc[1]));
		View center=endValues.view.findViewById(centerOnId);
        boolean revealExpand=startLoc[0] < endLoc[1];
		Animator reveal=ViewAnimationUtils.createCircularReveal
		(center														
		 , center.getLeft() + center.getWidth() / 2														
		 , center.getTop() + center.getHeight() / 2
		 , revealExpand ? center.getWidth() / 2: getProperRevealRadius(center)
		 , revealExpand ? getProperRevealRadius(center): center.getWidth() / 2);
		
		set.play(transAnimator).with(reveal);
		return set;
	}


	private float getProperRevealRadius(View target)
	{
		View parent=(View) target.getParent();
		float top=target.getTop() + target.getHeight() / 2;
		float bottom=parent.getHeight() - top;
		float left=target.getLeft() + target.getWidth() / 2;
		float right=parent.getWidth() - left;
		return (float)Math.hypot(
			Math.max(top, bottom)
			, Math.max(left, right));
	};
}
