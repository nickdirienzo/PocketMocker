
package edu.buffalo.cse.blue.pocketmocker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import edu.buffalo.cse.blue.pocketmocker.models.Objective;
import edu.buffalo.cse.blue.pocketmocker.models.ObjectivesManager;

public class NewObjectiveDialog extends DialogFragment {

    public static final String FIRST_KEY = "first";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        boolean first = false;
        Bundle b = this.getArguments();
        if (b != null) {
            if (b.containsKey(FIRST_KEY)) {
                first = b.getBoolean(FIRST_KEY);
            }
        }
        final MainActivity activity = (MainActivity) this.getActivity();
        final ObjectivesManager objectivesManager = ObjectivesManager.getInstance(activity
                .getApplicationContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.objective_dialog_title);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.create_objective_dialog, null);
        builder.setView(view).setPositiveButton(R.string.create,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing here because we override it after calling
                        // show() below because Android is stupid.
                    }
                });
        if (!first) {
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NewObjectiveDialog.this.getDialog().cancel();
                }
            });
        }
        final AlertDialog d = builder.create();
        d.show();
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText e = (EditText) view.findViewById(R.id.objective_name);
                if (e.getText().toString().equals("")) {
                    TextView textView = (TextView) view.findViewById(R.id.new_objective_error);
                    textView.setText(R.string.empty_objective_name);
                } else {
                    Objective o = new Objective();
                    o.setName(e.getText().toString());
                    objectivesManager.addObjective(o);
                    activity.populateObjectivesSpinner();
                    dismiss();
                }

            }
        });
        return d;
    }
}
