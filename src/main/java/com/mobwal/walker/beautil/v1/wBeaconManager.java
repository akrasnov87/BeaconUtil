package com.mobwal.walker.beautil.v1;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class wBeaconManager
        implements RangeNotifier, MonitorNotifier {

    private BeaconManager beaconManager;
    private final wBeaconBufferManager wBeaconBufferManager;

    private OnBeaconListener onBeaconListener;
    private BeaconSetting setting;

    private wBeacon[] beacons;

    public wBeaconManager(@NonNull Context context, wBeacon[] items) {
        beacons = items;
        wBeaconBufferManager = new wBeaconBufferManager(context);
        beaconManager = BeaconManager.getInstanceForApplication(context);
        setting = new BeaconSetting();
    }

    public void setBeaconListener(@NonNull OnBeaconListener listener) {
        this.onBeaconListener = listener;
    }

    public void setBeaconSetting(@NonNull BeaconSetting setting) {
        this.setting = setting;
    }

    /**
     * Добавление региона прослушивания маяка
     *
     * @param region описание региона
     */
    public void addRegion(@NonNull Region region) {
        beaconManager.startMonitoring(region);
    }

    /**
     * Установка парсера для маяка
     *
     * @param beaconLayout например BeaconParser.EDDYSTONE_UID_LAYOUT
     */
    public void setBeaconLayout(@NonNull String beaconLayout) {
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(beaconLayout));
    }

    public void startMonitor() {
        beaconManager.addRangeNotifier(this);

        beaconManager.addMonitorNotifier(this);
    }

    public void stopMonitor() {
        beaconManager.removeRangeNotifier(this);
        beaconManager.removeMonitorNotifier(this);
        wBeaconBufferManager.clearAll();
    }

    public void destroy() {
        beaconManager.removeAllRangeNotifiers();
        beaconManager.removeAllMonitorNotifiers();
        beaconManager = null;
        wBeaconBufferManager.clearAll();
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons != null && !beacons.isEmpty()) {
            for (Beacon b : beacons) {
                if(wBeaconBufferManager.exists(b)) {
                    wBeaconBufferManager.add(b);
                    List<wBeacon> list = wBeaconBufferManager.get(b); // получение информации по beacon

                    wBeacon first = list.get(0);
                    wBeacon prev = list.get(list.size() - 2);
                    wBeacon last = list.get(list.size() - 1);

                    int biasInterval = (int) (new Date().getTime() - first.date.getTime());
                    if(biasInterval >= setting.interval) {
                        wBeaconBufferManager.remove(b);
                        wBeaconBufferManager.add(b);

                        if(onBeaconListener != null) {
                            onBeaconListener.onCalculatePosition(getPosition());
                            return;
                        }
                    }

                    int biasTxPower = Math.abs(last.txPower - prev.txPower);
                    if(biasTxPower >= setting.biasTxPower) {
                        wBeaconBufferManager.remove(b);
                        wBeaconBufferManager.add(b);

                        if(onBeaconListener != null) {
                            onBeaconListener.onCalculatePosition(getPosition());
                            return;
                        }
                    }

                    double biasDistance = Math.abs(last.distance - prev.distance);
                    if(biasDistance >= setting.biasDistance) {
                        wBeaconBufferManager.remove(b);
                        wBeaconBufferManager.add(b);

                        if(onBeaconListener != null) {
                            onBeaconListener.onCalculatePosition(getPosition());
                            return;
                        }
                    }
                } else {
                    // first
                    wBeaconBufferManager.add(b);

                    if(onBeaconListener != null) {
                        onBeaconListener.onCalculatePosition(getPosition());
                    }
                }
            }
        }
    }

    @Override
    public void didEnterRegion(Region region) {
        beaconManager.startRangingBeacons(region);
    }

    @Override
    public void didExitRegion(Region region) {
        beaconManager.stopRangingBeacons(region);
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        wBeaconBufferManager.removeContainsKey(region.getId1().toString());
    }

    /**
     * Вычисление положения в пространстве
     *
     * @return массив с координатами
     */
    private double[] getPosition() {
        // получение последней информации по beacon с учётом фильтра по дистанции
        wBeacon[] items = wBeaconBufferManager.getLastBeacons(setting.distance);

        List<double[]> positions = new ArrayList<>();
        List<Double> distances = new ArrayList<>();

        for (wBeacon b: items) {
            wBeacon baseBeacon = BeaconUtil.findFirstBeacon(b, beacons);
            if(baseBeacon != null) {
                positions.add(new double[] { baseBeacon.x, baseBeacon.y });
                distances.add(b.distance);
            }
        }

        try {
            // https://github.com/lemmingapex/Trilateration
            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions.toArray(new double[0][0]), BeaconUtil.toDoubleArray(distances)), new LevenbergMarquardtOptimizer());
            LeastSquaresOptimizer.Optimum optimum = solver.solve();

            return optimum.getPoint().toArray();
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }
}
