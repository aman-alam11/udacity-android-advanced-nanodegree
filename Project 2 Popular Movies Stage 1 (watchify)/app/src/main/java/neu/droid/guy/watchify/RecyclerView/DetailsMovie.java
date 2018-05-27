package neu.droid.guy.watchify.RecyclerView;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import neu.droid.guy.watchify.DAO.Movie;
import neu.droid.guy.watchify.R;

public class DetailsMovie extends AppCompatActivity {

    @BindView(R.id.details_backdrop_image_view)
    ImageView backdropImageView;
    @BindView(R.id.details_text_view)
    TextView descriptionMovieTextView;
    @BindView(R.id.details_movie_title)
    TextView mMovieTitleTextView;
    @BindView(R.id.details_vote_average)
    TextView mAverageVoteTextView;
    @BindView(R.id.details_language)
    TextView mLanguageTextView;
    @BindView(R.id.details_certifications)
    TextView mCertificationsTextView;
    @BindView(R.id.release_date)
    TextView mReleaseDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);
        ButterKnife.bind(this);

        //Change Color Of Notification Bar to match the theme and colors chosen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccentGRAY));
        }


        Movie mMovieObjectData = null;
        if (getIntent().getExtras() != null) {
            mMovieObjectData = getIntent().getExtras().getParcelable("DETAILS_EXTRA");
        } else {
            return;
        }

        if(mMovieObjectData==null){
            return;
        }

        if (mMovieObjectData.getBackdropImageURL() != null) {
            //Set placeholder and error images accordingly
            Picasso.get()
                    .load(mMovieObjectData.getBackdropImageURL())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.image_error)
                    .into(backdropImageView);
        }


        // Get all data from Intent
        descriptionMovieTextView.setText(mMovieObjectData.getMovieDescription());
        mMovieTitleTextView.setText(mMovieObjectData.getMovieName());
        mAverageVoteTextView.setText(getResources().getString(R.string.ratings_tag));
        mAverageVoteTextView.append(mMovieObjectData.getAverageVote());
        mLanguageTextView.setText(mMovieObjectData.getMovieLanguage().toUpperCase());
        mCertificationsTextView.setText(getResources().getString(R.string.adult_tag));
        mCertificationsTextView.append(mMovieObjectData.getMovieRestrictions());
        mReleaseDateTextView.setText(getResources().getString(R.string.release_date_tag));
        mReleaseDateTextView.append(mMovieObjectData.getReleaseDate());
    }
}
