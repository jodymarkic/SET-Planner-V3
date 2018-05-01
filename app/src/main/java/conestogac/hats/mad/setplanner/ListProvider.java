package conestogac.hats.mad.setplanner;

import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.UserDictionary;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Locale;

import conestogac.hats.mad.setplanner.Project;
import conestogac.hats.mad.setplanner.R;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 *
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private ArrayAdapter<Project> projectArrayAdapter;

    private Context context = null;
    private int appWidgetId;
    private ContentResolver resolver;
    private String[] projection;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);


        resolver = context.getContentResolver();
        projection = new String[]{BaseColumns._ID, UserDictionary.Words.WORD};

        populateListItem();
    }

    private void populateListItem() {
        for (int i = 0; i < 10; i++) {
            Uri uri = Uri.parse( "conestogac.hats.mad.setplanner.DAL");

            Cursor cursor =
                    resolver.query(uri,
                            projection,
                            null,
                            null,
                            null);

            if (cursor.moveToFirst()) {
                do {
                    Project proj = new Project(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getInt(4));
                    projectArrayAdapter.add(proj);
                } while (cursor.moveToNext());
            }


        }

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return projectArrayAdapter.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {

        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.set_planner_widget);

        Project proj = projectArrayAdapter.getItem(position);

        remoteView.setTextViewText(R.id.courseName, "");

        // Create a date in the format year-month-day using the due date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String d = sdf.format(proj.getDueDate());
        Resources res = context.getResources();
        // Then, display it in the section for the due dat
        String text = String.format(res.getString(R.string.dueString), d);

        remoteView.setTextViewText(R.id.date, text);
        remoteView.setTextViewText(R.id.projName, proj.getProjectName());
        String s = "";
        int c = 0;
        // Next we will check the status of the project
        switch(proj.getStatus()) {
            case -1:
                s = "Overdue";
                c = Color.RED;
                break;
            case 0:
                s = "Incomplete";
                c = Color.BLUE;
                break;
            case 1:
                s = "Complete";
                c = Color.GREEN;
                break;
            default:
                break;
        }

        remoteView.setTextViewText(R.id.status, s);
        remoteView.setTextColor(R.id.status, c);

        return remoteView;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }
}