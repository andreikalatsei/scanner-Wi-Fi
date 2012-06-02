package scanner.namespace;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
/**
 *	@brief �������� ����� ����������.
 *	@detailed ������������ �������������� � �������������, ������� �� ����� ��������� � ������ ������ ����������, ��������� ������
 *	����� ����������.
 */
public class ScannerActivity extends Activity {
	private boolean isWork;
	TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    boolean wifiState; //�������� ��������� ��������� wifi � ��������
    
    String[] providers = new String[] 
    	    { LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER};
    SQLiteDatabase db;
    private static final String TAG = "myLogs";
    Location location;
    LocationManager locationManager;
    
    LocationListener locationListener = new LocationListener(){
/**
 *	@brief ��� ��������� ���������� ��������� ���� Wi-Fi.
 */
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			mainWifi.startScan();
		}

		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
    };
/**
 *	@brief �������� ������� �����.
 *	@detailed �������� � ����������� ��������� �����, ������ �������� ��������� ��������� � ������������ Wi-Fi �����.
 */
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE); //�������� ���������
       setContentView(R.layout.main);
       mainText = (TextView) findViewById(R.id.TextView01);
       mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
       receiverWifi = new WifiReceiver();
       registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
       //���� wifi � �������� ��������, �������� ���
       wifiState = mainWifi.isWifiEnabled();
       if (!wifiState){
    	   mainWifi.setWifiEnabled(true);
       }
       
       isWork = true;
       
       // ������� ������ ��� �������� � ���������� �������� ��
      // dbHelper = DBHelper.getInstance();
       mainWifi.startScan();
       mainText.setText("\n������������...\n");
       
       //context = Context.LOCATION_SERVICE;
       locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
       for (String provider : providers) {
    	   locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
       }
       //provider = LocationManager.GPS_PROVIDER;
       //location = locationManager.getLastKnownLocation(provider);
       
       Log.d(TAG,"------end oncreate------");
    }
 /**
  *  @brief �������� ����.
  */
    public boolean onCreateOptionsMenu(Menu menu) {     
    	 menu.add (Menu.FIRST, 1, 1, "��������");  
         menu.add (Menu.FIRST, 2, 2, "�����");           
         menu.add (Menu.FIRST, 3, 3, "�������� ����");  
         menu.add (Menu.FIRST, 4, 4, "������� ����");  
         menu.add (Menu.FIRST, 5, 5, "�����");  
           
         //menu.add (Menu.CATEGORY_SECONDARY, 6, 6, "Item 1");  
         //menu.add (Menu.CATEGORY_SECONDARY, 7, 7, "Item 2");  
         //menu.add (Menu.CATEGORY_SECONDARY, 8, 8, "Item 3");  
        return super.onCreateOptionsMenu(menu);
    }
