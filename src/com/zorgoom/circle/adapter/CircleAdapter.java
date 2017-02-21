package com.zorgoom.circle.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zorgoom.circle.adapter.CommentAdapter.ICommentItemClickListener;
import com.zorgoom.circle.bean.ActionItem;
import com.zorgoom.circle.bean.CircleItem;
import com.zorgoom.circle.bean.CommentItem;
import com.zorgoom.circle.bean.FavortItem;
import com.zorgoom.circle.bean.User;
import com.zorgoom.circle.contral.CirclePublicCommentContral;
import com.zorgoom.circle.mvp.presenter.CirclePresenter;
import com.zorgoom.circle.mvp.view.ICircleViewUpdateListener;
import com.zorgoom.circle.utils.DatasUtil;
import com.zorgoom.circle.widgets.AppNoScrollerListView;
import com.zorgoom.circle.widgets.CircularImage;
import com.zorgoom.circle.widgets.MultiImageView;
import com.zorgoom.circle.widgets.SnsPopupWindow;
import com.zorgoom.circle.widgets.custom.CustomListView;
import com.zorgoom.circle.widgets.dialog.CommentDialog;
import com.zorgoom.zhihework.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @ClassName: CircleAdapter
 * @Description: 圈子列表的adapter
 * @author yiw
 * @date 2015-12-28 上午09:37:23
 *
 */
public class CircleAdapter extends BaseAdapter implements ICircleViewUpdateListener {
	private static final int ITEM_VIEW_TYPE_DEFAULT = 0;
	private static final int ITEM_VIEW_TYPE_URL = 1;
	private static final int ITEM_VIEW_TYPE_IMAGE = 2;

	private static final String ITEM_TYPE_URL = "1";
	private static final String ITEM_TYPE_IMAGE = "2";
	private static final int ITEM_VIEW_TYPE_COUNT = 3;

	private Context mContext;
	private CirclePresenter mPresenter;
	private CirclePublicCommentContral mCirclePublicCommentContral;
	private List<CircleItem> datas = new ArrayList<CircleItem>();

	public void setmCirclePublicCommentContral(CirclePublicCommentContral mCirclePublicCommentContral) {
		this.mCirclePublicCommentContral = mCirclePublicCommentContral;
	}

	public List<CircleItem> getDatas() {
		return datas;
	}

	public void setDatas(List<CircleItem> datas) {
		if (datas != null) {
			this.datas = datas;
		}
	}

	public CircleAdapter(Context context) {
		mContext = context;
		mPresenter = new CirclePresenter(this);
	}

	@Override
	public int getItemViewType(int position) {
		int itemType = ITEM_VIEW_TYPE_DEFAULT;
		CircleItem item = datas.get(position);
		if (ITEM_TYPE_URL.equals(item.getType())) {
			itemType = ITEM_VIEW_TYPE_URL;
		} else if (ITEM_TYPE_IMAGE.equals(item.getType())) {
			itemType = ITEM_VIEW_TYPE_IMAGE;
		} else {
			itemType = ITEM_VIEW_TYPE_DEFAULT;
		}
		return itemType;
	}

