package com.example.android.scorekeeper.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class of the game's model with all information of the game
 *
 * @package com.example.android.scorekeeper
 * (c) 2018, Igor Korovchenko.
 */

public class Game {

    /**
     * TAG name for debugging
     */
    private static final String TAG = "Game";

    /**
     * Messages for debugging
     */
    private static final String MSG_ERR_WRONG_JSON_TEAM_OBJECT = "Wrong JSON. Team's substructure is not a JSON object";
    private static final String MSG_ERR_WRONG_JSON_PLAYER_OBJECT = "Wrong JSON. Player's substructure is not a JSON object";
    private static final String MSG_ERR_WRONG_JSON_ACTION_OBJECT = "Wrong JSON. GameAction's substructure is not a JSON object";
    private static final String MSG_ERR_WRONG_JSON_TEAM_COUNT = "Wrong JSON. Team count is not equal to scores count";

    /**
     * Game name
     */
    private String mGameName;

    /**
     * Game's info
     */
    private ArrayList<Team> mTeams;

    public Game(String jsonString) {
        try {
            JSONObject gameInfo = new JSONObject(jsonString);
            parseJSON(gameInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getting common scores for all teams affected by this team
     *
     * @return scores changes for each team
     */
    public int[] getScores() {
        int[] scores = new int[0];
        for (int i = 0; i < this.mTeams.size(); i++) {
            int[] teamsScores = this.mTeams.get(i).getScores();
            if (scores.length != 0) {
                for (int j = 0; j < scores.length; j++) {
                    scores[j] = scores[j] + teamsScores[j];
                }
            } else {
                scores = teamsScores;
            }
        }
        return scores;
    }

    /**
     * Getting the team at index
     *
     * @param index index of the team. First team has zero index.
     * @return Team object
     */
    public Team getTeam(int index) {
        return this.mTeams.get(index);
    }

    /**
     * Getting count of the team
     *
     * @return teams count
     */
    public int getTeamCount() {
        return this.mTeams.size();
    }

    /**
     * Getting name of the game
     *
     * @return game's name
     */
    public String getGameName() {
        return mGameName;
    }

    /**
     * Getting current state of all actions as mActionsCount
     *
     * @return list of actions' states
     */
    public ArrayList<Integer> getCurrentState() {
        ArrayList<Integer> result = new ArrayList<>(0);
        for (int i = 0; i < mTeams.size(); i++) {
            result.addAll(mTeams.get(i).getCurrentState());
        }
        return result;
    }

    /**
     * Setting current states for all actions
     *
     * @param states of the actions
     */
    public void setCurrentState(ArrayList<Integer> states) {
        if (states.size() % mTeams.size() == 0) {
            int partSize = states.size() / mTeams.size();
            for (int i = 0; i < mTeams.size(); i++) {
                ArrayList<Integer> teamStates = new ArrayList<>(0);
                for (int j = 0; j < partSize; j++) {
                    teamStates.add(states.get(i * partSize + j));
                }
                mTeams.get(i).setCurrentState(teamStates);
            }
        }
    }

    /**
     * Parsing of the team model from the JSON string.
     * Convert JSON info to instances of class fields.
     *
     * @param gameInfo JSON object with all information of the game
     */
    private void parseJSON(JSONObject gameInfo) {
        final String GAME_NAME = "name";
        final String TEAMS_INFO = "teams";
        final String BUTTONS_INFO = "buttons";

        try {
            mGameName = gameInfo.getString(GAME_NAME);
            if (gameInfo.get(TEAMS_INFO) instanceof JSONObject) {
                ArrayList<Team> teams = new ArrayList<>(0);
                JSONObject teamsJSON = gameInfo.getJSONObject(TEAMS_INFO);
                int teamCount = teamsJSON.names().length();
                int[] scores;

                Iterator<?> teamKeys = teamsJSON.keys();
                while (teamKeys.hasNext()) {
                    String teamName = String.valueOf(teamKeys.next());
                    if (teamsJSON.get(teamName) instanceof JSONObject) {
                        ArrayList<Player> players = new ArrayList<>(0);
                        JSONObject team = new JSONObject(teamsJSON.get(teamName).toString());
                        Iterator<?> playerKeys = team.keys();

                        while (playerKeys.hasNext()) {
                            String playerName = String.valueOf(playerKeys.next());
                            if (team.get(playerName) instanceof JSONObject) {
                                ArrayList<GameAction> gameActions = new ArrayList<>(0);
                                JSONObject playerJSON = new JSONObject(team.get(playerName).toString());
                                JSONObject buttonsJSON = playerJSON.getJSONObject(BUTTONS_INFO);
                                Iterator<?> actionKeys = buttonsJSON.keys();

                                while (actionKeys.hasNext()) {
                                    String actionName = String.valueOf(actionKeys.next());
                                    JSONArray scoresJSON = buttonsJSON.getJSONArray(actionName);

                                    if (scoresJSON.length() == teamCount) {
                                        int[] scoresChanges = new int[teamCount];
                                        scores = scoresChanges;
                                        for (int i = 0; i < teamCount; i++) {
                                            scoresChanges[i] = scoresJSON.getInt(i);
                                            scores[i] = scoresChanges[i];
                                        }
                                        gameActions.add(new GameAction(actionName, scoresChanges));
                                    } else {
                                        Log.d(TAG, MSG_ERR_WRONG_JSON_TEAM_COUNT);
                                    }
                                }
                                players.add(new Player(playerName, gameActions));
                            } else {
                                Log.d(TAG, MSG_ERR_WRONG_JSON_ACTION_OBJECT);
                            }
                        }

                        teams.add(new Team(teamName, players));
                    } else {
                        Log.d(TAG, MSG_ERR_WRONG_JSON_PLAYER_OBJECT);
                    }
                }

                mTeams = teams;
            } else {
                Log.d(TAG, MSG_ERR_WRONG_JSON_TEAM_OBJECT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
