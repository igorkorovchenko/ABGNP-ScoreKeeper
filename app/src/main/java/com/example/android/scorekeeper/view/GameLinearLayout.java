package com.example.android.scorekeeper.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.scorekeeper.R;
import com.example.android.scorekeeper.model.Game;
import com.example.android.scorekeeper.model.GameAction;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Class of the LinearLayout for representation of the Game
 *
 * @package com.example.android.scorekeeper.controller
 * (c) 2018, Igor Korovchenko.
 */

@SuppressLint("ViewConstructor")
public class GameLinearLayout extends LinearLayout {

    /**
     * TAG name for debugging
     */
    private static final String TAG = "GameLinearLayout";

    /**
     * Setup for scores
     */
    private static final int MARGIN_TOP_BOTTOM_SCORE = 7; // 7dp
    private static final float TEXT_SIZE_SCORE = 36.0f; // 36sp

    /**
     * Setup for team's name
     */
    private static final float TEXT_SIZE_TEAM_NAME = 20.0f; // 20sp

    /**
     * ID offset for scoreboards' TextViews
     */
    public static final int SCORES_TEXT_VIEW_ID_OFFSET = 1000;

    /**
     * ID offset for buttons' TextViews
     */
    public static final int BUTTONS_TEXT_VIEW_ID_OFFSET = 2000;

    /**
     * Last clicked button for undo button
     */
    private int mLastClickedButtonID;

    /**
     * Model of the game for storing all statistics
     */
    private Game mGame;

    /**
     * Constructor of the class from the JSON file in assets folder
     *
     * @param context context of the app
     * @param jsonFileName JSON file from the assets folder
     */
    public GameLinearLayout(Context context, String jsonFileName) {
        super(context);
        mGame = new Game(loadJSONFromAsset(context, jsonFileName));
        buildLayout(context);
    }

    /**
     * Layout builder for creating ViewGroup with all views
     *
     * @param context context of the app
     */
    public void buildLayout(Context context) {
        int teamCount = mGame.getTeamCount();

        for (int t = 0; t < teamCount; t++) {
            LinearLayout teamLinearLayout = new LinearLayout(context);
            teamLinearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = getViewGroupLayoutParams();
            params.weight = 1.0f;
            teamLinearLayout.setLayoutParams(params);
            teamLinearLayout.addView(getNameForTeam(context, t));
            teamLinearLayout.addView(getScoreboard(context, t));
            LinearLayout playersLayout = new LinearLayout(context);
            playersLayout.setOrientation(LinearLayout.VERTICAL);
            for (int p = 0; p < mGame.getTeam(t).getPlayerCount(); p++) {
                TextView playerName = new TextView(context);
                playerName.setText(mGame.getTeam(t).getPlayer(p).getName());
                playerName.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                playersLayout.addView(playerName);
                playersLayout.addView(getButtons(context, t, p));
            }
            teamLinearLayout.addView(playersLayout);
            this.addView(teamLinearLayout);
        }
    }

    /**
     * Updating scores due to players' statistics
     *
     * @param buttonId id of the button which sent updating request
     */
    public void updateScores(int buttonId) {
        int[] scores = mGame.getScores();
        for (int i = 0; i < scores.length; i++) {
            try {
                TextView teamScores = findViewById(SCORES_TEXT_VIEW_ID_OFFSET + i);
                teamScores.setText(String.valueOf(scores[i]));
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
        }
        mLastClickedButtonID = buttonId;
    }

    /**
     * Updating all buttons' captions
     */
    public void updateCaptions() {
        for (int i = 0; i < mGame.getTeamCount(); i++) {
            for (int j = 0; j < mGame.getTeam(i).getPlayerCount(); j++) {
                for (int k = 0; k < mGame.getTeam(i).getPlayer(j).getActionCount(); k++) {
                    ((GameActionButton) findViewById(getButtonId(i, j, k))).updateCaptionsAndScores();
                }
            }
        }
    }

    /**
     * Undoing last action
     */
    public void undoLastAction() {
        Log.d(TAG, String.valueOf(mLastClickedButtonID));
        if (mLastClickedButtonID > 0) {
            GameActionButton lastButton = findViewById(mLastClickedButtonID);
            lastButton.undoAction();
            mLastClickedButtonID = -1;
        }
    }

    /**
     * Getting last pressed button's Id for state saving
     *
     * @return Id of the last pressed button
     */
    public int getLastActionId() {
        return mLastClickedButtonID;
    }

    /**
     * Setting last pressed button's Id
     *
     * @param Id saved Id of the last pressed button
     */
    public void setLastActionId(int Id) {
        mLastClickedButtonID = Id;
    }

    /**
     * Getting name of the game
     * @return game name
     */
    public String getGameName() {
        return mGame.getGameName();
    }

    /**
     * Getting game model with current state of the game
     *
     * @return game model
     */
    public ArrayList<Integer> getGameModelCurrentState() {
        return mGame.getCurrentState();
    }

    /**
     * Setting game model with saved state of the game
     */
    public void setGameModelCurrentState(ArrayList<Integer> states) {
        mGame.setCurrentState(states);
    }

    /**
     * Getting textView with name of the team
     *
     * @param context context of the app
     * @param teamIndex index of the team. First team has zero index.
     * @return textView with name of the team
     */
    private TextView getNameForTeam(Context context, int teamIndex) {
        TextView teamName = new TextView(context);
        String name = mGame.getTeam(teamIndex).getName();
        teamName.setText(name);
        teamName.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        teamName.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_TEAM_NAME);
        return teamName;
    }

