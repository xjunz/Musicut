package meta.z.musicut.util;
import android.app.*;
import android.content.*;
import android.net.*;
import meta.z.musicut.widget.*;

public class FeedbackUtils
{
	private final static String feedback_qgroup_id="479340880";
	private final static String qgroup_uri="mqq://card/show_pslcard?src_type=internal&version=1&uin="+feedback_qgroup_id+"&card_type=group&source=qrcode";
	
	public static void gotoQgroupCard(Context context){
		try{
		Intent i=new Intent();
		i.setData(Uri.parse(qgroup_uri));
		context.startActivity(i);}
		catch(Exception e){
		MusicutToast.makeAndShow(context,"反馈发生错误");
		}
	}
	
	public static void showExceptionCaughtDialog(final Context context,Exception e){
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
	    builder.setTitle("出了点问题...").setMessage("哦！程序在运行中捕获一只爬虫，上交给开发者？"+e.getMessage())
			.setPositiveButton("反馈", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					gotoQgroupCard(context);
				}
			}).setNegativeButton("忽略",null)
			.setNeutralButton("以后不再提示", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					
				}
			}).show();
		}
}
