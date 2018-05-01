package conestogac.hats.mad.setplanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Filename     : MyThread.java
 * Project      : SETPlanner
 * Author       : Nathan Bray, Jody Markic, Gabriel Paquette
 * Date Created : 2017-02-03
 * Description  : This file holds the code needed to create and run a thread in the background of the
 *                SETPlanner program.
 */


/*
*   Class: MyThread
*   Description: This class holds the code for an asynchronous task
*
*
*/
class MyThread extends AsyncTask<String, Void, String> {

    private DAL dataLayer;
    private Context context;

    /*
    * Function    : MyThread
    * Description :This function is an overloaded constructor for the MyThread class
    */
    MyThread(Context c) {
        dataLayer = new DAL(c);
        context = c;
    }

    /*
    * Function    : onPreExecute
    * Description :This method executes code on pre execute of a thread
    */
    @Override
    protected void onPreExecute() {
    }

    /*
    * Function    : doInBackground
    * Description : This method checks if a project has become overdue.
    *
    */
    @Override
    protected String doInBackground(String... params) {
        String ret = "";
        int overdueCount = 0;

        try {

            //create an ArrayList of Projects
            ArrayList<Project> thisProjectList;

            while (true) // loop until we break
            {
                thisProjectList = dataLayer.getProjectListSorted();

                //check if there are any projects
                if (thisProjectList.size() != 0) {
                    // for each project
                    for (Project proj : thisProjectList) {
                        // as long as it is a past date
                        long twentyThreeHours = 82800000;
                        if (System.currentTimeMillis() >= (proj.getDueDate().getTime() + twentyThreeHours)){
                            // if it is incomplete
                            if (proj.getStatus() == 0) {
                                proj.setStatus(-1);
                                dataLayer.updateProject(proj);
                                overdueCount++;
                            }
                        } else {
                            // otherwise, it is a future date, so we don't need to check anymore
                            break;
                        }
                    }
                }

                // sleep for 5 seconds
                Thread.sleep(5000);
                // if we have some projects overdue
                if (overdueCount > 0) {
                    ret = Integer.toString(overdueCount);
                    //local variables
                    CharSequence contentTitle = "SETPlanner";
                    CharSequence contentText = overdueCount + " project(s) overdue!";
                    int icon = R.drawable.default_image;

                    Intent intent = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
                    //create a notification object
                    Notification notification = new Notification.Builder(context)
                            .setSmallIcon(icon)
                            .setContentTitle(contentTitle)
                            .setContentText(contentText)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    //send notification.
                    NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    manager.notify(1, notification);
                    break;
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }


        return ret;
    }

    /*
    * Function    : onPostExecute
    * Description :This method spawns another thread after the current thread has finished executing
    */
    @Override
    protected void onPostExecute(String result) {
        //goto the thread that informs the user that a project has
        //gone overdue.
       MyThread t = new MyThread(context);
        t.execute();
    }
}