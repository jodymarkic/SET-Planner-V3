package conestogac.hats.mad.setplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Filename     : ProjectArrayAdapter.java
 * Project      : SETPlanner
 * Author       : Nathan Bray, Jody Markic, Gabriel Paquette
 * Date Created : 2017-02-03
 * Description  : this file hold the Main2Activity class, the second activity of
 *                the MADHatters project
 */

/*
* Class: Main2Activity
* Description: This Class holds the second Activity in the MADHatters project
*              it handles all user interation with the activity which includes
*              navigation to other activites along with creating and deleting
*              projects.
*
*/
public class Main2Activity extends Activity implements View.OnClickListener, DialogInterface.OnClickListener, AdapterView.OnItemLongClickListener{

    private AlertDialog alert;
    private EditText input;
    private DatePicker picker;
    private TimePicker timePicker;
    private ListView listView;
    private DAL dataLayer;
    private int cID;
    private String courseName;
    private TextView header;
    private ArrayAdapter<Project> projListAdapter;
    private long whichID;
    private Switch sw;

    private int AddOrDelete;


    /*
    * Method: onCreate()
    * Description: Creates the Main2Activity activity
    * Parameters: Bundle : savedInstanceState
    * Returns: N/A
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //set the View to this Activity
        setContentView(R.layout.activity_main2);
        try {
            //set the Action Bar title to "Projects"
            getActionBar().setTitle(R.string.activity2);
        }catch (NullPointerException e)
        {
            //log if unsuccessful
            Log.e("setTitle", e.getMessage());
        }

        //create a list view with R.id.list
        listView = (ListView) findViewById(R.id.list);
        //create a button with R.id.add_project_button
        Button btnAddProjects = (Button) findViewById(R.id.add_project_button);
        //set a listener on the local
        btnAddProjects.setOnClickListener(this);
        //setup our AlertDialog for this page.
        SetAlertDialog();

        //get container and course ID from main activity
        Intent intent = getIntent();
        cID = (int) intent.getExtras().getSerializable("cID");

        dataLayer = new DAL(this.getApplicationContext());
        //set the title to the course name
        courseName = dataLayer.getCourseByID(cID).toString();
        getActionBar().setTitle(courseName);
    }

    /*
    * Method: creatList()
    * Description: this method sets up our project listAdapter
    * Parameters: N/A
    * Returns: N/A
    */
    public void createList()
    {
        //create an ArrayList of Projects
        ArrayList<Project> thisProjectList = new ArrayList<>();
        //setup our adapter
        projListAdapter = new ProjectArrayAdapter(this, 0, thisProjectList, false);

        //check if any prof for the course selected
        if(dataLayer.getProjectList(courseName).size() != 0){

            // We will check each projects' courseId to see if it matches the selected course
            for (Project proj: dataLayer.getProjectList(courseName))
            {
                projListAdapter.add(proj);
            }
        }

        //make header if no courses in proj list or no proj for the selected course
        if(projListAdapter.isEmpty()){
            header = new TextView(this);
            header.setText(R.string.ProjectPrompt);
            header.setTextSize(20);
            listView.addHeaderView(header, null, false);
        }

        //set our adapter to the listView
        listView.setAdapter( projListAdapter );
        //setup a long click lister to the listView
        listView.setOnItemLongClickListener(this);
    }




    /*
    * Method: SetDeleteDialog()
    * Description: Setup a Dialog when spawned on a longClick event
    * Parameters: N/A
    * Returns: N/A
    */
    public void SetDeleteDialog(){

        //create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set the title of the Dialog
        builder.setTitle("Delete a Course");

        //set OK and Cancel Buttons of the Dialog
        builder.setPositiveButton("OK", this);
        builder.setNegativeButton("Cancel", this);

        // setup a LinearLayout to be used in the Dialog so we can have multiple widgets within
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        parms.weight = 0.0f;

        // Set up Widgets for the Dialog
        TextView tv = new TextView(this);
        tv.setText(R.string.DelCourse);

        sw = new Switch(this);
        sw.setTextOn("start");
        sw.setTextOff("close");
        sw.setWidth(200);

        //set our widgets to the layout
        layout.setLayoutParams(parms);
        layout.addView(tv);
        layout.addView(sw);

        //set our layout to the Dialog
        builder.setView(layout);

        //create the dialog
        alert = builder.create();
    }

