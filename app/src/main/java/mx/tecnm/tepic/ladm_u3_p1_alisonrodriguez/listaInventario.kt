package mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
//import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.CRUD.ASIGNACION
///import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.CRUD.INVENTARIO
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.databinding.FragmentListaInventarioBinding
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.recyclers.RecyclerInventario
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.tipoDato.Inventario
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [listaInventario.newInstance] factory method to
 * create an instance of this fragment.
 */
class listaInventario : Fragment(),RecyclerInventario.onClickListener, SearchView.OnQueryTextListener {
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

    private lateinit var _binding: FragmentListaInventarioBinding
    private val b get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListaInventarioBinding.inflate(inflater, container, false)
        return b.root
    }


    lateinit var listaInventario: MutableList<Inventario>
    var listaInventarioOriginal: MutableList<Inventario>? = null
    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        setupRecyclerView()
        b.search.setOnQueryTextListener(this)
    }

    private fun setupRecyclerView() {
        b.listaInventario.layoutManager = LinearLayoutManager(activity?.applicationContext!!)
        b.listaInventario.addItemDecoration(
            DividerItemDecoration(
                activity?.applicationContext!!,
                DividerItemDecoration.VERTICAL
            )
        )
        //ListaInventario is empty
        listaInventario = mutableListOf()
        listaInventarioOriginal = mutableListOf()
        getInventario()
    }

    override fun onResume() {
        super.onResume()
        RecargarRecycler()
    }

    private fun getInventario(){
        val listaAux = mutableListOf<Inventario>()
        FirebaseFirestore.getInstance()
            .collection("inventario")
            .addSnapshotListener { query, error ->
                listaInventarioOriginal?.clear()
                listaInventario.clear()
                if (error != null) {
                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener
                }
                for (documento in query!!) {
                    var equipo = Inventario(
                        documento.getString("CODIGOBARRAS"),
                        documento.getString("TIPOEQUIPO"),
                        documento.getString("CARACTERISTICAS"),
                        documento.getString("FECHACOMPRA"),
                        documento.getBoolean("ASIGNADO"),
                        documento.id
                    )
                    listaAux.add(equipo)

                }
                try {
                    listaInventario = listaAux
                    listaInventarioOriginal?.addAll(listaAux)
                    b.listaInventario.adapter =
                        RecyclerInventario(activity?.applicationContext!!, listaInventario, this)
                }catch (e: Exception){
                }
            }


    }

    private fun RecargarRecycler(){
        b.listaInventario.adapter = RecyclerInventario(activity?.applicationContext!!,listaInventario,this)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment listaInventario.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            listaInventario().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(
        codigoBarras: String,
        tipoEquipo: String,
        caracteristicas: String,
        asignado:Boolean,
        id:String,
        itemView: View,
        position: Int
    ): Boolean {
        val menu = PopupMenu(context,itemView)
        val builder = AlertDialog.Builder(context)
        val info = AlertDialog.Builder(context)
        //val c = INVENTARIO(activity?.applicationContext!!)
        menu.menu.add("Borrar")
        menu.menu.add("Editar")
        menu.menu.add("Ver")
        if(asignado){
            menu.menu.add("Desasignar")
        }else{
            menu.menu.add("Asignar")
        }
        menu.setOnMenuItemClickListener {
            //asignar only appears when the item is not assigned
            when(it.title){
                "Borrar"->{
                    builder.setTitle("Borrar")
                    builder.setMessage("¿Desea borrar el equipo?")
                    builder.setPositiveButton("Sí"){_,_->
                        val baseRemota = FirebaseFirestore.getInstance()
                        baseRemota.collection("inventario")
                            .document(id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "EXITO, SE ELIMINÓ", Toast.LENGTH_LONG)
                                    .show()


                            }
                            .addOnFailureListener {
                                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                    .setMessage(it.message)
                                    .show()
                            }
                    }
                    builder.setNegativeButton("No"){_,_->
                    }
                    builder.show()
                }
                "Editar"->{
                    val bundle = bundleOf("id" to id)
                    view?.findNavController()
                        ?.navigate(R.id.action_listaInventario_to_editarInventario, bundle)
                }
                "Ver"->{
                    info.setTitle("Información")
                    info.setMessage("Tipo: $tipoEquipo\nCodigo: $codigoBarras \nCaracteristicas: $caracteristicas")
                    info.setPositiveButton("Ok"){_,_->
                    }
                    info.show()
                }
                "Asignar"->{
                    val bundle = bundleOf("codigobarras" to codigoBarras)
                    view?.findNavController()
                        ?.navigate(R.id.action_listaInventario_to_agregarAsignacion,bundle)
                }
                "Desasignar"->{
                    val baseRemota = FirebaseFirestore.getInstance()
                    baseRemota.collection("asignacion")
                        .whereEqualTo("CODIGOBARRAS",codigoBarras)
                        .get()
                        .addOnSuccessListener {
                            for(document in it){
                                baseRemota.collection("asignacion")
                                    .document(document.id)
                                    .delete()
                                    .addOnSuccessListener {
                                           Toast.makeText(requireContext(), "EXITO, SE DESASIGNÓ", Toast.LENGTH_LONG)
                                        .show()
                                        baseRemota.collection("inventario")
                                            .whereEqualTo("CODIGOBARRAS",codigoBarras)
                                            .get()
                                            .addOnSuccessListener {
                                                for(document in it){
                                                    baseRemota.collection("inventario")
                                                        .document(document.id)
                                                        .update("ASIGNADO",false)
                                                }
                                            }
                                    }
                            }
                        }
                    RecargarRecycler()
                }
            }
            true
        }
        menu.show()
        return true
    }

    fun filtrado(txtBuscar: String) {
        val longitud = txtBuscar.length
        Log.d("longitud", listaInventarioOriginal.toString())
        if(longitud == 0){
            listaInventario.clear()
            listaInventario.addAll(listaInventarioOriginal!!)
        }else{
            Log.d("longitud2", listaInventarioOriginal.toString())
            listaInventario.clear()
            listaInventario.addAll(listaInventarioOriginal!!)
            val collecion = listaInventario.filter {
                it.codigoBarras!!.lowercase().contains(txtBuscar.lowercase()).or(
                    it.tipoEquipo!!.lowercase().contains(txtBuscar.lowercase()).or(
                        it.fechaCompra!!.lowercase().contains(txtBuscar.lowercase())
                    )
                )
            }
            listaInventario.clear()
            listaInventario.addAll(collecion)
        }
        RecargarRecycler()
    }


    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {

        if (p0 != null) {
            filtrado(p0)
        }
        return false
    }
}