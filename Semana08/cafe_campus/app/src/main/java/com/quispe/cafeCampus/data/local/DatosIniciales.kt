package com.quispe.cafeCampus.data.local

import com.quispe.cafeCampus.data.local.entity.Producto
import com.quispe.cafeCampus.data.local.entity.Usuario
import com.quispe.cafeCampus.model.Rol

object DatosIniciales {

    suspend fun sembrar(db: CafeDatabase) {
        val productoDao = db.productoDao()
        val usuarioDao = db.usuarioDao()

        if (usuarioDao.contar() == 0) {
            productoDao.insertar(
                Producto(
                    nombre = "Café americano",
                    descripcion = "Espresso doble con agua caliente.",
                    categoria = "Bebidas",
                    precio = 4.00,
                    icono = "cafe"
                )
            )
            productoDao.insertar(
                Producto(
                    nombre = "Sándwich de pollo",
                    descripcion = "Pan tostado con pollo, lechuga y tomate.",
                    categoria = "Almuerzos",
                    precio = 9.00,
                    icono = "plato"
                )
            )
            productoDao.insertar(
                Producto(
                    nombre = "Menú del día",
                    descripcion = "Entrada + segundo + refresco.",
                    categoria = "Almuerzos",
                    precio = 12.00,
                    icono = "menu_dia"
                )
            )
            productoDao.insertar(
                Producto(
                    nombre = "Jugo de naranja",
                    descripcion = "Jugo natural exprimido al momento.",
                    categoria = "Bebidas",
                    precio = 5.00,
                    icono = "jugo"
                )
            )
            productoDao.insertar(
                Producto(
                    nombre = "Empanada",
                    descripcion = "Empanada horneada rellena de carne.",
                    categoria = "Snacks",
                    precio = 4.50,
                    icono = "pan"
                )
            )
            productoDao.insertar(
                Producto(
                    nombre = "Chicha morada",
                    descripcion = "Bebida tradicional de maíz morado.",
                    categoria = "Bebidas",
                    precio = 3.50,
                    icono = "botella"
                )
            )

            usuarioDao.insertar(
                Usuario(
                    nombre = "Alexandra Pérez",
                    correo = "alexandra@campus.test",
                    clave = "1234",
                    rol = Rol.CLIENTE.name
                )
            )
            usuarioDao.insertar(
                Usuario(
                    nombre = "Ana Torres",
                    correo = "admin@cafe.test",
                    clave = "admin",
                    rol = Rol.ADMIN.name
                )
            )
        }
    }
}
