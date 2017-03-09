package chaatz.tl1.views;

/**
 * Created by SHEILD on 3/3/2017.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import chaatz.tl1.R;
import chaatz.tl1.models.DataItem;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<DataItem> mDataset = new ArrayList<DataItem>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class DetailViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTiTleTextView;
        public TextView mDescTextView;
        public DetailViewHolder(View v) {
            super(v);

            mImageView = (ImageView) v.findViewById(R.id.iv_image);
            mTiTleTextView = (TextView) v.findViewById(R.id.tv_title);
            mDescTextView = (TextView) v.findViewById(R.id.tv_desc);
        }
    }
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public ImageViewHolder(View v) {
            super(v);

            mImageView = (ImageView) v.findViewById(R.id.iv_image_large);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar mProgressBar;

        public ProgressViewHolder(View v) {
            super(v);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(Context context) {
        mContext = context;
    }
    public void appendData(DataItem data) {
        mDataset.add(data);
    }

    public void clearData() { mDataset.clear(); }

    public void insertProgressFooter() {
        mDataset.add(new DataItem(null));
        notifyItemInserted(mDataset.size() - 1);
    }
    public void removeProgressFooter() {
        mDataset.remove(mDataset.size() - 1);
        notifyItemRemoved(mDataset.size());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case DataItem.IMAGE_WITH_DETAIL: {
                // create a new view
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.detail_item, parent, false);
                // set the view's size, margins, paddings and layout parameters
                DetailViewHolder vh = new DetailViewHolder(v);
                return vh;
            }
            case DataItem.IMAGE_ONLY: {
                // create a new view
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.image_item, parent, false);
                // set the view's size, margins, paddings and layout parameters
                ImageViewHolder vh = new ImageViewHolder(v);
                return vh;
            }
            case DataItem.LOADING_FOOTER: {
                // create a new view
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.loading_footer, parent, false);
                // set the view's size, margins, paddings and layout parameters
                ProgressViewHolder vh = new ProgressViewHolder(v);
                return vh;
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Drawable placeHolder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            placeHolder = mContext.getResources().getDrawable(R.mipmap.ic_launcher, null);
        } else{
            placeHolder = mContext.getResources().getDrawable(R.mipmap.ic_launcher);
        }
        if (position < mDataset.size()) {
            switch (mDataset.get(position).getType()) {
                case DataItem.IMAGE_WITH_DETAIL:
                    DetailViewHolder h = (DetailViewHolder) holder;
                    h.mTiTleTextView.setText(mDataset.get(position).getTitle() + " #" + position);
                    h.mDescTextView.setText(mDataset.get(position).getDescription() + " #" + position);
                    Picasso.with(mContext).load(mDataset.get(position).getImageUrl()).resize(150, 150).centerCrop().placeholder(placeHolder).into(h.mImageView);
                    break;
                case DataItem.IMAGE_ONLY:
                    ImageViewHolder ih = (ImageViewHolder) holder;
                    Picasso.with(mContext).load(mDataset.get(position).getImageUrl()).fit().placeholder(placeHolder).into(ih.mImageView);
                    break;
                default:
                    break;
            }
        } else {
            ProgressViewHolder vh = (ProgressViewHolder)holder;
            vh.mProgressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mDataset.size()) {
            return mDataset.get(position).getType();
        } else {
            return DataItem.LOADING_FOOTER;
        }
    }
}
