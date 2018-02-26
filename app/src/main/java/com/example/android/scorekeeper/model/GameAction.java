package com.example.android.scorekeeper.model;

/**
 * Class of the actions in games
 *
 * @package com.example.android.scorekeeper
 * (c) 2018, Igor Korovchenko.
 */

public class GameAction {

    /**
     * Name fot he action
     */
    private String mName;

    /**
     * Action count
     */
    private int mActionsCount;

    /**
     * Score changes for each team beginning from the first team (zero index)
     */
    private int[] mScoreChanges;

    /**
     * Constructor of the game action model, which provide caption array of the
     * changes scores for each team. First team is according to zero index.
     *
     * @param scoreChanges changes of the score for each team
     */
    GameAction(String actionName, int[] scoreChanges) {
        this.mName = actionName;
        this.mActionsCount = 0;
        this.mScoreChanges = scoreChanges;
    }

    /**
     * Getting a count of the game actions
     *
     * @return actions count
     */
    public int getActionsCount() {
        return mActionsCount;
    }

    /**
     * Getting common scores for all teams affected by this game action
     *
     * @return scores changes for each team
     */
    int[] getScores() {
        int teamCount = this.mScoreChanges.length;
        int[] result = new int[teamCount];
        for (int i = 0; i < teamCount; i++) {
            result[i] = mActionsCount * mScoreChanges[i];
        }
        return result;
    }

    /**
     * Getting name of the action
     *
     * @return action's name
     */
    public String getName() {
        return mName;
    }

    public GameAction getModel() {
        return this;
    }

    /**
     * Getting current state of all actions as mActionsCount
     *
     * @return list of action states
     */
    Integer getCurrentState() {
        return mActionsCount;
    }

    /**
     * Setting current states for all action
     *
     * @param state of the action
     */
    void setCurrentState(Integer state) {
        mActionsCount = state;
    }

    /**
     * Adding new action to counter
     */
    public void addAction() {
        mActionsCount += 1;
    }

    /**
     * Undo last action to counter
     */
    public void undoAction() {
        if (mActionsCount > 0) mActionsCount -= 1;
    }
}
