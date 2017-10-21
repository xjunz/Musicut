package meta.z.musicut.util;
import android.transition.*;
import android.view.*;

public class TransitionUtils
{
	public static class TransitionListenerAdapter implements Transition.TransitionListener
	{

		@Override
		public void onTransitionStart(Transition p1)
		{
			
		}

		@Override
		public void onTransitionEnd(Transition p1)
		{
			
		}

		@Override
		public void onTransitionCancel(Transition p1)
		{
			
		}

		@Override
		public void onTransitionPause(Transition p1)
		{
			
		}

		@Override
		public void onTransitionResume(Transition p1)
		{
			
		}
		
		
	}
	
	public static void beginDelayedTransition(ViewGroup target){
		AutoTransition t=new AutoTransition();
		t.setInterpolator(AnimUtils.getFastOutSlowInInterpolator());
		TransitionManager.beginDelayedTransition(target);
	}
}
