package conestogac.hats.mad.setplanner;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class OverdueService extends Service {
    int mStartMode;
    IBinder mBinder;
    MyThread thread;

    // Called when the service is being created.
    @Override
    public void onCreate() {
        thread = new MyThread(this.getApplicationContext());
    }

    // The service is starting, due to a call to startService()
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // create and execute the thread
         thread.execute();
        return mStartMode;
    }

    // A client is binding to the service with bindService()
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // Called when The service is being destroyed
    @Override
    public void onDestroy() {

    }
}
