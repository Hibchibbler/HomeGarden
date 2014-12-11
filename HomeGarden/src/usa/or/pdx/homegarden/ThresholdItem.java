package usa.or.pdx.homegarden;

/**
 * This class represents the data that backs an item in a ListView 
 */
public class ThresholdItem {
    public String actuator_name;
    public int actuator_id;
    public String sensor_name;
    public int sensor_id;
    public int min;
    public int max;
    
    public ThresholdItem(String actuator_name, int actuator_id, String sensor_name, int sensor_id, int min, int max){
        this.actuator_name = actuator_name;
        this.actuator_id = actuator_id;
        this.sensor_name = sensor_name;
        this.sensor_id = sensor_id;
        this.min = min;
        this.max = max;
    }
}
