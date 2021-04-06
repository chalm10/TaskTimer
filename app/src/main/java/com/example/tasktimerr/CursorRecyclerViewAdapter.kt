package com.example.tasktimerr

import android.database.Cursor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CursorRVAdapter"

class TaskViewHolder( val containerView: View) : RecyclerView.ViewHolder(containerView) {
    // no need for using findViewById(), we can use LayoutContainer

    //*new note* :-> findViewById was required since LayoutContainer did not work

    val name : TextView = containerView.findViewById(R.id.tli_name)
    val description : TextView= containerView.findViewById(R.id.tli_description)
    val btn_edit : ImageButton= containerView.findViewById(R.id.tli_edit)
    val btn_delete: ImageButton = containerView.findViewById(R.id.tli_delete)


    fun bind(task : Task , listener: CursorRecyclerViewAdapter.OnTaskClickListener){
        name.text = task.name
        description.text = task.description
        btn_delete.visibility = View.VISIBLE
        btn_edit.visibility = View.VISIBLE

        btn_edit.setOnClickListener{
            listener.onEditClicked(task)
        }
        btn_delete.setOnClickListener{
            listener.onDeleteClicked(task)
        }
        containerView.setOnLongClickListener{
            listener.onTaskLongClick(task)
            true
        }

    }

}

class CursorRecyclerViewAdapter(private var cursor : Cursor? , private val listener : OnTaskClickListener)
    : RecyclerView.Adapter<TaskViewHolder>() {

    interface OnTaskClickListener{
       fun onEditClicked(task : Task)
       fun onDeleteClicked(task : Task)
       fun onTaskLongClick(task : Task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.d(TAG , "onCreateViewHolder : new view requested")
        //no need for viewType since the recycler view displays the same kind of views
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_item , parent , false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        Log.d(TAG , "onBindViewHolder : starts")
        //to display the content in the views
        val cursor = cursor //to avoid smartcast errors

        if(cursor==null || cursor.count==0){
            Log.d(TAG , "onBindViewHolder : providing instructions") // currently no data downloaded and hence to be displayed ,
                                                                            //show the instructions
            holder.name.setText(R.string.Instructions_heading)
            holder.description.setText(R.string.Instructions_description)
            holder.btn_edit.visibility = View.GONE
            holder.btn_delete.visibility = View.GONE
        }else{
            if(!cursor.moveToPosition(position)){
                throw IllegalStateException("cursor not able to move to position $position")
            }else{

                //create a Task object from the data in the cursor
                val task = Task(
                    cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_NAME)),
                    cursor.getString(cursor.getColumnIndex(TasksContract.Columns.TASK_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(TasksContract.Columns.TASK_SORT_ORDER)))

                //remember that the ID isn't set in the constructor and hence have to initialise it separately
                task.id = cursor.getLong(cursor.getColumnIndex(TasksContract.Columns.ID))

                holder.bind(task , listener)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG , "getItemCount : starts")
        val cursor = cursor
        val count = if(cursor==null || cursor.count==0){
            1       //since we populate the list with one view to display the instructions
        }else{
            cursor.count
        }
        Log.d(TAG , "getItemCount : returning $count views")
        return count
    }

    /**
     * swap in a newCursor , returning the oldCursor
     * the returned oldCursor is *not* closed
     *
     * @param newCursor to be used
     * @return returns the previously set cursor , or null if there wasn't one
     *
     * if the given newCursor is the same instance as the previously set cursor , return null
     */
    fun swapCursor(newCursor : Cursor?) : Cursor?{
        if(newCursor == cursor){
            return null
        }
        val numItems = itemCount
        val oldCursor = cursor
        cursor = newCursor
        if(newCursor != null){
            //notify the observers about the new cursor
            notifyDataSetChanged()
        }else{
            //notify the observers about the lack of a data set
            notifyItemRangeRemoved(0 , numItems)
        }
        return oldCursor
    }




}