package neu.droid.guy.watchify.FirstRunScreens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import neu.droid.guy.watchify.POJO.Movie;
import neu.droid.guy.watchify.MainActivity.MainActivity;
import neu.droid.guy.watchify.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ScreenThree#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenThree extends Fragment{


    public ScreenThree() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScreenThree.
     */
    // TODO: Rename and change types and number of parameters
    public static ScreenThree newInstance() {
        return new ScreenThree();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_screen_three, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button doneButton  = view.findViewById(R.id.doneButton);
        final List<Movie> dataToSend = ((FirstRun) Objects.requireNonNull(getActivity())).getMovieData();
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMainActivity = new Intent(getActivity().getBaseContext(), MainActivity.class);
                startMainActivity.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                        (ArrayList<? extends Parcelable>) dataToSend);
                startActivity(startMainActivity);
                getActivity().finish();
            }
        });
    }

}
