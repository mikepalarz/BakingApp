package com.palarz.mike.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.palarz.mike.bakingapp.R;
import com.palarz.mike.bakingapp.fragments.StepSelection;
import com.palarz.mike.bakingapp.fragments.StepWatcher;
import com.palarz.mike.bakingapp.utilities.StepAdapter;
import com.palarz.mike.bakingapp.utilities.Utilities;

import timber.log.Timber;

public class StepDisplay extends AppCompatActivity
        implements StepAdapter.StepLoader, StepWatcher.StepSwitcher {

    // A key that is used for the Bundle created within onSaveInstanceState()
    private static final String BUNDLE_SIS_KEY_RECIPE_ID = "step_display_recipe_id";

    private static final String FRAGMENT_TAG = "fragmentTAG";

    StepSelection mFragment;
    private boolean mTwoPane;
    private int mRecipeID;

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

        if (savedInstanceState == null) {

            Intent receivedIntent = getIntent();
            if (receivedIntent.hasExtra(RecipeDetails.EXTRA_RECIPE_ID)) {

                mRecipeID = receivedIntent.getIntExtra(RecipeDetails.EXTRA_RECIPE_ID, 0);
                StepSelection stepSelection = StepSelection.newInstance(mRecipeID);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.step_display_list_of_steps, stepSelection, StepSelection.class.getSimpleName())
                        .commit();

//                mFragment = StepSelection.newInstance(mRecipeID);
            }
        }
        else {
            // Otherwise, if savedInstanceState does exist, then we extract mRecipeID from the
            // Bundle so that we have it on hand.
            mRecipeID = savedInstanceState.getInt(BUNDLE_SIS_KEY_RECIPE_ID);

//            mFragment = (StepSelection) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }

//        if (!mFragment.isInLayout()){
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.step_display_list_of_steps, mFragment, FRAGMENT_TAG)
//                    .commit();
//        }

        // All of the previous setup applies to both a handset and a tablet. Additional setup is
        // required for the tablet.
        if (findViewById(R.id.step_display_tablet_contents) != null){
            mTwoPane = true;

            StepWatcher watcher = StepWatcher.newInstance(mRecipeID, 0);

            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.step_display_tablet_video, watcher)
                    .commit();
        }
    }


    /*
    We ensure that mRecipeID is saved so that it can retrieved after an orientation change. This is
    necessary since certain parts of StepDisplay depend on mRecipeID, such as in loadNextStep() for
    the StepAdapter.StepLoader interface.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_SIS_KEY_RECIPE_ID, mRecipeID);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStack(StepWatcher.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void loadNextStep(int stepIndex) {

        if (mTwoPane){
            loadTabletVideo(stepIndex);
        }
        else {
            switchStep(stepIndex);

        }
    }

    @Override
    public void switchStep(int stepIndex) {
        if (mTwoPane){
            loadTabletVideo(stepIndex);
        }
        else {
            StepWatcher watcher = StepWatcher.newInstance(mRecipeID, stepIndex);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.step_display_list_of_steps, watcher, FRAGMENT_TAG)
                    .addToBackStack(StepWatcher.class.getSimpleName())
                    .commit();
        }
    }

    private void loadTabletVideo(int stepIndex){
        StepWatcher watcher = StepWatcher.newInstance(mRecipeID, stepIndex);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.step_display_tablet_video, watcher)
                .commit();
    }
}
