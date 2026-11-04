package com.quispe.cafeCampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.quispe.cafeCampus.data.local.dao.*
import com.quispe.cafeCampus.data.local.entity.*
@Database(
    entities = [
        Usuario::class,
        Producto::class,
        Pedido::class,
        DetallePedido::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CafeDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun pedidoDao(): PedidoDao

    companion object {
        @Volatile
        private var INSTANCE: CafeDatabase? = null

        fun getDatabase(context: Context): CafeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CafeDatabase::class.java,
                    "cafe_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}