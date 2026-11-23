
package com.tiendasbarrio.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tiendasbarrio.data.dao.EstablecimientoDao
import com.tiendasbarrio.data.dao.ProductoDao
import com.tiendasbarrio.data.entity.EstablecimientoEntity
import com.tiendasbarrio.data.entity.ProductoEntity
import com.tiendasbarrio.data.entity.ClienteEntity
import com.tiendasbarrio.data.dao.ClienteDao
import com.tiendasbarrio.data.entity.VentaEntity
import com.tiendasbarrio.data.entity.DetalleVentaEntity
import com.tiendasbarrio.data.entity.AbonoEntity
import com.tiendasbarrio.data.dao.VentaDao
import com.tiendasbarrio.data.dao.DetalleVentaDao
import com.tiendasbarrio.data.dao.AbonoDao
import androidx.room.withTransaction
import com.tiendasbarrio.data.dao.GastoDao
import com.tiendasbarrio.data.entity.GastoEntity

@Database(
    entities = [
        EstablecimientoEntity::class,
        ProductoEntity::class,
        ClienteEntity::class,
        VentaEntity::class,
        DetalleVentaEntity::class,
        AbonoEntity::class,
        GastoEntity::class
    ],
    version = 6,
    exportSchema = false
)

abstract class TiendasBarrioDatabase : RoomDatabase() {

    abstract fun establecimientoDao(): EstablecimientoDao

    abstract fun productoDao(): ProductoDao

    abstract fun clienteDao(): ClienteDao

    abstract fun ventaDao(): VentaDao

    abstract fun detalleVentaDao(): DetalleVentaDao

    abstract fun abonoDao(): AbonoDao

    abstract fun gastoDao(): GastoDao

    suspend fun registrarVentaCompleta(
        venta: VentaEntity,
        detalles: List<DetalleVentaEntity>,
        abonoInicial: AbonoEntity?
    ): Long = withTransaction {

        require(detalles.isNotEmpty()) {
            "Debes agregar al menos un producto"
        }

        require(venta.establecimientoId > 0) {
            "El establecimiento no es válido"
        }

        require(venta.total > 0) {
            "El total de la venta debe ser mayor que cero"
        }

        require(
            venta.tipoVenta == "CONTADO" ||
                    venta.tipoVenta == "CREDITO"
        ) {
            "El tipo de venta no es válido"
        }

        require(
            detalles.all {
                it.cantidad > 0 &&
                        it.productoId > 0 &&
                        it.precioUnitario > 0 &&
                        it.subtotal == it.cantidad.toLong() * it.precioUnitario
            }
        ) {
            "Hay productos con datos inválidos"
        }

        val totalCalculado = detalles.sumOf { it.subtotal }

        require(totalCalculado == venta.total) {
            "El total de la venta no coincide con sus productos"
        }

        val montoAbono = abonoInicial?.monto ?: 0L

        require(montoAbono >= 0) {
            "El abono inicial no puede ser negativo"
        }

        require(montoAbono <= venta.total) {
            "El abono inicial no puede superar el total"
        }

        if (venta.tipoVenta == "CONTADO") {
            require(montoAbono == venta.total) {
                "La venta de contado debe estar pagada completamente"
            }
        }

        if (venta.tipoVenta == "CREDITO") {
            require(venta.clienteId != null) {
                "Debes seleccionar un cliente para la venta a crédito"
            }
        }

        require(venta.totalAbonado == montoAbono) {
            "El abono inicial no coincide con el total abonado de la venta"
        }

        if (venta.clienteId != null) {
            val cliente = clienteDao().obtenerPorId(venta.clienteId)

            require(cliente != null) {
                "El cliente seleccionado no existe"
            }

            require(cliente.establecimientoId == venta.establecimientoId) {
                "El cliente no pertenece al establecimiento activo"
            }
        }

        val productoDao = productoDao()

        val productosAgrupados = detalles.groupBy { it.productoId }

        for ((productoId, detallesProducto) in productosAgrupados) {

            val producto = productoDao.obtenerPorId(productoId)

            require(producto != null) {
                "Uno de los productos no existe"
            }

            require(producto.establecimientoId == venta.establecimientoId) {
                "El producto no pertenece al establecimiento activo"
            }

            val cantidadSolicitada = detallesProducto.sumOf { it.cantidad }

            require(producto.existencia >= cantidadSolicitada) {
                "Existencias insuficientes para ${producto.nombre}"
            }

            require(detallesProducto.all {
                it.nombreProducto == producto.nombre
            }) {
                "El nombre del producto no coincide con el inventario"
            }
        }

        for ((productoId, detallesProducto) in productosAgrupados) {

            val cantidadTotal = detallesProducto.sumOf { it.cantidad }

            val filasActualizadas = productoDao.descontarExistencias(
                productoId = productoId,
                cantidad = cantidadTotal
            )

            require(filasActualizadas == 1) {
                "No se pudo actualizar el inventario de uno de los productos"
            }
        }

        val ventaIdGenerado = ventaDao().insertar(venta)
        val ventaId = ventaIdGenerado.toInt()

        val detallesConVenta = detalles.map { detalle ->
            detalle.copy(ventaId = ventaId)
        }

        detalleVentaDao().insertarTodos(detallesConVenta)

        if (abonoInicial != null) {
            val abonoConVenta = abonoInicial.copy(
                ventaId = ventaId
            )

            abonoDao().insertar(abonoConVenta)
        }

        ventaIdGenerado
    }

