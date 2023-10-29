package Project;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MqttPublisher_API{
	static MqttClient sampleClient;
		
    public static void main(String[] args) {
    	connectBroker();
    	
    	String busstationID = get_busstationID_data();
    	String[][] businfo = get_businfo_data(busstationID);
    	
    	for(int i = 0; i < 4; i++) {
    		publish_data(businfo[i][0], "{\"busNo\": "+'"'+businfo[i][0]+'"'+", \"locationNo1\": "+'"'+businfo[i][1]+'"'+", \"predictTime1\": "+'"'+businfo[i][2]+'"'+"}");
    	}
    	
    	try {
			sampleClient.disconnect();
	        System.out.println("Disconnected");
	        System.exit(0);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void connectBroker() {
        String broker       = "tcp://127.0.0.1:1883";
        String clientId     = "practice";
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
    
    public static void publish_data(String topic_input, String data) {
        String topic        = topic_input;
        int qos             = 0;
        try {
            System.out.println("Publishing message: "+data);
            sampleClient.publish(topic, data.getBytes(), qos, false);
            System.out.println("Message published");
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
    
    public static String get_routeName_data(String routeID) {
    	String url = "http://apis.data.go.kr/6410000/busrouteservice/getBusRouteInfoItem" 
    			+ "?serviceKey=%2FJcS%2BTaKSfcx%2FPR%2FUj%2BgJYG9sar7bYd6sbqb8p42nCNIAIyw0vXA4IwADw7ULGGKVcfdaMz1RneUuUHMmLl9BQ%3D%3D"
    			+ "&routeId="+routeID;
    	
		String routeName = "";
		
    	Document doc = null;
		
		// 2.파싱
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(doc);
		
		Elements elements = doc.select("busRouteInfoItem");
		for (Element e : elements) {
				routeName = e.select("routeName").text();
		}
    	return routeName;
    }
    
    public static String get_busstationID_data() {
    	String url = "http://apis.data.go.kr/6410000/busstationservice/getBusStationList" 
    			+ "?serviceKey=%2FJcS%2BTaKSfcx%2FPR%2FUj%2BgJYG9sar7bYd6sbqb8p42nCNIAIyw0vXA4IwADw7ULGGKVcfdaMz1RneUuUHMmLl9BQ%3D%3D"
    			+ "&keyword=49175";
    	
		String stationID = "";
		
    	Document doc = null;
		
		// 2.파싱
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(doc);
		
		Elements elements = doc.select("busStationList");
		for (Element e : elements) {
				stationID = e.select("stationId").text();
			
		}
    	return stationID;
    }
    
    public static String[][] get_businfo_data(String bsi) {
    	String url = "http://apis.data.go.kr/6410000/busarrivalservice/getBusArrivalList" 
    			+ "?serviceKey=%2FJcS%2BTaKSfcx%2FPR%2FUj%2BgJYG9sar7bYd6sbqb8p42nCNIAIyw0vXA4IwADw7ULGGKVcfdaMz1RneUuUHMmLl9BQ%3D%3D"
    			+ "&stationId="+bsi;
    	
		String[][] businfo = new String[4][3];
				
    	Document doc = null;
		
		// 2.파싱
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(doc);
		
		Elements elements = doc.select("busArrivalList");
		for (Element e : elements) {
				if(get_routeName_data(e.select("routeId").text()).equals("23")) {
					businfo[0][0] = get_routeName_data(e.select("routeId").text());
					businfo[0][1] = e.select("locationNo1").text();
					businfo[0][2] = e.select("predictTime1").text();
				}
				if(get_routeName_data(e.select("routeId").text()).equals("1-4")) {
					businfo[1][0] = get_routeName_data(e.select("routeId").text());
					businfo[1][1] = e.select("locationNo1").text();
					businfo[1][2] = e.select("predictTime1").text();
				}
				if(get_routeName_data(e.select("routeId").text()).equals("93")) {
					businfo[2][0] = get_routeName_data(e.select("routeId").text());
					businfo[2][1] = e.select("locationNo1").text();
					businfo[2][2] = e.select("predictTime1").text();
				}
				if(get_routeName_data(e.select("routeId").text()).equals("30")) {
					businfo[3][0] = get_routeName_data(e.select("routeId").text());
					businfo[3][1] = e.select("locationNo1").text();
					businfo[3][2] = e.select("predictTime1").text();
				}
			}
    	return businfo;
    }
}
    
    
    