package meta.z.musicut.widget;
import android.view.*;
import android.support.v4.widget.ViewDragHelper;
import android.widget.RelativeLayout;
import meta.z.musicut.R;
import android.support.v4.view.ViewCompat;
import meta.z.musicut.util.MathUtils;

public class CurtainPanel extends RelativeLayout 
{

    private ViewDragHelper helper;
	private OnCurtainSlideListener listener;

	public CurtainPanel(android.content.Context context)
	{
		super(context);
		init();
	}

    public CurtainPanel(android.content.Context context, android.util.AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

    public CurtainPanel(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init();
	}

    public CurtainPanel(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	private void init()
	{
		helper = ViewDragHelper.create(this, 1.0f, callback);
		// this.setOnClickListener(this);
	}

	public void setOnCurtainSlideListener(OnCurtainSlideListener listener)
	{
		this.listener = listener;
	}

	private View target;
	private View margin;
	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		target = findViewById(R.id.rl_filter);
		margin = ((ViewGroup)target).findViewById(R.id.lf_margin);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		return helper.shouldInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		helper.processTouchEvent(event);
		return true;
	}

	private int minTop;
	private int maxTop;


	private int curTop;
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		maxTop = target.getTop();
		minTop = maxTop - target.getHeight() + margin.getHeight();
		target.layout(0, minTop+curTop, getRight(), maxTop + margin.getHeight()+curTop);	
	}

  


	@Override
	public void computeScroll()
	{
		if (helper.continueSettling(true))
		{ViewCompat.postInvalidateOnAnimation(this);}
	}

	public void closeCurtain()
	{
		if (helper.smoothSlideViewTo(target, 0, minTop))
		{
	    	ViewCompat.postInvalidateOnAnimation(this);
		}}


	public void autoCurtain()
	{
		if (target.getTop() == minTop)
		{
			openCurtain();
		}
		else
		{
			closeCurtain();		
		}
	}
	public void openCurtain()
	{
		if (helper.smoothSlideViewTo(target, 0, maxTop))
		{
			ViewCompat.postInvalidateOnAnimation(this);
		}

	}

	public boolean isCurtainOpen()
	{
		return target.getTop() > minTop;
	}

	private ViewDragHelper.Callback callback=new ViewDragHelper.Callback(){

		@Override
		public boolean tryCaptureView(View p1, int p2)
		{
			return p1.getId() == R.id.rl_filter;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy)
		{   
			return (int) MathUtils.constrain(top, minTop, maxTop);
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy)
		{  
		    curTop+=dy;
			listener.onCurtainSlide(CurtainPanel.this, (float)(top - minTop) / (maxTop - minTop));
			if (top == minTop)
			{listener.onCurtainClosed(CurtainPanel.this);
				
			}
			else if (top == maxTop)
			{
				listener.onCurtainOpened(CurtainPanel.this);
			}
			
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel)
		{
		    if (yvel > 0)
			{
				openCurtain();
			}
			else
			{
				closeCurtain();
			}
			super.onViewReleased(releasedChild, xvel, yvel);
		}


		@Override
		public int getViewVerticalDragRange(View child)
		{
			return getMeasuredHeight() - child.getMeasuredHeight();
		}

		@Override
		public int getViewHorizontalDragRange(View child)
		{
			return getMeasuredWidth() - child.getMeasuredWidth();
		}

	};

	public static interface OnCurtainSlideListener
	{

		void onCurtainClosed(CurtainPanel curtain)
		void onCurtainSlide(CurtainPanel curtain, float fraction)
		void onCurtainOpened(CurtainPanel curtain)
	}
}
