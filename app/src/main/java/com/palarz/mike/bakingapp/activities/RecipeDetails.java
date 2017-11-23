/*
The following code is the property and sole work of Mike Palarz, a student at Udacity.
 */

package com.palarz.mike.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.palarz.mike.bakingapp.R;
import com.palarz.mike.bakingapp.model.Recipe;
import com.palarz.mike.bakingapp.utilities.Bakery;
import com.palarz.mike.bakingapp.utilities.RecipeAdapter;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
An activity which displays additional information about the recipe that was selected in
RecipeSelection.
 */
public class RecipeDetails extends AppCompatActivity {

    // String used to obtain Recipe ID from Intent that started this activity
    public static final String EXTRA_RECIPE_ID = "com.palarz.mike.bakingapp.recipe_id";

    // Views that are bound using Butterknife
    @BindView(R.id.recipe_details_name) TextView mNameTV;
    @BindView(R.id.recipe_details_image) ImageView mImageIV;
    @BindView(R.id.recipe_details_ingredients) TextView mIngredientsTV;
    @BindView(R.id.recipe_details_start_cooking_button) Button mStartCookingButton;

    // References to the recipe and it's corresponding ID
    Recipe mClickedRecipe;
    int mRecipeID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        // We bind the views using Butterknife
        ButterKnife.bind(this);

        // If the Intent was not null and if it contained the recipe's ID...
        Intent receivedIntent = getIntent();
        if (receivedIntent != null){
            if (receivedIntent.hasExtra(RecipeAdapter.EXTRA_RECIPE_ID)){
                mRecipeID = receivedIntent.getIntExtra(RecipeAdapter.EXTRA_RECIPE_ID, 0);

                // ...We obtain the recipe from the Bakery and set up the views accordingly.
                mClickedRecipe = Bakery.get().getRecipe(mRecipeID);

                mNameTV.setText(mClickedRecipe.getName());
                String recipeImage = mClickedRecipe.getImage();

                /*
                Due to the way that we're setting the image of the recipe within RecipeAdapter, it
                is possible that the recipe's image is actually a drawable ID. Therefore, we check
                for this and either load the drawable or download the image and display it.
                 */
                try{
                    mImageIV.setImageResource(Integer.valueOf(recipeImage));
                }
                catch (NumberFormatException nfe){
                    Picasso.with(this)
                            .load(recipeImage)
                            .placeholder(R.drawable.hourglass)
                            .into(mImageIV);
                }
                catch (Exception e){
                    Toast.makeText(this, "Could not load image recourse", Toast.LENGTH_SHORT).show();
                    finish();
                }

                mIngredientsTV.setText(mClickedRecipe.printIngredients());

                // Lastly, we set the onClickListener of the "Let's start cooking" button to
                // launch the StepSelection activity.
                mStartCookingButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        Intent stepDisplayIntent = new Intent(getApplicationContext(), StepDisplay.class);
                        stepDisplayIntent.putExtra(EXTRA_RECIPE_ID, mRecipeID);
                        startActivity(stepDisplayIntent);
                    }
                });
            }
        }

    }

}
