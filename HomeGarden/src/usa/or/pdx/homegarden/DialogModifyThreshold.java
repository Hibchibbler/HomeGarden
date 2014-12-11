package usa.or.pdx.homegarden;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;

/**
 * This class declares the Dialog that appears when the user selects a threshold to modify
 */
public class DialogModifyThreshold extends DialogFragment {

    private EditText mMin;
    private EditText mMax;
 
    public interface DialogModifyThresholdListener {
        public void onDialogModifyThresholdPositiveClick(DialogFragment dialog, int min, int max);
        public void onDialogModifyThresholdNegativeClick(DialogFragment dialog);
    }
 
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_dialog_threshold, null))
        // Add action buttons
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       mMin = (EditText)getDialog().findViewById(R.id.editMinThreshold);
                       mMax = (EditText)getDialog().findViewById(R.id.editMaxThreshold);
                       ((DialogModifyThresholdListener)getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_FRAGMENTTHRESHOLDS)).onDialogModifyThresholdPositiveClick(DialogModifyThreshold.this, Integer.parseInt(mMin.getText().toString()), Integer.parseInt(mMax.getText().toString()));
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       DialogModifyThreshold.this.getDialog().cancel();
                       ((DialogModifyThresholdListener)getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.TAG_FRAGMENTTHRESHOLDS)).onDialogModifyThresholdNegativeClick(DialogModifyThreshold.this);
                   }
               });      
        return builder.create();
    }
    
}
