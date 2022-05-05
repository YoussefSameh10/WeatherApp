package com.youssef.weatherapp.utils

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.youssef.weatherapp.R

object UIHelper {
    fun showAlertDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(context.getString(R.string.ok)) { _, _ -> }
        builder.show()
    }

    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(context.getString(R.string.ok)) { _, _ -> positiveAction()}
        builder.setNegativeButton(context.getString(R.string.cancel)) { _, _ -> negativeAction()}
        builder.show()
    }
}