package meta.z.musicut;
import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import meta.z.musicut.adapter.*;
import meta.z.musicut.manager.*;
import meta.z.musicut.widget.*;
import meta.z.musicut.util.*;

public class MainActivity extends Activity implements CurtainPanel.OnCurtainSlideListener
{

	private Toolbar toolbar;
	private RecyclerView rvMusic;
	private CurtainPanel panel;
	private View scrim;
	private ImageButton fabFilter;
	private RelativeLayout rlFilter;
	private MusicAdapter musicAdapter;

	private final int PERMISSION_REQUEST_CODE=0;
	
	//软件入口
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try
		{
            //判断应用是否拥有读取内置储存的权限
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_DENIED)
			{   //没有权限则申请
				ActivityCompat.requestPermissions(this,
												  new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
												  PERMISSION_REQUEST_CODE);
			}
			else
			{   //有权限加载界面
				afterPermissionGranted();
			}
			
		}
		catch (Exception e)
		{
			FeedbackUtils.showExceptionCaughtDialog(this, e);
		}


	}
	
	//拥有权限后加载界面
	private void afterPermissionGranted()
	{
		this.panel = (CurtainPanel) LayoutInflater.from(this).inflate(R.layout.main, null);
	    this.setContentView(panel);
		//从媒体储存获取本地歌曲
		SongManager.scanLocalSongs(this);
		
		this.panel.setOnCurtainSlideListener(this);

		this.initViews();

		this.musicAdapter = new MusicAdapter(this, rvMusic);
		this.rvMusic.setAdapter(musicAdapter);

		this.rvMusic.getItemAnimator().setChangeDuration(200);
		this.rvMusic.setOnScrollListener(new RecyclerView.OnScrollListener(){
				public void onScrolled(RecyclerView recyclerView, int dx, int dy)
				{
					/*if (!panel.isCurtainOpen())
					 {
					 if (dy < 0 && fabFilter.getScaleX() == 1)
					 {fabFilter.animate().scaleX(0).setInterpolator(new AnticipateInterpolator()).scaleY(0)
					 .start();
					 }
					 else if (dy > 0 && fabFilter.getScaleX() == 0)
					 {
					 fabFilter.animate().scaleX(1).setInterpolator(new OvershootInterpolator()).scaleY(1)
					 .start();	
					 }
					 }*/
				}
			});

		//APP升级提示
		if (SharedPrefsManager.needNotifyUpdate())
		{
			MusicutToast.makeAndShow(this, "APP已升级");
		}

		
	}

	//初始化控件
	private void initViews() 
	{
		fabFilter = (ImageButton) findViewById(R.id.fab_filter);
		rlFilter = (RelativeLayout) findViewById(R.id.rl_filter);
	    rvMusic = (RecyclerView) findViewById(R.id.rvMusic);
		rvMusic.setLayoutManager(new LinearLayoutManager(this));
		toolbar = (Toolbar)findViewById(R.id.toolbar);
	    setActionBar(toolbar);
		scrim = findViewById(R.id.scrim);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{  
		new MenuInflater(this).inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{

		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onCurtainSlide(CurtainPanel curtain, float fraction)
	{   
	    scrim.setVisibility(0);
		scrim.setAlpha(fraction);
		fabFilter.setY(rlFilter.getBottom() - fabFilter.getHeight() / 2);
	}

	@Override
	public void onCurtainOpened(CurtainPanel curtain)
	{   if (fabFilter.getScaleX() < 1)
		{
			fabFilter.animate()
				.scaleX(1)
				.setInterpolator(new OvershootInterpolator())
				.scaleY(1)
				.start();
		}
	}

	@Override
	public void onCurtainClosed(CurtainPanel curtain)
	{
		scrim.setVisibility(8);
	}

	@Override
	public void onBackPressed()
	{
		if (panel.isCurtainOpen())
		{
			panel.closeCurtain();
		}
		else
		{
			super.onBackPressed();
		}
	}

	private boolean reenter_from_settings;
	//权限申请结果回调
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == this.PERMISSION_REQUEST_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
				MusicutToast.makeAndShow(this, "授权成功");
				afterPermissionGranted();
            }
			else
            {
                //用户拒绝授权，弹窗提示
				AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setTitle("授权失败")
					.setMessage("请授予该应用权限，否则无法运行。放心，我们不会使用该权限危害您的设备或窃取您的隐私。")
					.setPositiveButton("设置", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							//打开设置，让用户手动允许授权
							Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
							String pkg = "com.android.settings";
							String cls = "com.android.settings.applications.InstalledAppDetails";
							i.setComponent(new ComponentName(pkg, cls));
							i.setData(Uri.parse("package:" + getPackageName()));
							startActivity(i);
							reenter_from_settings = true;
							MusicutToast.makeAndShow(MainActivity.this, "请在「设置-->权限管理」中给予该应用权限");
						}
					})
					.setNegativeButton("退出", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
						    android.os.Process.killProcess(android.os.Process.myPid());
							System.exit(0);
						}
					})
					.setCancelable(false)
					.show();
			}
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


	//用户从设置界面（授权管理）返回该应用的处理
	@Override
	protected void onResume()
	{
		super.onResume();
	    if (reenter_from_settings)
		{
			//保证这个方法不被反复误调用
			reenter_from_settings = false;
			//用户要是还是拒绝授权
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
				== PackageManager.PERMISSION_DENIED)
			{
				//我们不再自作多情。（︶︿︶）=凸，直接退出应用
				MusicutToast.makeAndShow(this, "授权失败，无法运行此应用");
				finish();
			}
			else
			{ //用户在设置里授权了并且界面未加载
				if (musicAdapter == null)
				{
					//则加载界面
					afterPermissionGranted();
				}
			}
		}
	}





	//来自main.xml的声明
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.scrim:
				panel.closeCurtain();
				break;
			case R.id.fab_filter:		
				panel.autoCurtain();
				break;

		}
	}


}
