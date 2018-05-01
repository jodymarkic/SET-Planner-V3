package conestogac.hats.mad.setplanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by jmarkic8674 on 4/21/2017.
 */

public class BootReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent){
            Log.d("SETPlanner", "Boot Complete");
            Intent serviceIntent = new Intent(context, NotificationService.class);
            context.startService(serviceIntent);
        }
}
