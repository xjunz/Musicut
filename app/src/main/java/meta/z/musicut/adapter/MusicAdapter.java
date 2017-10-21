package meta.z.musicut.adapter;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.widget.*;
import android.support.v7.widget.helper.*;
import android.transition.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.util.*;
import meta.z.musicut.*;
import meta.z.musicut.bean.*;
import meta.z.musicut.manager.*;
import meta.z.musicut.util.*;
import meta.z.musicut.widget.*;
import android.util.*;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> implements SongManager.GroupDescriptor
{
	private Context context;
	public static final int TYPE_ITEM_SONG=0;
	public static final int TYPE_ITEM_HEADER=1;
	private ArrayList<ItemInfo> itemInfoList;
	private RecyclerView rvMusic;
	private Transition transition=new ChangeBounds();
	private SimultaneousAnimator animator=new SimultaneousAnimator();
    private class ItemInfo
	{
		Song song;
		String description;
		boolean expand;
		boolean collapz;
		int type;
		ItemInfo(Song s, String d, int t)
		{   song = s;
			description = d;
			type = t;
		}
		ItemInfo(String d)
		{
			description = d;
			type = TYPE_ITEM_HEADER;
		}
	}
	private OnTouchListener touchEater=new OnTouchListener(){
		@Override
		public boolean onTouch(View p1, MotionEvent p2)
		{
			return true;
		}


	};
	public MusicAdapter(Context context, RecyclerView rv)
	{
		this.context = context;
		this.rvMusic = rv;

		transition.addListener(new TransitionUtils.TransitionListenerAdapter(){
				public void onTransitionStart(Transition p1)
				{
					rvMusic.setOnTouchListener(touchEater);
				}
				public void onTransitionEnd(Transition p1)
				{
					rvMusic.setOnTouchListener(null);
					animator.setAnimateMoves(true);
				}
			}).setDuration(200).setInterpolator(AnimUtils.getFastOutSlowInInterpolator());
		rvMusic.setItemAnimator(animator);
		itemInfoList = new ArrayList<ItemInfo>();
		SongManager.sort(SongManager.ORDER_ARTIST);
		SongManager.setSongGroupDescriptor(this);
		SongManager.refreshDescription();
	}


	private void addItemInfoToList(int sortPos, String des)
	{   
		if (sortPos == 0)
		{
			itemInfoList.add(new ItemInfo(des));
		}
		else
		{
			if (!des.equals(itemInfoList.get(itemInfoList.size() - 1).description))
			{
				itemInfoList.add(new ItemInfo(des));
			}
		}

		itemInfoList.add(new ItemInfo(SongManager.cur_song_list.get(sortPos), des, TYPE_ITEM_SONG));
	}


	@Override
	public void describe(int sortPos)
	{
		Song cur=SongManager.cur_song_list.get(sortPos);
		switch (SongManager.current_order)
		{
			case SongManager.ORDER_ALBUM:
				addItemInfoToList(sortPos, cur.album);
				break;
			case SongManager.ORDER_ARTIST:
                addItemInfoToList(sortPos, cur.artist);
				break;
			case SongManager.ORDER_DATE:
				addItemInfoToList(sortPos, cur.date_added);
				break;
			case SongManager.ORDER_DURATION:
			    int min=(int) cur.duration / 60000;
				boolean tail=(cur.duration / 60000) % 1 < .5;
				addItemInfoToList(sortPos, min + ":" + (tail ?"00": "30") + "-" + (tail ?min: min + 1) + ":" + (tail ?"30": "00"));
				break;
			case SongManager.ORDER_PATH:
				addItemInfoToList(sortPos, cur.path.substring(0, cur.path.lastIndexOf("/") + 1));
				break;
			case SongManager.ORDER_SIZE:
				int size=(int) cur.size / 1024 / 1024;
				addItemInfoToList(sortPos, size + "M-" + (size + 1) + "M");
				break;
			case SongManager.ORDER_TITLE:
				addItemInfoToList(sortPos, cur.title.substring(0, 1).toUpperCase());
				break;
		}

	}

	@Override
	public int getItemViewType(int position)
	{
		return itemInfoList.get(position).type;
	}


	@Override
	public MusicAdapter.MusicViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		if (p2 == TYPE_ITEM_SONG)
		{
			return new MusicViewHolder(LayoutInflater.from(context).inflate(R.layout.item_song, p1, false), p2);
		}
		return new MusicViewHolder(LayoutInflater.from(context).inflate(R.layout.item_header, p1, false), p2);
	}

	private void expandItem(MusicViewHolder holder, boolean shouldExpand)
	{
		itemInfoList.get(holder.getAdapterPosition()).expand = shouldExpand;
		notifyItemChanged(holder.getAdapterPosition(), shouldExpand);
	}

	@Override
	public void onBindViewHolder(MusicAdapter.MusicViewHolder holder, int position, List<Object> payloads)
	{
		if (payloads.size() == 0)
		{
			onBindViewHolder(holder, position);
			return;
		}
		if (payloads.contains(true))
		{
			holder.itemView.setActivated(true);
			UiUtils.visible(holder.llOption);
		}
		else
		{
			holder.itemView.setActivated(false);
			UiUtils.gone(holder.llOption);
		}
	}


	@Override
	public void onBindViewHolder(final MusicAdapter.MusicViewHolder p1, final int p2)
	{ final ItemInfo itemInfo=itemInfoList.get(p2);
		switch (getItemViewType(p2))
		{
			case TYPE_ITEM_SONG:
				final Song song=itemInfo.song;
				if (itemInfo.collapz)
				{

					UiUtils.gone(p1.rlInfo, p1.llOption);
					return;
				}
				UiUtils.visible(p1.rlInfo);
				p1.tvSongTitle.setText(song.title);
				p1.tvSongInfo.setText(song.artist + " - " + song.album);
				p1.civAlbumArt.setImageResource(R.mipmap.ic_default_album_art);
				new Thread(new Runnable(){
						@Override
						public void run()
						{   
							final Bitmap mp=MusicUtils.getArtwork(context, song.song_id, song.album_id);		
							((Activity)context).runOnUiThread(new Runnable(){
									@Override
									public void run()
									{ 
										if (mp != null)
										{
											p1.civAlbumArt.setImageBitmap(mp);
										}
									}
								});
						}
					}).start();
				if (itemInfo.expand)
				{
					p1.itemView.setActivated(true);
					UiUtils.visible(p1.llOption);
				}
				else
				{
     				p1.itemView.setActivated(false);
					UiUtils.gone(p1.llOption);
				}
				break;

			case TYPE_ITEM_HEADER:
				p1.tvHeader.setText(itemInfo.description);

				if (itemInfo.collapz)
				{
					p1.ivChevron.setRotation(-90);
				}
				else
				{
					p1.ivChevron.setRotation(0);
				}

				break;
		}
	}


	@Override
	public int getItemCount()
	{
	    return  itemInfoList.size();
	}




	public class MusicViewHolder extends RecyclerView.ViewHolder
	{   
	    //普通条目
	    private TextView tvSongTitle;
		private TextView tvSongInfo;
		private CircularImageView civAlbumArt;
		private View llOption, rlInfo;
        private Button btnItemSongPlay,btnItemSongCut;
		private ImageButton ibEdit;
		//header条目
		private TextView tvHeader;
		private ImageView ivChevron;
		//播放器

		public MusicViewHolder(View item, int itemType)
		{
			super(item);
			final MusicViewHolder viewHolder=MusicViewHolder.this;

			if (itemType == TYPE_ITEM_SONG)
			{
				tvSongTitle = (TextView) item.findViewById(R.id.tv_song_title);
				tvSongInfo = (TextView) item.findViewById(R.id.tv_song_info);
				civAlbumArt = (CircularImageView) item.findViewById(R.id.item_song_civ_album_art);
				llOption = item.findViewById(R.id.rl_option_container);
				rlInfo = item.findViewById(R.id.rl_main);
				btnItemSongCut = (Button) item.findViewById(R.id.item_song_btn_cut);
				btnItemSongPlay = (Button) item.findViewById(R.id.item_song_btn_play);
				ibEdit = (ImageButton) item.findViewById(R.id.ib_edit);

				btnItemSongCut.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View p1)
						{
							
						}
					});

				btnItemSongPlay.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View p1)
						{
							Intent i=new Intent(context, MusicPlayerActivity.class);
							Bundle bundle=ActivityOptions.makeSceneTransitionAnimation((Activity)context, new Pair[]
																					   {    Pair.create(civAlbumArt, "transition_album_art")
																						   ,Pair.create(tvSongTitle, "transition_song_title")
																						   ,Pair.create(tvSongInfo, "transition_song_info")
																						   ,Pair.create(itemView, "transition_dialog")}).toBundle();
							Song song=itemInfoList.get(getAdapterPosition()).song;
							if (!song.exists())
							{
								SnackBar.build(context, R.string.song_not_exists, android.R.string.ok)
									.show(1000);
								return;
							}
							i.putExtra("song", song);
							context.startActivity(i, bundle);

						}
					});

				itemView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View view)
						{
							TransitionManager.beginDelayedTransition(rvMusic, transition);	
							animator.setAnimateMoves(false);
							if (!itemView.isActivated())
							{
								itemView.setActivated(true);
								expandItem(MusicViewHolder.this, true);
							}
							else
							{
								itemView.setActivated(false);
								expandItem(MusicViewHolder.this, false);
							}
						}

					});

			}
			else
			{   ivChevron = (ImageView) ((ViewGroup)item).getChildAt(0);
				tvHeader = (TextView) ((ViewGroup)item).getChildAt(1);
				itemView.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v)
						{
							int i=viewHolder.getAdapterPosition();
							if (viewHolder.ivChevron.getRotation() == 0)
							{
								itemInfoList.get(i).collapz = true;
								viewHolder.ivChevron.animate().rotation(-90).start();
								for (i = i + 1;i < itemInfoList.size() && itemInfoList.get(i).type == TYPE_ITEM_SONG;i++)
								{
									itemInfoList.get(i).collapz = true;
									notifyItemChanged(i);
								}
							}
							else
							{
								itemInfoList.get(i).collapz = false;
								viewHolder.ivChevron.animate().rotation(0).start();
								itemInfoList.get(viewHolder.getAdapterPosition()).collapz = false;
								for (i = i + 1;i < itemInfoList.size() && itemInfoList.get(i).type == TYPE_ITEM_SONG;i++)
								{
									itemInfoList.get(i).collapz = false;
									notifyItemChanged(i);
								}

							}
						}
					});
			}
		}
	}


	public static class SimultaneousAnimator extends DefaultItemAnimator
	{

		private boolean animateMoves = true;

		public SimultaneousAnimator()
		{
            super();
        }

        void setAnimateMoves(boolean animateMoves)
		{
            this.animateMoves = animateMoves;
        }

        @Override
        public boolean animateMove(
			RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY)
		{
            if (!animateMoves)
			{
                dispatchMoveFinished(holder);
                return false;
            }
            return super.animateMove(holder, fromX, fromY, toX, toY);
        }
    }
}
