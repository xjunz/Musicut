package meta.z.musicut.util;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.net.*;
import android.os.*;
import java.io.*;
import java.util.*;
import meta.z.musicut.bean.*;
import android.text.*;
import meta.z.musicut.*;
import android.icu.text.*;

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
	    if (albumArtCache.containsKey(album_id))
		{
			return albumArtCache.get(album_id);
		}
	    Bitmap bm=null;
        ContentResolver res = context.getContentResolver();  
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);  
        if (uri != null)
		{  
            InputStream in = null;  
            try
			{  
                in = res.openInputStream(uri);  
				bm = BitmapFactory.decodeStream(in, null, sBitmapOptions);  

			}
			catch (Exception ex)
			{  
                // The album art thumbnail does not actually exist. Maybe the user deleted it, or  
                // maybe it never existed to begin with.  
				bm = getArtworkFromFile(context, song_id, album_id);  

            }
			finally
			{  
                try
				{  
                    if (in != null)
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

        if (albumid < 0 && songid < 0)
		{  
            throw new IllegalArgumentException("Must specify an album or a song id");  
        }  
        try
		{  
            if (albumid < 0)
			{  
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");  
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");  
                if (pfd != null)
				{  
                    FileDescriptor fd = pfd.getFileDescriptor();  
                    bm = BitmapFactory.decodeFileDescriptor(fd);  
                }  
            }
			else
			{  
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);  
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");  
                if (pfd != null)
				{  
                    FileDescriptor fd = pfd.getFileDescriptor();  
                    bm = BitmapFactory.decodeFileDescriptor(fd);  
                }  
            }  
        }
		catch (FileNotFoundException ex)
		{ }  
        if (bm != null)
		{  
            mCachedBit = bm;  
        }  
        return bm;  
    }  

	public static String formatSongDuration(long duration)
	{
        int min=(int) duration / 60000;
		int sec=(int) ((duration - min * 60000) / 1000);
		return min + ":" + (sec < 10 ?"0" + sec: sec);
	}

	public static String formatFileSize(long size){
		return android.text.format.Formatter.formatFileSize(MusicutApplication.context,size);
	}

	private static final SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	public static String formatDate(long time){
		return dateFormat.format(time);
	}
	public Bitmap getArtWorkFromMetadata(String path)
	{
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();  
		try 
		{  
			mmr.setDataSource(path);  
			byte[] pic = mmr.getEmbeddedPicture();  // 图片，可以通过BitmapFactory.decodeByteArray转换为bitmap图片
			mmr.release();
			return BitmapFactory.decodeByteArray(pic, 0, pic.length);
		} 
		catch (Exception e) 
		{  
			e.printStackTrace();  
			return null;
		}
		finally
		{
			mmr.release();
		}

	}


	public Song createSongFromFile(String path)
	{
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();  
		Song song=new Song();
		mmr.setDataSource(path);
		song.title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE); 
		song.album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);  
		song. artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);  
		song.duration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)); 
		song.path = path;
		song.date_last_modified=new File(path).lastModified();
		mmr.release();
		return song;
	}
}
