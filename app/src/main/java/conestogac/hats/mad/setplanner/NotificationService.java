package conestogac.hats.mad.setplanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends Service {
    // indicates how to behave if the service is killed
    int mStartMode;
    // interface for clients that bind
    IBinder mBinder;

    // Called when the service is being created.
    @Override
    public void onCreate() {
        //thread = new MyThread(this.getApplicationContext());
        //ret = Integer.toString(overdueCount);
        //local variables
        CharSequence contentTitle = "SETPlanner";
        CharSequence contentText = "Check your Due dates in you planner!";
        int icon = R.mipmap.icon;

        Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getService(this.getApplicationContext(), 0, intent, 0);
        //create a notification object
        Notification notification = new Notification.Builder(this.getApplicationContext())
                .setSmallIcon(icon)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        //send notification.
        NotificationManager manager = (NotificationManager) this.getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1, notification);

        mStartMode = START_STICKY;
    }

    // The service is starting, due to a call to startService()
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // create and execute the thread
       // thread.execute();
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

