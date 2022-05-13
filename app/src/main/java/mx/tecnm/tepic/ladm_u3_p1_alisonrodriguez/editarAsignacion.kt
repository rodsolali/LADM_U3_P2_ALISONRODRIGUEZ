package mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.databinding.FragmentEditarAsignacionBinding
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.databinding.FragmentEditarInventarioBinding
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [editarAsignacion.newInstance] factory method to
 * create an instance of this fragment.
 */
class editarAsignacion : Fragment() {
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

    private lateinit var binding: FragmentEditarAsignacionBinding
    private val b get() = binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditarAsignacionBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = (arguments?.getString("id"))
        val baseRemota = FirebaseFirestore.getInstance()
        val coleccion = baseRemota.collection("asignacion")
        coleccion.document(id!!).get().addOnSuccessListener {
            b.txtNombreEmpleado.setText(it.getString("NOMBREEMPLEADO"))
            b.txtArea.setText(it.getString("AREA"))
            b.txtFechaCompra.setText(it.getString("FECHA"))
        }
        val builder: MaterialDatePicker.Builder<Long> = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("Selecciona la fecha de vencimiento")
        val picker: MaterialDatePicker<*> = builder.build()
        var calendar: Calendar
        var format: SimpleDateFormat
        var formattedDate = "2022-01-01"

        b.btnFecha.setOnClickListener {
            picker.show(activity?.supportFragmentManager!!, picker.toString())
        }
        picker.addOnPositiveButtonClickListener {
            b.txtFechaCompra.setText(picker.headerText)
            calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it as Long
            format = SimpleDateFormat("yyyy-MM-dd")
            calendar.add(Calendar.DATE, 1)
            formattedDate = format.format(calendar.getTime())
        }

        b.btnEditar.setOnClickListener {
            val nombreEmpleado = b.txtNombreEmpleado.text.toString()
            val area = b.txtArea.text.toString()
            val fecha = b.txtFechaCompra.text.toString()
            coleccion.document(id).update("NOMBREEMPLEADO", nombreEmpleado, "AREA", area, "FECHA", fecha)
                .addOnSuccessListener {
                    Toast.makeText(context, "Asignacion editada", Toast.LENGTH_SHORT).show()
                    activity?.onBackPressed()
                }
                .addOnFailureListener{
                    Toast.makeText(context, "Error al editar la asignacion", Toast.LENGTH_SHORT).show()
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
         * @return A new instance of fragment editarAsignacion.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            editarAsignacion().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}