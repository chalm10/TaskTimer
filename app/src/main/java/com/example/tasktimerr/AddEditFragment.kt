package com.example.tasktimerr

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.fragment_add_edit.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_TASK = "task"
private const val TAG = "AddEditFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [AddEditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddEditFragment : Fragment() {
    private var task: Task? = null
    private var listener : OnSaveClicked?  = null
    private val viewModel : TaskTimerViewModel by viewModels()


    interface OnSaveClicked{
        fun onSaveClicked()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        addedit_save.setOnClickListener {
            saveTask()
            listener?.onSaveClicked()
        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG , "onCreate : starts")
        super.onCreate(savedInstanceState)
        task = arguments?.getParcelable<Task>(ARG_TASK)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG , "onCreateView : starts")
        return inflater.inflate(R.layout.fragment_add_edit, container, false)
    }

//    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    Log.d(TAG , "onViewCreated : starts")
        if (savedInstanceState==null){
            val task = task
            if(task!=null){
            Log.d(TAG , "onViewCreated : Editing an existing task of id : ${task.id}")
            addedit_name.setText(task.name)
            addedit_description.setText(task.description)
            addedit_sortorder.setText(task.sortOrder.toString())

            }else{
            //no task to edit, we are adding a new task
            Log.d(TAG , "onViewCreated : adding a new record")
            }
        }
    }

    /**
     * function created to save the details in the editText widgets
     * to a Task object
     */
    private fun taskfromUI() : Task{
        val sortOrder = if(addedit_sortorder.text.isNotEmpty()){
              addedit_sortorder.text.toString().toInt()
        }else{
              0
        }
        val newTask = Task(addedit_name.text.toString() , addedit_description.text.toString() , sortOrder)
        newTask.id = task?.id ?:0
        return newTask
    }

    private fun saveTask(){
        //create a newTask object with details to be saved,then
        //call the ViewModel's saveTask() function to save it.
        //Task is now a data class so we can compare the new details with the original task
        //and only save if they are different
        val newTask = taskfromUI()
        //save task only if details were changed
        if(newTask!=task){
            Log.d(TAG , "saveTask: saving task with task id ${newTask.id}")
            task = viewModel.saveTask(newTask)

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG , "onAttach : starts")

        if(context is OnSaveClicked){
            listener = context
        }else{
            throw RuntimeException("$context must implement OnSaveClicked")
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG , "onDetach : starts")
        listener = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param task the task to be edited, or null to add a new task
         * @return A new instance of fragment AddEditFragment.
         */
        @JvmStatic
        fun newInstance(task: Task?) =
            AddEditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TASK , task)
                }
            }
    }
}