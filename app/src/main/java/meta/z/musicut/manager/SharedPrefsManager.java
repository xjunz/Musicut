package meta.z.musicut.manager;
import android.content.SharedPreferences;
import meta.z.musicut.MusicutApplication;

public class SharedPrefsManager
{
	private static SharedPreferences spGlobal=MusicutApplication.global_shared_prefs;
	private static SharedPreferences.Editor spEditorGlobal=MusicutApplication.global_shared_prefs_editor;

	//判断这次安装的本APP版本号是否和原来的相同
	public static boolean isVersionCodeChanged()
	{
		return spGlobal.getBoolean("version_code_changed", true);
	}

	//判断是否需要进行APP升级提示
	public static boolean needNotifyUpdate()
	{
		boolean need=spGlobal.getBoolean("notify_update", true);
		//只有当版本号变化并且没有提示过才需要提示
		need &= isVersionCodeChanged();
		if (need)
		{
			//如果这次需要提示，下次再判断就不需要，因为升级提示只进行一次
			spEditorGlobal.putBoolean("notify_update", false).commit();
		}
		return need;
	}
	

}
