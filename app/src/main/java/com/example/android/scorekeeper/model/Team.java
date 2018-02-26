package com.example.android.scorekeeper.model;

import java.util.ArrayList;

/**
 * Class of the team's model with all information of the team
 *
 * @package com.example.android.scorekeeper
 * (c) 2018, Igor Korovchenko.
 */

public class Team {

    /**
     * Team name
     */
    private String mTeamName;

    /**
     * Players' info
     */
    private ArrayList<Player> mPlayers;

    /**
     * Constructor of the team model
     *
     * @param teamName name of the team
     * @param players array of the players
     */
    Team(String teamName, ArrayList<Player> players) {
        this.mTeamName = teamName;
        this.mPlayers = players;
    }

    /**
     * Getting a name of the team. If team consists of only one player,
     * the name of the team will be change to player's name.
     *
     * @return name of the team
     */
    public String getName() {
        return mTeamName;
    }

    /**
     * Getting common scores for all teams affected by this team
     *
     * @return scores changes for each team
     */
    int[] getScores() {
        int[] scores = new int[0];
        for (int i = 0; i < mPlayers.size(); i++) {
            int[] playersScores = mPlayers.get(i).getScores();
            if (scores.length != 0) {
                for (int j = 0; j < scores.length; j++) {
                    scores[j] = scores[j] + playersScores[j];
                }
            } else {
                scores = playersScores;
            }
        }
        return scores;
    }

    /**
     * Getting a size of the player array
     *
     * @return count of the players in the team
     */
    public int getPlayerCount() {
        return mPlayers.size();
    }

    /**
     * Getting one player at index of the team players
     *
     * @param index index of the player. First player has zero index.
     * @return Player at index
     */
    public Player getPlayer(int index) {
        return mPlayers.get(index);
    }

    /**
     * Getting current state of all actions as mActionsCount
     *
     * @return list of actions' states
     */
    ArrayList<Integer> getCurrentState() {
        ArrayList<Integer> result = new ArrayList<>(0);
        for (int i = 0; i < mPlayers.size(); i++) {
            result.addAll(mPlayers.get(i).getCurrentState());
        }
        return result;
    }

    /**
     * Setting current states for all actions
     *
     * @param states of the actions
     */
    void setCurrentState(ArrayList<Integer> states) {
        if (states.size() % mPlayers.size() == 0) {
            int partSize = states.size() / mPlayers.size();
            for (int i = 0; i < mPlayers.size(); i++) {
                ArrayList<Integer> playerStates = new ArrayList<>(0);
                for (int j = 0; j < partSize; j++) {
                    playerStates.add(states.get(i * partSize + j));
                }
                mPlayers.get(i).setCurrentState(playerStates);
            }
        }
    }
}
