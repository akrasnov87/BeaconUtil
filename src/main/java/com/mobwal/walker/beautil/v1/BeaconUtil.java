package com.mobwal.walker.beautil.v1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.List;

public class BeaconUtil {
    /**
     * Поиск первого маяка в массиве данных
     *
     * @param beacon маяк для поиска
     * @param beacons массив с маяками
     * @return если маяк не найден вернётся null
     */
    @Nullable
    public static wBeacon findFirstBeacon(@NonNull wBeacon beacon, @NonNull wBeacon[] beacons) {
        for (wBeacon item:
             beacons) {
            if( item.id1.equals(beacon.id1) &&
                item.id2 != null && item.id2.equals(beacon.id2) &&
                item.id3 != null && item.id3.equals(beacon.id3)
            ) {
                return item;
            }
        }

        return null;
    }

    public static double[] toDoubleArray(List<Double> ld) {
        double[] results = new double[ld.size()];
        int i = 0;
        for (Double d : ld) {
            results[i++] = (d != null ? d : 0);
        }
        return results;
    }

    public static List<Region> getRegions(wBeacon[] beacons, Integer level) {
        List<Region> regions = new ArrayList<>();

        for (wBeacon item: beacons) {
            boolean regionExists = false;

            for (Region region: regions) {
                if(region.getId1().equals(item.id1) && region.getId2().equals(item.id2) && region.getId3().equals(item.id3) && level != null && level == 3) {
                    regionExists = true;
                    break;
                }

                if(region.getId1().equals(item.id1) && region.getId2().equals(item.id2) && level != null && level == 2) {
                    regionExists = true;
                    break;
                }

                if(region.getId1().equals(item.id1) && (level == null || level == 1)) {
                    regionExists = true;
                    break;
                }
            }

            if(!regionExists) {
                if(level != null && level == 3) {
                    regions.add(new Region("beacon", item.id1, item.id2, item.id3));
                    continue;
                }

                if(level != null && level == 2) {
                    regions.add(new Region("beacon", item.id1, item.id2, null));
                    continue;
                }

                if(level == null || level == 1) {
                    regions.add(new Region("beacon", item.id1, null, null));
                }
            }
        }

        return regions;
    }
}
