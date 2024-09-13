package com.mobwal.walker.beautil.v1;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class ServerBeaconDataProviderTest extends TestCase {

    private ServerBeaconDataProvider serverBeaconDataProvider;
    private LocalSave localSave;

    @Before
    public void setUp() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        localSave = new LocalSave(appContext, "tracker.csv");
        localSave.truncate();
        localSave.writeLine(("0;0;" + DateUtil.convertDateToUserString(new Date(), "yyyy-MM-dd") + ";" + DateUtil.convertDateToUserString(new Date(), "HH:mm:ss.SSS")).getBytes());
        serverBeaconDataProvider = new ServerBeaconDataProvider(appContext, "http://10.10.6.76:5001/walker/dev", "0.0.0.0");
    }

    @Test
    public void authTest() {
        String token = serverBeaconDataProvider.auth("a-krasnov@it-serv.ru", "123456");
        Assert.assertNotNull(token);
    }

    @Test
    public void getBeaconsTest() {
        String token = serverBeaconDataProvider.auth("a-krasnov@it-serv.ru", "123456");
        wBeacon[] beacons = serverBeaconDataProvider.getBeacons(token);

        Assert.assertNotNull(beacons);

        Assert.assertTrue(beacons.length > 0);
    }

    @Test
    public void pushTest() throws IOException {
        String token = serverBeaconDataProvider.auth("a-krasnov@it-serv.ru", "123456");
        Assert.assertTrue(serverBeaconDataProvider.push(token));

        Assert.assertTrue(localSave.isEmpty());
    }

    @After
    public void tearDown() throws Exception {
        localSave.truncate();
    }
}