    suspend fun registrarAbonoCompleto(
        ventaId: Int,
        abono: AbonoEntity
    ) = withTransaction {

        require(ventaId > 0) {
            "La venta no es válida"
        }

        require(abono.monto > 0) {
            "El abono debe ser mayor que cero"
        }

        val venta = ventaDao().obtenerPorId(ventaId)

        require(venta != null) {
            "La venta no existe"
        }

        require(venta.tipoVenta == "CREDITO") {
            "Solo se pueden registrar abonos en ventas a crédito"
        }

        val saldoPendiente = venta.total - venta.totalAbonado

        require(saldoPendiente > 0) {
            "Esta venta ya está pagada"
        }

        require(abono.monto <= saldoPendiente) {
            "El abono supera el saldo pendiente"
        }

        require(abono.ventaId == ventaId) {
            "El abono no corresponde a esta venta"
        }

        abonoDao().insertar(abono)

        val ventaActualizada = venta.copy(
            totalAbonado = venta.totalAbonado + abono.monto
        )

        val filasActualizadas = ventaDao().actualizar(ventaActualizada)

        require(filasActualizadas == 1) {
            "No se pudo actualizar el saldo de la venta"
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: TiendasBarrioDatabase? = null

        // Migración de la versión 1 a la versión 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE establecimientos " +
                            "ADD COLUMN activo INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        // Migración de la versión 2 a la versión 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS productos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        establecimientoId INTEGER NOT NULL,
                        codigo TEXT NOT NULL,
                        nombre TEXT NOT NULL,
                        categoria TEXT NOT NULL,
                        descripcion TEXT NOT NULL,
                        precioCompra INTEGER NOT NULL,
                        precioVenta INTEGER NOT NULL,
                        existencia INTEGER NOT NULL,
                        stockMinimo INTEGER NOT NULL,
                        fechaRegistro INTEGER NOT NULL,
                        FOREIGN KEY(establecimientoId)
                        REFERENCES establecimientos(id)
                        ON UPDATE NO ACTION
                        ON DELETE RESTRICT
                    )
                """)

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS
                    index_productos_establecimientoId
                    ON productos (establecimientoId)
                """)

                db.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS
                    index_productos_establecimientoId_codigo
                    ON productos (establecimientoId, codigo)
                """)
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
            CREATE TABLE IF NOT EXISTS clientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                establecimientoId INTEGER NOT NULL,
                documento TEXT NOT NULL,
                nombre TEXT NOT NULL,
                telefono TEXT NOT NULL,
                direccion TEXT NOT NULL,
                correo TEXT NOT NULL,
                fechaRegistro INTEGER NOT NULL,
                activo INTEGER NOT NULL,
                FOREIGN KEY(establecimientoId)
                REFERENCES establecimientos(id)
                ON UPDATE NO ACTION
                ON DELETE RESTRICT
            )
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_clientes_establecimientoId
            ON clientes(establecimientoId)
        """)

                db.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS index_clientes_establecimientoId_documento
            ON clientes(establecimientoId, documento)
        """)
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `ventas` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `establecimientoId` INTEGER NOT NULL,
                `clienteId` INTEGER,
                `tipoVenta` TEXT NOT NULL,
                `total` INTEGER NOT NULL,
                `totalAbonado` INTEGER NOT NULL,
                `fechaVenta` INTEGER NOT NULL,
                `observacion` TEXT NOT NULL,
                FOREIGN KEY(`establecimientoId`)
                    REFERENCES `establecimientos`(`id`)
                    ON UPDATE NO ACTION ON DELETE RESTRICT,
                FOREIGN KEY(`clienteId`)
                    REFERENCES `clientes`(`id`)
                    ON UPDATE NO ACTION ON DELETE RESTRICT
            )
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS
            `index_ventas_establecimientoId`
            ON `ventas` (`establecimientoId`)
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS
            `index_ventas_clienteId`
            ON `ventas` (`clienteId`)
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS
            `index_ventas_fechaVenta`
            ON `ventas` (`fechaVenta`)
        """)

                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `detalles_venta` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `ventaId` INTEGER NOT NULL,
                `productoId` INTEGER NOT NULL,
                `nombreProducto` TEXT NOT NULL,
                `cantidad` INTEGER NOT NULL,
                `precioUnitario` INTEGER NOT NULL,
                `subtotal` INTEGER NOT NULL,
                FOREIGN KEY(`ventaId`)
                    REFERENCES `ventas`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`productoId`)
                    REFERENCES `productos`(`id`)
                    ON UPDATE NO ACTION ON DELETE RESTRICT
            )
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS
            `index_detalles_venta_ventaId`
            ON `detalles_venta` (`ventaId`)
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS
            `index_detalles_venta_productoId`
            ON `detalles_venta` (`productoId`)
        """)

                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `abonos` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `ventaId` INTEGER NOT NULL,
                `monto` INTEGER NOT NULL,
                `fechaAbono` INTEGER NOT NULL,
                `observacion` TEXT NOT NULL,
                FOREIGN KEY(`ventaId`)
                    REFERENCES `ventas`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS
            `index_abonos_ventaId`
            ON `abonos` (`ventaId`)
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS
            `index_abonos_fechaAbono`
            ON `abonos` (`fechaAbono`)
        """)
            }
        }
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS `gastos` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `establecimientoId` INTEGER NOT NULL,
                `categoria` TEXT NOT NULL,
                `descripcion` TEXT NOT NULL,
                `monto` INTEGER NOT NULL,
                `fechaGasto` INTEGER NOT NULL,
                FOREIGN KEY(`establecimientoId`)
                    REFERENCES `establecimientos`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS
            `index_gastos_establecimientoId`
            ON `gastos` (`establecimientoId`)
        """)
            }
        }


        fun getDatabase(context: Context): TiendasBarrioDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TiendasBarrioDatabase::class.java,
                    "tiendas_barrio.db"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6
                    )
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
