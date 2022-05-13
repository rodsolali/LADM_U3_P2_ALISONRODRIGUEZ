package mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
//import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.CRUD.INVENTARIO
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.databinding.FragmentEditarInventarioBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [editarInventario.newInstance] factory method to
 * create an instance of this fragment.
 */
class editarInventario : Fragment() {
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

    private lateinit var binding: FragmentEditarInventarioBinding
    private val b get() = binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditarInventarioBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val id = (arguments?.getString("id"))
        val baseRemota = FirebaseFirestore.getInstance()
        baseRemota.collection("inventario")
            .document(id!!)
            .get()//OBTIENE 1 DOCUMENTO
            .addOnSuccessListener {
                b.txtCaracteristicas.setText(it.getString("CARACTERISTICAS"))
                b.txtCodigobarra.setText(it.getString("CODIGOBARRAS"))
                b.txtTipoEquipo.setText(it.getString("TIPOEQUIPO"))
                b.txtFechaCompra.setText(it.getString("FECHACOMPRA"))
            }
            .addOnFailureListener {
                //ERROR
                AlertDialog.Builder(requireContext())
                    .setMessage(it.message)
                    .show()
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
            val baseRemota = FirebaseFirestore.getInstance()
            baseRemota.collection("inventario")
                .document(id)
                .update(
                    "CODIGOBARRAS", binding.txtCodigobarra.text.toString(),
                    "TIPOEQUIPO", binding.txtTipoEquipo.text.toString(),
                    "CARACTERISTICAS", binding.txtCaracteristicas.text.toString(),
                    "FECHACOMPRA", binding.txtFechaCompra.text.toString()
                )
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Se actualizó correctamente",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    activity?.onBackPressed()
                }
                .addOnFailureListener {
                    //ERROR
                    AlertDialog.Builder(requireContext())
                        .setMessage(it.message)
                        .show()

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
         * @return A new instance of fragment editarInventario.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            editarInventario().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}