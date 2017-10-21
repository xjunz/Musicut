package meta.z.musicut;

import android.content.*;
import android.os.*;
import android.widget.*;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler
{

	private static Context mContext;
	private static ExceptionHandler mHandler=new ExceptionHandler();
	
	private ExceptionHandler(){
		
	}
	public static synchronized ExceptionHandler getInstance(){
		return mHandler;
	}
	
	
	public void init(Context context){
		mContext=context;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	@Override
	public void uncaughtException(Thread p1, final Throwable p2)
	{
		new Thread(new Runnable(){

				@Override
				public void run()
				{
					Looper.prepare();
					Toast.makeText(mContext,p2.getMessage(),0).show();
					//Looper.loop();
					android.os.Process.killProcess(android.os.Process.myPid());
					
				}
			}).start();
		
		
	}
	
}
