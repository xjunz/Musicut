package meta.z.musicut.util;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import java.io.*;
import meta.z.musicut.*;
import java.util.*;
import java.text.Format;
import java.text.NumberFormat;

public class MusicUtils
{   
    //Adapted from aosp music player
	//改写自安卓开源项目音乐播放器

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");  
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();  
    private static Bitmap mCachedBit = null; 
    private static HashMap<Long,Bitmap> albumArtCache=new HashMap<Long,Bitmap>();

	public static Bitmap getArtwork(Context context, long song_id, long album_id)
	{  
	    if(albumArtCache.containsKey(album_id)){
			return albumArtCache.get(album_id);
		}
	    Bitmap bm=null;
        ContentResolver res = context.getContentResolver();  
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);  
        if (uri!=null)
		{  
            InputStream in = null;  
            try
			{  
                in=res.openInputStream(uri);  
				bm=BitmapFactory.decodeStream(in, null, sBitmapOptions);  
				
                }
			catch (Exception ex)
			{  
                // The album art thumbnail does not actually exist. Maybe the user deleted it, or  
                // maybe it never existed to begin with.  
				bm=getArtworkFromFile(context, song_id, album_id);  
				
            }
			finally
			{  
                try
				{  
                    if (in!=null)
					{in.close();}  
                }
				catch (IOException ex)
				{}  
            }  
        }  
		albumArtCache.put(album_id, bm);
        return bm;  
    }  

    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid)
	{  
        Bitmap bm = null;  

        if (albumid<0&&songid<0)
		{  
            throw new IllegalArgumentException("Must specify an album or a song id");  
        }  
        try
		{  
            if (albumid<0)
			{  
                Uri uri = Uri.parse("content://media/external/audio/media/"+songid+"/albumart");  
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");  
                if (pfd!=null)
				{  
                    FileDescriptor fd = pfd.getFileDescriptor();  
                    bm=BitmapFactory.decodeFileDescriptor(fd);  
                }  
            }
			else
			{  
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);  
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");  
                if (pfd!=null)
				{  
                    FileDescriptor fd = pfd.getFileDescriptor();  
                    bm=BitmapFactory.decodeFileDescriptor(fd);  
                }  
            }  
        }
		catch (FileNotFoundException ex)
		{ }  
        if (bm!=null)
		{  
            mCachedBit=bm;  
        }  
        return bm;  
    }  

	public static String formatSongDuration(long duration){
        int min=(int) duration/60000;
		int sec=(int) ((duration-min*60000)/1000);
		return min+":"+(sec<10?"0"+sec:sec);
	}

}
