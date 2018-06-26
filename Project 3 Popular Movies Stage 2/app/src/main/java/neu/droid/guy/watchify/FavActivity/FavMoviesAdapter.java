package neu.droid.guy.watchify.FavActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.watchify.POJO.Movie;
import neu.droid.guy.watchify.R;
import timber.log.Timber;

public class FavMoviesAdapter extends RecyclerView.Adapter<FavMoviesAdapter.FavViewHolder> {

    private List<Movie> listOfFavMovies;
    private onUnfavouriteButtonClicked unfavoriteClicked;
    private final String LOG_TAG = getClass().getSimpleName();

    public interface onUnfavouriteButtonClicked {
        void getMovieClicked(Movie movieToDelete, Boolean shoudlDelete);
    }


    /**
     * Default Constructor
     */
    public FavMoviesAdapter(List<Movie> listOfMovies,
                            onUnfavouriteButtonClicked itemClicked) {
        this.listOfFavMovies = listOfMovies;
        this.unfavoriteClicked = itemClicked;
    }

    /**
     * Called when RecyclerView needs a new {@link FavViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(FavViewHolder, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(FavViewHolder, int)
     */
    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_fav, parent, false);
        return new FavViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link FavViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link FavViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(FavViewHolder, int)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        holder.favMovieNameTextView.setText(listOfFavMovies.get(position).getMovieName());
        try {
            Float movieRating = Float.valueOf(listOfFavMovies.get(position).getAverageVote());
            holder.ratingsTextView.setText(String.valueOf(movieRating / 2));
            holder.favMovieRatingBar.setRating(movieRating / 2);
        } catch (Exception e) {
            Timber.e(e);
        }

        Picasso.get().load(listOfFavMovies.get(position).getBackdropImageURL()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                holder.rootCardView.setBackground(new BitmapDrawable(bitmap));
                // Set Alpha to 12.5%
                holder.rootCardView.getBackground().setAlpha(32);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e(LOG_TAG, e.getMessage());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (listOfFavMovies != null && listOfFavMovies.size() > 0) {
            return listOfFavMovies.size();
        }
        return 0;
    }


    /**
     * View Holder For Favourites View
     */
    class FavViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_name_fav_screen)
        TextView favMovieNameTextView;
        @BindView(R.id.ratings_bar_fav_screen)
        RatingBar favMovieRatingBar;
        @BindView(R.id.ratings_tv_fav_screen)
        TextView ratingsTextView;
        @BindView(R.id.remove_fav_button_fav_screen)
        ImageView removeFavButton;
        @BindView(R.id.root_view_item_fav_screen)
        CardView rootCardView;

        FavViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.findViewById(R.id.remove_fav_button_fav_screen).setOnClickListener(v1 -> {
                unfavoriteClicked.getMovieClicked(listOfFavMovies.get(getAdapterPosition()), true);
            });
            itemView.findViewById(R.id.root_view_item_fav_screen).setOnClickListener(v ->
                    unfavoriteClicked.getMovieClicked(listOfFavMovies.get(getAdapterPosition()), false));
        }

    }
}
