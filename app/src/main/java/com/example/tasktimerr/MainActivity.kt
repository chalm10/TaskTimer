package com.example.tasktimerr

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

//import androidx.support.v4.app.Fragment

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), AddEditFragment.OnSaveClicked , FirstFragment.OnTaskEdit{

    //mTwoPane is a variable to store the running mode of device(PORTRAIT / LANDSCAPE)
    var mTwoPane = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        Log.d(TAG , "onCreate : called")
        mTwoPane = resources.configuration.orientation ==Configuration.ORIENTATION_LANDSCAPE
        var fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        if (fragment!=null){
            //there was an existing fragment to edit the task, make sure panes are set correctly
            showEditPane()
        }else{
            task_details_container.visibility = if (mTwoPane) View.INVISIBLE else View.GONE
            firstFragment.view?.visibility = View.VISIBLE
        }
    }

    private fun showEditPane(){
        task_details_container.visibility = View.VISIBLE

        //hide the left hand pane if in portrait mode
        firstFragment.view?.visibility = if (mTwoPane) View.VISIBLE else View.GONE
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    private fun removeEditPane ( fragment : Fragment? = null){
        Log.d(TAG , "removeEditPane : starts")
        //remove the right hand pane(taskdetailscontainer)
        if(fragment!=null){
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
        //set the visibility of right hand pane
        //it is INVISIBLE FOR LANDSCAPE and  GONE FOR PORTRAIT
        task_details_container.visibility = if(mTwoPane) View.INVISIBLE else View.GONE
        //and make the left pane visible(LIST OF TASKS)
        firstFragment.view?.visibility = View.VISIBLE
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onSaveClicked() {
        Log.d(TAG , "onSaveClicked : starts" )
        var fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        removeEditPane(fragment)
        Log.d(TAG , "onSaveClicked : ends" )
    }

    override fun onTaskEdit(task: Task) {
        taskEditRequest(task)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
//            R.id.menumain_settings -> true
            R.id.menumain_addTask -> taskEditRequest(null)
            android.R.id.home -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
                removeEditPane(fragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun taskEditRequest(task : Task?){
        Log.d(TAG , "taskEditRequest : starts")

        // function used to edit and add tasks
        //create new fragment to edit/add the task

        val newFragment = AddEditFragment.newInstance(task)
        supportFragmentManager.beginTransaction()
            .replace(R.id.task_details_container,newFragment)
            .commit()
        showEditPane()


        Log.d(TAG , "taskEditRequest : ends ")

    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.task_details_container)
        if(fragment==null || mTwoPane){
            super.onBackPressed()
        }else{
            removeEditPane(fragment)
        }

    }
}