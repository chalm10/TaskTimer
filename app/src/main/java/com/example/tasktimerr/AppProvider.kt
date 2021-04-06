package com.example.tasktimerr

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log

/**
this is ContentProvider class for TaskTimer which uses [AppDatabase]
*/
private const val TAG = "AppProvider"

const val CONTENT_AUTHORITY = "com.example.tasktimerr.provider"
val CONTENT_AUTHORITY_URI : Uri = Uri.parse("content://$CONTENT_AUTHORITY")

private const val TASKS = 100
private const val TASKS_ID = 101

private const val TIMINGS = 200
private const val TIMINGS_ID = 201

private const val TASK_DURATION = 300
private const val TASK_DURATION_ID = 301


class AppProvider : ContentProvider(){

    private val uriMatcher by lazy { buildUriMatcher() }

    private fun buildUriMatcher() : UriMatcher{
        Log.d(TAG , "buildUriMatcher : starts")
        val matcher = UriMatcher(UriMatcher.NO_MATCH)

        //eg-> content://com.example.tasktimer.provider.TASKS -> tablename
        matcher.addURI(CONTENT_AUTHORITY,TasksContract.TABLE_NAME , TASKS)

        //eg-> content://com.example.tasktimer.provider.TASKS/8 -> tablename searched with id for  particular row
        matcher.addURI(CONTENT_AUTHORITY,"${TasksContract.TABLE_NAME}/#" , TASKS_ID)

        matcher.addURI(CONTENT_AUTHORITY,TimingsContract.TABLE_NAME , TIMINGS)
        matcher.addURI(CONTENT_AUTHORITY,"${TimingsContract.TABLE_NAME}/#" , TIMINGS_ID)

//        matcher.addURI(CONTENT_AUTHORITY,DurationContract.TABLE_NAME , TASK_DURATION)
//        matcher.addURI(CONTENT_AUTHORITY,"${DurationContract.TABLE_NAME}/#" , TASK_DURATION_ID)

        return matcher
    }



    override fun onCreate(): Boolean {
        Log.d(TAG , "onCreate : Starts") //we could create the database here but it is already created
        return true                             //cannot create instance of [AppDatabase] since it is singleton class
    }

