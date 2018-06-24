package neu.droid.guy.watchify.MainActivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.watchify.POJO.Movie;
import neu.droid.guy.watchify.R;

public class WatchifyAdapter extends RecyclerView.Adapter<WatchifyAdapter.WatchifyViewHolder> {

    private clickListener mClickListener;
    private List<Movie> movieData;

    /**
     * For registering clicks
     */
    public interface clickListener {
        void itemClicked(int index);
    }


    /**
     * Default constructor
     */
    WatchifyAdapter(List<Movie> dataFromAPI, clickListener passedClickListener) {
        movieData = dataFromAPI;
        this.mClickListener = passedClickListener;
    }


    @NonNull
    @Override
    public WatchifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View viewInflated = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_main, parent, false);
        return new WatchifyViewHolder(viewInflated);
    }

    @Override
    public void onBindViewHolder(@NonNull final WatchifyViewHolder holder, int position) {
        holder.movieNameTextView.setText(movieData.get(position).getMovieName());
        holder.movieRatingTextView.setText(movieData.get(position).getAverageVote());

        Picasso.get()
                .load(movieData.get(position).getImageURL())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.image_error)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(holder.moviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (movieData != null) {
            return movieData.size();
        }
        return 0;
    }


    /*
     * Cache Views
     * */
    class WatchifyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_rv_movie_title)
        TextView movieNameTextView;
        @BindView(R.id.item_rv_movie_rating)
        TextView movieRatingTextView;
        @BindView(R.id.item_rv_image)
        ImageView moviePosterImageView;

        WatchifyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            mClickListener.itemClicked(getAdapterPosition());
        }
    }//End of ViewHolder

}

