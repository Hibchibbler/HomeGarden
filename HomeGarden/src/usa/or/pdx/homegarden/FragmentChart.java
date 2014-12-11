package usa.or.pdx.homegarden;

import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Instantiates an AChartEngine line chart.
 * It populates it with data from a single sensor.
 * It retrieves the sensor data from the cache.
 */
public class FragmentChart extends Fragment implements OnClickListener{

	private int currentIndex;
	private GraphicalView graphView;
	private LinearLayout ll;
	
	public interface OnFragmentChartListener{
		public Map<Integer, List<DBData.Sample>> onGetDataMap();
	}


	public static Fragment createInstance(int index){
		Bundle b = new Bundle();
		FragmentChart fc = new FragmentChart();
		
		b.putInt("ChartIndex", index);
		fc.setArguments(b);
		return fc;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_chart, container, false);		
		
		Bundle b = getArguments();
		currentIndex = b.getInt("ChartIndex");		
		
		
		ll = (LinearLayout)v.findViewById(R.id.fragment_chart_container);
		
		graphView = createChart(currentIndex);
		if (graphView != null)
		    ll.addView(graphView);
		
		
		
		return v;
	}
	
	private GraphicalView createChart(int index){
		
		Map<Integer, List<DBData.Sample>> data = CacheHelpers.loadData();		
		if (data == null)
		    return null;
		
		List<DBData.Sensor> sensors = CacheHelpers.loadSensors();
		if (sensors == null)
		    return null;
		
		int sensorId = sensors.get(index).getSensorId();
		
		XYSeries series = new XYSeries(sensors.get(index).getSensorName());
		
		int time=0;

		if (!data.containsKey(sensorId))
		    return null;
		for (DBData.Sample s : data.get(sensorId)){
			series.add(time, s.getDataf());
			time++;
		}
        
        
        XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
        dataSet.addSeries(series);
        
        
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(Color.RED);
        renderer.setDisplayBoundingPoints(true);
        
        
        
        XYMultipleSeriesRenderer renderers = new XYMultipleSeriesRenderer();
        renderers.addSeriesRenderer(renderer);
        
        //Maregins - top, left, bottom, right
        int[] margins = renderers.getMargins();
        margins[1]+=5;
        margins[2]=0;
        renderers.setMargins(margins);
        
        renderers.setApplyBackgroundColor(true);
        renderers.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        renderers.setBackgroundColor(Color.TRANSPARENT);
        renderers.setYLabelsAlign(Align.RIGHT, 0);
        renderers.setYLabelsColor(0, Color.BLACK);
        renderers.setXLabelsColor(Color.BLACK);
        
        return ChartFactory.getLineChartView(getActivity(), dataSet, renderers);
	}

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

        }

    }
    


}
