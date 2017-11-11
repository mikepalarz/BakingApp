package com.palarz.mike.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.palarz.mike.bakingapp.R;
import com.palarz.mike.bakingapp.fragments.StepWatcher;
import com.palarz.mike.bakingapp.model.Step;
import com.palarz.mike.bakingapp.fragments.StepSelection;
import com.palarz.mike.bakingapp.utilities.Bakery;
import com.palarz.mike.bakingapp.utilities.StepAdapter;

import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by mpala on 10/19/2017.
 */

public class StepDisplay extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    public static final String BUNDLE_KEY_STEPS = "com.palarz.mike.bakingapp.activities.steps";
    public static final String TAG = StepDisplay.class.getSimpleName();

    private boolean mTwoPane;

    // TODO: Perhaps look into reusing that SingleFragmentActivity class from Big Nerd Ranch?

    @Override
    public void onBackStackChanged() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Timber.d("Contents of backstack with onBackStackChanged():\n");
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++){
            Timber.d("Index: " + i + "; backstack entry ID: " + fragmentManager.getBackStackEntryAt(i));
        }
        Timber.d("\n");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_display);

        /* It is important for us to check if savedInstanceState is null. It is only null the first
        * time the activity is created. In that moment, we want the StepSelection fragment to
        * be the only fragment that is being displayed.
        *
        * If we didn't check this, then a StepSelection would be added to the display every time
        * the screen would be rotated.
        */

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState == null) {

            Intent receivedIntent = getIntent();
            if (receivedIntent.hasExtra(RecipeDetails.EXTRA_RECIPE_ID)) {

                int recipeID = receivedIntent.getIntExtra(RecipeDetails.EXTRA_RECIPE_ID, 0);
                StepSelection stepSelection = StepSelection.newInstance(recipeID);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.step_display_current_step, stepSelection)
                        .addToBackStack(null)
                        .commit();
            }

        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStack();
            Timber.d("Contents of backstack within onBackPressed():\n");
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++){
                Timber.d("Index: " + i + "; backstack entry ID: " + fragmentManager.getBackStackEntryAt(i));
            }
            Timber.d("\n");
        }
        else {
            super.onBackPressed();
        }
    }
}
