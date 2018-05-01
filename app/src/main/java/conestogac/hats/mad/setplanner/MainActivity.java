package conestogac.hats.mad.setplanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Filename     : ProjectArrayAdapter.java
 * Project      : SETPlanner
 * Author       : Nathan Bray, Jody Markic, Gabriel Paquette
 * Date Created : 2017-02-03
 * Description  : this file hold the MainActivity class, the first activity of
 *                the MADHatters project
 */

/*
* Class: MainActivity
* Description: This Class holds the first Activity in the MADHatters project
*              it handles all user interaction with the activity which includes
*              navigation to other activities along with adding and deleting courses.
*
*/
public class MainActivity extends Activity
        implements View.OnClickListener,
        View.OnKeyListener,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    final int RESULT_LOAD_IMAGE = 0;
    private DAL dataLayer;
    private ArrayAdapter<Course> courseListAdapter;
    private EditText mEditText;
    private ListView listView;
    private int itemClicked;
    private Intent serviceIntent;


    /*
    * Method: onCreate()
    * Description: Creates the MainActivity activity
    * Parameters: Bundle : savedInstanceState
    * Returns: N/A
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set our activity to display this
        setContentView(R.layout.activity_main);

        //change action bar to courses
        getActionBar().setTitle(R.string.activity1);

        //set btnAddCourses to the R.id.add_course_button
        Button btnAddCourses = (Button) findViewById(R.id.add_course_button);
        //set an on click listener to the button
        btnAddCourses.setOnClickListener(this);

        Context c = this.getApplicationContext();

        // setup the DAL
        dataLayer = new DAL(c);

        Intent serviceIntent = new Intent(this, OverdueService.class);
        c.stopService(serviceIntent);
        c.startService(serviceIntent);

        //set the listView to R.id.list
        listView = (ListView) findViewById(R.id.list);
        //set the edit text to the R.id.editText
        mEditText = (EditText) findViewById(R.id.editText);

        //set an on key listen to the edit text
        mEditText.setOnKeyListener(this);
        //set a on item click listener to the list view
        listView.setOnItemClickListener(this);
        //set a long click listener to the list view
        listView.setOnItemLongClickListener(this);
        itemClicked = 0;

        //CHECKS OUR CURRENT SDK BUILD
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            this.getApplicationContext().checkSelfPermission(Manifest.permission.INTERNET);
        }


       /* int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        if (!permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // User may have declined earlier, ask Android if we should show him a reason
            if (shouldShowRequestPermissionRationale(thisActivity, Manifest.permission.WRITE_CALENDAR)) {
                // show an explanation to the user
                // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
            } else {
                // request the permission.
                // CALLBACK_NUMBER is a integer constants
                requestPermissions(thisActivity,  new String[]{Manifest.permission.WRITE_CALENDAR}, CALLBACK_NUMBER);
                // The callback method gets the result of the request.
            }
        } else {
        // got permission use it
        }*/
    }
    /*public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }*/



    /*
    * Method: createNewCourse()
    * Description: Add a course to the Adapter
    * Parameters: String cName
    * Returns: N/A
    */
    private void createNewCourse(String cName) {
        //create a course object
        Course course = new Course(dataLayer.nextCourseIndex(), cName);
        //add course to the adapter
        courseListAdapter.add(course);
        dataLayer.insertCourse(course);
    }


    /*
    * Method: createList()
    * Description: This method sets the adpter to the listView
    * Parameters: N/A
    * Returns: N/A
    */
    public void createList() {
        //create aa adapter for the courseList
        courseListAdapter = new CourseArrayAdapter(this, R.layout.course_list_option, dataLayer.getCourseList());
        //set the adapter to the listView
        listView.setAdapter(courseListAdapter);

    }

    /*
    * Method: onCreateOptionsMenu()
    * Description: Event handle that inflates the activities menu
    * Parameters: Menu menu
    * Returns: true : boolean
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate out main activity menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    /*
    * Method: onOptionsItemSelected()
    * Description: Event handle for an onclick event for the Menu
    * Parameters: MenuItem item
    * Returns: true : boolean
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //our event handle for when we select a menu item
        switch (item.getItemId()) {
            case R.id.menu_activity3:
                Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
                startActivity(intent);
                return true;
            case R.id.cCollege:
                String link = getApplicationContext().getString(R.string.eConestoga);
                Uri viewUri = Uri.parse(link);
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, viewUri);
                startActivity(viewIntent);
            default:
                return super.onOptionsItemSelected((item));
        }
    }

    /*
    * Method: onClick
    * Description: Event handle for an onclick event for a button
    * Parameters: View v
    * Returns: N/A
    */
    @Override
    public void onClick(View v) {
        //our event handle when we click anything in our view
        //currently our button
        switch (v.getId()) {
            case R.id.add_course_button:
                mEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
                break;

        }
    }


    /*
    * Method: onItemClick()
    * Description: Event handle for the onclick event for the ListView
    * Parameters: AdapterView<?> parent
    *             View View
    *             int position
    *             long id
    * Returns: N/A
    */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int courseID = dataLayer.getCourseList().get((int) id).getCourseID();
        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
        intent.putExtra("cID", courseID);
        startActivity(intent);
    }

    /*
    * Method: onKey()
    * Description: Event handle for a keydown on the enter key
    * Parameters: View v
    *             int keyCode
    *             KeyEvent event
    * Returns: false : boolean
    */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            addCourse();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
        return false;
    }

    /*
    * Method: addCourse()
    * Description: Adds a course to the Adapter through calling
    *              create new course
    * Parameters: N/A
    * Returns: N/A
    */
    public void addCourse() {
        String textContent = mEditText.getText().toString().trim();
        //check that there's something in the editText
        if (!textContent.equals("")) {
            //add the course
            createNewCourse(textContent);
            //reset the Edit text
            mEditText.setText("");
            Toast toast = Toast.makeText(this, textContent + " added successfully", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /*
    * Method: onItemLongClick()
    * Description: Event handle for long clicks on the List view
    * Parameters: AdapterView<?> parent
    *             View view
    *             int position
    *             long id
    * Returns: true : boolean
    */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        itemClicked = position;
        //set the Dialog
        SetAlertDialog(position);

        //log the event
        Log.v("long clicked", "pos: " + position);
        return true;
    }

    /*
    * Method: SetAlertDialog()
    * Description: Setup a Dialog when spawned on a button Click event
    * Parameters: N/A
    * Returns: N/A
    */
    public void SetAlertDialog(final int id) {


        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.course_alert_dialog, null);

        final Switch deleteSwitch = (Switch) alertLayout.findViewById(R.id.delete_Switch);
        final Button addPhotoButton = (Button) alertLayout.findViewById(R.id.add_Photo_Button);

        //create an AlertDialog builder
        final AlertDialog.Builder courseAlert = new AlertDialog.Builder(this);
        //set the title of the Dialog
        courseAlert.setTitle("Course Options");
        //set buttons


        courseAlert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (deleteSwitch.isChecked()) {
                    removeCourse(id);
                }
            }
        });

        courseAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        courseAlert.setView(alertLayout);
        //create the Dialog
        AlertDialog alert = courseAlert.create();

        //show the Dialog
        alert.show();
    }

    /*
    * Method: onActivityResult
    * Description: This function is called when the used returns from the gallery
    *              which returns a path to the photo that they chose
    * Parameters: int requetCode -> this is the code to say we are returning an image path
    *             int resultCode -> this says that the return was successful
    *             Intent data -> this contains the data
    * Return: N/A
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Course c = dataLayer.getCourseList().get(itemClicked);
            c.setImage(picturePath);
            dataLayer.updateCourse(c);
            Log.v("picture path", "path:" + picturePath);
            Log.d("ITEM CLICKED", "test - "+ itemClicked);
        }
    }


    /*
    * Method: onResume()
    * Description: event handle for on resume of activity
    * Parameters: N/A
    * Returns: N/A
    */
    @Override
    public void onResume() {
        super.onResume();
        createList();
        //log the event
        Log.d("FileIO", "onResume Activity");
    }

    /*
    * Method: onPause()
    * Description: event handle for on pause of the Activity
    * Parameters: N/A
    * Returns: N/A
    */
    @Override
    public void onPause() {
        super.onPause();
        //save the container to a file
        //log the event
        Log.d("FileIO", "onPause Activity");
    }


    /*
    * Method: removeCourse
    * Description: this function takes in the id of the course we are going to remove and
    *              removes it from the database
    * Parameters: long id -> this is the id of the course we are going to remove
    * Return: n/a
    */
    public void removeCourse(long id) {
        //find the course that was selected
        int cID = dataLayer.getCourseList().get((int) id).getCourseID();
        dataLayer.deleteCourse(cID);
        Log.d("Course Removal"," Course removed: " + cID);
        //update Adapter
        Toast toast = Toast.makeText(this, "Course removed", Toast.LENGTH_SHORT);
        toast.show();
        createList();
    }


    /*
    * Method: onSaveInstanceState
    * Description: this function saves the item clicked
    * Parameters: Bundle outState -> this is where the data is saved when saving states
    * Return: n/a
    */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("itemClicked", itemClicked);
    }


    /*
    * Method: onRestoreInstanceState
    * Description: this function returns the item clicked
    * Parameters: Bundle outState -> this is where the data is restored from
    * Return: n/a
    */
    @Override
    public void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);

      itemClicked = outState.getInt("itemClicked");
    }
}


