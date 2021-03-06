package meta.z.musicut.widget;

import android.animation.*;
import android.view.*;
import android.widget.*;
import meta.z.musicut.*;
import meta.z.musicut.util.*;
import android.app.Activity;
import android.content.Context;
import android.view.View.OnClickListener;
import static java.lang.Math.abs;

//一个非常简单的SnackBar实现
public class SnackBar
{
	private Context context;
	private ViewGroup  container;
	private ViewGroup decor;
	private TextView text;
	private Button action;
	private int w=MusicutApplication.screen_width;
	private boolean shouldDismiss;
	private long formerMillis;
	private float formerRawX,v,formerX;
	public int var_feedback_duration=500;
	public int var_show_duration=2000;

	private Callback callback=callback = new Callback(){

		@Override
		public void onShow(SnackBar snackbar)
		{

		}

		@Override
		public void onDismiss(SnackBar snackbar)
		{

		}
	};


	public SnackBar(Context context)
	{   this.decor = (ViewGroup)((Activity)context).getWindow().getDecorView();
	    this.context = context;
		this.container = (ViewGroup) LayoutInflater.from(context).inflate
		(R.layout.layout_snack_bar, decor, false);
		this.text = (TextView) container.findViewById(R.id.layout_snack_bar_text);
		this.action = (Button) container.findViewById(R.id.layout_snack_bar_action);
		this.shouldDismiss = true;

		this.container.setOnTouchListener(new View.OnTouchListener(){
				public boolean onTouch(View view, MotionEvent event)
				{
					if (event.getAction() == event.ACTION_DOWN)
					{
						shouldDismiss = false;
						formerMillis = System.currentTimeMillis();
						formerRawX = event.getX();
						formerX = formerRawX;}
					else if (event.getAction() == event.ACTION_MOVE)
					{
						//简单的手指速度测量
						if (abs(event.getRawX() - formerRawX) >= 5)
						{
							v = (event.getRawX() - formerRawX) / (System.currentTimeMillis() - formerMillis);
						}
						formerMillis = System.currentTimeMillis();
						formerRawX = event.getRawX();
						view.setTranslationX(event.getRawX() - formerX);
						view.setAlpha(1 - abs(view.getTranslationX()) / w);
					}
					else if (event.getAction() == event.ACTION_UP)
					{
						shouldDismiss = true;
						int sign=(int) Math.signum(v);
						v = sign * v < w / 250 ?sign * w / 250: v;
						float tx=view.getTranslationX();

						if (v > 0)
						{
							if (tx < 0)
							{
								animate(0, -tx / v);
							}
							else
							{
								animate(w, (w - tx) / v);
							}
						}
						else if (v < 0)
						{
							if (tx > 0)
							{
								animate(0, tx / -v);
							}
							else
							{
								animate(-w, (w + tx) / -v);
							}
						}
						formerX = 0;
					}
					return true;
				}
			});
	}
	private void animate(final float transX, float dur)
	{

		ValueAnimator animator= new ValueAnimator().ofFloat(new float[]{(float)container.getTranslationX(),transX})
			.setDuration((int)(dur + .5));
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
			{
				public void onAnimationUpdate(ValueAnimator p1)
				{
					float f=p1.getAnimatedValue();
					container.setTranslationX(f);
					container.setAlpha(1 - abs(container.getTranslationX()) / w);
				}
			});
		animator.addListener(new AnimatorListenerAdapter(){
				@Override
				public void onAnimationEnd(Animator p1)
				{
					if (abs(transX) >= w)
					{
						decor.removeView(container);
						callback.onDismiss(SnackBar.this);
					}
				}
			});
		animator.start();
	}

	public static SnackBar build(Context context, int resText, int resActionText)
	{   
		SnackBar sb=new SnackBar(context);
		sb.setMessage(resText);
	    sb.setActionText(resActionText);
		return sb;
	}

	public SnackBar setMessage(int res)
	{
		text.setText(res);
		return this;
	}


	public SnackBar setActionText(int res)
	{   action.setVisibility(0);
		action.setText(res);
		action.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					action.setEnabled(false);
					if (hasFeedback())
					{
						setMessage(resFeedback);
						feedingback = true;
						indefiniteDismiss(var_feedback_duration);		  
					}
					else
					{			  
						dismiss();
					}
				}
			});
		return this;
	}
	private int resFeedback;

    public SnackBar setFeedback(int res)
	{
		this.resFeedback = res;
		return this;
	}
	

	public void clearFeedback()
	{
		resFeedback = 0;
	}
	public boolean hasFeedback()
	{
		return resFeedback != 0;
	}

	public SnackBar setAction(final View.OnClickListener listener)
	{   action.setVisibility(0);
		action.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					action.setEnabled(false);
					listener.onClick(p1);
					if (resFeedback != 0)
					{
						setMessage(resFeedback);
						feedingback = true;
						indefiniteDismiss(var_feedback_duration);		  
					}
					else
					{			  
						dismiss();
					}
				}
			});
		return this;
	}
    private void rawShow()
	{
		decor.addView(container);
		container.post(new Runnable(){
				@Override
				public void run()
				{   UiUtils.setTranslationYBy(container, container.getHeight());
					UiUtils.visible(container);
					container.animate().translationYBy(-container.getHeight()).setListener(new AnimatorListenerAdapter(){
							@Override
							public void onAnimationEnd(Animator p1)
							{
								callback.onShow(SnackBar.this);			
							}
						}).setDuration(200)
						.setInterpolator(AnimUtils.getFastOutSlowInInterpolator())
						.start();
				}
			});
	}
	public void show()
	{   
	    rawShow();
		indefiniteDismiss(var_show_duration);
	}

	public static int DURATION_INFINATE=-1;
	public void show(int duration)
	{
		rawShow();
		if (duration == DURATION_INFINATE)
		{return;}
		indefiniteDismiss(duration);
	}

	//无期限关闭（当用户手指按住SnackBar时(不包括按钮)，不关闭)
	private void indefiniteDismiss(final int duration)
	{
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					try
					{
						Thread.sleep(duration);
					}
					catch (InterruptedException e)
					{}
					((Activity)context).runOnUiThread(new Runnable(){
							@Override
							public void run()
							{
								if (shouldDismiss)
								{
									if (container.getParent() != null)
									{   
										dismiss();
									}
								}
								else
								{ 
									indefiniteDismiss(duration);
								}
							}
						});
				}
			}).start();

	}

    private boolean dismissing,feedingback;
	public boolean isFeedingback()
	{
		return feedingback;
	}
	public void dismiss()
	{         
		//保证即使连续调用dismiss(),动画也能完整执行
		if (!dismissing)
		{
			dismissing = true;
			container.animate().translationYBy(container.getHeight()).setListener(new AnimatorListenerAdapter(){
					@Override
					public void onAnimationEnd(Animator p1)
					{
						UiUtils.gone(container);
						decor.removeView(container);
						callback.onDismiss(SnackBar.this);
						dismissing = false;
						feedingback = false;
					}
				}).setDuration(200)
				.setInterpolator(AnimUtils.getFastOutSlowInInterpolator())
				.start();
		}
	}



	public boolean isShown()
	{
		if (this.container.isShown())
		{
			return true;
		}
		return false;
	}

	public interface Callback
	{
		void onShow(SnackBar snackbar);
		void onDismiss(SnackBar snackbar);
	}

	public SnackBar setCallback(Callback cb)
	{
		this.callback = cb;
		return this;
	}
}
