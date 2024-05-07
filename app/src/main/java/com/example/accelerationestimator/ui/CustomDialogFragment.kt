package com.example.accelerationestimator.ui
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.accelerationestimator.R

class CustomDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(messageResId: Int): CustomDialogFragment {
            val fragment = CustomDialogFragment()
            val args = Bundle()
            args.putInt("messageResId", messageResId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val messageResId = arguments?.getInt("messageResId") ?: 0
        val message = getString(messageResId)

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.custom_dialog_layout, null)

            val messageTextView = view.findViewById<TextView>(R.id.messageTextView)
            val iconImageView = view.findViewById<ImageView>(R.id.iconImageView)

            messageTextView.text = message

            // Check if the message is equal to "X" and show the icon accordingly
            if (message == getString(R.string.tires_width_description_description) || message ==getString((R.string.tires_aspect_ratio_description)) || message == getString(R.string.tires_wheel_diameter_description)) {
                iconImageView.setImageResource(R.drawable.sidewall_markings_diagram_icon)
                iconImageView.visibility = View.VISIBLE
            }

            builder.setView(view)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}