package com.mobwal.walker.beautil.v1;

import static org.junit.Assert.assertEquals;

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
public class LocalSaveTest extends TestCase {

    private LocalSave localSave;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        localSave = new LocalSave(appContext, "test-data");
        localSave.clear();
    }

    @Test
    public void writeTest() throws IOException {
        String line1 = "0;0;" + DateUtil.convertDateToUserString(new Date(), "yyyy-MM-dd") + ";" + DateUtil.convertDateToUserString(new Date(), "HH:mm:ss.SSS");
        localSave.writeLine(line1.getBytes());

        String line2 = "1;1;" + DateUtil.convertDateToUserString(new Date(), "yyyy-MM-dd") + ";" + DateUtil.convertDateToUserString(new Date(), "HH:mm:ss.SSS");
        localSave.writeLine(line2.getBytes());

        String[] lines = localSave.readLines();
        Assert.assertNotNull(lines);

        Assert.assertEquals(2, lines.length);

        Assert.assertEquals(lines[0], line1);
        Assert.assertEquals(lines[1], line2);

        Assert.assertFalse(localSave.isEmpty());

        localSave.truncate();

        Assert.assertTrue(localSave.isEmpty());
    }

    @After
    public void downUp() {
        localSave.clear();
    }
}