package com.udacity.sandwichclub.utils;

import android.text.TextUtils;
import android.util.Log;
import com.udacity.sandwichclub.model.Sandwich;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    // This contains the "mainName" String and "alsoKnownAs" JSONArray
    private static final String OUTER_JSON_NAME_JSON_TAG = "name";
    private static final String MAIN_NAME_JSON_TAG = "mainName";
    private static final String ALSO_KNOWN_AS_JSON_TAG = "alsoKnownAs";
    private static final String PLACE_OF_ORIGIN_JSON_TAG = "placeOfOrigin";
    private static final String DESCRIPTION_JSON_TAG = "description";
    private static final String IMAGE_URL_JSON_TAG = "image";
    private static final String INGREDIENTS_ARRAY_JSON_TAG = "ingredients";


    public static Sandwich parseSandwichJson(String json) throws JSONException {

        // This contains the whole JSON Object response
        JSONObject mainJSON = new JSONObject(json);

        JSONObject nameObject = mainJSON.getJSONObject(OUTER_JSON_NAME_JSON_TAG);
        String mainName = nameObject.optString(MAIN_NAME_JSON_TAG);

        //Get The JSON Array for alternate names and parse it into List<String>
        JSONArray alternateNamesJSONList = nameObject.getJSONArray(ALSO_KNOWN_AS_JSON_TAG);
        // Change JSONArray to ArrayList
        List<String> altNamesList = new ArrayList<>(alternateNamesJSONList.length());
            for (int i = 0; i < alternateNamesJSONList.length(); i++) {
                String eachAltName = alternateNamesJSONList.optString(i);
                if(!TextUtils.isEmpty(eachAltName)) {
                    altNamesList.add(alternateNamesJSONList.optString(i));
                }
            }

        // Get all the fields from JSON
        String placeOfOrigin = mainJSON.optString(PLACE_OF_ORIGIN_JSON_TAG);
        String descriptionOfFood = mainJSON.optString(DESCRIPTION_JSON_TAG);
        String urlImage = mainJSON.optString(IMAGE_URL_JSON_TAG);

        JSONArray listOfIngredientsJSONArray = mainJSON.getJSONArray(INGREDIENTS_ARRAY_JSON_TAG);
        List<String> listOfIngredients = new ArrayList<>(listOfIngredientsJSONArray.length());
        for(int i=0; i < listOfIngredientsJSONArray.length(); i++ ){
            String eachIngredient = listOfIngredientsJSONArray.optString(i);
            if(!TextUtils.isEmpty(eachIngredient)) {
                listOfIngredients.add(eachIngredient);
            }
        }

        return new Sandwich(mainName, altNamesList, placeOfOrigin, descriptionOfFood, urlImage, listOfIngredients);
    }
}
