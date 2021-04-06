package com.example.tasktimerr

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**basic database class for the application
only class that should use this is [AppProvider]
* */
private const val TAG = "AppDatabase"
private const val DATABASE_NAME = "TaskTimer.db"
private const val DATABASE_VERSION = 1

internal class AppDatabase private constructor(context: Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){

    init {
        Log.d(TAG, "AppDatabase initialising")
    }

    override fun onCreate(db : SQLiteDatabase) {
        //CREATE TABLE TASKS(_id INTEGER PRIMARY KEY NOT NULL, NAME TEXT NOT NULL ,DESCRIPTION TEXT , SortOrder INTEGER);
        val sSQL = """  CREATE TABLE ${TasksContract.TABLE_NAME}(
            ${TasksContract.Columns.ID} INTEGER PRIMARY KEY NOT NULL, 
            ${TasksContract.Columns.TASK_NAME} TEXT NOT NULL,
            ${TasksContract.Columns.TASK_DESCRIPTION} TEXT,
            ${TasksContract.Columns.TASK_SORT_ORDER} INTEGER);""".trimIndent()
        Log.d(TAG , sSQL)
        db.execSQL(sSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase , oldVersion: Int, newVersion: Int){
        Log.d(TAG , "onUpgrade : starts")
        when(oldVersion){
            1-> {
                //upgrade logic from version 1
            }
            else -> throw IllegalStateException("onUpgrade with unknown version $newVersion")
        }
    }

    companion object : SingletonHolder<AppDatabase, Context>(::AppDatabase)
}