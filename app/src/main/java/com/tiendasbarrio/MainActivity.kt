
package com.tiendasbarrio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiendasbarrio.data.local.TiendasBarrioDatabase
import com.tiendasbarrio.data.repository.EstablecimientoRepository
import com.tiendasbarrio.data.repository.ProductoRepository
import com.tiendasbarrio.viewmodel.EstablecimientoViewModel
import com.tiendasbarrio.viewmodel.ProductoViewModel
import com.tiendasbarrio.viewmodel.ProductoViewModelFactory
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.tiendasbarrio.data.entity.ProductoEntity
import androidx.compose.foundation.background
import com.tiendasbarrio.data.repository.ClienteRepository
import com.tiendasbarrio.viewmodel.ClienteViewModel
import com.tiendasbarrio.viewmodel.ClienteViewModelFactory
import com.tiendasbarrio.data.entity.ClienteEntity

import com.tiendasbarrio.data.repository.VentaRepository
import com.tiendasbarrio.data.repository.DetalleVentaRepository
import com.tiendasbarrio.data.repository.AbonoRepository

import com.tiendasbarrio.viewmodel.VentaViewModel
import com.tiendasbarrio.viewmodel.VentaViewModelFactory
import com.tiendasbarrio.viewmodel.DetalleVentaViewModel
import com.tiendasbarrio.viewmodel.DetalleVentaViewModelFactory
import com.tiendasbarrio.viewmodel.AbonoViewModel
import com.tiendasbarrio.viewmodel.AbonoViewModelFactory

import com.tiendasbarrio.data.repository.VentaCompletaRepository
import com.tiendasbarrio.viewmodel.VentaCompletaViewModel
import com.tiendasbarrio.viewmodel.VentaCompletaViewModelFactory
import com.tiendasbarrio.ui.PantallaVentas
import com.tiendasbarrio.ui.PantallaRegistrarVenta
import com.tiendasbarrio.ui.PantallaDetalleVenta

import com.tiendasbarrio.viewmodel.ReporteViewModel
import com.tiendasbarrio.viewmodel.ReporteViewModelFactory
import com.tiendasbarrio.ui.PantallaReportes

import com.tiendasbarrio.data.repository.GastoRepository
import com.tiendasbarrio.viewmodel.GastoViewModel
import com.tiendasbarrio.viewmodel.GastoViewModelFactory

import com.tiendasbarrio.ui.PantallaGastos
import com.tiendasbarrio.ui.PantallaRegistrarGasto
import com.tiendasbarrio.ui.PantallaReporteGastos

private val Azul = Color(0xFF173B67)
private val Fondo = Color(0xFFF4F6F9)


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos
        val database = TiendasBarrioDatabase.getDatabase(
            applicationContext)

        // Repositorio de establecimientos
        val repository = EstablecimientoRepository(
            database.establecimientoDao())

        // Repositorio de productos
        val productoRepository = ProductoRepository(
            database.productoDao())

        val clienteRepository = ClienteRepository(
            database.clienteDao())
        val ventaRepository = VentaRepository(database.ventaDao())

        val detalleVentaRepository =
            DetalleVentaRepository(database.detalleVentaDao())

        val abonoRepository = AbonoRepository(database.abonoDao())
        // Repositorio de gastos
        val gastoRepository = GastoRepository(database.gastoDao())
        val gastoFactory = GastoViewModelFactory(gastoRepository)

        val ventaCompletaRepository = VentaCompletaRepository(database)

        // Factory de establecimientos
        val factory = EstablecimientoViewModelFactory(repository)

        // Factory de productos
        val productoFactory = ProductoViewModelFactory(
            productoRepository)

        val clienteFactory = ClienteViewModelFactory(clienteRepository)

        val ventaFactory = VentaViewModelFactory(ventaRepository)

        val reporteFactory = ReporteViewModelFactory(
            ventaRepository,
            abonoRepository
        )

        val detalleVentaFactory =
            DetalleVentaViewModelFactory(detalleVentaRepository)

        val abonoFactory = AbonoViewModelFactory(abonoRepository)

        val ventaCompletaFactory =
            VentaCompletaViewModelFactory(ventaCompletaRepository)

        // ViewModel de establecimientos
        val viewModel = ViewModelProvider(
            this,
            factory
        )[EstablecimientoViewModel::class.java]

        // ViewModel de productos
        val productoViewModel = ViewModelProvider(
            this,
            productoFactory)[ProductoViewModel::class.java]

        val clienteViewModel = ViewModelProvider(
            this,
            clienteFactory)[ClienteViewModel::class.java]

        val ventaViewModel = ViewModelProvider(
            this,
            ventaFactory
        )[VentaViewModel::class.java]

        val reporteViewModel = ViewModelProvider(
            this,
            reporteFactory
        )[ReporteViewModel::class.java]

        val detalleVentaViewModel = ViewModelProvider(
            this,
            detalleVentaFactory
        )[DetalleVentaViewModel::class.java]

        val abonoViewModel = ViewModelProvider(
            this,
            abonoFactory
        )[AbonoViewModel::class.java]
        val gastoViewModel = ViewModelProvider(
            this,
            gastoFactory
        )[GastoViewModel::class.java]

        val ventaCompletaViewModel = ViewModelProvider(
            this,
            ventaCompletaFactory
        )[VentaCompletaViewModel::class.java]


        lifecycleScope.launch {
            val establecimientoActivo = viewModel.obtenerEstablecimientoActivo()

            if (establecimientoActivo != null) {
                productoViewModel.cargarInventario(establecimientoActivo.id)
                clienteViewModel.cargarClientes(establecimientoActivo.id)
                ventaViewModel.cargarVentas(establecimientoActivo.id)
            }
        }

        setContent {
            MaterialTheme {
                TiendasBarrioApp(
                    viewModel = viewModel,
                    productoViewModel = productoViewModel,
                    clienteViewModel = clienteViewModel,
                    ventaViewModel = ventaViewModel,
                    detalleVentaViewModel = detalleVentaViewModel,
                    abonoViewModel = abonoViewModel,
                    ventaCompletaViewModel = ventaCompletaViewModel,
                    gastoViewModel = gastoViewModel,
                    reporteViewModel = reporteViewModel
                )
            }
        }
    }
}

