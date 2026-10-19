package com.quispe.cafeCampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quispe.cafeCampus.data.local.dao.ProductoDao
import com.quispe.cafeCampus.data.local.entity.Producto

@Database(entities = [Producto::class], version = 1, exportSchema = false)
abstract class CafeDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    // TODO (Paso 3): registrar Usuario, Pedido, DetallePedido y sus DAOs

    companion object {
        @Volatile
        private var INSTANCE: CafeDatabase? = null

        fun obtener(context: Context): CafeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CafeDatabase::class.java,
                    "cafe_campus.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
