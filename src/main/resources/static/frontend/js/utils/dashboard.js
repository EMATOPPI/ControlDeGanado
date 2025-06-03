// dashboard.js
document.addEventListener('DOMContentLoaded', function() {
    const apiBase = 'http://localhost:8080/api';

    // Referencias a elementos del DOM
    const totalAnimales = document.getElementById('total-animales');
    const totalCompras = document.getElementById('total-compras');
    const totalVentas = document.getElementById('total-ventas');
    const rentabilidad = document.getElementById('rentabilidad');

    const fechaInicio = document.getElementById('fecha-inicio');
    const fechaFin = document.getElementById('fecha-fin');
    const btnFiltrar = document.getElementById('btn-filtrar');

    const topVentas = document.getElementById('top-ventas');
    const topCompras = document.getElementById('top-compras');
    const tablaUltimasOperaciones = document.getElementById('tabla-ultimas-operaciones');

    // Funciones de utilidad
    function showLoading() {
        document.getElementById('loading-bar').classList.add('active');
        document.getElementById('loading-overlay').classList.add('active');
    }

    function hideLoading() {
        document.getElementById('loading-bar').classList.remove('active');
        document.getElementById('loading-overlay').classList.remove('active');
    }

    function formatDate(date) {
        if (!date) return '';
        const d = new Date(date);
        return d.toLocaleDateString('es-PY');
    }

    // Inicializar fechas (último mes)
    const hoy = new Date();
    const inicioMes = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
    fechaInicio.valueAsDate = inicioMes;
    fechaFin.valueAsDate = hoy;

    // Cargar resumen
    function cargarResumen() {
        showLoading();

        fetch(`${apiBase}/reportes/resumen?fechaInicio=${fechaInicio.value}&fechaFin=${fechaFin.value}`)
            .then(res => res.json())
            .then(data => {
                totalAnimales.textContent = data.totalAnimales || 0;
                totalCompras.textContent = data.totalCompras || 0;
                totalVentas.textContent = data.totalVentas || 0;

                const rentabilidadValor = data.rentabilidad || 0;
                rentabilidad.textContent = Currency.format(rentabilidadValor);

                // Actualizar clase para color de rentabilidad
                if (rentabilidadValor > 0) {
                    rentabilidad.parentElement.querySelector('.stat-trend').className = 'stat-trend positive';
                    rentabilidad.parentElement.querySelector('.stat-trend i').className = 'fas fa-arrow-up';
                } else {
                    rentabilidad.parentElement.querySelector('.stat-trend').className = 'stat-trend negative';
                    rentabilidad.parentElement.querySelector('.stat-trend i').className = 'fas fa-arrow-down';
                }

                hideLoading();
            })
            .catch(error => {
                console.error('Error al cargar resumen:', error);
                Toast.error('Error al cargar datos de resumen');
                hideLoading();
            });
    }

    // Cargar tops
    function cargarTops() {
        // Top ventas
        fetch(`${apiBase}/reportes/animales/top-ventas?fechaInicio=${fechaInicio.value}&fechaFin=${fechaFin.value}&limite=5`)
            .then(res => res.json())
            .then(data => {
                topVentas.innerHTML = '';

                if (data && data.length > 0) {
                    data.forEach(item => {
                        const li = document.createElement('li');
                        li.className = 'top-item';
                        li.innerHTML = `
                            <div class="top-item-info">
                                <div class="top-item-name">${item.nombreAnimal}</div>
                                <div class="top-item-detail">${item.tipoAnimal} ${item.razaAnimal ? `- ${item.razaAnimal}` : ''}</div>
                            </div>
                            <div class="top-item-value">${Currency.format(item.montoTotal)}</div>
                        `;
                        topVentas.appendChild(li);
                    });
                } else {
                    topVentas.innerHTML = '<li class="top-item"><div class="top-item-info"><div class="top-item-name">No hay datos disponibles</div></div></li>';
                }
            })
            .catch(error => {
                console.error('Error al cargar top ventas:', error);
                topVentas.innerHTML = '<li class="top-item"><div class="top-item-info"><div class="top-item-name">Error al cargar datos</div></div></li>';
            });

        // Top compras
        fetch(`${apiBase}/reportes/animales/top-compras?fechaInicio=${fechaInicio.value}&fechaFin=${fechaFin.value}&limite=5`)
            .then(res => res.json())
            .then(data => {
                topCompras.innerHTML = '';

                if (data && data.length > 0) {
                    data.forEach(item => {
                        const li = document.createElement('li');
                        li.className = 'top-item';
                        li.innerHTML = `
                            <div class="top-item-info">
                                <div class="top-item-name">${item.nombreAnimal}</div>
                                <div class="top-item-detail">${item.tipoAnimal} ${item.razaAnimal ? `- ${item.razaAnimal}` : ''}</div>
                            </div>
                            <div class="top-item-value">${Currency.format(item.montoTotal)}</div>
                        `;
                        topCompras.appendChild(li);
                    });
                } else {
                    topCompras.innerHTML = '<li class="top-item"><div class="top-item-info"><div class="top-item-name">No hay datos disponibles</div></div></li>';
                }
            })
            .catch(error => {
                console.error('Error al cargar top compras:', error);
                topCompras.innerHTML = '<li class="top-item"><div class="top-item-info"><div class="top-item-name">Error al cargar datos</div></div></li>';
            });
    }

    // Cargar últimas operaciones
    function cargarUltimasOperaciones() {
        // Obtener compras y ventas y ordenarlas por fecha
        Promise.all([
            fetch(`${apiBase}/compras`).then(res => res.json()),
            fetch(`${apiBase}/ventas`).then(res => res.json())
        ])
            .then(([compras, ventas]) => {
                // Combinar y formatear operaciones
                const operaciones = [
                    ...compras.map(c => ({
                        fecha: c.fecha,
                        tipo: 'Compra',
                        detalle: c.animal ? `${c.animal.nombre} (${c.animal.tipo || ''} ${c.animal.raza || ''})` : 'Lote',
                        monto: c.precioTotal,
                        moneda: c.moneda || 'PYG'
                    })),
                    ...ventas.map(v => ({
                        fecha: v.fecha,
                        tipo: 'Venta',
                        detalle: v.animal ? `${v.animal.nombre} (${v.animal.tipo || ''} ${v.animal.raza || ''})` : 'Lote',
                        monto: v.precioTotal,
                        moneda: v.moneda || 'PYG'
                    }))
                ];

                // Ordenar por fecha (más reciente primero)
                operaciones.sort((a, b) => new Date(b.fecha) - new Date(a.fecha));

                // Mostrar solo las 10 más recientes
                const ultimas = operaciones.slice(0, 10);

                // Actualizar tabla
                tablaUltimasOperaciones.innerHTML = '';

                if (ultimas.length > 0) {
                    ultimas.forEach(op => {
                        const tr = document.createElement('tr');
                        tr.innerHTML = `
                            <td>${formatDate(op.fecha)}</td>
                            <td>${op.tipo}</td>
                            <td>${op.detalle}</td>
                            <td>${Currency.format(op.monto, op.moneda)}</td>
                        `;

                        // Aplicar clase según tipo
                        if (op.tipo === 'Venta') {
                            tr.classList.add('table-success');
                        } else {
                            tr.classList.add('table-info');
                        }

                        tablaUltimasOperaciones.appendChild(tr);
                    });
                } else {
                    tablaUltimasOperaciones.innerHTML = '<tr><td colspan="4" class="text-center">No hay operaciones recientes</td></tr>';
                }
            })
            .catch(error => {
                console.error('Error al cargar operaciones:', error);
                tablaUltimasOperaciones.innerHTML = '<tr><td colspan="4" class="text-center text-danger">Error al cargar operaciones</td></tr>';
            });
    }

    // Inicializar gráfico
    function inicializarGrafico() {
        const ctx = document.getElementById('chart-ventas-compras').getContext('2d');

        // Obtener datos para el gráfico
        Promise.all([
            fetch(`${apiBase}/reportes/resumen?fechaInicio=${fechaInicio.value}&fechaFin=${fechaFin.value}`).then(res => res.json())
        ])
            .then(([resumen]) => {
                // Crear configuración del gráfico
                const chart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: ['Compras', 'Ventas'],
                        datasets: [{
                            label: 'Monto en Guaraníes',
                            data: [resumen.montoTotalCompras || 0, resumen.montoTotalVentas || 0],
                            backgroundColor: [
                                'rgba(54, 162, 235, 0.6)',
                                'rgba(75, 192, 192, 0.6)'
                            ],
                            borderColor: [
                                'rgba(54, 162, 235, 1)',
                                'rgba(75, 192, 192, 1)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    callback: function(value) {
                                        return Currency.format(value).replace('₲', '₲ ');
                                    }
                                }
                            }
                        },
                        plugins: {
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return Currency.format(context.raw);
                                    }
                                }
                            }
                        }
                    }
                });

                // Guardar referencia para actualizar
                window.ventasComprasChart = chart;
            })
            .catch(error => {
                console.error('Error al inicializar gráfico:', error);
            });
    }

    // Actualizar gráfico
    function actualizarGrafico() {
        if (!window.ventasComprasChart) {
            inicializarGrafico();
            return;
        }

        fetch(`${apiBase}/reportes/resumen?fechaInicio=${fechaInicio.value}&fechaFin=${fechaFin.value}`)
            .then(res => res.json())
            .then(resumen => {
                window.ventasComprasChart.data.datasets[0].data = [
                    resumen.montoTotalCompras || 0,
                    resumen.montoTotalVentas || 0
                ];
                window.ventasComprasChart.update();
            })
            .catch(error => {
                console.error('Error al actualizar gráfico:', error);
            });
    }

    // Manejar filtro
    btnFiltrar.addEventListener('click', function() {
        cargarResumen();
        cargarTops();
        actualizarGrafico();
        Toast.info('Datos actualizados para el período seleccionado');
    });

    // Inicialización
    function init() {
        cargarResumen();
        cargarTops();
        cargarUltimasOperaciones();
        inicializarGrafico();

        Toast.info('Dashboard inicializado correctamente');
    }

    init();
});