// Factory para construir el ViewModel con el repositorio
class EstablecimientoViewModelFactory(
    private val repository: EstablecimientoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(
                EstablecimientoViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return EstablecimientoViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel no reconocido"
        )
    }
}


@Composable
fun TiendasBarrioApp(
    viewModel: EstablecimientoViewModel,
    productoViewModel: ProductoViewModel,
    clienteViewModel: ClienteViewModel,
    ventaViewModel: VentaViewModel,
    detalleVentaViewModel: DetalleVentaViewModel,
    abonoViewModel: AbonoViewModel,
    ventaCompletaViewModel: VentaCompletaViewModel,
    reporteViewModel: ReporteViewModel,
    gastoViewModel: GastoViewModel
) {
    var pantalla by remember {
        mutableStateOf("inicio")
    }
    val establecimientos by viewModel.establecimientos.collectAsState()

    var ventaSeleccionadaId by remember {
        mutableIntStateOf(0)
    }

    var productoSeleccionado by remember {
        mutableStateOf<ProductoEntity?>(null)
    }

    var clienteSeleccionado by remember {
        mutableStateOf<ClienteEntity?>(null)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Fondo
    ) {
        when (pantalla) {

            // ==========================================
            // PANTALLA PRINCIPAL
            // ==========================================


            "inicio" -> PantallaInicio(
                abrirEstablecimiento = {
                    pantalla = "establecimiento"
                },
                abrirProductos = {
                    pantalla = "productos"
                },
                abrirClientes = {
                    clienteViewModel.restaurarClientes()
                    pantalla = "clientes"
                },
                abrirVentas = {
                    val establecimientoActivoId = establecimientos
                        .firstOrNull { it.activo }
                        ?.id

                    if (establecimientoActivoId != null) {
                        productoViewModel.cargarInventario(establecimientoActivoId)
                        clienteViewModel.cargarClientes(establecimientoActivoId)
                        ventaViewModel.cargarVentas(establecimientoActivoId)
                        pantalla = "ventas"
                    } else {
                        pantalla = "establecimiento"
                    }
                },
                abrirReportes = {
                    val establecimientoActivoId = establecimientos
                        .firstOrNull { it.activo }
                        ?.id

                    if (establecimientoActivoId != null) {
                        reporteViewModel.cargarReporte(establecimientoActivoId)
                        pantalla = "reportes"
                    } else {
                        pantalla = "establecimiento"
                    }
                },
                abrirGastos = {
                    val establecimientoActivoId = establecimientos
                        .firstOrNull { it.activo }
                        ?.id

                    if (establecimientoActivoId != null) {
                        gastoViewModel.cargarGastos(establecimientoActivoId)
                        gastoViewModel.cargarTotalGastos(establecimientoActivoId)
                        pantalla = "gastos"
                    } else {
                        pantalla = "establecimiento"
                    }
                }
            )

            // ==========================================
            // MÓDULO DE VENTAS
            // ==========================================

            "ventas" -> {
                PantallaVentas(
                    regresar = {
                        pantalla = "inicio"
                    },
                    abrirRegistroVenta = {
                        val establecimientoId = viewModel.establecimientos
                            .value
                            .firstOrNull { it.activo }
                            ?.id

                        if (establecimientoId != null) {
                            productoViewModel.cargarInventario(establecimientoId)
                            clienteViewModel.cargarClientes(establecimientoId)
                            pantalla = "registrar_venta"
                        }
                    },
                    ventaViewModel = ventaViewModel,
                    alSeleccionarVenta = { ventaId ->
                        ventaSeleccionadaId = ventaId

                        detalleVentaViewModel.cargarDetalles(ventaId)
                        abonoViewModel.cargarAbonos(ventaId)

                        pantalla = "detalle_venta"
                    }
                )
            }
            // ==========================================
            // MÓDULO DE REPORTES
            // ==========================================

            "reportes" -> {
                val establecimientoId = establecimientos
                    .firstOrNull { it.activo }
                    ?.id

                if (establecimientoId != null) {
                    PantallaReportes(
                        establecimientoId = establecimientoId,
                        reporteViewModel = reporteViewModel,
                        gastoViewModel = gastoViewModel,
                        regresar = {
                            pantalla = "inicio"
                        },
                        abrirReporteGastos = {
                            pantalla = "reporte_gastos"
                        }
                    )
                } else {
                    pantalla = "establecimiento"
                }
            }
            // ==========================================
            // MÓDULO DE GASTOS Y EGRESOS
            // ==========================================

            "gastos" -> {
                val establecimientoId = establecimientos
                    .firstOrNull { it.activo }
                    ?.id

                if (establecimientoId != null) {
                    PantallaGastos(
                        establecimientoId = establecimientoId,
                        gastoViewModel = gastoViewModel,
                        regresar = {
                            pantalla = "inicio"
                        },
                        abrirRegistroGasto = {
                            pantalla = "registrar_gasto"
                        }
                    )
                } else {
                    pantalla = "establecimiento"
                }
            }
            // ==========================================
            // REGISTRAR GASTO
            // ==========================================

            "registrar_gasto" -> {
                val establecimientoId = establecimientos
                    .firstOrNull { it.activo }
                    ?.id

                if (establecimientoId != null) {
                    PantallaRegistrarGasto(
                        establecimientoId = establecimientoId,
                        gastoViewModel = gastoViewModel,
                        regresar = {
                            gastoViewModel.limpiarMensaje()
                            pantalla = "gastos"
                        }
                    )
                } else {
                    pantalla = "establecimiento"
                }
            }
            // ==========================================
            // REPORTE DE GASTOS POR CATEGORÍA
            // ==========================================

            "reporte_gastos" -> {
                val establecimientoId = establecimientos
                    .firstOrNull { it.activo }
                    ?.id

                if (establecimientoId != null) {
                    PantallaReporteGastos(
                        establecimientoId = establecimientoId,
                        gastoViewModel = gastoViewModel,
                        regresar = {
                            pantalla = "reportes"
                        }
                    )
                } else {
                    pantalla = "establecimiento"
                }
            }

            // ==========================================
            // REGISTRAR VENTA
            // ==========================================

            "registrar_venta" -> {
                val establecimientoId = establecimientos
                    .firstOrNull { it.activo }
                    ?.id

                if (establecimientoId != null) {
                    PantallaRegistrarVenta(
                        establecimientoId = establecimientoId,
                        productoViewModel = productoViewModel,
                        clienteViewModel = clienteViewModel,
                        ventaCompletaViewModel = ventaCompletaViewModel,
                        regresar = {
                            ventaCompletaViewModel.limpiarMensaje()
                            ventaCompletaViewModel.limpiarResultado()
                            pantalla = "ventas"
                        }
                    )
                } else {
                    pantalla = "inicio"
                }
            }

            // ==========================================
            // DETALLE DE VENTA
            // ==========================================

            "detalle_venta" -> {
                PantallaDetalleVenta(
                    ventaId = ventaSeleccionadaId,
                    ventaViewModel = ventaViewModel,
                    detalleVentaViewModel = detalleVentaViewModel,
                    abonoViewModel = abonoViewModel,
                    ventaCompletaViewModel = ventaCompletaViewModel,
                    regresar = {
                        pantalla = "ventas"
                    }
                )
            }

            // ==========================================
            // MÓDULO DE ESTABLECIMIENTOS
            // ==========================================

            "establecimiento" -> PantallaEstablecimiento(
                viewModel = viewModel,
                regresar = {
                    pantalla = "inicio"
                }
            )

            // ==========================================
            // MÓDULO DE CLIENTES
            // ==========================================

            "clientes" -> PantallaClientes(
                clienteViewModel = clienteViewModel,
                regresar = {
                    pantalla = "inicio"
                },
                abrirRegistro = {
                    pantalla = "registrar_cliente"
                },
                abrirEdicion = { cliente ->
                    clienteSeleccionado = cliente
                    pantalla = "editar_cliente"
                }
            )

            // ==========================================
            // REGISTRAR CLIENTE
            // ==========================================

            "registrar_cliente" -> PantallaRegistrarCliente(
                regresar = {
                    pantalla = "clientes"
                },
                mensaje = clienteViewModel.mensaje.collectAsState().value,
                limpiarMensaje = {
                    clienteViewModel.limpiarMensaje()
                },
                registrar = {
                        documento, nombre, telefono, direccion, correo ->

                    val establecimientoActivoId =
                        viewModel.establecimientos.value
                            .firstOrNull { it.activo }?.id

                    if (establecimientoActivoId == null) {

                        clienteViewModel.mostrarMensaje(
                            "No hay un establecimiento activo."
                        )

                    } else {

                        val cliente = ClienteEntity(
                            establecimientoId = establecimientoActivoId,
                            documento = documento,
                            nombre = nombre,
                            telefono = telefono,
                            direccion = direccion,
                            correo = correo
                        )

                        clienteViewModel.registrarCliente(cliente)
                    }
                }
            )

            // ==========================================
            // EDITAR CLIENTE
            // ==========================================

            "editar_cliente" -> {
                clienteSeleccionado?.let { cliente ->

                    PantallaEditarCliente(
                        cliente = cliente,
                        regresar = {
                            pantalla = "clientes"
                        },
                        mensaje = clienteViewModel.mensaje.collectAsState().value,
                        limpiarMensaje = {
                            clienteViewModel.limpiarMensaje()
                        },
                        actualizar = {
                                documento, nombre, telefono, direccion, correo ->

                            val clienteActualizado = cliente.copy(
                                documento = documento,
                                nombre = nombre,
                                telefono = telefono,
                                direccion = direccion,
                                correo = correo
                            )

                            clienteViewModel.actualizarCliente(
                                clienteActualizado
                            )
                        }
                    )
                }
            }

            // ==========================================
            // MÓDULO DE PRODUCTOS E INVENTARIO
            // ==========================================

            "productos" -> PantallaProductosInventario(
                productoViewModel = productoViewModel,
                regresar = {
                    pantalla = "inicio"
                },
                abrirRegistro = {
                    pantalla = "registrar_producto"
                },
                abrirEdicion = { producto ->
                    productoSeleccionado = producto
                    pantalla = "editar_producto"
                }
            )

            // ==========================================
            // REGISTRAR PRODUCTO
            // ==========================================

            "registrar_producto" -> PantallaRegistrarProducto(
                regresar = {
                    pantalla = "productos"
                },
                mensaje = productoViewModel.mensaje.collectAsState().value,
                limpiarMensaje = {
                    productoViewModel.limpiarMensaje()
                },
                mostrarMensaje = { texto ->
                    productoViewModel.mostrarMensaje(texto)
                },
                registrar = {
                        codigo, nombre, categoria, descripcion,
                        precioCompra, precioVenta, existencia, stockMinimo ->

                    val establecimientoActivoId =
                        viewModel.establecimientos.value
                            .firstOrNull { it.activo }?.id

                    if (establecimientoActivoId == null) {

                        productoViewModel.mostrarMensaje(
                            "No hay un establecimiento activo. Selecciona uno antes de registrar productos."
                        )

                    } else {

                        val compra = precioCompra.toLongOrNull()
                        val venta = precioVenta.toLongOrNull()
                        val cantidad = existencia.toIntOrNull()
                        val minimo = stockMinimo.toIntOrNull()

                        if (
                            codigo.isBlank() ||
                            nombre.isBlank() ||
                            categoria.isBlank()
                        ) {

                            productoViewModel.mostrarMensaje(
                                "El código, nombre y categoría son obligatorios."
                            )

                        } else if (
                            compra == null ||
                            venta == null ||
                            cantidad == null ||
                            minimo == null
                        ) {

                            productoViewModel.mostrarMensaje(
                                "Verifica los valores numéricos ingresados."
                            )

                        } else if (
                            compra < 0 ||
                            venta < 0 ||
                            cantidad < 0 ||
                            minimo < 0
                        ) {

                            productoViewModel.mostrarMensaje(
                                "Los valores numéricos no pueden ser negativos."
                            )

                        } else {

                            val producto = ProductoEntity(
                                establecimientoId = establecimientoActivoId,
                                codigo = codigo,
                                nombre = nombre,
                                categoria = categoria,
                                descripcion = descripcion,
                                precioCompra = compra,
                                precioVenta = venta,
                                existencia = cantidad,
                                stockMinimo = minimo
                            )

                            productoViewModel.registrarProducto(producto)
                        }
                    }
                }
            )

            // ==========================================
            // EDITAR PRODUCTO
            // ==========================================

            "editar_producto" -> {
                productoSeleccionado?.let { producto ->

                    PantallaEditarProducto(
                        producto = producto,
                        regresar = {
                            pantalla = "productos"
                        },
                        mensaje = productoViewModel.mensaje.collectAsState().value,
                        limpiarMensaje = {
                            productoViewModel.limpiarMensaje()
                        },
                        actualizar = {
                                codigo, nombre, categoria, descripcion,
                                precioCompra, precioVenta, existencia, stockMinimo ->

                            val compra = precioCompra.toLongOrNull()
                            val venta = precioVenta.toLongOrNull()
                            val cantidad = existencia.toIntOrNull()
                            val minimo = stockMinimo.toIntOrNull()

                            if (
                                codigo.isBlank() ||
                                nombre.isBlank() ||
                                categoria.isBlank()
                            ) {

                                productoViewModel.mostrarMensaje(
                                    "El código, nombre y categoría son obligatorios."
                                )

                            } else if (
                                compra == null ||
                                venta == null ||
                                cantidad == null ||
                                minimo == null
                            ) {

                                productoViewModel.mostrarMensaje(
                                    "Verifica los valores numéricos ingresados."
                                )

                            } else if (
                                compra < 0 ||
                                venta < 0 ||
                                cantidad < 0 ||
                                minimo < 0
                            ) {

                                productoViewModel.mostrarMensaje(
                                    "Los valores numéricos no pueden ser negativos."
                                )

                            } else {

                                val productoActualizado = producto.copy(
                                    codigo = codigo,
                                    nombre = nombre,
                                    categoria = categoria,
                                    descripcion = descripcion,
                                    precioCompra = compra,
                                    precioVenta = venta,
                                    existencia = cantidad,
                                    stockMinimo = minimo
                                )

                                productoViewModel.actualizarProducto(
                                    productoActualizado
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaInicio(
    abrirEstablecimiento: () -> Unit,
    abrirProductos: () -> Unit,
    abrirClientes: () -> Unit,
    abrirVentas: () -> Unit,
    abrirReportes: () -> Unit,
    abrirGastos: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "TIENDASBARRIO",
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            color = Azul
        )

        Text(
            text = "Gestión inteligente para tu tienda",
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(25.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Azul
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp)
            ) {
                Text(
                    text = "¡Bienvenido!",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Administra los productos, las ventas " +
                            "y los clientes de tu establecimiento.",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "MENÚ PRINCIPAL",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Azul
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = abrirEstablecimiento,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("Mi establecimiento")
        }

        listOf(
            "Productos e inventario",
            "Ventas y comprobantes",
            "Clientes y créditos",
            "Reportes",
            "Gastos y egresos"
        ).forEach { modulo ->

            OutlinedButton(
                onClick = {
                    when (modulo) {
                        "Productos e inventario" -> abrirProductos()
                        "Ventas y comprobantes" -> abrirVentas()
                        "Clientes y créditos" -> abrirClientes()
                        "Reportes" -> abrirReportes()
                        "Gastos y egresos" -> abrirGastos()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(modulo)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "TiendasBarrio | Versión inicial 1.0",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun PantallaProductosInventario(
    productoViewModel: ProductoViewModel,
    regresar: () -> Unit,
    abrirRegistro: () -> Unit,
    abrirEdicion: (ProductoEntity) -> Unit
) {
    var busqueda by remember {
        mutableStateOf("")
    }

    var productoPorEliminar by remember {
        mutableStateOf<ProductoEntity?>(null)
    }

    val productos by productoViewModel.productos.collectAsState()
    val productosStockBajo by productoViewModel.productosStockBajo.collectAsState()
    val totalProductos by productoViewModel.totalProductos.collectAsState()

    LaunchedEffect(busqueda) {
        if (busqueda.isBlank()) {
            productoViewModel.restaurarInventario()
        } else {
            productoViewModel.buscarProductos(busqueda)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        TextButton(onClick = regresar) {
            Text("← Regresar al menú")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "PRODUCTOS E INVENTARIO",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Azul
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Buscador
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = {
                Text("Buscar por nombre o código")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Resumen del inventario
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Resumen del inventario",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Azul
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Total de productos: $totalProductos",
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Productos con stock bajo: ${productosStockBajo.size}",
                    fontSize = 16.sp,
                    color = if (productosStockBajo.isNotEmpty())
                        Color.Red else Color(0xFF2E7D32)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Registrar producto
        Button(
            onClick = abrirRegistro,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("＋ Registrar nuevo producto")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Listado de productos",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Azul
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Listado de productos
        if (productos.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = if (busqueda.isBlank())
                        "Todavía no hay productos registrados."
                    else
                        "No se encontraron productos.",
                    modifier = Modifier.padding(20.dp),
                    color = Color.Gray
                )
            }

        } else {

            productos.forEach { producto ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = producto.nombre,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Azul
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Código: ${producto.codigo}")
                        Text("Categoría: ${producto.categoria}")
                        Text("Descripción: ${producto.descripcion}")
                        Text("Precio de compra: $${producto.precioCompra}")
                        Text("Precio de venta: $${producto.precioVenta}")
                        Text("Existencia: ${producto.existencia}")
                        Text("Stock mínimo: ${producto.stockMinimo}")

                        if (producto.existencia <= producto.stockMinimo) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "⚠ STOCK BAJO",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Editar producto
                        OutlinedButton(
                            onClick = {
                                abrirEdicion(producto)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Editar producto")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Solicitar confirmación de eliminación
                        OutlinedButton(
                            onClick = {
                                productoPorEliminar = producto
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red
                            )
                        ) {
                            Text("Eliminar producto")
                        }
                    }
                }
            }
        }

        // Diálogo de confirmación
        productoPorEliminar?.let { producto ->

            AlertDialog(
                onDismissRequest = {
                    productoPorEliminar = null
                },
                title = {
                    Text("Confirmar eliminación")
                },
                text = {
                    Text(
                        "¿Estás seguro de que deseas eliminar " +
                                "${producto.nombre} del inventario?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            productoViewModel.eliminarProducto(producto)
                            productoPorEliminar = null
                        }
                    ) {
                        Text(
                            "Eliminar",
                            color = Color.Red
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            productoPorEliminar = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun PantallaRegistrarProducto(
    regresar: () -> Unit,
    mensaje: String?,
    limpiarMensaje: () -> Unit,
    mostrarMensaje: (String) -> Unit,
    registrar: (
        codigo: String,
        nombre: String,
        categoria: String,
        descripcion: String,
        precioCompra: String,
        precioVenta: String,
        existencia: String,
        stockMinimo: String
    ) -> Unit
) {
    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioCompra by remember { mutableStateOf("") }
    var precioVenta by remember { mutableStateOf("") }
    var existencia by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        TextButton(onClick = regresar) {
            Text("← Regresar al inventario")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "REGISTRAR PRODUCTO",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Azul
        )
        mensaje?.let { texto ->
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Text(
                    text = texto,
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        CampoTexto(
            etiqueta = "Código del producto *",
            valor = codigo,
            cambio = { codigo = it }
        )

        CampoTexto(
            etiqueta = "Nombre del producto *",
            valor = nombre,
            cambio = { nombre = it }
        )

        CampoTexto(
            etiqueta = "Categoría *",
            valor = categoria,
            cambio = { categoria = it }
        )

        CampoTexto(
            etiqueta = "Descripción",
            valor = descripcion,
            cambio = { descripcion = it }
        )

        CampoTexto(
            etiqueta = "Precio de compra *",
            valor = precioCompra,
            cambio = { precioCompra = it }
        )

        CampoTexto(
            etiqueta = "Precio de venta *",
            valor = precioVenta,
            cambio = { precioVenta = it }
        )

        CampoTexto(
            etiqueta = "Existencia inicial *",
            valor = existencia,
            cambio = { existencia = it }
        )

        CampoTexto(
            etiqueta = "Stock mínimo *",
            valor = stockMinimo,
            cambio = { stockMinimo = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                limpiarMensaje()

                when {
                    codigo.isBlank() -> {
                        mostrarMensaje("El código del producto es obligatorio")
                    }

                    nombre.isBlank() -> {
                        mostrarMensaje("El nombre del producto es obligatorio")
                    }

                    categoria.isBlank() -> {
                        mostrarMensaje("La categoría es obligatoria")
                    }

                    precioCompra.toLongOrNull() == null -> {
                        mostrarMensaje("Ingresa un precio de compra válido")
                    }
                    precioCompra.toLongOrNull()!! < 0 -> {
                        mostrarMensaje("El precio de compra no puede ser negativo")
                    }

                    precioVenta.toLongOrNull() == null -> {
                        mostrarMensaje("Ingresa un precio de venta válido")
                    }
                    precioVenta.toLongOrNull()!! < 0 -> {
                        mostrarMensaje("El precio de venta no puede ser negativo")
                    }

                    existencia.toIntOrNull() == null -> {
                        mostrarMensaje("Ingresa una existencia válida")
                    }
                    existencia.toIntOrNull()!! < 0 -> {
                        mostrarMensaje("La existencia no puede ser negativa")
                    }

                    stockMinimo.toIntOrNull() == null -> {
                        mostrarMensaje("Ingresa un stock mínimo válido")
                    }
                    stockMinimo.toIntOrNull()!! < 0 -> {
                        mostrarMensaje("El stock mínimo no puede ser negativo")
                    }

                    else -> {
                        registrar(
                            codigo.trim(),
                            nombre.trim(),
                            categoria.trim(),
                            descripcion.trim(),
                            precioCompra.trim(),
                            precioVenta.trim(),
                            existencia.trim(),
                            stockMinimo.trim()
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("Guardar producto")
        }
    }
}

@Composable
fun PantallaEditarProducto(
    producto: ProductoEntity,
    regresar: () -> Unit,
    mensaje: String?,
    limpiarMensaje: () -> Unit,
    actualizar: (
        codigo: String,
        nombre: String,
        categoria: String,
        descripcion: String,
        precioCompra: String,
        precioVenta: String,
        existencia: String,
        stockMinimo: String
    ) -> Unit
) {
    var codigo by remember(producto.id) {
        mutableStateOf(producto.codigo)
    }

    var nombre by remember(producto.id) {
        mutableStateOf(producto.nombre)
    }

    var categoria by remember(producto.id) {
        mutableStateOf(producto.categoria)
    }

    var descripcion by remember(producto.id) {
        mutableStateOf(producto.descripcion)
    }

    var precioCompra by remember(producto.id) {
        mutableStateOf(producto.precioCompra.toString())
    }

    var precioVenta by remember(producto.id) {
        mutableStateOf(producto.precioVenta.toString())
    }

    var existencia by remember(producto.id) {
        mutableStateOf(producto.existencia.toString())
    }

    var stockMinimo by remember(producto.id) {
        mutableStateOf(producto.stockMinimo.toString())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = regresar) {
            Text("← Regresar al inventario")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "EDITAR PRODUCTO",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Azul
        )
        mensaje?.let { texto ->

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = texto,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )

                    TextButton(
                        onClick = limpiarMensaje
                    ) {
                        Text("Cerrar mensaje")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        CampoTexto(
            etiqueta = "Código del producto",
            valor = codigo,
            cambio = { codigo = it }
        )

        CampoTexto(
            etiqueta = "Nombre del producto",
            valor = nombre,
            cambio = { nombre = it }
        )

        CampoTexto(
            etiqueta = "Categoría",
            valor = categoria,
            cambio = { categoria = it }
        )

        CampoTexto(
            etiqueta = "Descripción",
            valor = descripcion,
            cambio = { descripcion = it }
        )

        CampoTexto(
            etiqueta = "Precio de compra",
            valor = precioCompra,
            cambio = { precioCompra = it }
        )

        CampoTexto(
            etiqueta = "Precio de venta",
            valor = precioVenta,
            cambio = { precioVenta = it }
        )

        CampoTexto(
            etiqueta = "Existencia",
            valor = existencia,
            cambio = { existencia = it }
        )

        CampoTexto(
            etiqueta = "Stock mínimo",
            valor = stockMinimo,
            cambio = { stockMinimo = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                actualizar(
                    codigo.trim(),
                    nombre.trim(),
                    categoria.trim(),
                    descripcion.trim(),
                    precioCompra.trim(),
                    precioVenta.trim(),
                    existencia.trim(),
                    stockMinimo.trim()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("Guardar cambios")
        }
    }
}

@Composable
fun PantallaEstablecimiento(
    viewModel: EstablecimientoViewModel,
    regresar: () -> Unit
) {

    var nombreTienda by remember { mutableStateOf("") }
    var propietario by remember { mutableStateOf("") }
    var municipio by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var mensajeError by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf(false) }
    var mensajeSeleccion by remember { mutableStateOf("") }
    var guardando by remember { mutableStateOf(false) }

    val establecimientos by viewModel.establecimientos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        TextButton(onClick = regresar) {
            Text("← Regresar al menú")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "REGISTRO DEL ESTABLECIMIENTO",
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            color = Azul
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa la información básica de tu tienda.",
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampoTexto(
            etiqueta = "Nombre de la tienda *",
            valor = nombreTienda,
            cambio = { nombreTienda = it }
        )

        CampoTexto(
            etiqueta = "Propietario o administrador *",
            valor = propietario,
            cambio = { propietario = it }
        )

        CampoTexto(
            etiqueta = "Municipio *",
            valor = municipio,
            cambio = { municipio = it }
        )

        CampoTexto(
            etiqueta = "Dirección",
            valor = direccion,
            cambio = { direccion = it }
        )

        CampoTexto(
            etiqueta = "Teléfono de contacto",
            valor = telefono,
            cambio = { telefono = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {

                if (
                    nombreTienda.isBlank() ||
                    propietario.isBlank() ||
                    municipio.isBlank()
                ) {

                    mensajeError =
                        "Completa los campos obligatorios (*)."

                    mensajeExito = false

                } else {

                    mensajeError = ""
                    mensajeExito = false
                    guardando = true

                    viewModel.registrarEstablecimiento(
                        nombre = nombreTienda.trim(),
                        propietario = propietario.trim(),
                        municipio = municipio.trim(),
                        direccion = direccion.trim(),
                        telefono = telefono.trim()
                    ) { resultado ->

                        guardando = false

                        if (resultado) {
                            mensajeExito = true
                            mensajeError = ""

                            nombreTienda = ""
                            propietario = ""
                            municipio = ""
                            direccion = ""
                            telefono = ""

                        } else {
                            mensajeExito = false
                            mensajeError =
                                "No fue posible guardar el establecimiento. Intenta nuevamente."
                        }
                    }
                }
            },
            enabled = !guardando,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {

            if (guardando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Registrar establecimiento")
            }
        }

        if (mensajeExito) {

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE0F2E9)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "¡Registro completado!",
                        color = Color(0xFF176B3A),
                        fontWeight = FontWeight.Bold
                    )

                    Text("El establecimiento fue guardado correctamente.")

                    Text(
                        text = "La información está almacenada en la base de datos local.",
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "ESTABLECIMIENTOS REGISTRADOS",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Azul
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Total registrados: ${establecimientos.size}",
            color = Color.DarkGray
        )

        if (mensajeSeleccion.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mensajeSeleccion,
                color = Color(0xFF176B3A),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (establecimientos.isEmpty()) {

            Text(
                text = "Todavía no hay establecimientos guardados.",
                color = Color.Gray
            )

        } else {

            establecimientos.forEach { establecimiento ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (establecimiento.activo)
                            Color(0xFFEAF3FC)
                        else
                            Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {

                        Text(
                            text = establecimiento.nombre,
                            fontWeight = FontWeight.Bold,
                            color = Azul
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        if (establecimiento.activo) {

                            Text(
                                text = "✓ ESTABLECIMIENTO ACTIVO",
                                color = Color(0xFF176B3A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )

                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Propietario: ${establecimiento.propietario}"
                        )

                        Text(
                            text = "Municipio: ${establecimiento.municipio}"
                        )

                        Text(
                            text = "Dirección: ${establecimiento.direccion}"
                        )

                        Text(
                            text = "Teléfono: ${establecimiento.telefono}"
                        )

                        Text(
                            text = "Código interno: ${establecimiento.id}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!establecimiento.activo) {

                            Button(
                                onClick = {

                                    mensajeSeleccion = ""

                                    viewModel.seleccionarEstablecimiento(
                                        establecimiento.id
                                    ) { resultado ->

                                        mensajeSeleccion = if (resultado) {
                                            "Establecimiento activo actualizado correctamente."
                                        } else {
                                            "No fue posible seleccionar el establecimiento."
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Seleccionar como activa")
                            }

                        } else {

                            Text(
                                text = "Esta tienda está seleccionada actualmente.",
                                color = Color(0xFF176B3A),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))
    }
}


@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    cambio: (String) -> Unit
) {

    Column(
        modifier = Modifier.padding(bottom = 12.dp)
    ) {

        Text(
            text = etiqueta,
            fontWeight = FontWeight.Medium,
            color = Azul
        )

        OutlinedTextField(
            value = valor,
            onValueChange = cambio,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
    }
}
