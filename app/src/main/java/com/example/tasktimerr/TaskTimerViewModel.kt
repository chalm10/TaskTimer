package com.example.tasktimerr

import android.app.Application
import android.content.ContentValues
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val  TAG = "TaskTimerViewModel"

class TaskTimerViewModel(application: Application) : AndroidViewModel(application) {

    //This ViewModel class is responsible for loading the cursor with data(task)

    private val databaseCursor = MutableLiveData<Cursor>()
    val cursor : LiveData<Cursor>
        get() = databaseCursor


    private val contentObserver = object : ContentObserver(Handler()){
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            Log.d(TAG , "ContentObserver.onChange : called with uri $uri")
            loadTask()
        }
    }


    init {
        Log.d(TAG , "TaskTimerViewModel : called")
        getApplication<Application>().contentResolver.registerContentObserver(TasksContract.CONTENT_URI,
        true,contentObserver)
        loadTask()
    }

    private fun loadTask(){
        val projection = arrayOf(
            TasksContract.Columns.ID,
            TasksContract.Columns.TASK_NAME,
            TasksContract.Columns.TASK_DESCRIPTION,
            TasksContract.Columns.TASK_SORT_ORDER
        )

        // <order-by> Task.SortOrder and Task.name

        //we could also use coroutines to do background tasks instead of threads
        Log.d(TAG , "loadTask : Loading all the available tasks")
        val sortOrder = "${TasksContract.Columns.TASK_SORT_ORDER}, ${TasksContract.Columns.TASK_NAME}"
        GlobalScope.launch {
            val cursor = getApplication<Application>().contentResolver.query(TasksContract.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder)

            databaseCursor.postValue(cursor)

        }

//        thread {
//            val cursor = getApplication<Application>().contentResolver.query(TasksContract.CONTENT_URI,
//                projection,
//                null,
//                null,
//                sortOrder)
//
//            databaseCursor.postValue(cursor)
//
//            }
        }

    fun saveTask(task: Task) : Task {
        if(task.name.isNotEmpty()){
            //don't save a task with no name
            val values = ContentValues()
            values.apply {
                put(TasksContract.Columns.TASK_NAME , task.name)
                put(TasksContract.Columns.TASK_DESCRIPTION , task.description)
                put(TasksContract.Columns.TASK_SORT_ORDER , task.sortOrder)
            }

            if(task.id == 0L){
                //saving a new task
                GlobalScope.launch {
                    val uri = getApplication<Application>().contentResolver.insert(TasksContract.CONTENT_URI , values)
                    if(uri!=null){
                        task.id = TasksContract.getId(uri)
                        Log.d(TAG , "new id assigned to the new task is ${task.id}")
                    }
                }
            }else{
                //saving an updated task
                GlobalScope.launch {
                    getApplication<Application>().contentResolver.update(TasksContract.buildUrifromID(task.id) ,
                         values,
                        null,
                        null)

                }
            }
        }
        return task
    }

    fun deleteTask(id : Long){
        Log.d(TAG , "Deleting task ")

        //we could also use coroutines to do background tasks instead of threads
        GlobalScope.launch {
            getApplication<Application>().contentResolver.delete(TasksContract.buildUrifromID(id),null,null)
        }

//        thread{
//            getApplication<Application>().contentResolver.delete(TasksContract.buildUrifromID(id),null,null)
//        }

    }

    override fun onCleared() {
        Log.d(TAG , "onCleared : called")
        getApplication<Application>().contentResolver.unregisterContentObserver(contentObserver)
    }
}