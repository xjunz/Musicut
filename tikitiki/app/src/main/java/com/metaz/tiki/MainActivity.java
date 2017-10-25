package com.metaz.tiki;

import android.app.*;
import android.os.*;
import android.view.*;
import android.content.*;
import android.net.*;

public class MainActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
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
	
	
	
	
}
