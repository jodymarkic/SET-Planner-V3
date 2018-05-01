package conestogac.hats.mad.setplanner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 *
 * Filename     : PCourseArrayAdapter.java
 * Project      : SETPlanner
 * Author       : Nathan Bray, Jody Markic, Gabriel Paquette
 * Date Created : 2017-03-16
 * Description  : This file holds the code needed to create a custom ArrayAdapter.
 * This is so we can display our list items in the template that we want and they will
 * remain consistent throughout the use of it
 */

public class CourseArrayAdapter extends ArrayAdapter<Course> {
    private Context context;
    private List<Course> courseList;

    // The constructor takes the normal array adapter parameters, as well as the set parameters we
    // need for the layout to view the way we want
    CourseArrayAdapter(Context c, int r, ArrayList<Course> obj){
        super(c,r,obj);

        this.context = c;
        this.courseList = obj;
    }

    /*
     * Function    : getView
     * Description :This function provides the view that we customize throughout it to the adapter so it can
     *              use that as a template for each menu item
     */
    public View getView(int position, View convertView, ViewGroup parent) {


        Course course = courseList.get(position);

        // inflate the layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.course_list_option, null);

        // instantiate a reference to the widgets
        TextView courseName = (TextView) view.findViewById(R.id.courseName);
        ImageView imageView = (ImageView) view.findViewById(R.id.imgName);


        // Display the project name
        courseName.setText(course.toString());
        File imagePath;
        if(!course.getImgPath().equalsIgnoreCase("none"))
        {
            BitmapFactory.Options mBitmapOptions = new BitmapFactory.Options();
            mBitmapOptions.inSampleSize =4;
            imagePath = new File(course.getImgPath());
            Bitmap bmImg = BitmapFactory.decodeFile(imagePath.toString(), mBitmapOptions);
            imageView.setImageBitmap(bmImg);
        }


        // Finally, return the view
        return view;
    }
}
