/**
 * Filename     : DAL.java
 * Project      : SETPlanner
 * Author       : Nathan Bray, Jody Markic, Gabriel Paquette
 * Date Created : 2017-03-03
 * Description  : This class is the Data access Layer of the program. It is used
 *                  to connect to a sqlite database to store Course and Project objects
 */
package conestogac.hats.mad.setplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/*
* Class: DAL
* Description: This class hold all significant information needed
*               to persist data using a database
*/
class DAL {

    // Database constants
    private static final String DB_NAME = "SETPlanner.db";
    private static final int DB_VERSION = 1;

    // Course table constants
    private static final String COURSE_TABLE = "Course";

    private static final String COURSE_ID = "course_id";
    private static final int COURSE_ID_COL = 0;

    private static final String COURSE_NAME = "course_name";
    private static final int COURSE_NAME_COL = 1;

    private static final String COURSE_PATH_NAME = "course_path_name";
    private static final int COURSE_PATH_NAME_COL = 2;

    // Project table constants
    private static final String PROJECT_TABLE = "Project";

    private static final String PROJECT_ID = "project_id";
    private static final int PROJECT_ID_COL = 0;

    private static final String PROJECT_COURSE_ID = "course_id";
    private static final int PROJECT_COURSE_ID_COL = 1;

    private static final String PROJECT_NAME = "project_name";
    private static final int PROJECT_NAME_COL = 2;

    private static final String PROJECT_DUE_DATE = "due_date";
    private static final int PROJECT_DUE_DATE_COL = 3;

    private static final String PROJECT_STATUS = "status";
    private static final int PROJECT_STATUS_COL = 4;

    // CREATE and DROP TABLE statements
    private static final String CREATE_COURSE_TABLE =
            "CREATE TABLE " + COURSE_TABLE + " (" +
                    COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_NAME + " TEXT NOT NULL UNIQUE, " +
                    COURSE_PATH_NAME + " TEXT);";

    private static final String CREATE_PROJECT_TABLE =
            "CREATE TABLE " + PROJECT_TABLE + " (" +
                    PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PROJECT_COURSE_ID + " INTEGER NOT NULL, " +
                    PROJECT_NAME + " TEXT NOT NULL, " +
                    PROJECT_DUE_DATE + " TEXT, " +
                    PROJECT_STATUS + " INTEGER);";

    private static final String DROP_COURSE_TABLE =
            "DROP TABLE IF EXISTS " + COURSE_TABLE;

    private static final String DROP_PROJECT_TABLE =
            "DROP TABLE IF EXISTS " + PROJECT_TABLE;

    // database and database helper objects
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    // constructor
    DAL(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }

    // static methods

    /*
    * Method: getProjectFromCursor()
    * Description: uses a Cursor object to get a project and return it
    * Parameters: cursor
    * Returns: Project
    */
    private static Project getProjectFromCursor(Cursor cursor) {
        Project proj = null;
        assert cursor != null;

        if (cursor.getCount() != 0) {
            try {
                // make a temporary project
                proj = new Project(
                        cursor.getInt(PROJECT_ID_COL),
                        cursor.getInt(PROJECT_COURSE_ID_COL),
                        cursor.getString(PROJECT_NAME_COL),
                        cursor.getString(PROJECT_DUE_DATE_COL),
                        cursor.getInt(PROJECT_STATUS_COL));
            } catch (Exception e) {
                Log.e("DAL", "Could not get project from cursor:" + e.getMessage());
            }
        }
        return proj;
    }

    // private methods

