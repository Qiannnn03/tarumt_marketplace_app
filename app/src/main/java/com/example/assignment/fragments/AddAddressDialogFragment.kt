package com.example.assignment.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.assignment.R

class AddAddressDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_add_address_dialog, null)

        builder.setView(view)
            .setTitle("Add New Address")
            .setPositiveButton("Add") { dialog, id ->
                // Handle the positive button click (e.g., save the address)
            }
            .setNegativeButton("Cancel") { dialog, id ->
                // Handle the negative button click (e.g., dismiss the dialog)
                dialog.dismiss()
            }

        return builder.create()
    }
}
