package mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
//import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.CRUD.ASIGNACION
//import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.CRUD.INVENTARIO
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.databinding.FragmentListaAsignacionBinding
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.databinding.FragmentListaInventarioBinding
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.recyclers.RecyclerAsignacion
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.recyclers.RecyclerInventario
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.tipoDato.Asignacion
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.tipoDato.Inventario

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [listaAsignacion.newInstance] factory method to
 * create an instance of this fragment.
 */
class listaAsignacion : Fragment(), RecyclerAsignacion.onClickListener, SearchView.OnQueryTextListener {
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

    private lateinit var _binding: FragmentListaAsignacionBinding
    private val b get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListaAsignacionBinding.inflate(inflater, container, false)
        return b.root
    }
    lateinit var listaAsignacion:MutableList<Asignacion>
    var listaAsignacionOriginal:MutableList<Asignacion>? = null
    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        setupRecyclerView()
        b.search.setOnQueryTextListener(this)
    }
    private fun setupRecyclerView(){
        b.listaAsignacion.layoutManager = LinearLayoutManager(activity?.applicationContext!!)
        b.listaAsignacion.addItemDecoration(
            DividerItemDecoration(activity?.applicationContext!!,
                DividerItemDecoration.VERTICAL)
        )
        listaAsignacion = mutableListOf()
        listaAsignacionOriginal = mutableListOf()

        getAsignacion()
    }
    override fun onResume() {
        super.onResume()
        RecargarRecycler()
    }

    private fun getAsignacion(){
        val listaAux = mutableListOf<Asignacion>()
        FirebaseFirestore.getInstance()
            .collection("asignacion")
            .addSnapshotListener { query, error ->
                listaAsignacion?.clear()
                listaAsignacionOriginal?.clear()
                if (error != null) {
                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener
                }
                for (documento in query!!) {
                    var asignacion = Asignacion(
                        documento.getString("NOMBREEMPLEADO"),
                        documento.getString("AREA"),
                        documento.getString("FECHA"),
                        documento.getString("CODIGOBARRAS"),
                        documento.id
                    )
                    listaAux.add(asignacion)
                    Log.d("asignacion", asignacion.toString())
                }
                try {
                    listaAsignacion = listaAux
                    listaAsignacionOriginal?.addAll(listaAux)
                    b.listaAsignacion.adapter =
                        RecyclerAsignacion(activity?.applicationContext!!, listaAsignacion, this)
                }catch (e: Exception){
                }
            }


    }

    private fun RecargarRecycler(){
        b.listaAsignacion.adapter = RecyclerAsignacion(activity?.applicationContext!!,listaAsignacion,this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment listaAsignacion.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            listaAsignacion().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(
        codigoBarras: String,
        nombre: String,
        area: String,
        fecha: String,
        id: String,
        itemView: View,
        position: Int
    ): Boolean {
        val menu = PopupMenu(context,itemView)
        val builder = AlertDialog.Builder(context)
        val info = AlertDialog.Builder(context)
        menu.menu.add("Editar")
        menu.menu.add("Ver")
        menu.menu.add("Desasignar")
        menu.setOnMenuItemClickListener {
            when(it.title){
                "Desasignar" -> {
                    builder.setTitle("Borrar")
                    builder.setMessage("¿Esta seguro de borrar la asignacion?")
                    builder.setPositiveButton("Si"){_,_ ->
                        FirebaseFirestore.getInstance()
                            .collection("asignacion")
                            .document(id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(context,"Asignacion borrada",Toast.LENGTH_SHORT).show()
                                FirebaseFirestore.getInstance().collection("inventario")
                                    .whereEqualTo("CODIGOBARRAS",codigoBarras)
                                    .get()
                                    .addOnSuccessListener {
                                        for(document in it){
                                            FirebaseFirestore.getInstance().collection("inventario")
                                                .document(document.id)
                                                .update("ASIGNADO",false)
                                        }
                                    }
                            }
                    }
                    builder.setNegativeButton("No"){_,_ ->
                    }
                    builder.show()
                }
                "Editar" -> {
                    val bundle = bundleOf("id" to id)
                    view?.findNavController()
                        ?.navigate(R.id.action_listaAsignacion_to_editarAsignacion, bundle)
                }
                "Ver" -> {
                    info.setTitle("Informacion")
                    info.setMessage("Codigo de barras: ${codigoBarras}\n" +
                           "Nombre: ${nombre}\n" +
                          "Area: ${area}\n" +
                           "Fecha de asignación: ${fecha}\n")
                    info.show()
                }
            }
            true
        }
        menu.show()
        return true
    }

    fun filtrado(txtBuscar: String) {
        val longitud = txtBuscar.length
        if(longitud == 0){
            listaAsignacion.clear()
            listaAsignacion.addAll(listaAsignacionOriginal!!)
        }else{
            listaAsignacion.clear()
            listaAsignacion.addAll(listaAsignacionOriginal!!)
            val collecion = listaAsignacion.filter {
                it.CodigoBarra!!.lowercase().contains(txtBuscar.lowercase()).or(
                    it.area!!.lowercase().contains(txtBuscar.lowercase())
                ).or(it.empleado!!.lowercase().contains(txtBuscar.lowercase())).or(
                    it.fecha!!.lowercase().contains(txtBuscar.lowercase())
                )
            }
            listaAsignacion.clear()
            listaAsignacion.addAll(collecion)
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