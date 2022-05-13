package mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.recyclers


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.R
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.base.BaseViewHolder2
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.databinding.AsignacionRowBinding
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.tipoDato.Asignacion


class RecyclerAsignacion (private val context: Context, val listaAsinacion:List<Asignacion>, private val itemClickListener:onClickListener): RecyclerView.Adapter<BaseViewHolder2<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder2<*> {
        return AsignacionViewHolder(LayoutInflater.from(context).inflate(R.layout.asignacion_row,parent,false))
    }
    interface onClickListener{
        fun onClick(
            codigoBarras:String,
            nombre:String,
            area:String,
            fecha:String,
            id:String,
            itemView: View,
            position: Int
        ):Boolean
    }

    //Para cada informacion
    override fun onBindViewHolder(holder: BaseViewHolder2<*>, position: Int) {
        when(holder){
            is RecyclerAsignacion.AsignacionViewHolder -> holder.bind(listaAsinacion[position],position)
            else -> throw IllegalArgumentException("No se paso el viewholder")
        }
    }

    override fun getItemCount(): Int = listaAsinacion.size
    inner class AsignacionViewHolder(itemView: View): BaseViewHolder2<Asignacion>(itemView){
        val b = AsignacionRowBinding.bind(itemView)
        override fun bind(item: Asignacion, position: Int) {
            itemView.setOnClickListener { itemClickListener.onClick( item.CodigoBarra!!,item.empleado!!,item.area!!,item.fecha!!,item.id!!,itemView, position) }
            b.txtId.text = item.id.toString()
            b.txtRowCodigo.text = item.CodigoBarra
            b.txtRowFecha.text = item.fecha
            b.txtRowNombre.text = item.empleado
            b.txtRowArea.text = item.area
        }
    }
}