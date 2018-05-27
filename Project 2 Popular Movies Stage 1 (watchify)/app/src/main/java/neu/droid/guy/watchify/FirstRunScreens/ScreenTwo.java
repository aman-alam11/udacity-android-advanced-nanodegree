package neu.droid.guy.watchify.FirstRunScreens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Objects;
import neu.droid.guy.watchify.R;
import neu.droid.guy.watchify.RecyclerView.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ScreenTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenTwo extends Fragment implements View.OnClickListener{

    Button skipButton2;
    Button nextButton2;

    public ScreenTwo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScreenTwo.
     */
    public static ScreenTwo newInstance() {
        return new ScreenTwo();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_screen_two, container, false);

        skipButton2 = root.findViewById(R.id.skip_button_2);
        nextButton2 = root.findViewById(R.id.next_button_2);
        skipButton2.setOnClickListener(this);
        nextButton2.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.skip_button_2:
                Intent startMainActivity = new Intent(Objects.requireNonNull(getActivity()).getBaseContext(),
                        MainActivity.class);
                startMainActivity.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                        (ArrayList<? extends Parcelable>) ((FirstRun) getActivity()).getMovieData());
                startActivity(startMainActivity);
                getActivity().finish();
                break;
            case R.id.next_button_2:
                ((FirstRun) Objects.requireNonNull(getActivity())).mViewPager.setCurrentItem(2);
                break;
        }
    }
}
