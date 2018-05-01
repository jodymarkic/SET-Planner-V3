package conestogac.hats.mad.setplanner;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Filename     : ProjectArrayAdapter.java
 * Project      : SETPlanner
 * Author       : Nathan Bray, Jody Markic, Gabriel Paquette
 * Date Created : 2017-02-03
 * Description  : This file holds the code needed to create a custom ArrayAdapter.
 * This is so we can display our list items in the template that we want and they will
 * remain consistent throughout the use of it
 */


/// This class is to make our own custom adapter with a layout we can control in the xml.
public class ProjectArrayAdapter extends ArrayAdapter<Project> {

    private Context context;
    private List<Project> projList;
    private boolean flag;
    private DAL dataAccess;

    // The constructor takes the normal array adapter parameters, as well as the set parameters we
    // need for the layout to view the way we want
    ProjectArrayAdapter(Context c, int r, ArrayList<Project> obj, boolean showCourseName){
        super(c,r,obj);

        dataAccess = new DAL(c);
        this.context = c;
        this.projList = obj;
        // Flag represents whether we want to show the courseName or not
        this.flag = showCourseName;
    }

    /*
     * Function    : getView
     * Description :This function provides the view that we customize throughout it to the adapter so it can
     *              use that as a template for each menu item
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        Project proj = projList.get(position);

        // inflate the layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.project_layout, null);

        // instantiate a reference to the widgets
        TextView courseName = (TextView) view.findViewById(R.id.courseName);
        TextView projName = (TextView) view.findViewById(R.id.projName);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView status = (TextView) view.findViewById(R.id.status);


        // If we don't want course name, then just leave the field blank
        courseName.setText("");
        // Otherwise the page wants course name to show, then
        if(flag)
        {
            try {
                    courseName.setText(dataAccess.getCourseByID(proj.getCourseID()).toString());
            } catch (Exception e) {
                Log.e("ArrayAdapter-CourseName", e.getMessage());
            }
         }
        // Display the project name
        projName.setText(proj.getProjectName());

        // Create a date in the format year-month-day using the due date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String d = sdf.format(proj.getDueDate());
        Resources res = context.getResources();
        // Then, display it in the section for the due dat
        String text = String.format(res.getString(R.string.dueString), d);
        date.setText(text);

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

        status.setText(s);
        status.setTextColor(c);
        // Finally, return the view
        return view;
    }
}
