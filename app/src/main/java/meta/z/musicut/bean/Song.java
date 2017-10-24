package meta.z.musicut.bean;
import android.os.*;
import java.io.*;
import java.util.ArrayList;

//歌曲类
public class Song implements Parcelable
{


	public String title;
	public String artist;
	public String album;
	public String path;
	public long date_added;
	public long album_id;
	public long song_id;
	public long duration;
	public int  postion;
	
	public Song(){}

	public boolean exists()
	{
		return new File(path).exists();
	}
	
	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p1, int p2)
	{
		String[] strs=new String[]{album,artist,title,path};
		long[] longs=new long[]{album_id,song_id,duration,date_added};
		p1.writeStringArray(strs);
		p1.writeLongArray(longs);
		p1.writeInt(postion);
	}
	
	public static final Parcelable.Creator<Song> CREATOR
	= new Parcelable.Creator<Song>() {
		public Song createFromParcel(Parcel in)
		{
			return new Song(in);
		}

		public Song[] newArray(int size)
		{
			return new Song[size];
		}
	};

	public Song(Parcel in)
	{
		String[] strs=new String[4];
		in.readStringArray(strs);
		long[] longs=new long[4];
		in.readLongArray(longs);
	    album=strs[0];
		artist=strs[1];
		title=strs[2];
		path=strs[3];
		album_id=longs[0];
		song_id=longs[1];
		duration=longs[2];
		date_added=longs[3];
		postion=in.readInt();
	}
}
