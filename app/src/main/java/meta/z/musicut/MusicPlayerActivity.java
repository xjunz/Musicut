package meta.z.musicut;
import android.animation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.transition.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import meta.z.musicut.*;
import meta.z.musicut.bean.*;
import meta.z.musicut.manager.*;
import meta.z.musicut.util.*;
import meta.z.musicut.widget.*;

//播放界面
public class MusicPlayerActivity extends Activity implements MusicPlayer.OnProgressUpdateListener,SeekBar.OnSeekBarChangeListener
{
	private SeekBar sbPlayProgress;
	private CircularImageView civAlbumArt;
    private TextView tvStart,tvEnd,tvTitle,tvArtist; 
	private View dialog;
	private Song song;
	private MusicPlayer player;
	private ImageButton ibPlay;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_player);
		initViews();

		player = new MusicPlayer(song);
		player.var_should_fade_in = true;
		player.var_should_fade_out = true;
		player.var_fade_duration = 500;
		player.setOnProgressUpdateListener(this, 10);
		
        setupViews();
		
			}


	 void initViews()
	{
		
		sbPlayProgress = (SeekBar) findViewById(R.id.activity_music_player_seekbar);
		sbPlayProgress.setEnabled(false);
		// 简单地美化一下SeekBar
		sbPlayProgress.getProgressDrawable().setColorFilter
		(getColor(R.color.pink), PorterDuff.Mode.SRC_ATOP);

		civAlbumArt = (CircularImageView) findViewById(R.id.activity_music_player_civ_album_art);

		Intent i=getIntent();
	    song = i.getParcelableExtra("song");
		
		tvTitle = (TextView)findViewById(R.id.activity_music_player_tv_title);
		
		tvEnd = (TextView) findViewById(R.id.activity_music_player_tv_end);
		tvArtist = (TextView) findViewById(R.id.activity_music_player_tv_artist);
		
		tvStart = (TextView) findViewById(R.id.activity_music_player_tv_start);
		sbPlayProgress = (SeekBar) findViewById(R.id.activity_music_player_seekbar);
		sbPlayProgress.setOnSeekBarChangeListener(this);
		
		dialog = findViewById(R.id.activity_music_player_rl_dialog);
		ibPlay = (ImageButton) findViewById(R.id.activity_music_player_ib_play);
	}

	//切换歌曲时的界面设置
	void setupViews(){
		TransitionManager.beginDelayedTransition((ViewGroup)dialog,new ChangeBounds()
		.setDuration(200)
		.setInterpolator(AnimUtils.getFastOutSlowInInterpolator()));
        sbPlayProgress.setProgress(0);
		sbPlayProgress.setMax(player.getDuration());
		tvEnd.setText(MusicUtils.formatSongDuration(player.getDuration()));
		tvTitle.setText(song.title);
		tvArtist.setText(song.artist);
		Bitmap bmp=MusicUtils.getArtwork
		(this, song.song_id, song.album_id);
		if (bmp == null)
		{civAlbumArt.setImageResource(R.mipmap.ic_default_album_art);}
		else
		{
		 civAlbumArt.setImageBitmap(bmp);
		}
	}
	
	//跳转到下一首
	void jumpToNextSong(){
	    this.song=SongManager.getNextSongOf(song);
		player.resetDataSource(song);
		setupViews();
		animatePausePlaying();
	}
	
	//跳转到上一首
	void jumpToPreSong(){
		this.song=SongManager.getPreviousSongOf(song);
		player.resetDataSource(song);
		setupViews();
		animatePausePlaying();
	}
	
	//暂停音乐的动画
	void animatePausePlaying()
	{
		AnimUtils.windmillTrick(ibPlay, R.mipmap.ic_play, 0);
	}

	//开始播放的动画
	void animateStartPlaying()
	{
		AnimUtils.windmillTrick(ibPlay, R.mipmap.ic_pause, 180);
	}

	
	public void onClick(View v)
	{
		switch (v.getId())
		{
			
			case R.id.activity_music_player_ib_play:
                sbPlayProgress.setEnabled(true);
				if (player.isPlaying())
				{
					
					animatePausePlaying();
					player.pause();

				}
				else
				{
					animateStartPlaying();
					
					
					player.start();
				    if (!player.var_timer_enabled)
					{
						player.enableUpdateProgress();
					}
				}

				break;
			case R.id.activity_music_player_ib_pre:
				jumpToPreSong();
				break;
			case R.id.activity_music_player_ib_next:
				jumpToNextSong();
				break;
		}
	}
	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3)
	{
		tvStart.setText(MusicUtils.formatSongDuration(p2));
		//根据播放进度调整图片旋转角度
	}

	@Override
	public void onStartTrackingTouch(SeekBar p1)
	{
		player.disableUpdateProgress();
	}

	@Override
	public void onStopTrackingTouch(SeekBar p1)
	{
		player.enableUpdateProgress();
		player.seekTo(p1.getProgress());
	}


	@Override
	public void onProgress(final MusicPlayer player, final int progress)
	{
		runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					sbPlayProgress.setProgress(progress);
					if (progress>=sbPlayProgress.getMax())
					{
						animatePausePlaying();
						player.disableUpdateProgress();
					}
				}
			});
	}

	@Override
	public void onBackPressed()
	{
        if (player != null)
		{
			player.stop();
			player.release();
		}
		
		super.onBackPressed();
	}


}
