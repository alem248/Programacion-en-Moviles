package com.quispe.cafeCampus.data.local

import com.quispe.cafeCampus.data.local.entity.*
import com.quispe.cafeCampus.data.local.dao.*

object DatosIniciales {

    suspend fun sembrar(database: CafeDatabase) {
        val usuarioDao = database.usuarioDao()

        if (usuarioDao.contar() == 0) {

            usuarioDao.insert(Usuario(id = 1, nombre = "Admin", correo = "admin@cafe.com", clave = "clave", rol = "ADMIN"))
            usuarioDao.insert(Usuario(id = 2, nombre = "Cliente", correo = "cliente@cafe.com", clave = "clave", rol = "CLIENTE"))


            val productoDao = database.productoDao()
            productoDao.insert(Producto(id = 1, nombre = "Café americano", descripcion = "Café puro", precio = 4.00, disponible = true, icono = "cafe"))
            productoDao.insert(Producto(id = 2, nombre = "Sándwich de pollo", descripcion = "Con papas", precio = 9.00, disponible = true, icono = "plato"))
            productoDao.insert(Producto(id = 3, nombre = "Menú del día", descripcion = "Sopa + Fondo", precio = 12.00, disponible = true, icono = "plato"))
            productoDao.insert(Producto(id = 4, nombre = "Jugo de naranja", descripcion = "Natural", precio = 5.00, disponible = true, icono = "jugo"))
            productoDao.insert(Producto(id = 5, nombre = "Empanada", descripcion = "Carne/Pollo", precio = 4.50, disponible = true, icono = "pan"))
            productoDao.insert(Producto(id = 6, nombre = "Chicha morada", descripcion = "Copa 500ml", precio = 3.50, disponible = true, icono = "botella"))
        }
    }
}