	@Override
	public int getViewTypeCount() {
		return ITEM_VIEW_TYPE_COUNT;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		System.out.println("====================getView====================");
		try {
			int itemViewType = getItemViewType(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = View.inflate(mContext, R.layout.cf_adapter_circle_item, null);

				System.out.println("====================getView step1====================");
				ViewStub linkOrImgViewStub = (ViewStub) convertView.findViewById(R.id.linkOrImgViewStub);
				switch (itemViewType) {
				case ITEM_VIEW_TYPE_URL:// 链接view
					linkOrImgViewStub.setLayoutResource(R.layout.cf_viewstub_urlbody);
					linkOrImgViewStub.inflate();
					LinearLayout urlBodyView = (LinearLayout) convertView.findViewById(R.id.urlBody);
					if (urlBodyView != null) {
						holder.urlBody = urlBodyView;
						holder.urlImageIv = (ImageView) convertView.findViewById(R.id.urlImageIv);
						holder.urlContentTv = (TextView) convertView.findViewById(R.id.urlContentTv);
					}
					break;
				case ITEM_VIEW_TYPE_IMAGE:// 图片view
					linkOrImgViewStub.setLayoutResource(R.layout.cf_viewstub_imgbody);
					linkOrImgViewStub.inflate();
					MultiImageView multiImageView = (MultiImageView) convertView.findViewById(R.id.multiImagView);
					if (multiImageView != null) {
						holder.multiImageView = multiImageView;
					}
					break;
				default:
					break;
				}
				holder.headIv = (CircularImage) convertView.findViewById(R.id.headIv);
				holder.nameTv = (TextView) convertView.findViewById(R.id.nameTv);
				holder.digLine = convertView.findViewById(R.id.lin_dig);

				holder.contentTv = (TextView) convertView.findViewById(R.id.contentTv);
				holder.urlTipTv = (TextView) convertView.findViewById(R.id.urlTipTv);
				holder.timeTv = (TextView) convertView.findViewById(R.id.timeTv);
				holder.deleteBtn = (TextView) convertView.findViewById(R.id.deleteBtn);
				holder.snsBtn = (ImageView) convertView.findViewById(R.id.snsBtn);

				holder.digCommentBody = (LinearLayout) convertView.findViewById(R.id.digCommentBody);
				holder.digBody = (LinearLayout) convertView.findViewById(R.id.digBody);

				holder.digList = (CustomListView) convertView.findViewById(R.id.digList);
				holder.commentList = (AppNoScrollerListView) convertView.findViewById(R.id.commentList);

				holder.digList.setDividerHeight(5);
				holder.digList.setDividerWidth(3);

				holder.bbsAdapter = new CommentAdapter(mContext);
				holder.favortAdapter = new FavortAdapter(mContext);

				holder.digList.setAdapter(holder.favortAdapter);
				holder.digList.setOnItemClickListener(null);
				holder.commentList.setAdapter(holder.bbsAdapter);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			CircleItem circleItem = datas.get(position);
			final String circleId = circleItem.getId();
			String name = circleItem.getUser().getName();
			String headImg = circleItem.getUser().getHeadUrl();
			String content = circleItem.getContent();
			String createTime = circleItem.getCreateTime();
			List<FavortItem> favortDatas = circleItem.getFavorters();
			final List<CommentItem> commentsDatas = circleItem.getComments();
			boolean hasFavort = circleItem.hasFavort();
			boolean hasComment = circleItem.hasComment();
			ImageLoader.getInstance().displayImage(headImg, holder.headIv);
			holder.nameTv.setText(name);
			holder.timeTv.setText(createTime);
			holder.contentTv.setText(content);
			if (DatasUtil.curUser.getId().equals(circleItem.getUser().getId())) {
				holder.deleteBtn.setVisibility(View.VISIBLE);
			} else {
				holder.deleteBtn.setVisibility(View.GONE);
			}
			holder.deleteBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 删除
					mPresenter.deleteCircle(circleId);
				}
			});
			if (hasFavort || hasComment) {
				if (hasFavort) {// 处理点赞列表
					holder.favortAdapter.setDatas(favortDatas);
					holder.favortAdapter.notifyDataSetChanged();
					holder.digBody.setVisibility(View.VISIBLE);
				} else {
					holder.digBody.setVisibility(View.GONE);
				}
				if (hasComment) {// 处理评论列表
					holder.bbsAdapter.setDatasource(commentsDatas);
					holder.bbsAdapter.setCommentClickListener(new ICommentItemClickListener() {
						@Override
						public void onItemClick(int commentPosition) {
							CommentItem commentItem = commentsDatas.get(commentPosition);
							if (DatasUtil.curUser.getId().equals(commentItem.getUser().getId())) {// 复制或者删除自己的评论
								CommentDialog dialog = new CommentDialog(mContext, mPresenter, commentItem, position);
								dialog.show();
							} else {// 回复别人的评论
								if (mCirclePublicCommentContral != null) {
									mCirclePublicCommentContral.editTextBodyVisible(View.VISIBLE, mPresenter, position,
											TYPE_REPLY_COMMENT, commentItem.getUser(), commentPosition);
								}
							}
						}
					});
					holder.bbsAdapter.notifyDataSetChanged();
					holder.commentList.setVisibility(View.VISIBLE);
					holder.commentList.setOnItemClickListener(null);
					holder.commentList.setOnItemLongClickListener(new OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> arg0, View view, final int commentPosition,
								long id) {
							// 长按进行复制或者删除
							CommentItem commentItem = commentsDatas.get(commentPosition);
							CommentDialog dialog = new CommentDialog(mContext, mPresenter, commentItem, position);
							dialog.show();
							return true;
						}
					});
				} else {
					holder.commentList.setVisibility(View.GONE);
				}
				holder.digCommentBody.setVisibility(View.VISIBLE);
			} else {
				holder.digCommentBody.setVisibility(View.GONE);
			}
			if (hasFavort && hasComment) {
				holder.digLine.setVisibility(View.VISIBLE);
			} else {
				holder.digLine.setVisibility(View.GONE);
			}

			final SnsPopupWindow snsPopupWindow = new SnsPopupWindow(mContext);
			// 判断是否已点赞
			String curUserFavortId = circleItem.getCurUserFavortId(DatasUtil.curUser.getId());
			if (!TextUtils.isEmpty(curUserFavortId)) {
				snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
			} else {
				snsPopupWindow.getmActionItems().get(0).mTitle = "赞";
			}
			snsPopupWindow.update();
			snsPopupWindow.setmItemClickListener(new PopupItemClickListener(position, circleItem, curUserFavortId));
			holder.snsBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					// 弹出popupwindow
					snsPopupWindow.showPopupWindow(view);
				}
			});
			holder.urlTipTv.setVisibility(View.GONE);
			switch (itemViewType) {
			case ITEM_VIEW_TYPE_URL:// 处理链接动态的链接内容和和图片
				String linkImg = circleItem.getLinkImg();
				String linkTitle = circleItem.getLinkTitle();
				ImageLoader.getInstance().displayImage(linkImg, holder.urlImageIv);
				holder.urlContentTv.setText(linkTitle);
				holder.urlBody.setVisibility(View.VISIBLE);
				holder.urlTipTv.setVisibility(View.VISIBLE);
				break;
			case ITEM_VIEW_TYPE_IMAGE:// 处理图片
				List<String> photos = circleItem.getPhotos();
				if (photos != null && photos.size() > 0) {
					holder.multiImageView.setVisibility(View.VISIBLE);
					holder.multiImageView.setList(photos, MultiImageView.MAX_WIDTH);
				} else {
					holder.multiImageView.setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("====================getView step2====================");
		return convertView;
	}

	class ViewHolder {
		public CircularImage headIv;
		public TextView nameTv;
		public TextView urlTipTv;
		/** 动态的内容 */
		public TextView contentTv;
		public TextView timeTv;
		public TextView deleteBtn;
		public ImageView snsBtn;

		public LinearLayout urlBody;
		public LinearLayout digCommentBody;
		public LinearLayout digBody;
		public View digLine;
		/** 点赞列表 */
		public CustomListView digList;
		/** 评论列表 */
		public AppNoScrollerListView commentList;
		/** 链接的图片 */
		public ImageView urlImageIv;
		/** 链接的标题 */
		public TextView urlContentTv;
		/** 图片 */
		public MultiImageView multiImageView;
		// ===========================
		public FavortAdapter favortAdapter;
		public CommentAdapter bbsAdapter;
		public SnsPopupWindow snsPopupWindow;
	}

	private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener {
		private String mFavorId;
		// 动态在列表中的位置
		private int mCirclePosition;
		private long mLasttime = 0;
		private CircleItem mCircleItem;

		public PopupItemClickListener(int circlePosition, CircleItem circleItem, String favorId) {
			this.mFavorId = favorId;
			this.mCirclePosition = circlePosition;
			this.mCircleItem = circleItem;
		}

		@Override
		public void onItemClick(ActionItem actionitem, int position) {
			switch (position) {
			case 0:// 点赞、取消点赞
				if (System.currentTimeMillis() - mLasttime < 700)// 防止快速点击操作
					return;
				mLasttime = System.currentTimeMillis();
				if ("赞".equals(actionitem.mTitle.toString())) {
					mPresenter.addFavort(mCirclePosition);
				} else {// 取消点赞
					mPresenter.deleteFavort(mCirclePosition, mFavorId);
				}
				break;
			case 1:// 发布评论
				if (mCirclePublicCommentContral != null) {
					mCirclePublicCommentContral.editTextBodyVisible(View.VISIBLE, mPresenter, mCirclePosition,
							TYPE_PUBLIC_COMMENT, null, 0);
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void update2DeleteCircle(String circleId) {
		for (int i = 0; i < datas.size(); i++) {
			if (circleId.equals(datas.get(i).getId())) {
				getDatas().remove(i);
				notifyDataSetChanged();
				return;
			}
		}
	}

	@Override
	public void update2AddFavorite(int circlePosition) {
		FavortItem item = DatasUtil.createCurUserFavortItem();
		getDatas().get(circlePosition).getFavorters().add(item);
		notifyDataSetChanged();
	}

	@Override
	public void update2DeleteFavort(int circlePosition, String favortId) {
		List<FavortItem> items = getDatas().get(circlePosition).getFavorters();
		for (int i = 0; i < items.size(); i++) {
			if (favortId.equals(items.get(i).getId())) {
				getDatas().get(circlePosition).getFavorters().remove(i);
				notifyDataSetChanged();
				return;
			}
		}
	}

	@Override
	public void update2AddComment(int circlePosition, int type, User replyUser) {
		CommentItem newItem = null;
		String content = "";
		if (mCirclePublicCommentContral != null) {
			content = mCirclePublicCommentContral.getEditTextString();
		}
		if (type == TYPE_PUBLIC_COMMENT) {
			newItem = DatasUtil.createPublicComment(content);
		} else if (type == TYPE_REPLY_COMMENT) {
			newItem = DatasUtil.createReplyComment(replyUser, content);
		}
		getDatas().get(circlePosition).getComments().add(newItem);
		notifyDataSetChanged();
		if (mCirclePublicCommentContral != null) {
			mCirclePublicCommentContral.clearEditText();
		}
	}

	@Override
	public void update2DeleteComment(int circlePosition, String commentId) {
		List<CommentItem> items = getDatas().get(circlePosition).getComments();
		for (int i = 0; i < items.size(); i++) {
			if (commentId.equals(items.get(i).getId())) {
				getDatas().get(circlePosition).getComments().remove(i);
				notifyDataSetChanged();
				return;
			}
		}
	}

}
