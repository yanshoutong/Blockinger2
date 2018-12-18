package org.blockinger.game.activities;

import org.blockinger.game.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DefeatDialogFragment extends DialogFragment {

    private CharSequence scoreString;
    private CharSequence timeString;
    private CharSequence apmString;
    private long score;

    public DefeatDialogFragment() {
        super();
        scoreString = "unknown";
        timeString = "unknown";
        apmString = "unknown";
    }

    public void setData(long scoreArg, String time, int apm) {
        scoreString = String.valueOf(scoreArg);
        timeString = time;
        apmString = String.valueOf(apm);
        score = scoreArg;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.defeatDialogTitle);
        builder.setMessage(
                getResources().getString(R.string.scoreLabel) +
                "\n    " + scoreString + "\n\n" +
                getResources().getString(R.string.timeLabel) +
                "\n    " + timeString + "\n\n" +
                getResources().getString(R.string.apmLabel) +
                "\n    " + apmString + "\n\n" +
                getResources().getString(R.string.hint)
                );
        builder.setNeutralButton(R.string.defeatDialogReturn, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((GameActivity)getActivity()).putScore(score);
            }
        });
        return builder.create();
    }
}
