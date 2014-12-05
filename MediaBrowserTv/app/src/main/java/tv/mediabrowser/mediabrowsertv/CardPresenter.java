package tv.mediabrowser.mediabrowsertv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import mediabrowser.model.dto.BaseItemDto;

public class CardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    private static Context mContext;
    private TvApp application;

    public CardPresenter() {
        super();
        application = TvApp.getApplication();

    }

    static class ViewHolder extends Presenter.ViewHolder {
        private int cardWidth = 230;
        private int cardHeight = 360;
        private BaseItemDto mItem;
        private ImageCardView mCardView;
        private Drawable mDefaultCardImage;
        private PicassoImageCardViewTarget mImageCardViewTarget;

        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;

            mImageCardViewTarget = new PicassoImageCardViewTarget(mCardView);
            mDefaultCardImage = mContext.getResources().getDrawable(R.drawable.movie);
        }

        public void setItem(BaseItemDto m) {
            mItem = m;
            cardWidth = (int)(mItem.getPrimaryImageAspectRatio() * cardHeight);
            mCardView.setMainImageDimensions(cardWidth, cardHeight);
        }

        public BaseItemDto getItem() {
            return mItem;
        }

        public ImageCardView getCardView() {
            return mCardView;
        }

        protected void updateCardViewImage(String url) {
            Picasso.with(mContext)
                    .load(url)
                    .resize(cardWidth, cardHeight)
                    .centerCrop()
                    .error(mDefaultCardImage)
                    .into(mImageCardViewTarget);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        //Log.d(TAG, "onCreateViewHolder");
        mContext = parent.getContext();

        ImageCardView cardView = new ImageCardView(mContext);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setBackgroundColor(mContext.getResources().getColor(R.color.fastlane_background));
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        BaseItemDto baseItem = (BaseItemDto) item;
        ((ViewHolder) viewHolder).setItem(baseItem);

        //Log.d(TAG, "onBindViewHolder");
        if (baseItem != null) {
            ((ViewHolder) viewHolder).mCardView.setTitleText(baseItem.getName());
            //((ViewHolder) viewHolder).mCardView.setContentText(baseItem.getProductionYear().toString());
            //((ViewHolder) viewHolder).mCardView.setBadgeImage(mContext.getResources().getDrawable(
            //        R.drawable.videos_by_google_icon));

            ((ViewHolder) viewHolder).updateCardViewImage(Utils.getPrimaryImageUrl(baseItem, application.getConnectionManager().GetApiClient(baseItem)));

        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        //Log.d(TAG, "onUnbindViewHolder");
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
        //Log.d(TAG, "onViewAttachedToWindow");
    }

    public static class PicassoImageCardViewTarget implements Target {
        private ImageCardView mImageCardView;

        public PicassoImageCardViewTarget(ImageCardView mImageCardView) {
            this.mImageCardView = mImageCardView;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            Drawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
            mImageCardView.setMainImage(bitmapDrawable);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            mImageCardView.setMainImage(drawable);
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
            // Do nothing, default_background manager has its own transitions
        }
    }

}