    override fun getType(uri: Uri): String? {
        //function used to get MIME types(type of data being returned through URI, here its a cursor so not much use of it)
        val match = uriMatcher.match(uri)
        return when(match){
            TASKS -> TasksContract.CONTENT_TYPE
            TASKS_ID -> TasksContract.CONTENT_ITEM_TYPE

            TIMINGS -> TimingsContract.CONTENT_TYPE
            TIMINGS_ID -> TimingsContract.CONTENT_ITEM_TYPE

//            TASK_DURATION -> DurationsContract.CONTENT_TYPE
//            TASK_DURATION_ID -> DurationsContract.CONTENT_ITEM_TYPE

            else -> throw IllegalArgumentException("unknown uri passed : $uri")

        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG , "query : called with uri $uri ")
        val match= uriMatcher.match(uri)
        Log.d(TAG , "query : match is $match")

    val queryBuilder : SQLiteQueryBuilder = SQLiteQueryBuilder()

        when (match) {
            TASKS -> queryBuilder.tables = TasksContract.TABLE_NAME

            TASKS_ID -> {
                queryBuilder.tables = TasksContract.TABLE_NAME
                val taskId = TasksContract.getId(uri)
                queryBuilder.appendWhere("${TasksContract.Columns.ID} = ")  //adds simple where clause to the query
                queryBuilder.appendWhereEscapeString("$taskId")  //adds simple where clause to the query

            }

            TIMINGS -> queryBuilder.tables = TimingsContract.TABLE_NAME

            TIMINGS_ID -> {
                queryBuilder.tables = TimingsContract.TABLE_NAME
                val timingId = TimingsContract.getId(uri)
                queryBuilder.appendWhere("${TimingsContract.Columns.ID} = ")
                queryBuilder.appendWhereEscapeString("$timingId")

            }

//            TASK_DURATIONS -> queryBuilder.tables = DurationsContract.TABLE_NAME
//
//            TASK_DURATIONS_ID -> {
//                queryBuilder.tables = DurationsContract.TABLE_NAME
//                val durationId = DurationsContract.getId(uri)
//                queryBuilder.appendWhere("${DurationsContract.Columns.ID} = ")
//                queryBuilder.appendWhereEscapeString("$durationId")
//            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val db = AppDatabase.getInstance(context!!).readableDatabase
        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        Log.d(TAG, "query: rows in returned cursor = ${cursor.count}") // TODO remove this line

        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG , "insert : called")
        val match = uriMatcher.match(uri)
        Log.d(TAG , "insert : called with match = $match")
        val recordId : Long
        val returnUri : Uri
        when(match){

            TASKS->{
                val db = AppDatabase.getInstance(context!!).writableDatabase
                recordId = db.insert(TasksContract.TABLE_NAME , null , values)
                if(recordId!=-1L){
                    returnUri = TasksContract.buildUrifromID(recordId)
                }else{
                    throw SQLException("failed to insert records ")
                }
            }

            TIMINGS ->{
                val db = AppDatabase.getInstance(context!!).writableDatabase
                recordId = db.insert(TasksContract.TABLE_NAME , null , values)
                if(recordId!=-1L){
                    returnUri = TimingsContract.buildUrifromID(recordId)
                }else{
                    throw SQLException("failed to insert records ")
                }
            }

            else -> throw java.lang.IllegalArgumentException(" unknown uri passed : $uri")
        }

        if(recordId>0){
            //something was inserted
            //we need to notify the contentProvider if our database is changed
            Log.d(TAG , "insert : setting notifyChange with $uri")
            context?.contentResolver?.notifyChange(uri , null)
        }

        Log.d(TAG , "insert function returning uri $returnUri")
        return returnUri
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {                                                    //returns the number of rows(INT) that are updated
        Log.d(TAG , "update : called")
        val match = uriMatcher.match(uri)
        Log.d(TAG , "update : called with match as $match")
        val count : Int
        var selectionCriteria : String

        when(match){
            TASKS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.update(TasksContract.TABLE_NAME,values,selection,selectionArgs)
            }
            TASKS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"
                if(selection!=null && selection.isNotEmpty()){
                    selectionCriteria += " AND ($selection)"
                }
                count = db.update(TasksContract.TABLE_NAME,values,selectionCriteria,selectionArgs)
            }

            TIMINGS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.update(TimingsContract.TABLE_NAME,values,selection,selectionArgs)
            }
            TIMINGS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TimingsContract.getId(uri)
                selectionCriteria = "${TimingsContract.Columns.ID} = $id"
                if(selection!=null && selection.isNotEmpty()){
                    selectionCriteria += " AND ($selection)"
                }
                count = db.update(TimingsContract.TABLE_NAME,values,selectionCriteria,selectionArgs)
            }

            else -> throw IllegalArgumentException("unknown uri passed : $uri ")
        }

        if(count>0){
            //something was updated
            //we need to notify the contentProvider if our database is changed
            Log.d(TAG , "update : setting notifyChange with $uri")
            context?.contentResolver?.notifyChange(uri , null)
        }
        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {      //returns the number of records deleted
        Log.d(TAG , "delete : called")
        val match = uriMatcher.match(uri)
        Log.d(TAG , "delete : called with match as $match")
        val count : Int
        var selectionCriteria : String

        when(match){
            TASKS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.delete(TasksContract.TABLE_NAME,selection,selectionArgs)
            }
            TASKS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"
                if(selection!=null && selection.isNotEmpty()){
                    selectionCriteria += " AND ($selection)"
                }
                count = db.delete(TasksContract.TABLE_NAME,selectionCriteria,selectionArgs)
            }

            TIMINGS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.delete(TimingsContract.TABLE_NAME,selection,selectionArgs)
            }
            TIMINGS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TimingsContract.getId(uri)
                selectionCriteria = "${TimingsContract.Columns.ID} = $id"
                if(selection!=null && selection.isNotEmpty()){
                    selectionCriteria += " AND ($selection)"
                }
                count = db.delete(TimingsContract.TABLE_NAME,selectionCriteria,selectionArgs)
            }

            else -> throw IllegalArgumentException("unknown uri passed : $uri ")
        }

        if(count>0){
            //something was deleted
            //we need to notify the contentProvider if our database is changed
            Log.d(TAG , "delete : setting notifyChange with $uri")
            context?.contentResolver?.notifyChange(uri , null)
        }
        return count
    }
}