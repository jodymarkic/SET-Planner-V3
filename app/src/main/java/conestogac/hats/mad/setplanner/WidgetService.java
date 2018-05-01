package conestogac.hats.mad.setplanner;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by gpaquette1144 on 4/21/2017.
 */

public class WidgetService extends RemoteViewsService {
/*
* So pretty simple just defining the Adapter of the listview
* here Adapter is conestogac.hats.mad.setplanner.ListProvider
* */

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ListProvider(this.getApplicationContext(), intent));
    }

}
