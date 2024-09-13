# Beacon Util

Библиотека для работы с beacon, позволяет определять местоположения пользователя в 2D.

## Область применения

Мобильные приложения на Android, где требуется определять (фиксировать) местоположения пользователя по маякам beacon

## Работа с кодом

Пример приложения тут https://github.com/akrasnov87/BeaconDemo

### Получение координат в foreground service

1. Создаём сервис

<pre>
public class BeaconForegroundService extends Service
        implements OnBeaconListener {
    ...
}
</pre>

2. Инициализация

<pre>
...
public int onStartCommand(Intent intent, int flags, int startId) {
    ...
    beaconDataProvider = new ServerBeaconDataProvider(this, Names.getConnectUrl(), "0.0.0.0");
    localSave = new LocalSave(this, "tracker.csv");
    
    wBeaconManager = new wBeaconManager(this, beacons);
    wBeaconManager.addRegion(BeaconUtil.getRegions(beacons, 1).get(0));
    wBeaconManager.setBeaconListener(this);
    
    BeaconSetting setting = new BeaconSetting();
    
    wBeaconManager.setBeaconSetting(setting);
    wBeaconManager.setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT);
    
    wBeaconManager.startMonitor();
    ...
}
...
</pre>

3. Обработка координат

<pre>
...
@Override
public void onCalculatePosition(double[] position) {
    if(position != null) {
        mCurrentPosition = position;

        try {
            String line = position[0] + ";" + position[1] + ";" + DateUtil.convertDateToUserString(new Date(), "yyyy-MM-dd") + ";" + DateUtil.convertDateToUserString(new Date(), "HH:mm:ss.SSS");
            localSave.writeLine(line.getBytes());

            String token = beaconDataProvider.auth("a-krasnov@it-serv.ru", "12345");
            beaconDataProvider.push(token);

            Log.d(TAG, "current position x=" + position[0] + " y=" + position[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } else {
        Log.d(TAG, "current position unknown");
    }
}
...
</pre>