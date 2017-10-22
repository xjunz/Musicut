package meta.z.musicut.widget;
import android.content.*;
import android.view.*;
import android.widget.*;
import meta.z.musicut.*;

//自定义View的Toast
public class MusicutToast extends Toast
{
	private Context context;
    private TextView tvText;
	
	public MusicutToast(Context context){
		super(context);
		this.context=context;
		LinearLayout v=(LinearLayout) LayoutInflater.from(context)
		.inflate(R.layout.layout_toast,null,false);
		tvText=(TextView) v.getChildAt(2);
		this.setView(v);
		this.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,200);
		this.setDuration(LENGTH_SHORT);
	}

	@Override
	public void setText(int resId)
	{
		tvText.setText(resId);
	}

	@Override
	public void setText(CharSequence s)
	{
		tvText.setText(s);
	}
	
	public static void makeAndShow(Context context,CharSequence text,int len){
		MusicutToast toast=new MusicutToast(context);
		toast.setText(text);
	    toast.setDuration(len);
		toast.show();
	}
	
	public static void makeAndShow(Context context,int textRes,int len){
		MusicutToast toast=new MusicutToast(context);
        toast.setText(textRes);
	    toast.setDuration(len);
		toast.show();
	}
	
	public static void makeAndShow(Context context,CharSequence text){
		MusicutToast toast=new MusicutToast(context);
		toast.setText(text);
		toast.show();
	    
	}

	public static void makeAndShow(Context context,int textRes){
		MusicutToast toast=new MusicutToast(context);
        toast.setText(textRes);
		toast.show();
	}
}
