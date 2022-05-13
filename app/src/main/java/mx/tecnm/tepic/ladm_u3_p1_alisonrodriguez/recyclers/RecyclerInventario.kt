package mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.recyclers

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.R
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.base.BaseViewHolder
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.databinding.InventarioRowBinding
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.tipoDato.Inventario
import java.lang.IllegalArgumentException

class RecyclerInventario (private val context: Context, val listaAsinacion:List<Inventario>, private val itemClickListener:onClickListener): RecyclerView.Adapter<BaseViewHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return InventarioViewHolder(LayoutInflater.from(context).inflate(R.layout.inventario_row,parent,false))
    }
    interface onClickListener{
        fun onClick(
            codigoBarras:String,
            tipoEquipo:String,
            caracteristicas:String,
            asignado:Boolean,
            id:String,
            itemView: View,
            position: Int
        ):Boolean
    }

    //Para cada informacion
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is RecyclerInventario.InventarioViewHolder -> holder.bind(listaAsinacion[position],position)
            else -> throw IllegalArgumentException("No se paso el viewholder")
        }
    }

    override fun getItemCount(): Int = listaAsinacion.size
    inner class InventarioViewHolder(itemView: View): BaseViewHolder<Inventario>(itemView){
        val b = InventarioRowBinding.bind(itemView)
        override fun bind(item: Inventario, position: Int) {
            itemView.setOnClickListener { itemClickListener.onClick( item.codigoBarras!!, item.tipoEquipo!!,item.caracteristicas!!,item.Asignado!!, item.id!!,itemView, position) }
            b.txtRowCodigo.text = item.codigoBarras
            b.txtRowfCompra.text = item.fechaCompra
            b.txtRowtequipo.text = item.tipoEquipo
            if(item.Asignado!!){
                b.txtRowAsignado.setTextColor(Color.GREEN)
                b.txtRowAsignado.text = "Asignado"
            }
            else{
                b.txtRowAsignado.setTextColor(Color.RED)
                b.txtRowAsignado.text = "No Asignado"
            }
        }
    }
}