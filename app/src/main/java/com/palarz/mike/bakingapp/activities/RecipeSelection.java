/*
The following code is the property and sole work of Mike Palarz, a student at Udacity.
 */

package com.palarz.mike.bakingapp.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.palarz.mike.bakingapp.R;
import com.palarz.mike.bakingapp.utilities.Bakery;
import com.palarz.mike.bakingapp.utilities.RecipeAdapter;
import com.palarz.mike.bakingapp.model.Recipe;
import com.palarz.mike.bakingapp.utilities.RecipesClient;
import com.palarz.mike.bakingapp.widget.GroceryListAppWidgetProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/*
Primary purpose: This class allows the user to select a recipe from a grid of recipes. It is the
main activity of this app.
 */
public class RecipeSelection extends AppCompatActivity {

    // View that is bound using Butterknife
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    RecipeAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_selection);

        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager recyclerViewManager =
                new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(recyclerViewManager);

        mAdapter = new RecipeAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        // We use Retrofit in order to obtain the HTTP response and parse the JSON data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RecipesClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // We then create an instance of our interface and create our Call object
        RecipesClient client = retrofit.create(RecipesClient.class);
        Call<List<Recipe>> call = client.getAllRecipes();

        call.enqueue(new Callback<List<Recipe>>() {
            // If we were successful in obtaining a response to our HTTP request...
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                // ...we extract the recipes from the response...
                List<Recipe> recipes = response.body();

                // ...and then add all of the recipes to our bakery as well as our adapter for this
                // activity.
                Bakery.get().addRecipes(recipes);
                mAdapter.swapRecipes(Bakery.get().getRecipes());

                // After all of this is complete, we ensure that our widget also contains the latest data
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(RecipeSelection.this);
                int[] appWidgetIDs = appWidgetManager.getAppWidgetIds(new ComponentName(RecipeSelection.this,
                        GroceryListAppWidgetProvider.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIDs, R.id.app_widget_list_view);
            }

            // If we failed to obtain an HTTP response, we at least notify the user.
            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Toast.makeText(RecipeSelection.this, "Something went wrong :(", Toast.LENGTH_LONG).show();
            }
        });

    }

}
