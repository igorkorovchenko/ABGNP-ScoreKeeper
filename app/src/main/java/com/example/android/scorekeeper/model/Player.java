package com.example.android.scorekeeper.model;

import java.util.ArrayList;

/**
 * Class of the player's model with all information of the player
 *
 * @package com.example.android.scorekeeper
 * (c) 2018, Igor Korovchenko.
 */

public class Player {

    /**
     * Player's name
     */
    private String mPlayerName;

    /**
     * Game actions contains score changes for each player
     */
    private ArrayList<GameAction> mGameActions;

    /**
     * Constructor of the player's model
     *
     * @param playerName name of the player
     * @param gameActions game actions of the player
     */
    Player(String playerName, ArrayList<GameAction> gameActions) {
        this.mPlayerName = playerName;
        this.mGameActions = gameActions;
    }

    /**
     * Getting a name of the player
     *
     * @return name of the player
     */
    public String getName() {
        return this.mPlayerName;
    }

    /**
     * Getting common scores for all teams affected by this player
     *
     * @return scores changes for each team
     */
    int[] getScores() {
        int[] scores = new int[0];
        for (int i = 0; i < this.mGameActions.size(); i++) {
            int[] btnScores = this.mGameActions.get(i).getScores();
            if (scores.length != 0) {
                for (int j = 0; j < scores.length; j++) {
                    scores[j] = scores[j] + btnScores[j];
                }
            } else {
                scores = btnScores;
            }
        }
        return scores;
    }

    public GameAction getGameAction(int index) {
        return mGameActions.get(index);
    }

    public int getActionCount() {
        return mGameActions.size();
    }

    /**
     * Getting current state of all actions as mActionsCount
     *
     * @return list of actions' states
     */
    ArrayList<Integer> getCurrentState() {
        ArrayList<Integer> result = new ArrayList<>(0);
        for (int i = 0; i < mGameActions.size(); i++) {
            result.add(mGameActions.get(i).getCurrentState());
        }
        return result;
    }

    /**
     * Setting current states for all actions
     *
     * @param states of the actions
     */
    void setCurrentState(ArrayList<Integer> states) {
        if (states.size() % mGameActions.size() == 0) {
            int partSize = states.size() / mGameActions.size();
            for (int i = 0; i < mGameActions.size(); i++) {
                ArrayList<Integer> actionStates = new ArrayList<>(0);
                for (int j = 0; j < partSize; j++) {
                    actionStates.add(states.get(i * partSize + j));
                }
                mGameActions.get(i).setCurrentState(actionStates.get(0));
            }
        }
    }
}
