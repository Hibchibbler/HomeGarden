package usa.or.pdx.homegarden;

import java.sql.Timestamp;

/**
 * This class describes the data structures that are used to store data
 * that is found in the database. 
 * 
 * Sensor, Actuator, Sample, and Threshold.
 * Typically, these 4 classes are held in a List.
 * For example: List<DBData.Sensor> would be a list of sensors.
 * 
 * Sample is different, in that we use a Map.
 * For example: Map<Integer, List<DBData.Sample>
 * The key is a sensor_id, and the value is a List.
 * In this way, we know exactly which sample comes from what sensor
 */
public class DBData {
	
	public static class Sensor {

		public int sensor_id;
		public String sensor_name;
		public int sample_rate_hz;
		public int sample_size_b;
		Timestamp timestamp;
		
		public int getSensorId() {
			return sensor_id;
		}
		public void setSensorId(int sensor_id) {
			this.sensor_id = sensor_id;
		}
		public String getSensorName() {
			return sensor_name;
		}
		public void setSensorName(String sensor_name) {
			this.sensor_name = sensor_name;
		}
		public int getSampleRate() {
			return sample_rate_hz;
		}
		public void setSampleRate(int sample_rate_hz) {
			this.sample_rate_hz = sample_rate_hz;
		}
		public int getSampleSize() {
			return sample_size_b;
		}
		public void setSampleSize(int sample_size_b) {
			this.sample_size_b = sample_size_b;
		}
		public void setTimestamp(Timestamp ts){
			timestamp = ts;
		}
		
		public Timestamp getTimestamp(){
			return timestamp;
		}
	}
	
	public static class Actuator {

		int actuator_id;
		String actuator_name;
		int state;
		int override;
		Timestamp timestamp;
		
		public int getActuatorId() {
			return actuator_id;
		}
		public void setActuatorId(int actuator_id) {
			this.actuator_id = actuator_id;
		}
		public String getActuatorName() {
			return actuator_name;
		}
		public void setActuatorName(String actuator_name) {
			this.actuator_name = actuator_name;
		}
        public int getState() {
            return state;
        }
        public void setState(int state) {
            this.state = state;
        }
        public int getOverride() {
            return override;
        }
        public void setOverride(int override) {
            this.override = override;
        }
		public void setTimestamp(Timestamp ts){
			timestamp = ts;
		}		
		public Timestamp getTimestamp(){
			return timestamp;
		}		
		public Actuator()
		{
		}
	}
	
	public static class Threshold{
	    private int threshold_id;
	    private int actuator_id;
	    private int sensor_id;
	    private int min;
	    private int max;
	    private Timestamp timestamp;
	    
        public int getThresholdId() {
            return threshold_id;
        }
        public void setThresholdId(int threshold_id) {
            this.threshold_id = threshold_id;
        }	    
	    public int getActuatorId() {
            return actuator_id;
        }
        public void setActuatorId(int actuator_id) {
            this.actuator_id = actuator_id;
        }
        public int getSensorId() {
            return sensor_id;
        }
        public void setSensorId(int sensor_id) {
            this.sensor_id = sensor_id;
        }        
        public int getMin() {
            return min;
        }
        public void setMin(int min) {
            this.min = min;
        }
        public int getMax() {
            return max;
        }
        public void setMax(int max) {
            this.max = max;
        }

        public void setTimestamp(Timestamp ts){
            timestamp = ts;
        }        
        public Timestamp getTimestamp(){
            return timestamp;
        }
	}
	
	public static class Sample {

	    int data_id;
		int sensor_id;
		int data;
		float dataf;
		
		Timestamp timestamp;
		
		public int getDataId() {
            return data_id;
        }

        public void setDataId(int data_id) {
            this.data_id = data_id;
        }
		public int getSensorId() {
			return sensor_id;
		}
		public void setSensorId(int sensor_id) {
			this.sensor_id = sensor_id;
		}
		public int getData() {
			return data;
		}
		public float getDataf() {
            return dataf;
        }
		public void setData(int data) {
			this.data = data;
			this.dataf = (float)data/100.0f;
		}
		public void setTimestamp(Timestamp ts){
			timestamp = ts;
		}		
		public Timestamp getTimestamp(){
			return timestamp;
		}		
		public Sample()
		{}
		
		
	}

}
