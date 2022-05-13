package mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
//import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.CRUD.ASIGNACION
//import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.CRUD.INVENTARIO
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.databinding.FragmentAgregarAsignacionBinding
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [agregarAsignacion.newInstance] factory method to
 * create an instance of this fragment.
 */
class agregarAsignacion : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentAgregarAsignacionBinding
    private val b get() = binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAgregarAsignacionBinding.inflate(inflater,container,false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val codigobarras = (arguments?.getString("codigobarras"))
        //search for the inventario
        //val i = ASIGNACION(activity?.applicationContext!!)
        val builder : MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("Selecciona la fecha de vencimiento")
        val picker: MaterialDatePicker<*> = builder.build()
        var calendar: Calendar
        var format : SimpleDateFormat
        var formattedDate = "2022-01-01"

        b.btnFecha.setOnClickListener {
            picker.show(activity?.supportFragmentManager!!,picker.toString())
        }

        picker.addOnPositiveButtonClickListener {
            b.txtFechaCompra.setText(picker.headerText)
            calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it as Long
            format = SimpleDateFormat("yyyy-MM-dd")
            calendar.add(Calendar.DATE, 1)
            formattedDate = format.format(calendar.getTime())
        }

        b.btnInsertAsig.setOnClickListener {
            if (codigobarras != null) {
                val baseRemota = FirebaseFirestore.getInstance()
                val datos = hashMapOf(
                    "NOMBREEMPLEADO" to b.txtNombreEmpleado.text.toString(),
                    "AREA" to b.txtArea.text.toString(),
                    "FECHA" to b.txtFechaCompra.text.toString(),
                    "CODIGOBARRAS" to codigobarras
                )

                baseRemota.collection("asignacion")
                    .add(datos)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Asignado", Toast.LENGTH_LONG)
                            .show()
                        baseRemota.collection("inventario")
                            .whereEqualTo("CODIGOBARRAS", codigobarras)
                            .get()
                            .addOnSuccessListener {
                                for (document in it) {
                                    baseRemota.collection("inventario")
                                        .document(document.id)
                                        .update("ASIGNADO", true)
                                }
                                activity?.onBackPressed()
                            }

                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(requireContext())
                            .setMessage(it.message)
                            .show()
                    }
                b.txtNombreEmpleado.setText("")
                b.txtArea.setText("")
                b.txtFechaCompra.setText("")
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment agregarAsignacion.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            agregarAsignacion().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}