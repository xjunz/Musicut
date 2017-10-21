package meta.z.musicut;
import android.app.*;
import android.content.*;
import android.content.pm.*;

public class MusicutApplication extends Application
{
	public static  int screen_width,screen_height;
	public static  int version_code;
	public static String version_name;
	public static Context context;
	//储存应用级的偏好
	public static SharedPreferences global_shared_prefs;
	public static SharedPreferences.Editor global_shared_prefs_editor;

	@Override
	public void onCreate()
	{
		super.onCreate();
		//应用级变量
		screen_width = getResources().getDisplayMetrics().widthPixels;
		screen_height = getResources().getDisplayMetrics().heightPixels;
	    context = getApplicationContext();
	    //ExceptionHandler.getInstance().init(context);
		global_shared_prefs =  getSharedPreferences("global", MODE_PRIVATE);
		global_shared_prefs_editor = global_shared_prefs.edit();
		
		try
		{
			PackageInfo pinfo=getPackageManager().getPackageInfo(getPackageName(), 0);
			version_code = pinfo.versionCode;
			version_name = pinfo.versionName;
			
		}
		catch (PackageManager.NameNotFoundException e)
		{}
		

		if (global_shared_prefs.getInt("version_code", -1) != version_code)
		{
			//如果版本号变了
			//储存当前版本号
			global_shared_prefs_editor.putInt("version_code", version_code);
			//版本号改变
			global_shared_prefs_editor.putBoolean("version_code_changed", true);
			//需要提示升级
			global_shared_prefs_editor.putBoolean("notify_update", true);
		}
		else
		{   //版本号没变
			global_shared_prefs_editor.putBoolean("version_code_changed", false);
		}

		if (!global_shared_prefs.contains("init"))
		{
			//第一次初始化应用（第一次安装或清除数据后第一次打开）
			global_shared_prefs_editor.putBoolean("init", true);
		}
		else
		{
			global_shared_prefs_editor.putBoolean("init", false);
		}
		//提交
		    global_shared_prefs_editor.commit();
	}
}
