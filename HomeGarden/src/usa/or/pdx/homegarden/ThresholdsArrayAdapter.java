package usa.or.pdx.homegarden;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.EditText;

/**
 * This connects our Threshold model to the UI
 *  
 * The UI has four textviews. First for the name of an actuator, 
 * second name of trigger-sensor, third minimum trigger threshold, 
 * and the last maximum trigger threshold.
 * 
 */
public class ThresholdsArrayAdapter extends ArrayAdapter<ThresholdItem> {



    private List<ThresholdItem> mThresholdItems;
    private int mLayoutId;
    private Context mContext;
    
    public ThresholdsArrayAdapter(Context context, int resource, List<ThresholdItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutId = resource;
        mThresholdItems = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View row = convertView;
        
        ThresholdItemHolder thresholdItemHolder = null;
        
        if (row == null){
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutId, parent, false);
            
            thresholdItemHolder = new ThresholdItemHolder();
            thresholdItemHolder.txtActuatorName = (TextView)row.findViewById(R.id.txtActuatorName);
            thresholdItemHolder.txtSensorName = (TextView)row.findViewById(R.id.txtSensorName);
            thresholdItemHolder.txtMin = (TextView)row.findViewById(R.id.txtMin);
            thresholdItemHolder.txtMax = (TextView)row.findViewById(R.id.txtMax);
            
            row.setTag(thresholdItemHolder);
            
        }else{
            thresholdItemHolder = (ThresholdItemHolder)row.getTag();
        }
        
        ThresholdItem ti = (ThresholdItem)mThresholdItems.get(position);
        thresholdItemHolder.txtActuatorName.setText(ti.actuator_name);
        thresholdItemHolder.txtSensorName.setText(ti.sensor_name);
        thresholdItemHolder.txtMin.setText(String.valueOf(ti.min));
        thresholdItemHolder.txtMax.setText(String.valueOf(ti.max));
        
        
        return row;
    }
    
    public static class ThresholdItemHolder{
        public TextView txtActuatorName;
        public TextView txtSensorName;
        public TextView txtMin;
        public TextView txtMax;
    }
}
