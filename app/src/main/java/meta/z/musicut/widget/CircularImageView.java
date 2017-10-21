package meta.z.musicut.widget;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class CircularImageView extends ImageView
{ 
// Adapted from github.com/plaid
 private static final ViewOutlineProvider CIRCULAR_OUTLINE = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(view.getPaddingLeft(),
							view.getPaddingTop(),
							view.getWidth() - view.getPaddingRight(),
							view.getHeight() - view.getPaddingBottom());
        }
    };
	
	
public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOutlineProvider(CIRCULAR_OUTLINE);
        setClipToOutline(true);
    }
}