/**
 * @brief ������ ������� ����.
 * @detailed ���� ������ ������ ����� ���� - ����������� ���������� ����������� ����������� Wi-Fi �����.
 * ���� ������ ������ ����� ���� - ��������� Wi-Fi ����� ������������ �� �����.
 * ���� ������ ������ ����� ���� - ������������ ������ ���� �����-���� ��������� Wi-Fi �����.
 * ���� ������ �������� ����� ���� - ��������� �� ���������� ���� ������.
 * ���� ������ ����� ����� ���� - ���������� ����������.
 */
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()) {
    		case 1:
		        mainWifi.startScan();
		        mainText.setText("������������...");
		        
		        // location = locationManager.getLastKnownLocation(provider);
		        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		        break;
	        
    		case 2:
    			Intent intent = new Intent(this, Maps.class);
    			intent.putExtra("latitude", location.getLatitude());
    			intent.putExtra("longitude", location.getLongitude());
    		    startActivity(intent);
    		    break;
    		case 3:
    			intent = new Intent (this, ListBD.class);
    			startActivity(intent);
    			break;
    		case 4:
    			DbAdapter dbHelper = new DbAdapter(getBaseContext());
                dbHelper.open();
                dbHelper.deleteTable();
                dbHelper.close();
                mainWifi.startScan();
    			break;
    		case 5:
    			finish();
	    }
        return super.onMenuItemSelected(featureId, item);
    }
 /**
  * @brief ���������� ��� �������� � ������� activity.
  * @detailed ������������ ���� isWork, ���������, ��� ������ �� ���� �������� �� ����� ���������� ������������.
  */
    protected void onPause() {
    	// ��������� ������������
       // locationManager.removeUpdates(locationListener);
       // unregisterReceiver(receiverWifi);
    	isWork = false;
        super.onPause();
    }
    /**
     * @brief ���������� ��� �������� � �������� activity.
     * @detailed ��������������� ���� isWork, ���������, ��� ���� �������� �� ����� ���������� ������������.
     */
    protected void onResume() {
    	//�������� ������������
    	//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        //registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    	isWork = true;
        super.onResume();
    }
    
    /*protected void onStop(){
    	if(wifiState){
    		mainWifi.setWifiEnabled(false);
    	}
    }*/
 /**
  * @brief �����, ����������� ������ ������������ Wi-Fi �����.
  * @detailed ������������ ������������ ����� � ������ ����������� ���������� � ����,
  *  ���� �������� main activity, ��������� ���������� �� �����.
  */
    class WifiReceiver extends BroadcastReceiver {
 /**
  * @brief �����, ���������� ��� ��������� ��������� Wi-Fi �����.
  * @detailed ������������ ������������ ����� � ������ ����������� ���������� � ����,
  *  ���� �������� main activity, ��������� ���������� �� �����. �� ����� ��������� ���������� � ��������� Wi-Fi �����,
  *  ��������� ���������� ���������� � ���� ������ �� ���� �����. ����� ��������� �������������� ���������� � ������� ��������������.   	
  */
		public void onReceive(Context c, Intent intent) {
			Log.d(TAG,"------wifi receiver----");
            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            
            location = null;
            for (String provider : providers) {
            	location = locationManager.getLastKnownLocation(provider);
            		if (location != null) {
            	    break;
            	   }
            }
            
           // location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            // ������������ � ��
            DbAdapter dbHelper = new DbAdapter(getBaseContext());
            dbHelper.open();
                       
            for(int i = 0; i < wifiList.size(); i++){
                sb.append(new Integer(i+1).toString() + ".");
                sb.append((wifiList.get(i)).toString());
                sb.append("\n");
                
                long status = dbHelper.createRecord(wifiList.get(i).BSSID, wifiList.get(i).SSID, 
             		   location.getLatitude(), location.getLongitude(), wifiList.get(i).level, wifiList.get(i).frequency);
               
                switch ((int)status){
                	case -2:
                		sb.append("���������� ���������.");
                		break;
                	case -3:
                		sb.append("����� ���������� ��� ����������.");
                		break;
                	case -4:
                		sb.append("�� ������� ��������� ���������� wifi �����.");
                		break;
                	case -5:
                		sb.append("���������� wifi ����� ���������.");
                		break;
                	default:
                		sb.append("��������� ����� ����������.");
                		break;
                }
                sb.append("\n");
                sb.append("\n");
            }
            sb.append("\n");
          
            if (location != null){
	            sb.append("������: " + location.getLatitude());
	            sb.append("\n");
	            sb.append("�������: " + location.getLongitude());
	            sb.append("\n");
	            if (location.hasAccuracy()){
	            	sb.append("��������: ");
	            	sb.append(location.getAccuracy());
	            	sb.append("\n");
	            }
	            if (location.hasAltitude()){
	            	sb.append("������: " + location.getAltitude());
	            }
            }
            else
            	Toast.makeText(getApplicationContext(), "�� ������� �������� ����������", Toast.LENGTH_LONG).show();
           
            if (isWork)
            	mainText.setText(sb);
            dbHelper.close();
        }
    }
}