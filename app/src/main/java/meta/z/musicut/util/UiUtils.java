package meta.z.musicut.util;

import android.view.*;
import android.widget.*;
import meta.z.musicut.*;

import android.content.Context;
import android.util.Log;

public class UiUtils
{

	private static final Context context=MusicutApplication.context;

	
	public static int dip2px(int dipValue)
	{
		return (int)(dipValue * context.getResources().getDisplayMetrics().density + .5);
	}



	public static void visible(View...views)
	{
		for (View v:views)
		{
			v.setVisibility(v.VISIBLE);
		}
	}
	public static void invisible(View...views)
	{
		for (View v:views)
		{
			v.setVisibility(v.INVISIBLE);
		}
	}

	public static void gone(View...views)
	{
		for (View v:views)
		{
			v.setVisibility(v.GONE);
		}
	}

	public static void setTranslationYBy(View v, float yBy)
	{
		v.setY(v.getY() + yBy);
	}
	public static void setTranslationXBy(View v, float xBy)
	{
		v.setX(v.getX() + xBy);
	}

	public static void toast(String str)
	{
		Toast.makeText(context, str, 0);
	}
	private static Toast mToast=Toast.makeText(context, "", 0);

	public static void instantToast(String str)
	{
		TextView tv=(TextView)((ViewGroup)mToast.getView()).getChildAt(0);
		if (mToast.getView().isShown())
		{
			mToast.setText(tv.getText() + "\n" + str);
		}
		else
		{
			mToast.setText(str);
		}
		mToast.show();
	}

	public static void debugToast(Object...objs)
	{
		if (BuildConfig.DEBUG)
		{
			for (Object obj:objs)
			{
				if (obj == null)
				{
					instantToast("null");
					Log.d("debug", "null");
				}
				else if (obj instanceof Object[])
				{
					debugToast(obj);
				}
				else
				{
					instantToast(obj.toString());
					Log.i("dehug", obj.toString());
				}
			}
		}
	}
	
	public static void setCenter(View v,int centerX,int centerY){
		v.layout(centerX-v.getWidth()/2
		,centerY-v.getHeight()/2
		,centerX+v.getWidth()/2
		,centerY+v.getHeight()/2);
	}
}
