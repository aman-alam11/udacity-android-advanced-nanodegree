package neu.droid.guy.watchify.DetailsActivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.watchify.POJO.Review;
import neu.droid.guy.watchify.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewsData;
    private clickListenerReviewTextView mClickListener;
    private HashMap<Integer, String> reviewTextHashMap = new HashMap<>();
    private HashMap<Integer, String> authorNameHashmap = new HashMap<>();


    /**
     * For registering clicks
     */
    public interface clickListenerReviewTextView {
        void itemClicked(int index, String review, String authorName);
    }

    /**
     * Default constructor
     *
     * @param reviewsFromAPI
     * @param listener
     */
    ReviewAdapter(List<Review> reviewsFromAPI, clickListenerReviewTextView listener) {
        this.mClickListener = listener;
        this.reviewsData = reviewsFromAPI;
    }

    /**
     * Called when RecyclerView needs a new {@link ReviewViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ReviewViewHolder, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ReviewViewHolder, int)
     */
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_review, parent, false);
        return new ReviewViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ReviewViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ReviewViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ReviewViewHolder, int)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        authorNameHashmap.put(position, reviewsData.get(position).getAuthorName());
        reviewTextHashMap.put(position, reviewsData.get(position).getReviewWritten());
        holder.authorTextView.setText(authorNameHashmap.get(position));
        if (reviewTextHashMap.get(position).length() < 40
                && reviewTextHashMap.get(position).length() > 0
                && reviewTextHashMap.get(position) != null) {
            holder.reviewText.setText(reviewTextHashMap.get(position));
        } else {
            holder.reviewText.setText(reviewTextHashMap.get(position).substring(0, 40) + "....SEE MORE");
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return reviewsData.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.author_name)
        TextView authorTextView;
        @BindView(R.id.actual_review_text)
        TextView reviewText;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            mClickListener.itemClicked(getAdapterPosition(),
                    reviewTextHashMap.get(getAdapterPosition()),
                    authorNameHashmap.get(getAdapterPosition()));
        }
    }
}