package com.example.tasktimerr

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_first.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
private const val TAG = "FirstFragment"
class FirstFragment : Fragment() , CursorRecyclerViewAdapter.OnTaskClickListener{

    interface OnTaskEdit{
        fun onTaskEdit(task: Task)
    }

    private val viewModel : TaskTimerViewModel by viewModels()
    private val mAdapter = CursorRecyclerViewAdapter(null , this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG , "onCreate : Called")
        viewModel.cursor.observe(this , Observer { cursor -> mAdapter.swapCursor(cursor)?.close()})
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG , "onCreateView : Called")

        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG , "onViewCreated : Called")

        task_list.layoutManager = LinearLayoutManager(context)     //-> set up RecyclerView
        task_list.adapter = mAdapter
    }

    override fun onEditClicked(task: Task) {
        (activity as OnTaskEdit?)?.onTaskEdit(task)
    }

    override fun onDeleteClicked(task: Task) {
        viewModel.deleteTask(task.id)
    }

    override fun onTaskLongClick(task: Task) {
        TODO("Not yet implemented")
    }


}