    /*
    * Method: SetAlertDialog()
    * Description: Setup a Dialog when spawned on a button Click event
    * Parameters: N/A
    * Returns: N/A
    */
    public void SetAlertDialog(){

        //create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set the title of the Dialog
        builder.setTitle("Project Setup");

        //set OK and Cancel Buttons of the Dialog
        builder.setPositiveButton("OK", this);
        builder.setNegativeButton("Cancel", this);

        // setup a LinearLayout to be used in the Dialog so we can have multiple widgets within
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);


        // Set up Widgets for the Dialog
        input = new EditText(this);
        input.setHint("Enter a Project Name");
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        TextView dateLabel = new TextView(this);
        dateLabel.setText("\n  Select a Due Date :");
        dateLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        //ADDED AN EXTRA LABEL FOR TIME
        TextView timeLabel = new TextView(this);
        timeLabel.setText("\n  Select a Due Date :");
        timeLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        //END OF ADDITION

        //set our DatePicker
        picker = new DatePicker(this);

        //ADDED A TIMEPICKER TO TEST OUT
       // timePicker = new TimePicker(this);
        //END OF TIME PICKER

        //set our widgets to the layout
        layout.addView(input);
        layout.addView(dateLabel);
        layout.addView(picker);

        //ADD LABEL AND TIMEPICKER TO LAYOUT
      //  layout.addView(timeLabel);
       // layout.addView(timePicker);
        //END OF LABEL AND TIMEPICKER TO LAYOUT


        //set our layout to the Dialog
        builder.setView(layout);

        //create the Dialog
        alert = builder.create();
    }

    /*
    * Method: onClick()
    * Description: overloaded onClick event handle for a button click
    * Parameters: View v
    * Returns: N/a
    */
    @Override
    public void onClick(View v) {

        SetAlertDialog();

        switch(v.getId())
        {
            //show the Add Dialog
            case R.id.add_project_button:
                AddOrDelete = 0;
                alert.show();
                break;
        }
    }

    /*
    * Method: onClick()
    * Description: overloaded onClick event handle for a Dialog button click
    * Parameters: DialogInterface : dialog
    *             int             : which
    * Returns: N/A
    */
   @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which)
        {
            //if OK button in Dialog has been clicked
            case BUTTON_POSITIVE:
                //if this is the Add Dialog
                if(AddOrDelete == 0)
                {
                    //add the new project to the Adapter
                    Project newProj = new Project(input.getText().toString(), Project.getDueDateFromDatePicker(picker),cID, dataLayer.nextProjectIndex());
                    dataLayer.insertProject(newProj);
                    listView.removeHeaderView(header);
                    input.setText("");
                    Toast toast = Toast.makeText(this,"Project added successfully", Toast.LENGTH_SHORT);
                    toast.show();
                }
                //if this is the Delete Dialog
                else if(AddOrDelete == 1)
                {
                    //check that user has confirmed they want to delete the project
                    if(sw.isChecked()) {
                        //find and delete the appropriate project
                        int pID = dataLayer.getProjectList().get((int)whichID).getProjectID();
                        int length = dataLayer.getProjectList().size();
                        if (length != 0) {
                            dataLayer.deleteProject(pID);
                            //refresh the adapter to show the changes
                            Toast toast = Toast.makeText(this,"Project removed", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
                createList();
                break;
            //if Cancel button in Dialog has been clicked
            case BUTTON_NEGATIVE:
                //close the Dialog
                if(AddOrDelete == 0)
                {
                    input.setText("");
                }
                alert.cancel();
                break;
        }
    }

    /*
    * Method: onItemLongClick
    * Description: eventHandle for a longClick event on the ListView
    * Parameters: AdapterView<?> parent
    *             View view
    *             int position
    *             long id
    * Returns: true : boolean
    */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        //course ID
        whichID = id;
        AddOrDelete = 1;

        //setup the delete dialog
        SetDeleteDialog();
        //show the delete dialog
        alert.show();

        //log the event
        Log.v("long clicked","pos: " + position);
        return true;
    }

    /*
    * Method: onResume()
    * Description: this method handles the onResume event
    *              and loads the container from file
    * Parameters: N/A
    * Returns: N/A
    */
    @Override
    public void onResume(){
        super.onResume();
        //load container contents from a file
        //container = container.load(this);
        //reset adapters and lists
        createList();
        //log the event
        Log.d("FileIO", "onResume Activity2");
    }

    /*
    * Method: onPause()
    * Description: this method handles the onPause event
    *              and saves the container to a file
    * Parameters: N/A
    * Returns: N/A
    */
    @Override
    public void onPause(){
        super.onPause();
        //save container contents to a file
        //container.save(this, container);
        //log the event
        Log.d("FileIO", "onPause Activity3");
    }

}

