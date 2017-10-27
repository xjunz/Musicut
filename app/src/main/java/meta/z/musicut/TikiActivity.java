package meta.z.musicut;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import android.media.*;

public class TikiActivity extends Activity 
{
	private EditText etKeyWord;
	private RadioGroup rgVendor;
	
	private final String tiki_base_url="https://www.tikitiki.cn";
	ListView lv;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiki);
		setActionBar((Toolbar)findViewById(R.id.tiki_toolbar));
		etKeyWord = (EditText) findViewById(R.id.et_keyword);
		rgVendor = (RadioGroup) findViewById(R.id.radio_group);
        lv = (ListView) findViewById(R.id.lv);
		//android.R.attr.actionb
	}

	private final static int NO_VENDOR_TYPE=0;
	private int getVendorType()
	{
		switch (rgVendor.getCheckedRadioButtonId())
		{
			case R.id.rb_vendor_tencent:
				return 1;
			case R.id.rb_vendor_netease:
				return 2;
			case R.id.rb_vendor_kugou:
				return 3;
		}
		return NO_VENDOR_TYPE;
	}

	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu)
	{
		menu.add(R.string.about);
		return super.onCreatePanelMenu(featureId, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		new AlertDialog.Builder(this)
			.setTitle(R.string.about)
			.setMessage(R.string.about_tikitiki)
			.setPositiveButton(R.string.visit
			, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					startActivity(new Intent().setAction(Intent.ACTION_VIEW)
								  .setData(Uri.parse("http://www.tikitiki.cn")));
				}
			}).show();

		return super.onOptionsItemSelected(item);
	}

	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.btn_search:
				try
				{
					new TikiTask().execute(String.format(tiki_result_json_url, URLEncoder.encode(etKeyWord.getText().toString(), "UTF-8"), 1, getVendorType()));
				}
				catch (UnsupportedEncodingException e)
				{}
				break;
		}
	}


	private final String tiki_result_json_url=tiki_base_url + "/searchjson.do?keyword=%1$s&page=%2$s&type=%3$s";
	private final String tiki_download_url=tiki_base_url + "/downloadurl.do?quality=%1$sp&id＝%2$s&type=%3$s";
	private ArrayList<String> strs=new ArrayList<String>();
	class TikiTask extends AsyncTask<String,Void,ArrayList<SongInfo>>
	{

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
		@Override
		protected ArrayList<SongInfo> doInBackground(String...json_url)
		{
			JSONObject jobj;
			JSONArray jarray;
			ArrayList<SongInfo> songInfoList=new ArrayList<SongInfo>();
			byte[] data = new byte[1024];  
			int len = 0;  
			try
			{
				URL url = new URL(json_url[0]);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
				InputStream inStream = conn.getInputStream();  
				while ((len = inStream.read(data)) != -1)
				{  
					outStream.write(data, 0, len);  
				}  
				inStream.close();  
			}
			catch (Exception e)
			{}  
			JSONTokener parser=new JSONTokener(new String(outStream.toByteArray()));
			try
			{
				jobj = (JSONObject) parser.nextValue();
				jarray = jobj.getJSONArray("data");
			    for (int i=0;i < jarray.length();i++)
				{ 
					SongInfo si=SongInfo.parseFromJSONObject(jarray.getJSONObject(i));
					songInfoList.add(si);
				}
			}
			catch (JSONException e)
			{

			}
			return songInfoList;//"json"+new String(outStream.toByteArray());//通过

		}

		@Override
		protected void onProgressUpdate(Void[] values)
		{
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(ArrayList<SongInfo> result)
		{
			lv.setAdapter(new ArrayAdapter(TikiActivity.this, android.R.layout.simple_list_item_1, result.toArray()));
			super.onPostExecute(result);
		}
	}



	static class SongInfo
	{
		String songname;
		String singer;
		String album;
		String vendor_type;
		String id_s128;
		String id_s320;
		String id_mv;
		String id_sogg;
		String formated_duration;
		public static  SongInfo parseFromJSONObject(JSONObject jobj)
		{
			SongInfo info=new SongInfo();
			try
			{
				info.album = jobj.getString("album");
				info.formated_duration = jobj.getString("time");
				info.id_mv = jobj.getString("mv");
				info.id_s128 = jobj.getString("s128");
				info.id_s320 = jobj.getString("s320");
				info.id_sogg = jobj.getString("sogg");
				info.singer = jobj.getString("singer");
				info.songname = jobj.optString("name");

			}
			catch (JSONException e)
			{   

			}
			return info;
		}
	}


}
	
