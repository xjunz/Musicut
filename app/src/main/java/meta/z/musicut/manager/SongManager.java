package meta.z.musicut.manager;
import android.content.*;
import android.database.*;
import android.provider.*;
import java.io.*;
import java.util.*;
import meta.z.musicut.bean.*;

public class SongManager
{
	public static ArrayList<Song> local_song_list;
	public static ArrayList<Song> cur_song_list;
	public static final int ORDER_ALBUM=0;
	public static final int ORDER_ARTIST=1;
	public static final int ORDER_DATE=2;
	public static final int ORDER_DURATION=3;
	public static final int ORDER_PATH=4;
	public static final int ORDER_TITLE=6;
	public static  int current_order;
	private static GroupDescriptor descriptor=new GroupDescriptor(){
		@Override
		public void onDescribe(Song song,String des){}
	};
	
	//查询媒体储存数据库
	public static void scanLocalSongs(Context context)
	{
		local_song_list=new ArrayList<Song>();
		Cursor cursor =context.getContentResolver()
		.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.TITLE_KEY);
		while (cursor.moveToNext())
		{
			Song song=new Song();
			song.title=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
			song.artist=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
			song.path=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			song.album_id=Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
			song.song_id=Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
			song.size=cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
			song.album=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
			song.date_added=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
			song.duration=Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
			local_song_list.add(song);
		}
		cur_song_list=(ArrayList<Song>) local_song_list.clone();
		current_order=ORDER_ARTIST;
		cursor.close();
	}


	public static void sort(int order)
	{
		Collections.sort(cur_song_list,new SongComparator(order));
		current_order=order;
	}

	//排序后对不同分组的描述接口
	public static interface GroupDescriptor{
		//根据排序依据进行分组描述
		//比如以字典顺序排序，则以「S」开头的歌曲为一组并以「S」描述
		void onDescribe(Song song,String description);
	}
	
	public static void setSongGroupDescriptor(GroupDescriptor d){
		descriptor=d;
	}
	
	//刷新分组描述
	public static void refreshDescription()
	{   //遍历每一项
	    if (cur_song_list==null||cur_song_list.size()==0){return;}
				for (int i=0;i<cur_song_list.size();i++)
				{
					Song song=cur_song_list.get(i);
					song.postion=i;
					descriptor.onDescribe(song,getDescription(song,current_order));
			  }
		
	}
	
	//获取分组描述
	private static String getDescription(Song song,int order){
		switch(order){
			case ORDER_PATH:
				int index=song.path.lastIndexOf("/");
				return song.path.substring(0,index);
			case ORDER_TITLE:
				return song.title.substring(0,1);
			case ORDER_ARTIST:
				return song.artist;
			case ORDER_DURATION:
				int min=(int) song.duration / 60000;
				boolean tail=(song.duration / 60000) % 1 < .5;
				return min + ":" + (tail ?"00": "30") + "-" + (tail ?min: min + 1) + ":" + (tail ?"30": "00");
			case ORDER_ALBUM:
				return song.album;
			case ORDER_DATE:
				return song.date_added;
			}
		return "";
	}
	
	
	public static ArrayList<String> getGroupDescriptions(int order){
		ArrayList<String> list=new ArrayList<String>();
		for(Song song:local_song_list){
			String des=getDescription(song,order);
			if(!list.contains(getDescription(song,order))){
				list.add(des);
			}
		}

		Collections.sort(list);
		return list;
	}

	
	//获取当前列表特定歌曲的下一首歌曲，支持列表循环
	public static Song getNextSongOf(Song cur){
		return cur_song_list.get((cur.postion+1)%cur_song_list.size());
	}
	//获取当前列表特定歌曲的上一首歌曲，支持列表循环
	public static Song getPreviousSongOf(Song cur){
		return cur_song_list.get((cur.postion-1+cur_song_list.size())%cur_song_list.size());
	}
	
	//歌曲比较接口，实现以歌曲不同属性为依据进行排序
	private static class SongComparator implements Comparator<Song>
	{
		private int order;

		protected  SongComparator(int order)
		{
			this.order=order;
		}

		@Override
		public int compare(Song p1, Song p2)
		{   
			switch (this.order)
			{
				case ORDER_ALBUM:	
					return p1.album.compareToIgnoreCase(p2.album);
				case ORDER_ARTIST:
					return p1.artist.compareToIgnoreCase(p2.artist);
				case ORDER_DATE:
					return p1.date_added.compareToIgnoreCase(p2.date_added);
				case ORDER_DURATION:
					return (int)(p1.duration-p2.duration);
				case ORDER_PATH:
					return p1.path.compareToIgnoreCase(p2.path);
				case ORDER_TITLE:
					return 0;
			}
			return 0;
		}
	}
}
