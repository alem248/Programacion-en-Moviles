package com.quispe.cafeCampus.data.local.dao

import androidx.room.*
import com.quispe.cafeCampus.data.local.entity.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    fun getProductoById(id: Int): Flow<Producto?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: Producto)

    @Update
    suspend fun updateProducto(producto: Producto)

    @Delete
    suspend fun deleteProducto(producto: Producto)

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :query || '%'")
    fun buscarProductos(query: String): Flow<List<Producto>>
}