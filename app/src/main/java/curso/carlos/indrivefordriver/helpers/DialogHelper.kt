package curso.carlos.indrivefordriver.helpers

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.EditText
import curso.carlos.indrivefordriver.R

class DialogHelper {

    companion object {

        fun askForServiceMount(
            context: Context,
            yes: DialogInterface.OnClickListener,
            no: DialogInterface.OnClickListener
        ) {

            val dialogBuilder = AlertDialog.Builder(context)
            val mountText = EditText(context)
            mountText.setHint("Ingrese la cantidad a ofertar")
            mountText.inputType = InputType.TYPE_CLASS_NUMBER

            dialogBuilder.setView(mountText)
                .setPositiveButton("Si", yes)
                .setNegativeButton("Cancelar", no)

            dialogBuilder.create().show()
        }


    }

}