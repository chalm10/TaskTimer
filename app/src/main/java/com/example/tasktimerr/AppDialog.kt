package com.example.tasktimerr

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment

private const val TAG = "AppDialog"

const val DIALOG_ID = "id"
const val DIALOG_MESSAGE = "message"
const val DIALOG_POSITIVE_RID = "positive_rid"
const val DIALOG_NEGATIVE_RID = "negative_rid"

class AppDialog : AppCompatDialogFragment(){

    private var dialogEvents : DialogEvents? = null

    /**
     * The dialogue's callback interface, to note of user's responses (deletion confirmed, etc..)
     */
    interface DialogEvents{
        //we'll only be using positive responses tapped by the user in this app
        //negative responses and dialog cancel events could also be handled
        fun onPositiveDialogResult(dialogID : Int , args : Bundle)
//        fun onNegativeDialogResult(dialogID : Int , args : Bundle)
//        fun onDialogCancel(args : Bundle)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG , "onAttach : starts")
        //Activities/Fragments containing this fragment must implement its callbacks
        dialogEvents = try {
            //is there a parent fragment, if so then that is what we call back
            parentFragment as DialogEvents
        }
        catch (e :TypeCastException ){
            try {
                //no parent fragment found, call back the activity instead
                context as DialogEvents
            }
            catch (e : ClassCastException){
                //activity does not implement the interface
                throw ClassCastException("Activity $context must implement AppDialog.DialogEvents interface")
            }
        }
        catch (e : ClassCastException){
            //parent fragment does not implement the interface
            throw ClassCastException("Fragment $parentFragment must implement the interface")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG , "onCreateDialog : called")
        val arguments = arguments
        val builder = AlertDialog.Builder(requireContext())

        val dialogId : Int
        val messageString : String?
        var positiveStringId : Int  //these stringIDs could also be string, ive taken int here
        var negativeStringId : Int

        if (arguments!=null){
            dialogId = arguments.getInt(DIALOG_ID)
            messageString = arguments.getString(DIALOG_MESSAGE)

            if(dialogId==0 || messageString==null){
                throw IllegalArgumentException("DIALOG_ID and/or DIALOG_MESSAGE not present in the bundle")
            }
            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID)
            if(positiveStringId==0){
                positiveStringId = R.string.ok
            }

            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID)
            if(negativeStringId==0){
                negativeStringId = R.string.cancel
            }
        }else{
            throw IllegalArgumentException("must pass DIALOG_ID and DIALOG_MESSAGE in the bundle")
        }

        return builder.setMessage(messageString)
            .setPositiveButton(positiveStringId){ dialogInterface, which ->
                //callback positive result function
                dialogEvents?.onPositiveDialogResult(dialogId , arguments)
            }
            .setNegativeButton(negativeStringId){ dialogInterface, which ->
                //callback for negative result function
//                dialogEvents?.onNegativeDialogResult(dialogId , arguments)
            }
            .create()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG , "onDetach : starts")
        //reset the active callbacks interface , because we're no longer attached
        dialogEvents = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        Log.d(TAG , "onDismiss : called")
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        Log.d(TAG , "onCancel : called")
        val dialogID = arguments?.getInt(DIALOG_ID)
//        dialogEvents?.onDialogCancel(dialogID)
    }
}