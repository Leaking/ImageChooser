package com.example.imagescanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageGridAdapter extends BaseAdapter implements OnScrollListener,
		OnItemClickListener {

	private final static int VIEW_TYPE_SUM = 2;
	private final static int VIEW_TYPE_UNCHECKED = 0;
	private final static int VIEW_TYPE_CHECKED = 1;


	// 最多选择照片数量
	private int maxSumToChoose;
	// 当前选择照片数量
	private int currentChooseNum = 0;
	// 已经选择的照片
	ArrayList<String> uriList = new ArrayList<String>();
	// 运行上下文
	private Context context;
	// 数据集合
	private List<GridImageBean> listItems;
	// GridView
	private GridView gridView;
	// 视图容器
	private LayoutInflater listContainer;
	// 自定义项视图源
	private int itemViewResource;
	// 加载图片的进程,缓存
	private Set<BitmapWorkerTask> taskCollection;
	private LruCache<String, Bitmap> mMemoryCache;
	// 第一张可见图片的下标
	private int mFirstVisibleItem;
	// 一屏有多少张图片可见
	private int mVisibleItemCount;
	// 第一次进入
	private boolean isFirstEnter = true;
	// 上次记录的点击position
	private int pre_position = -1;
	// 每个item的大小
	private int gridSize;

	static class ViewHolder {
		public ImageView grid_img;
		public ImageView grid_ok;
		public View grid_vague;
	}

	public ImageGridAdapter(final Context context, GridView gridView,
			int maxSumToChoose, final List<GridImageBean> listItems,
			int itemViewResource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context);
		this.maxSumToChoose = maxSumToChoose;
		this.taskCollection = new HashSet<BitmapWorkerTask>();
		this.gridView = gridView;
		this.gridView.setSelected(true);
		this.listItems = listItems;
		this.itemViewResource = itemViewResource;
		this.gridSize = DimensionUtils.getScreenWidth(context) / 3 - 10;
		;
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
		gridView.setOnItemClickListener(this);
		gridView.setOnScrollListener(this);
	}

	public void initGrid(ViewHolder holder) {
		FrameLayout.LayoutParams lp_img = (FrameLayout.LayoutParams) holder.grid_img
				.getLayoutParams();
		lp_img.width = gridSize;
		lp_img.height = gridSize;
		holder.grid_img.setLayoutParams(lp_img);
		FrameLayout.LayoutParams lp_vague = (FrameLayout.LayoutParams) holder.grid_vague
				.getLayoutParams();
		lp_vague.width = gridSize;
		lp_vague.height = gridSize;
		holder.grid_vague.setLayoutParams(lp_vague);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridImageBean bean = listItems.get(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = listContainer.inflate(this.itemViewResource, null);
			holder = new ViewHolder();
			// 获取控件对象
			holder.grid_img = (ImageView) convertView
					.findViewById(R.id.grid_img);
			holder.grid_vague = convertView.findViewById(R.id.grid_vague);
			holder.grid_ok = (ImageView) convertView.findViewById(R.id.grid_ok);
			initGrid(holder);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ImageView img = holder.grid_img;
		img.setTag(bean.getPath());
		setImageView(bean.getPath(), img);
		switch (getItemViewType(position)) {
		case VIEW_TYPE_UNCHECKED:
			holder.grid_vague.setBackgroundColor(Color.TRANSPARENT);
			holder.grid_ok.setVisibility(View.INVISIBLE);
			break;
		case VIEW_TYPE_CHECKED:
			holder.grid_vague.setBackgroundColor(Color.parseColor("#88000000"));
			holder.grid_ok.setVisibility(View.VISIBLE);
			holder.grid_ok.setImageResource(R.drawable.ic_launcher);
			break;
		default:
			break;
		}
		return convertView;
	}

	@Override
	public int getItemViewType(int position) {
		if (listItems.get(position).isCheck())
			return VIEW_TYPE_CHECKED;
		else
			return VIEW_TYPE_UNCHECKED;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_SUM;
	}

	/**
	 * 将图片保存到缓存中
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从缓存中获取图片
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private void setImageView(String imageUrl, ImageView imageView) {
		Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.empty_photo);
		}
		// loadBitmaps(0, listItems.size());
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 仅当GridView静止时才去下载图片，GridView滑动时取消所有正在下载的任务
		if (scrollState == SCROLL_STATE_IDLE) {

			loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
		} else {
			cancelAllTasks();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，
		// 因此在这里为首次进入程序开启下载任务。
		if (isFirstEnter && visibleItemCount > 0) {
			// if (visibleItemCount > 0) {
			loadBitmaps(firstVisibleItem, visibleItemCount);
			isFirstEnter = false;
		}
	}

	private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
		try {
			for (int i = firstVisibleItem; i < firstVisibleItem
					+ visibleItemCount; i++) {
				String imageUrl = listItems.get(i).getPath();
				Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
				if (bitmap == null) {
					BitmapWorkerTask task = new BitmapWorkerTask(i);
					taskCollection.add(task);
					task.execute(imageUrl);
				} else {
					ImageView imageView = (ImageView) gridView
							.findViewWithTag(imageUrl);
					if (imageView == null)
						System.out.println("findViewWithTag fail ");
					if (imageView != null && bitmap != null) {
						imageView.setImageBitmap(bitmap);
						listItems.get(i).setClickable(true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancelAllTasks() {
		if (taskCollection != null) {
			for (BitmapWorkerTask task : taskCollection) {
				task.cancel(false);
			}
		}
	}

	/**
	 * 异步下载图片的任务。
	 * 
	 * @author
	 */
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		/**
		 * 图片的URL地址
		 */
		private String imageUrl;
		private int index;

		public BitmapWorkerTask(int index) {
			this.index = index;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			imageUrl = params[0];
			// 在后台开始下载图片
			Bitmap bitmap = downloadBitmap(params[0]);
			if (bitmap != null) {
				// 图片下载完成后缓存到LrcCache中
				addBitmapToMemoryCache(params[0], bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			// 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
			ImageView imageView = (ImageView) gridView
					.findViewWithTag(imageUrl);
			if (imageView == null)
				System.out.println("findViewWithTag fail ");
			if (imageView != null && bitmap != null) {

				imageView.setImageBitmap(bitmap);
				listItems.get(index).setClickable(true);
			}
			taskCollection.remove(this);
		}

		/**
		 * 
		 * 
		 * @param imageUrl
		 *            图片的URL地址
		 * @return 解析后的Bitmap对象
		 */
		private Bitmap downloadBitmap(String imageUrl) {

			int rectSize = DimensionUtils.getScreenHeight(context) / 3;
			Bitmap bitmap0 = BitmapUtils.decodeBitmapFromFile(imageUrl,
					gridSize/2, gridSize/2);
			Bitmap bitmap1 = BitmapUtils.centerSquareScaleBitmap(bitmap0,
					rectSize);
			return bitmap1;
		}

	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		GridImageBean bean = listItems.get(position);
		if (maxSumToChoose == 1) {
			//单选的设定下，每次点击，都得检查是否存在前一个选择项，将其取消
			if (pre_position >= 0 && pre_position < listItems.size()
					&& listItems.get(pre_position).isCheck()) {
				listItems.get(pre_position).setCheck(false);
				currentChooseNum = 0;
				removeURI(pre_position);				
			} 
			if(pre_position != position){
				listItems.get(position).setCheck(true);
				currentChooseNum = 1;
				addURI(position);
				pre_position = position;
			}
				
			
		} else {
			//选择数量已经到达上限
			if (currentChooseNum < maxSumToChoose) {
				if(false == bean.isCheck()){
					listItems.get(position).setCheck(true);
					++currentChooseNum;
					addURI(position);
				}else{
					listItems.get(position).setCheck(false);
					--currentChooseNum;
					removeURI(position);
				}	
			} else {
				if(false == bean.isCheck()){
					// 提醒已经到达上限

				}else{
					listItems.get(position).setCheck(false);
					--currentChooseNum;
					removeURI(position);
				}
				
			}
		}
		((ImageGridActivity)context).updateOKbtn(currentChooseNum);
		this.notifyDataSetChanged();
	}
	
	public void addURI(int position){
		uriList.add(listItems.get(position).getPath());
	}
	
	public void removeURI(int position){
		uriList.remove(listItems.get(position).getPath());
	}
	
	public ArrayList<String> getChooseURIlist(){
		return uriList;
	}

}
