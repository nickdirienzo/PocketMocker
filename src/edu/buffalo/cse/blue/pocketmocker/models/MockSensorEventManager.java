
package edu.buffalo.cse.blue.pocketmocker.models;

import android.content.Context;

public class MockSensorEventManager extends ModelManager {

    private static MockSensorEventManager sInstance;

    public static MockSensorEventManager getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new MockSensorEventManager(c);
        }
        return sInstance;
    }

    protected MockSensorEventManager(Context c) {
        super(c);
    }

}