    // these methods are used to open and close a connection to the database
    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (db != null)
            db.close();
    }

    // as well as the cursor objects used
    private void closeCursor(Cursor cursor) {
        if (cursor != null)
            cursor.close();
    }

    // public methods
    /*
    * Method: getCourseList
    * Description: returns the list of courses
    * Parameter: N/A
    * Return: returns the list of courses
    * */
    ArrayList<Course> getCourseList() {
        // create a temp list of courses
        ArrayList<Course> courses = new ArrayList<>();
        // open a connection
        openReadableDB();
        // use the database to query for the list of courses
        Cursor cursor = db.query(COURSE_TABLE,
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Course list = new Course();
            list.setCourseID(cursor.getInt(COURSE_ID_COL));
            list.setCourseName(cursor.getString(COURSE_NAME_COL));
            list.setImage(cursor.getString(COURSE_PATH_NAME_COL));
            // populate our list with the contents of the dtabase
            courses.add(list);
        }
        // cleanup
        closeCursor(cursor);
        closeDB();

        return courses;
    }


    /*
    * Method: getCourseByName
    * Description: takes in a course name and returns that course
    * Parameter: string name -> the course name
    * Return: Returns the course by name
    * */
    private Course getCourseByName(String name) {
        String where = COURSE_NAME + "= ?";
        String[] whereArgs = {name};

        openReadableDB();
        Cursor cursor = db.query(COURSE_TABLE, null,
                where, whereArgs, null, null, null);
        Course courseSelected;
        cursor.moveToFirst();
        courseSelected = new Course(cursor.getInt(COURSE_ID_COL),
                cursor.getString(COURSE_NAME_COL),
                cursor.getString(COURSE_PATH_NAME_COL));
        this.closeCursor(cursor);
        this.closeDB();

        return courseSelected;
    }


    /*
    * Method: getCourseByID
    * Description: takes in a course id and returns a course
    * Parameter: int id -> the course id
    * Return: Returns the course id
    * */
    Course getCourseByID(int id) {
        String where = COURSE_ID + "= ?";
        String[] whereArgs = {Integer.toString(id)};

        openReadableDB();
        Cursor cursor = db.query(COURSE_TABLE, null,
                where, whereArgs, null, null, null);
        Course courseSelected;
        cursor.moveToFirst();
        courseSelected = new Course(cursor.getInt(COURSE_ID_COL),
                cursor.getString(COURSE_NAME_COL),
                cursor.getString(COURSE_PATH_NAME_COL));
        this.closeCursor(cursor);
        this.closeDB();

        return courseSelected;
    }

    /*
    * Method: getProjectList
    * Description: takes int a course name and returns all projects for that course
    * Parameter: string courseName -> the is the course name
    * Return: Returns all projects for the course
    * */
    ArrayList<Project> getProjectList(String courseName) {
        String where = PROJECT_COURSE_ID + "= ?";
        int courseID = getCourseByName(courseName).getCourseID();
        String[] whereArgs = {Integer.toString(courseID)};

        this.openReadableDB();
        Cursor cursor = db.query(PROJECT_TABLE, null,
                where, whereArgs,
                null, null, null);
        ArrayList<Project> projects = new ArrayList<>();
        while (cursor.moveToNext()) {
            projects.add(getProjectFromCursor(cursor));
        }
        this.closeCursor(cursor);
        this.closeDB();

        return projects;
    }


    /*
    * Method: getProjectList
    * Description: returns the project list
    * Parameter: N/A
    * Return: Returns the list of projects
    * */
    ArrayList<Project> getProjectList() {
        this.openReadableDB();
        Cursor cursor = db.query(PROJECT_TABLE, null,
                null, null,
                null, null, null);
        ArrayList<Project> projects = new ArrayList<>();
        while (cursor.moveToNext()) {
            projects.add(getProjectFromCursor(cursor));
        }
        this.closeCursor(cursor);
        this.closeDB();

        return projects;
    }


    /*
    * Class: getProjectListSorted
    * Description: This method will give the entire list of projects sorted by due date soonest to latest
    */
    ArrayList<Project> getProjectListSorted() {
        // open connection
        this.openReadableDB();
        // query for all projects orderd by due date
        Cursor cursor = db.query(PROJECT_TABLE, null,
                null, null,
                null, null, PROJECT_DUE_DATE);
        // templ list
        ArrayList<Project> projects = new ArrayList<>();
        // while the cursor has something
        while (cursor.moveToNext()) {
            projects.add(getProjectFromCursor(cursor));
        }
        // close connections
        this.closeCursor(cursor);
        this.closeDB();
        // return the list
        return projects;
    }


    Cursor getProjectListCursor() {
        // open connection
        this.openReadableDB();
        // query for all projects orderd by due date
        Cursor cursor = db.query(PROJECT_TABLE, null,
                null, null,
                null, null, PROJECT_DUE_DATE);
        // temp list
        this.closeDB();
        // return the list
        return cursor;
    }

    /*
    * Class: insertProject
    * Description: This method wil insert the project into the database
    * with the  id as an identifier
    */
    long insertProject(Project proj) {
        ContentValues cv = new ContentValues();
        // store the course id and the project name
        cv.put(PROJECT_COURSE_ID, proj.getCourseID());
        cv.put(PROJECT_NAME, proj.getProjectName());

        // format the date into a string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String d = sdf.format(proj.getDueDate());
        cv.put(PROJECT_DUE_DATE, d);
        // status
        cv.put(PROJECT_STATUS, proj.getStatus());

        // and insert it
        this.openWriteableDB();
        long rowID = db.insert(PROJECT_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }

    /*
    * Class: deleteProject
    * Description: This method wil update the project from the database
    * with the  id as an identifier
    */
    int updateProject(Project proj) {
        // store the course values temporarily
        ContentValues cv = new ContentValues();
        cv.put(PROJECT_ID, proj.getProjectID());
        cv.put(PROJECT_COURSE_ID, proj.getCourseID());
        cv.put(PROJECT_NAME, proj.getProjectName());

        // format the date into a string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String d = sdf.format(proj.getDueDate());
        cv.put(PROJECT_DUE_DATE, d);

        cv.put(PROJECT_STATUS, proj.getStatus());

        // where project_id = proj.id
        String where = PROJECT_ID + "= ?";
        String[] whereArgs = {String.valueOf(proj.getProjectID())};
        // update
        this.openWriteableDB();
        int rowCount = db.update(PROJECT_TABLE, cv, where, whereArgs);
        this.closeDB();

        return rowCount;
    }


    /*
    * Class: deleteProject
    * Description: This method wil delete the project from the database
    * with the  id as an identifier
    */
    int deleteProject(long id) {
        String where = PROJECT_ID + "= ?";
        String[] whereArgs = {String.valueOf(id)};

        // and delete
        this.openWriteableDB();
        int rowCount = db.delete(PROJECT_TABLE, where, whereArgs);
        this.closeDB();

        return rowCount;
    }


    /*
    * Class: insertCourse
    * Description: This method will insert the course passed in
    */
    long insertCourse(Course course) {
        // store the course values temporarily
        ContentValues cv = new ContentValues();
        cv.put(COURSE_NAME, course.toString());
        cv.put(COURSE_PATH_NAME, course.getImgPath());

        // and insert it into the table
        this.openWriteableDB();
        long rowID = db.insert(COURSE_TABLE, null, cv);
        this.closeDB();

        return rowID;
    }


    /*
    * Class: deleteCourse
    * Description: This method wil update the course in the database
    * with the new course object passed in using the id as an identifier
    */
    int updateCourse(Course course) {
        // store our new values
        ContentValues cv = new ContentValues();
        cv.put(COURSE_ID, course.getCourseID());
        cv.put(COURSE_NAME, course.toString());
        cv.put(COURSE_PATH_NAME, course.getImgPath());

        String where = COURSE_ID + "= ?";
        String[] whereArgs = {String.valueOf(course.getCourseID())};

        // update the course table where course_id is the id of the course passed in
        this.openWriteableDB();
        int rowCount = db.update(COURSE_TABLE, cv, where, whereArgs);
        this.closeDB();

        return rowCount;
    }


    /*
    * Class: deleteCourse
    * Description: This method wil delete any projects from the database
    * that have a course id equal to the parameter passed in, and then delete the course
    */
    int deleteCourse(long id) {
        String where = COURSE_ID + "= ?";
        String[] whereArgs = {String.valueOf(id)};

        // get the project with the course id provided
        ArrayList<Project> temp = getProjectList(getCourseByID((int) id).toString());

        // delete each one
        for (Project proj : temp) {
            deleteProject(proj.getProjectID());
        }

        // then delete the course
        this.openWriteableDB();
        int rowCount = db.delete(COURSE_TABLE, where, whereArgs);
        this.closeDB();

        return rowCount;
    }


    /*
    * Class: nextProjectIndex
    * Description: This method wil query the sqlite sequence table for what sequence number
     * the project table is currently on
    */
    int nextProjectIndex()
    {
        int ret = 1;
        //where condition
        String where = "name = ?";
        String[] whereArgs = {PROJECT_TABLE};
        // open
        this.openReadableDB();
        // "select * from sqlite_sequence where name = Project"
        Cursor cursor = db.query("sqlite_sequence",
                null, where, whereArgs, null, null, null);

        // move to the first object in the cursor
        if (cursor.moveToFirst()){
            // and get the int, add 1 and make it the return
            ret = cursor.getInt(cursor.getColumnIndex("seq"))+1;
        }
        this.closeCursor(cursor);
        this.closeDB();
        return ret;
    }


    /*
    * Class: nextCourseIndex
    * Description: This method wil query the sqlite sequence table for what sequence number
     * the Course table is currently on
    */
    int nextCourseIndex()
    {
        int ret = 1;
        // where condition
        String where = "name = ?";
        String[] whereArgs = {COURSE_TABLE};
        //open
        this.openReadableDB();
        Cursor cursor = db.query("sqlite_sequence",
                null, where, whereArgs, null, null, null);

        // move to the first object in the cursor
        if (cursor.moveToFirst()){

            // and get the int, add 1 and make it the return
            ret = cursor.getInt(cursor.getColumnIndex("seq"))+1;
        }
        this.closeCursor(cursor);
        this.closeDB();
        return ret;
    }

    /*
    * Class: DBHelper
    * Description: This class will use the helper for sqlite to creata and update the database,
    * using the method overrides provided
    */
    private static class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // create tables
            db.execSQL(CREATE_COURSE_TABLE);
            db.execSQL(CREATE_PROJECT_TABLE);

            //Note: This is where we can add sample data for debugging purposes
            // insert default courses
            db.execSQL("INSERT INTO Course VALUES (1, 'Mobile Application Development', 'none');");
            db.execSQL("INSERT INTO Course VALUES (2, 'Industrial Application Development', 'none');");

            // insert sample projects
            db.execSQL("INSERT INTO Project VALUES (1, 2, 'Assignment 2', " +
                    "'2017-03-17', 1);");
            db.execSQL("INSERT INTO Project VALUES (2, 1, 'Presentation 1', " +
                    "'2017-04-22', 0);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {

            Log.d("DB", "Upgrading db from version "
                    + oldVersion + " to " + newVersion);
            // drop all tables
            db.execSQL(DAL.DROP_COURSE_TABLE);
            db.execSQL(DAL.DROP_PROJECT_TABLE);
            // recreate
            onCreate(db);
        }
    }
}
