package usa.or.pdx.homegarden;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * This connects our Sensor-Sample, Actuator-State model to the UI
 * The UI has two textviews. One for the name of the sensor or actuator, 
 * and another for the most recent Sample or State respectively.
 */
public class NumericsArrayAdapter extends ArrayAdapter<NumericItem> {
    Context context;
    List<NumericItem> data;
    int layoutResourceId;
    
    public NumericsArrayAdapter(Context context, int resource, List<NumericItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View row = convertView;
        NumericItemHolder nih = null;
        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            nih = new NumericItemHolder();
            nih.textInstrumentName = (TextView)row.findViewById(R.id.textInstrumentName);
            nih.textInstrumentValue = (TextView)row.findViewById(R.id.textInstrumentValue);
            
            row.setTag(nih);
            
        }else{
            nih = (NumericItemHolder)row.getTag();
        }
        
        NumericItem ni = (NumericItem)data.get(position);
        nih.textInstrumentName.setText(ni.name);
        nih.textInstrumentValue.setText(ni.value);
        
        return row;
    }

    public static class NumericItemHolder{
        public TextView textInstrumentName;
        public TextView textInstrumentValue;
    }

}
