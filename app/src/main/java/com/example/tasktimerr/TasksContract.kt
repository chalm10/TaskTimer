package com.example.tasktimerr

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object TasksContract {
    internal const val TABLE_NAME = "TASKS"

    //URI to access the TASKS table
    val CONTENT_URI : Uri = Uri.withAppendedPath(CONTENT_AUTHORITY_URI , TABLE_NAME)
    const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"
    const val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"


    //TASKS fields/columns
    object Columns {
        const val ID = BaseColumns._ID
        const val TASK_NAME = "NAME"
        const val TASK_DESCRIPTION = "DESCRIPTION"
        const val TASK_SORT_ORDER = "SortOrder"
    }

    fun getId(uri: Uri):Long{
        return ContentUris.parseId(uri)
    }

    fun buildUrifromID(id:Long): Uri{
        return ContentUris.withAppendedId(CONTENT_URI , id)
    }

}
