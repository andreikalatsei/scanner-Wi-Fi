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
 *	@brief Основной класс приложения.
 *	@detailed Осуществляет взаимодействие с пользователем, выводит на экран найденную в данный момент информацию, запускает сервис
 *	сбора информации.
 */
public class ScannerActivity extends Activity {
	private boolean isWork;
	TextView mainText;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    boolean wifiState; //хранение исходного состояния wifi в телефоне
    
    String[] providers = new String[] 
    	    { LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER};
    SQLiteDatabase db;
    private static final String TAG = "myLogs";
    Location location;
    LocationManager locationManager;
    
    LocationListener locationListener = new LocationListener(){
/**
 *	@brief При изменении координаты сканируем сети Wi-Fi.
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
 *	@brief Создание главной формы.
 *	@detailed Создание и активизация элементов формы, запуск сервисов получения координат и сканирования Wi-Fi сетей.
 */
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE); //скрываем заголовок
       setContentView(R.layout.main);
       mainText = (TextView) findViewById(R.id.TextView01);
       mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
       receiverWifi = new WifiReceiver();
       registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
       //если wifi в телефоне выключен, включаем его
       wifiState = mainWifi.isWifiEnabled();
       if (!wifiState){
    	   mainWifi.setWifiEnabled(true);
       }
       
       isWork = true;
       
       // создаем объект для создания и управления версиями БД
      // dbHelper = DBHelper.getInstance();
       mainWifi.startScan();
       mainText.setText("\nСканирование...\n");
       
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
  *  @brief Создание меню.
  */
    public boolean onCreateOptionsMenu(Menu menu) {     
    	 menu.add (Menu.FIRST, 1, 1, "Обновить");  
         menu.add (Menu.FIRST, 2, 2, "Карта");           
         menu.add (Menu.FIRST, 3, 3, "Просмотр базы");  
         menu.add (Menu.FIRST, 4, 4, "Удалить базу");  
         menu.add (Menu.FIRST, 5, 5, "Выход");  
           
         //menu.add (Menu.CATEGORY_SECONDARY, 6, 6, "Item 1");  
         //menu.add (Menu.CATEGORY_SECONDARY, 7, 7, "Item 2");  
         //menu.add (Menu.CATEGORY_SECONDARY, 8, 8, "Item 3");  
        return super.onCreateOptionsMenu(menu);
    }
/**
 * @brief Выбран элемент меню.
 * @detailed Если выбран первый пункт меню - выполняется обновление результатов сканиования Wi-Fi сетей.
 * Если выбран второй пункт меню - найденные Wi-Fi точки отображаются на карте.
 * Если выбран третий пункт меню - отображается список всех когда-либо найденных Wi-Fi точек.
 * Если выбран четвёртый пункт меню - удаляется всё содержимое базы данных.
 * Если выбран пятый пункт меню - завершение приложения.
 */
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId()) {
    		case 1:
		        mainWifi.startScan();
		        mainText.setText("Сканирование...");
		        
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
  * @brief Вызывается при переходе к другому activity.
  * @detailed Сбрасывается флаг isWork, говорящий, что больше не надо выводить на экран результаты сканирования.
  */
    protected void onPause() {
    	// выключаем отслеживание
       // locationManager.removeUpdates(locationListener);
       // unregisterReceiver(receiverWifi);
    	isWork = false;
        super.onPause();
    }
    /**
     * @brief Вызывается при возврате к главному activity.
     * @detailed Устанавливается флаг isWork, говорящий, что надо выводить на экран результаты сканирования.
     */
    protected void onResume() {
    	//включаем отслеживание
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
  * @brief Класс, реализующий сервис сканирования Wi-Fi сетей.
  * @detailed Производится сканирование сетей и запись необходимой информации в базу,
  *  если запущено main activity, выводится информация на экран.
  */
    class WifiReceiver extends BroadcastReceiver {
 /**
  * @brief Метод, вызывается при изменении состояния Wi-Fi сетей.
  * @detailed Производится сканирование сетей и запись необходимой информации в базу,
  *  если запущено main activity, выводится информация на экран. На экран выводится информация о найденной Wi-Fi точке,
  *  результат добавления информации в базу данных об этой точке. После выводится геолокационная информация о текущем местоположении.   	
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
            // подключаемся к БД
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
                		sb.append("Координата обновлена.");
                		break;
                	case -3:
                		sb.append("Такая координата уже существует.");
                		break;
                	case -4:
                		sb.append("Не удалось вычислить координату wifi точки.");
                		break;
                	case -5:
                		sb.append("Координата wifi точки обновлена.");
                		break;
                	default:
                		sb.append("Добавлено новое устройство.");
                		break;
                }
                sb.append("\n");
                sb.append("\n");
            }
            sb.append("\n");
          
            if (location != null){
	            sb.append("Широта: " + location.getLatitude());
	            sb.append("\n");
	            sb.append("Долгота: " + location.getLongitude());
	            sb.append("\n");
	            if (location.hasAccuracy()){
	            	sb.append("Точность: ");
	            	sb.append(location.getAccuracy());
	            	sb.append("\n");
	            }
	            if (location.hasAltitude()){
	            	sb.append("Высота: " + location.getAltitude());
	            }
            }
            else
            	Toast.makeText(getApplicationContext(), "Не удалось получить координаты", Toast.LENGTH_LONG).show();
           
            if (isWork)
            	mainText.setText(sb);
            dbHelper.close();
        }
    }
}