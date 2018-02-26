package com.example.android.scorekeeper.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.example.android.scorekeeper.model.GameAction;

/**
 * Class of the button connects with model and textView for showing the result
 *
 * @package com.example.android.scorekeeper
 * (c) 2018, Igor Korovchenko.
 */

@SuppressLint("ViewConstructor")
public class GameActionButton extends android.support.v7.widget.AppCompatButton {

    /**
     * TextView for showing the result on the Scoreboard
     */
    private String mCaption;

    /**
     * Model of the game action with all statistics
     */
    private GameAction mModel;

    /**
     * Constructor of the class connects with model
     *
     * @param context context of the app
     * @param caption caption of the button
     * @param model linked model of the game action with all statistics
     */
    public GameActionButton(Context context, String caption, GameAction model) {
        super(context);

        this.mCaption = caption;
        this.mModel = model;

        buildButton(caption);
    }

    /**
     * Button builder as an object of the GameActionButton
     *
     * @param caption caption of the button
     */
    public void buildButton(String caption) {
        this.setText(caption);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addAction();
            }
        });
    }

    /**
     * Undoing last action
     */
    public void undoAction() {
        mModel.undoAction();
        updateCaptionsAndScores();
    }

    /**
     * Updating the caption of the button and scoreboards
     */
    public void updateCaptionsAndScores() {
        updateCaption();
        ((GameLinearLayout) this.getParent().getParent().getParent().getParent()).updateScores(this.getId());
    }

    /**
     * Updating the caption of the button and scoreboards
     */
    private void updateCaption() {
        String updatedCaption = mCaption + " (" + mModel.getActionsCount() + ")";
        this.setText(updatedCaption);
    }

    /**
     * Adding new action
     */
    private void addAction() {
        mModel.addAction();
        updateCaptionsAndScores();
    }
}
