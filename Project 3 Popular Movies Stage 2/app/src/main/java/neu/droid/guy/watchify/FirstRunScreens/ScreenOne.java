package neu.droid.guy.watchify.FirstRunScreens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Objects;

import neu.droid.guy.watchify.R;
import neu.droid.guy.watchify.MainActivity.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScreenOne#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScreenOne extends Fragment implements View.OnClickListener{

    Button skipButtonFragment1;
    Button nextButtonFragment1;

    public ScreenOne() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ScreenOne.
     */
    public static ScreenOne newInstance() {
        return new ScreenOne();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_screen_one, container, false);

        skipButtonFragment1 = rootView.findViewById(R.id.skip_button_1);
        nextButtonFragment1 = rootView.findViewById(R.id.next_button_1);
        skipButtonFragment1.setOnClickListener(this);
        nextButtonFragment1.setOnClickListener(this);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.skip_button_1:
                Intent startMainActivity = new Intent(Objects.requireNonNull(getActivity()).getBaseContext(),
                        MainActivity.class);
                startMainActivity.putParcelableArrayListExtra(Intent.EXTRA_STREAM,
                        (ArrayList<? extends Parcelable>) ((FirstRun) getActivity()).getMovieData());
                startActivity(startMainActivity);
                getActivity().finish();
                break;
            case R.id.next_button_1:
                ((FirstRun) Objects.requireNonNull(getActivity())).mViewPager.setCurrentItem(1);
                break;
        }
    }
}
