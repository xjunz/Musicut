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
import java.util.*;
import android.widget.AdapterView.*;

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
			{   //有权限则加载界面
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
			MusicutToast.makeAndShow(this, R.string.app_upgraded);
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
		setAdapterForSpinners();
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
				MusicutToast.makeAndShow(this, R.string.permission_granted);
				afterPermissionGranted();
            }
			else
            {
                //用户拒绝授权，弹窗提示
				AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setTitle(R.string.permission_not_granted)
					.setMessage(R.string.notif_permission_not_granted)
					.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener(){

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
							MusicutToast.makeAndShow(MainActivity.this, R.string.tip_permission);
						}
					})
					.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener(){

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
				//直接退出应用
				MusicutToast.makeAndShow(this, R.string.warn_permisson_not_granted);
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
	
	private Spinner[] spinners=new Spinner[6];
	private Spinner spSort,spOrder;
	private CharSequence[] defPropmt=new String[8];
	
	private void  setAdapterForSpinners(){
		int[] spinnerPoses=new int[]{2,4,6,8,10,12};
		int[] orders=new int[]{SongManager.ORDER_ARTIST,SongManager.ORDER_ALBUM,
		SongManager.ORDER_DURATION,SongManager.ORDER_TITLE,SongManager.ORDER_PATH,SongManager.ORDER_DATE};
		for(int i=0;i<spinnerPoses.length;i++){
			Spinner sp= (Spinner) rlFilter.getChildAt(spinnerPoses[i]);
			spinners[i]=sp;
			ArrayList<String> strs=SongManager.getGroupDescriptions(orders[i]);
			strs.add(0,getString(R.string.all));
			ArrayAdapter adp=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,strs);
		    sp.setAdapter(adp);
			defPropmt[i]=sp.getPrompt();
		}
		 spSort=(Spinner) rlFilter.getChildAt(15);
		spSort.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
		new String[]{
		 getString(R.string.title), getString(R.string.artist),getString(R.string.album)
		,getString(R.string.duration),getString(R.string.dir)
		,getString(R.string.date_added)}));
		
		 spOrder=(Spinner) rlFilter.getChildAt(17);
		spOrder.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
		new String[]{getString(R.string.order_ascending),getString(R.string.order_descending)}));
		spSort.setOnItemSelectedListener(new OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
				{
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{
					
				}
			});
	}


	
};
