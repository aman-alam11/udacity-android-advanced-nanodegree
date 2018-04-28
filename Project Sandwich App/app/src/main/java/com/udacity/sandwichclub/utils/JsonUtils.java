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

//TODO: Check if JSON is passed without null and all

public class JsonUtils {

    public static Sandwich parseSandwichJson(String json) throws JSONException {

        // This contains the whole JSON Object response
        JSONObject mainJSON = new JSONObject(json);

        // This contains the "mainName" String and "alsoKnownAs" JSONArray
        JSONObject nameObject = mainJSON.getJSONObject("name");
        String mainName = nameObject.getString("mainName");

        JSONArray alternateNamesJSONList = nameObject.getJSONArray("alsoKnownAs");
        // Change JSONArray to ArrayList
        List<String> altNamesList = new ArrayList<>(alternateNamesJSONList.length());
            for (int i = 0; i < alternateNamesJSONList.length(); i++) {
                String eachAltName = (alternateNamesJSONList.getString(i) != null) ?
                                        alternateNamesJSONList.getString(i) : "";
                if(!TextUtils.isEmpty(eachAltName)) {
                    altNamesList.add(alternateNamesJSONList.getString(i));
                }
            }


        String placeOfOrigin = mainJSON.getString("placeOfOrigin");
        String descriptionOfood = mainJSON.getString("description");
        String urlImage = mainJSON.getString("image");

        JSONArray listOfIngredientsJSONArray = mainJSON.getJSONArray("ingredients");
        List<String> listOfIngredients = new ArrayList<>(listOfIngredientsJSONArray.length());
        for(int i=0; i < listOfIngredientsJSONArray.length(); i++ ){
            String eachIngredient = (listOfIngredientsJSONArray.getString(i) != null) ?
                                        listOfIngredientsJSONArray.getString(i) : "";

            if(!TextUtils.isEmpty(eachIngredient)) {
                listOfIngredients.add(eachIngredient);
            }
        }

        return new Sandwich(mainName, altNamesList, placeOfOrigin, descriptionOfood, urlImage, listOfIngredients);
    }
}
