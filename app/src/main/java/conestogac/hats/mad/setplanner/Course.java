package conestogac.hats.mad.setplanner;

import java.io.Serializable;

/**
 * Filename     : Course.java
 * Project      : SETPlanner
 * Author       : Nathan Bray, Jody Markic, Gabriel Paquette
 * Date Created : 2017-02-03
 * Description  : This class is to provide the basic accessors and creation of a Course object
 */

/*
* Class: Course
* Description: This class hold all significant data associated with a
*              course object, it has method that allow you to access and
*              mutate that data
*
*/
class Course implements Serializable {

    private String courseName;
    private int courseID;
    private String imgPath;

    Course() {
        super();
    }

    /*
    * Method: Course()
    * Description:A Constructor for the Course Class
    * Parameters: String : cName
    *             int    : coursSize
    * Returns: N/A
    */
    Course(int id, String courseName) {
        super();
        this.imgPath = "none";
        this.courseName = courseName;
        this.courseID = id;
    }

    /*
    * Method: Course()
    * Description:A Constructor for the Course Class
    * Parameters: String : cName
    *             int    : coursSize
    * Returns: N/A
    */
    Course(int id, String courseName, String filePath) {
        super();
        this.imgPath = filePath;
        this.courseName = courseName;
        this.courseID = id;
    }

    /*
    * Method: getCourseID()
    * Description: Accessor for courseID
    * Parameters: N/A
    * Returns: this.CourseID : int
    */
    int getCourseID() {
        return this.courseID;
    }

    /*
    * Method: setCourseID()
    * Description: setter for courseID
    * Parameters: int id -> the course ID
    * Returns: N/A
    */
    void setCourseID(int id) {
        courseID = id;
    }

    /*
    * Method: toString()
    * Description: override of toString() to return the course name
    * Parameters: N/A
    * Returns: courseName : string
    */
    @Override
    public String toString() {
        return courseName;
    }

    /*
    * Method: getImgPath()
    * Description: getter for imgPath
    * Parameters: N/A
    * Returns: imagPath : string
    */
    String getImgPath() {
        return imgPath;
    }

    /*
    * Method: setImage()
    * Description: sets imgPath
    * Parameters: string imgP
    * Returns: N/A
    */
    void setImage(String imgP) {
        imgPath = imgP;
    }

    /*
    * Method: setCourseName()
    * Description: setter for courseName
    * Parameters: string name -> the course name
    * Returns: N/A
    */
    void setCourseName(String name) {
        courseName = name;
    }

}
