package meta.z.musicut.bean;
import android.media.*;
import java.io.*;
import meta.z.musicut.util.*;
import java.util.*;
import android.animation.*;
import android.content.*;
import android.net.*;

//音乐播放类，继承自MediaPlayer
//实现播放进度监听，淡入淡出

public class MusicPlayer extends MediaPlayer
{
    private Timer timer;
	private TimerTask task;
	public Song song;
	public int var_update_period;
	public boolean var_should_fade_out,var_should_fade_in;
	public int var_fade_duration=1000;
    public boolean var_timer_enabled=false;

	private OnProgressUpdateListener onProgressUpdateListener;

	public interface OnProgressUpdateListener
	{
		public void onProgress(MusicPlayer player, int progress);
	}
	public MusicPlayer(Song song)
	{

		try
		{
			setDataSource(song.path);
			prepare();
		}
		catch (Exception e)
		{}
		this.song = song;
		setOnErrorListener(new OnErrorListener(){
				@Override
				public boolean onError(MediaPlayer p1, int p2, int p3)
				{
					return false;
				}
			});

	}

	public void resetDataSource(Song song)
	{
		reset();
		try
		{
			this.song = song;
			setDataSource(song.path);
			prepare();}
		catch (Exception e)
		{}
	}

	public void setOnProgressUpdateListener(OnProgressUpdateListener listener, int updatePeriod)
	{

		this.onProgressUpdateListener = listener;
		this.var_update_period = updatePeriod;
	}

	public void enableUpdateProgress()
	{ 
		timer = new Timer();
		task = new TimerTask(){
			@Override
			public void run()
			{
				onProgressUpdateListener.onProgress(MusicPlayer.this, getCurrentPosition());
			}
		};
		timer.schedule(task, 0, var_update_period);
		var_timer_enabled = true;
	}

	public void disableUpdateProgress()
	{ 
	    var_timer_enabled = false;
		if (task != null)
		{
			task.cancel();
		}
		if (timer != null)
		{
			timer.cancel();
		}
	}

	@Override
	public void start() 
	{
		super.start();
		if (var_should_fade_in)
		{
			fadeIn(var_fade_duration);
		}

	}

	@Override
	public void pause() 
	{
		if (var_should_fade_out)
		{
			fadeOut();
		}
		else
		{
			super.pause();
		}

	}

	@Override
	public void stop() 
	{
		disableUpdateProgress();
		if (fadeOutAnimator != null && fadeOutAnimator.isRunning())
		{
			fadeOutAnimator.end();
		}
		super.stop();
	}

	private ValueAnimator fadeOutAnimator;
	private void fadeOut()
	{

		if(fadeOutAnimator==null){
		fadeOutAnimator = new ValueAnimator()
			.ofFloat(1f, 0f).setDuration(var_fade_duration);
		fadeOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
				@Override
				public void onAnimationUpdate(ValueAnimator p1)
				{
					float volume=p1.getAnimatedValue();
					setVolume(volume, volume);
				}
			});

		fadeOutAnimator.addListener(new AnimatorListenerAdapter(){
				@Override
				public void onAnimationEnd(Animator p1)
				{
					MusicPlayer.super.pause();
				}
			});
		}
		fadeOutAnimator.start();
	}

	private ValueAnimator fadeInAnimator;
	private void fadeIn(int fadeDuration)
	{
		if(fadeInAnimator==null){
		 fadeInAnimator=new ValueAnimator()
			.ofFloat(0f, 1f).setDuration(fadeDuration);
		fadeInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
				@Override
				public void onAnimationUpdate(ValueAnimator p1)
				{
					float volume=p1.getAnimatedValue();
					setVolume(volume, volume);
				}
			});
			}
		fadeInAnimator.start();
	}



	@Override
	public void release()
	{

		if (timer != null)
		{
			timer.purge();
		}
		super.release();
	}

}
