package com.udacity.sandwichclub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

// REFERENCES -
// Error Image is open source and taken from : https://pixabay.com/en/monitor-404-error-problem-page-1350918/
//TODO: View Binding butterknife

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    //For list of alternate names of food
    @BindView(R.id.also_known_tv) TextView alternateNameTV;
    //For list of ingredients
    @BindView(R.id.ingredients_tv) TextView ingredientsTV;
    //For the name of originating Place
    @BindView(R.id.origin_tv) TextView originatingPlaceTV;
    //For the description about the food
    @BindView(R.id.description_tv) TextView descriptionTV;
    @BindView(R.id.image_iv) ImageView ingredientsIv;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        //Check if the Intent is null (on click from previous activity Intent)
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        int position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError();
            return;
        }

        String[] sandwiches = getResources().getStringArray(R.array.sandwich_details);
        String json = sandwiches[position];
        Sandwich sandwich = null;
        try {
            sandwich = JsonUtils.parseSandwichJson(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (sandwich == null) {
            // Sandwich data unavailable
            closeOnError();
            return;
        }

        populateUI(sandwich);

        // Add placeholder and error images for different cases
        Picasso.with(this)
                .load(sandwich.getImage())
                .placeholder(R.color.colorGray)
                .error(R.drawable.errorimage)
                .into(ingredientsIv);


        setTitle(sandwich.getMainName());
    }


    /**
     * A function to gracefully handle error in case Sandwich data can't be displayed due to any reason
     */
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Set the multiple text fields from the parsed JSON
     * Set the views and handle error cases
     *
     * @param sandwich object which was clicked on, is sent here
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populateUI(Sandwich sandwich) {
        // Handle the lists in a different function
        populateListCases(sandwich.getIngredients(), ingredientsTV);
        populateListCases(sandwich.getAlsoKnownAs(), alternateNameTV);

        // Check for empty strings
        if (TextUtils.isEmpty(sandwich.getPlaceOfOrigin())) {
            originatingPlaceTV.setText(R.string.blank_field_JSON_error_message);
        } else {
            originatingPlaceTV.setText(sandwich.getPlaceOfOrigin());
        }

        descriptionTV.setText(sandwich.getDescription());

        // Set center justified in case of Android Versions above Android O
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            descriptionTV.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }
    }


    /**
     * In case of List, set different text based on List size
     */
    private void populateListCases(List<String> sandwichList, TextView resultingTextView) {

        switch (sandwichList.size()) {

            case 0:
                resultingTextView.append(getResources().getText(R.string.blank_field_JSON_error_message));
                break;
            case 1:
                resultingTextView.append(sandwichList.get(0));
                break;

            default:
                int num = 0;
                for (String eachitem : sandwichList) {
                    resultingTextView.append(++num + ". " + eachitem + "\n");
                }
                break;
        }
    }

}