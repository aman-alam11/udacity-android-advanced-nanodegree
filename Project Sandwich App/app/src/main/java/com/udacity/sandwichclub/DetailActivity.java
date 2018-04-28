package com.udacity.sandwichclub;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.JsonUtils;

import org.json.JSONException;

import java.util.List;

// REFERENCES -
// Error Image is open source and taken from : https://pixabay.com/en/monitor-404-error-problem-page-1350918/

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;

    //For list of alternate names of food
    private TextView alternateNameTV;
    //For list of ingredients
    private TextView ingredientsTV;
    //For the name of originating Place
    private TextView originatingPlaceTV;
    //For the description about the food
    private TextView descriptionTV;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        ImageView ingredientsIv = findViewById(R.id.image_iv);
        alternateNameTV = findViewById(R.id.also_known_tv);
        ingredientsTV = findViewById(R.id.ingredients_tv);
        originatingPlaceTV = findViewById(R.id.origin_tv);
        descriptionTV = findViewById(R.id.description_tv);

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
    * */
    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }


    /**
    * Set the multiple text fields from the parsed JSON
    * Set the views and handle error cases
    * @param sandwich object which was clicked on, is sent here
    * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void populateUI(Sandwich sandwich) {

        // Handle the lists in a different function
        populateListCases(sandwich.getIngredients(), ingredientsTV);
        populateListCases(sandwich.getAlsoKnownAs(), alternateNameTV);

        // Check for empty strings
        if(TextUtils.isEmpty(sandwich.getPlaceOfOrigin())){
            originatingPlaceTV.setText(R.string.blank_field_JSON_error_message);
        }else {
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
    * */
    private void populateListCases(List<String> sandwichList, TextView resultingTextView){

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
