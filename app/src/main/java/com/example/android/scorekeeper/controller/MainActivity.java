package com.example.android.scorekeeper.controller;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.scorekeeper.R;
import com.example.android.scorekeeper.view.GameLinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Keys for Bundle state saving
     */
    static final String STATE_SELECTED_GAME = "selectedGameState";
    static final String STATE_GAME_STATE = "gameModelCurrentState";
    static final String STATE_LAST_PRESSED_BUTTON_ID = "lastButtonIdState";

    /**
     * Chosen game
     */
    private int mSelectedGame = -1;

    /**
     * GameLinearLayout id
     */
    private int GAME_LINEAR_LAYOUT_ID = 999;

    /**
     * Games' names
     */
    private ArrayAdapter<String> mGameAdapter;

    /**
     * Files name with games info as JSON format
     */
    private List<String> mGameFiles;

    /**
     * One of main methods of the activity
     * @param savedInstanceState saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGameAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.game_names)
        ){
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView listItem = view.findViewById(android.R.id.text1);
                listItem.setTextColor(getResources().getColor(R.color.color_text_secondary));
                return view;
            }
        };

        mGameFiles = Arrays.asList(getResources().getStringArray(R.array.game_files));

        setupGameSelector();
    }

    /**
     * Method for saving current state of the Activity
     *
     * @param savedInstanceState saved state if the activity
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STATE_SELECTED_GAME, mSelectedGame);
        if ((findViewById(R.id.parent_view)).getVisibility() == View.VISIBLE) {
            GameLinearLayout gameLayout = findViewById(GAME_LINEAR_LAYOUT_ID);
            savedInstanceState.putIntegerArrayList(STATE_GAME_STATE, gameLayout.getGameModelCurrentState());
            savedInstanceState.putInt(STATE_LAST_PRESSED_BUTTON_ID, gameLayout.getLastActionId());
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        mSelectedGame = savedInstanceState.getInt(STATE_SELECTED_GAME);
        if (mSelectedGame >= 0) {
            ArrayList<Integer> savedStates = savedInstanceState.getIntegerArrayList(STATE_GAME_STATE);
            inflateGameLayout(mGameFiles.get(mSelectedGame));
            GameLinearLayout gameLayout = findViewById(GAME_LINEAR_LAYOUT_ID);
            gameLayout.setGameModelCurrentState(savedStates);
            gameLayout.updateCaptions();
            gameLayout.setLastActionId(savedInstanceState.getInt(STATE_LAST_PRESSED_BUTTON_ID));
        }
    }

    /**
     * Actions due to clicking button "New game"
     *
     * @param view current view
     */
    public void onResetGame(View view) {
        setupGameSelector();
    }

    /**
     * Actions due to clicking button "Reset"
     *
     * @param view current view
     */
    public void onResetScores(View view) {
        inflateGameLayout(mGameFiles.get(mSelectedGame));
    }

    /**
     * Actions due to clicking button "Undo"
     *
     * @param view current view
     */
    public void onUndo(View view) {
        ((GameLinearLayout) findViewById(GAME_LINEAR_LAYOUT_ID)).undoLastAction();
    }

    /**
     * Setup ListView (child of the parent's ScrollView) for selection of the game
     */
    private void setupGameSelector() {
        String activityTitle =
                getResources().getString(R.string.app_name)
                + " - "
                + getResources().getString(R.string.first_selection);
        setTitle(activityTitle);
        ScrollView parentView = findViewById(R.id.parent_view);
        parentView.setVisibility(View.GONE);
        parentView.removeAllViews();
        ListView gameSelector = findViewById(R.id.game_selector);
        gameSelector.setVisibility(View.VISIBLE);
        enableGameButtons(false);
        inflateGameSelector(gameSelector);
        mSelectedGame = -1;
    }

    /**
     * Inflating game selector by content
     *
     * @param gameSelector ListView for the list of games
     */
    private void inflateGameSelector(ListView gameSelector) {
        gameSelector.setAdapter(mGameAdapter);
        gameSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedGame = position;
                inflateGameLayout(mGameFiles.get(position));
            }
        });
    }

    /**
     * Inflating parent view (ScrollView) by
     *
     * @param jsonFileName file name of the game info as JSON object
     */
    private void inflateGameLayout(String jsonFileName) {
        GameLinearLayout gameLayout = new GameLinearLayout(this, jsonFileName);
        gameLayout.setId(GAME_LINEAR_LAYOUT_ID);
        String activityTitle = getResources().getString(R.string.app_name) + " - " + gameLayout.getGameName();
        setTitle(activityTitle);
        ScrollView parentView = findViewById(R.id.parent_view);
        parentView.setVisibility(View.VISIBLE);
        parentView.removeAllViews();
        parentView.addView(gameLayout);
        ListView gameSelector = findViewById(R.id.game_selector);
        gameSelector.setVisibility(View.GONE);
        gameSelector.setAdapter(null);
        enableGameButtons(true);
    }

    /**
     * Enabling / Disabling Undo and Reset button
     *
     * @param flag boolean: true - enable, false - disable
     */
    private void enableGameButtons(boolean flag) {
        Button btnUndo = findViewById(R.id.button_undo);
        Button btnReset = findViewById(R.id.button_reset_scores);
        if (flag) {
            btnUndo.setBackgroundColor(getResources().getColor(R.color.color_primary_dark));
            btnReset.setBackgroundColor(getResources().getColor(R.color.color_primary_dark));
        } else {
            btnUndo.setBackgroundColor(getResources().getColor(R.color.color_accent));
            btnReset.setBackgroundColor(getResources().getColor(R.color.color_accent));
        }
        btnUndo.setEnabled(flag);
        btnReset.setEnabled(flag);
    }
}