    /**
     * Getting the simplest scoreboard for team
     *
     * @param context context of the app
     * @param teamIndex index of the team. First team has zero index.
     * @return LinearLayout with scores of the team
     */
    private ViewGroup getScoreboard(Context context, int teamIndex) {
        LinearLayout scoreboard = new LinearLayout(context);
        TextView teamScores = new TextView(context);
        teamScores.setText(R.string.zero_scores);
        teamScores.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        LinearLayout.LayoutParams params = getTextViewLayoutParams();
        params.setMargins(
                0,
                dp2px(MARGIN_TOP_BOTTOM_SCORE),
                0,
                dp2px(MARGIN_TOP_BOTTOM_SCORE)
        );
        teamScores.setLayoutParams(params);
        teamScores.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SCORE);
        teamScores.setId(SCORES_TEXT_VIEW_ID_OFFSET + teamIndex);
        scoreboard.addView(teamScores);
        return scoreboard;
    }

    /**
     * Getting all button for each player of the team
     *
     * @param context context of the app
     * @param teamIndex index of the team
     * @param playerIndex index of the player
     * @return ViewGroup with buttons
     */
    private ViewGroup getButtons(Context context, int teamIndex, int playerIndex) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        int buttonsCount = mGame.getTeam(teamIndex).getPlayer(playerIndex).getActionCount();
        for (int actionIndex = 0; actionIndex < buttonsCount; actionIndex++) {
            String name = mGame.getTeam(teamIndex).getPlayer(playerIndex).getGameAction(actionIndex).getName();
            GameAction model = mGame.getTeam(teamIndex).getPlayer(playerIndex).getGameAction(actionIndex).getModel();
            GameActionButton btn = new GameActionButton(context, name, model);
            btn.setId(getButtonId(teamIndex, playerIndex, actionIndex));
            layout.addView(btn);
        }
        return layout;
    }

    /**
     * Getting according game action button index
     *
     * @param teamIndex index of the team
     * @param playerIndex index of the player in selected team
     * @param actionIndex index of the action of a selected player
     * @return id of the object, instance of the GamActionButton
     */
    private int getButtonId(int teamIndex, int playerIndex, int actionIndex) {
        return BUTTONS_TEXT_VIEW_ID_OFFSET + 10 * (teamIndex + 1) + 100 * (playerIndex + 1) + (actionIndex + 1);
    }

    /**
     * Getting JSON string from the file in assets folder
     *
     * @param context context of the app
     * @param jsonFileName file name in assets folder
     * @return JSON object as a string from the file
     */
    private String loadJSONFromAsset(Context context, String jsonFileName) {
        String jsonString;
        try {
            InputStream inputStream = context.getAssets().open(jsonFileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonString;
    }

    /**
     * Getting layout parameters with matched parent width and height
     * @return LinearLayout.LayoutParams with width and height equal match_parent
     */
    private LinearLayout.LayoutParams getTextViewLayoutParams() {
        return new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
    }

    /**
     * Getting layout parameters with matched parent width and height
     * @return LinearLayout.LayoutParams with width and height equal match_parent
     */
    private LinearLayout.LayoutParams getViewGroupLayoutParams() {
        return new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
    }

    /**
     * Converting dp to px value
     *
     * @param dp value
     * @return px value
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
