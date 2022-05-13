package mx.tecnm.tepic.ladm_u3_p1_alisonrodriguez

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class baseDatos (
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(p0: SQLiteDatabase) {
        p0.execSQL("CREATE TABLE INVENTARIO(CODIGOBARRAS VARCHAR(50) PRIMARY KEY , TIPOEQUIPO VARCHAR(200),CARACTERISTICAS VARCHAR(500),FECHACOMPRA DATE)")
        p0.execSQL("CREATE TABLE ASIGNACION(IDASIGNACION INTEGER PRIMARY KEY AUTOINCREMENT, NOM_EMPLEADO VARCHAR(200),AREA_TRABAJO VARCHAR(200),FECHA DATE,CODIGOBARRAS VARCHAR(50), FOREIGN KEY(CODIGOBARRAS) REFERENCES INVENTARIO(CODIGOBARRAS))")
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}