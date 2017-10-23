package meta.z.musicut.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class Elevate extends Transition {

    private static final String PROPNAME_ELEVATION = "musicut:liftoff:elevation";

    private static final String[] transitionProperties = {
            PROPNAME_ELEVATION
    };

    
    public Elevate(Context context, AttributeSet attrs) {
        super(context, attrs);
  
    }

    @Override
    public String[] getTransitionProperties() {
        return transitionProperties;
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_ELEVATION, transitionValues.view.getElevation());
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_ELEVATION, transitionValues.view.getElevation());
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
								float a=startValues.values.get((PROPNAME_ELEVATION));
								float b=endValues.values.get(PROPNAME_ELEVATION);
								if(a>b){
									b=0;
								}else{
									a=0;
								}
								
        return ObjectAnimator.ofFloat(endValues.view, View.TRANSLATION_Z,
                a, b);
    }

}
