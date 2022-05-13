package mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.tipoDato.Asignacion
import mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez.tipoDato.Inventario

abstract class BaseViewHolder2<T>(itemView: View):RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: Asignacion, position:Int)
}