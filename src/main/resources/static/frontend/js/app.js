// Configuración global
const apiBase = 'http://localhost:8080/api';

// Mostrar/ocultar barra de carga
function showLoading() {
    document.getElementById('loading-bar').classList.add('active');
}

function hideLoading() {
    document.getElementById('loading-bar').classList.remove('active');
}

// --- Animales ---
const animalesList = document.getElementById('animales-list');
const animalForm = document.getElementById('animal-form');

function fetchAnimales() {
    showLoading();
    console.log("Obteniendo animales desde:", `${apiBase}/animales`);

    fetch(`${apiBase}/animales`)
        .then(res => {
            console.log("Respuesta del servidor:", res.status, res.statusText);
            if (!res.ok) {
                if (res.headers.get('content-type')?.includes('application/json')) {
                    return res.json().then(err => { throw new Error(JSON.stringify(err)) });
                } else {
                    return res.text().then(text => {
                        console.error("Respuesta no-JSON recibida:", text);
                        throw new Error("El servidor no respondió con JSON. Posible error en el backend.");
                    });
                }
            }
            return res.json();
        })
        .then(data => {
            console.log("Datos recibidos:", data);
            animalesList.innerHTML = '';
            if (data && data.length > 0) {
                data.forEach(animal => {
                    const li = document.createElement('li');
                    li.textContent = `ID: ${animal.id} - ${animal.nombre} (${animal.raza || animal.tipo}), Peso: ${animal.peso} kg`;

                    const editBtn = document.createElement('button');
                    editBtn.textContent = 'Editar';
                    editBtn.className = 'edit-btn';
                    editBtn.onclick = () => editAnimal(animal);

                    const delBtn = document.createElement('button');
                    delBtn.textContent = 'Eliminar';
                    delBtn.className = 'delete-btn';
                    delBtn.onclick = () => deleteAnimal(animal.id);

                    li.appendChild(editBtn);
                    li.appendChild(delBtn);
                    animalesList.appendChild(li);
                });
            } else {
                animalesList.innerHTML = '<li>No hay animales registrados</li>';
            }
            hideLoading();
        })
        .catch(err => {
            console.error("Error completo:", err);
            Toast.error('No se pudieron cargar los animales');
            animalesList.innerHTML = '<li>Error al cargar los animales</li>';
            hideLoading();
        });
}

function addAnimal(event) {
    event.preventDefault();
    const nombre = document.getElementById('animal-nombre').value;
    const tipo = document.getElementById('animal-tipo').value;
    const raza = document.getElementById('animal-raza').value;
    const color = document.getElementById('animal-color').value;
    const descripcion = document.getElementById('animal-descripcion').value;
    const peso = parseFloat(document.getElementById('animal-peso').value);

    console.log("Enviando datos:", { nombre, tipo, raza, color, descripcion, peso });

    showLoading();
    fetch(`${apiBase}/animales`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombre, tipo, raza, color, descripcion, peso })
    })
        .then(res => {
            console.log("Respuesta del servidor:", res.status, res.statusText);
            if (!res.ok) {
                if (res.headers.get('content-type')?.includes('application/json')) {
                    return res.json().then(err => { throw new Error(JSON.stringify(err)) });
                } else {
                    return res.text().then(text => {
                        console.error("Respuesta no-JSON recibida:", text);
                        throw new Error("El servidor no respondió con JSON. Posible error en el backend.");
                    });
                }
            }
            return res.json();
        })
        .then(data => {
            console.log("Animal creado:", data);
            animalForm.reset();
            fetchAnimales();
            Toast.success("Animal agregado exitosamente");
            hideLoading();
        })
        .catch(err => {
            console.error("Error completo:", err);
            Toast.error('Error al agregar animal: ' + err.message);
            hideLoading();
        });
}

function editAnimal(animal) {
    // Cargar datos en el formulario para editar
    document.getElementById('animal-nombre').value = animal.nombre;
    document.getElementById('animal-tipo').value = animal.tipo || '';
    document.getElementById('animal-raza').value = animal.raza || '';
    document.getElementById('animal-color').value = animal.color || '';
    document.getElementById('animal-descripcion').value = animal.descripcion || '';
    document.getElementById('animal-peso').value = animal.peso;

    // Cambiar el formulario para actualización
    const submitBtn = animalForm.querySelector('button[type="submit"]');
    submitBtn.textContent = 'Actualizar';

    // Guardar el ID del animal a actualizar
    animalForm.dataset.editId = animal.id;

    // Cambiar el comportamiento del formulario
    animalForm.onsubmit = updateAnimal;

    // Scroll hasta el formulario
    animalForm.scrollIntoView({ behavior: 'smooth' });

    // Mostrar mensaje informativo
    Toast.info('Editando animal: ' + animal.nombre);
}

function updateAnimal(event) {
    event.preventDefault();
    const id = animalForm.dataset.editId;
    const nombre = document.getElementById('animal-nombre').value;
    const tipo = document.getElementById('animal-tipo').value;
    const raza = document.getElementById('animal-raza').value;
    const color = document.getElementById('animal-color').value;
    const descripcion = document.getElementById('animal-descripcion').value;
    const peso = parseFloat(document.getElementById('animal-peso').value);

    showLoading();
    fetch(`${apiBase}/animales/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombre, tipo, raza, color, descripcion, peso })
    })
        .then(res => {
            console.log("Respuesta del servidor:", res.status, res.statusText);
            if (!res.ok) {
                if (res.headers.get('content-type')?.includes('application/json')) {
                    return res.json().then(err => { throw new Error(JSON.stringify(err)) });
                } else {
                    return res.text().then(text => {
                        console.error("Respuesta no-JSON recibida:", text);
                        throw new Error("El servidor no respondió con JSON. Posible error en el backend.");
                    });
                }
            }
            return res.json();
        })
        .then(data => {
            // Resetear formulario y comportamiento
            animalForm.reset();
            const submitBtn = animalForm.querySelector('button[type="submit"]');
            submitBtn.textContent = 'Agregar';
            delete animalForm.dataset.editId;
            animalForm.onsubmit = addAnimal;
            fetchAnimales();
            Toast.success('Animal actualizado correctamente');
            hideLoading();
        })
        .catch(err => {
            console.error("Error completo:", err);
            Toast.error('Error al actualizar animal: ' + err.message);
            hideLoading();
        });
}

function deleteAnimal(id) {
    if (!confirm('¿Está seguro de eliminar este animal?')) return;

    showLoading();
    fetch(`${apiBase}/animales/${id}`, { method: 'DELETE' })
        .then(res => {
            if (!res.ok) {
                if (res.headers.get('content-type')?.includes('application/json')) {
                    return res.json().then(err => { throw new Error(JSON.stringify(err)) });
                } else {
                    return res.text().then(text => {
                        throw new Error(text || "Error al eliminar animal");
                    });
                }
            }
            return res;
        })
        .then(() => {
            fetchAnimales();
            Toast.success('Animal eliminado correctamente');
            hideLoading();
        })
        .catch(err => {
            console.error("Error al eliminar:", err);
            Toast.error('Error al eliminar animal: ' + err.message);
            hideLoading();
        });
}

animalForm.addEventListener('submit', addAnimal);

// --- Compras ---
// Implementar de manera similar usando el sistema Toast...

// --- Ventas ---
// Implementar de manera similar usando el sistema Toast...

// --- Inicialización ---
function init() {
    console.log("Inicializando aplicación...");
    try {
        fetchAnimales();
        // Añadir las demás funciones fetch aquí
        Toast.info('Aplicación inicializada correctamente');
    } catch (error) {
        console.error("Error durante la inicialización:", error);
        Toast.error("Error al inicializar la aplicación: " + error.message);
    }
}

window.onload = init;





// En app.js
let animalSelect;

function initSelects() {
    // Inicializar el select de animales
    const animalSelectElement = document.getElementById('compra-animalId');
    if (animalSelectElement) {
        animalSelect = new CustomSelect(animalSelectElement, {
            placeholder: 'Seleccione un animal',
            emptyMessage: 'No hay animales disponibles',
            fetchUrl: `${apiBase}/animales`,
            valueField: 'id',
            textField: 'nombre',
            onSelect: (value, text) => {
                console.log(`Animal seleccionado: ${text} (ID: ${value})`);
            }
        });
    }

    // Hacer lo mismo para otros selects
}

// Añadir a la función init
function init() {
    console.log("Inicializando aplicación...");
    try {
        fetchAnimales();
        fetchCompras();
        fetchVentas();
        initSelects();  // Inicializar selects

        // Establecer fecha actual en campos de fecha
        document.querySelectorAll('input[type="date"]').forEach(input => {
            input.valueAsDate = new Date();
        });

        Toast.info('Aplicación inicializada correctamente');
    } catch (error) {
        console.error("Error durante la inicialización:", error);
        Toast.error("Error al inicializar la aplicación: " + error.message);
    }
}


// En app.js
function setupCurrencyInputs() {
    document.querySelectorAll('.currency-input').forEach(input => {
        // Al perder el foco, formatear
        input.addEventListener('blur', function() {
            const value = Currency.parse(this.value);
            this.value = value;  // Mantener el valor numérico para los cálculos

            // Mostrar el valor formateado en un elemento adyacente
            const displayElement = this.nextElementSibling;
            if (displayElement && displayElement.classList.contains('currency-display')) {
                displayElement.textContent = Currency.format(value);
            }
        });

        // Al enfocar, mostrar valor numérico para edición
        input.addEventListener('focus', function() {
            this.value = Currency.parse(this.value);
        });
    });
}


// Gestionar formulario de compra
function setupCompraForm() {
    const form = document.getElementById('compra-form');
    const tipoCompra = document.getElementById('compra-tipo');
    const precioUnitario = document.getElementById('compra-precio-unitario');
    const precioTotal = document.getElementById('compra-precio-total');
    const cantidad = document.getElementById('compra-cantidad');
    const moneda = document.getElementById('compra-moneda');

    // Cambiar visibilidad según tipo de compra
    tipoCompra.addEventListener('change', function() {
        const isLote = this.value === 'LOTE';
        document.querySelector('.animal-unitario').style.display = isLote ? 'none' : 'flex';
        document.querySelector('.animal-lote').style.display = isLote ? 'flex' : 'none';
    });

    // Actualizar símbolo de moneda
    moneda.addEventListener('change', function() {
        const symbol = this.value === 'USD' ? '$' : '₲';
        document.querySelectorAll('.currency-symbol').forEach(el => {
            el.textContent = symbol;
        });
    });

    // Calcular precio total al cambiar precio unitario o cantidad
    function actualizarPrecioTotal() {
        const unitario = parseFloat(precioUnitario.value) || 0;
        const cant = parseInt(cantidad.value) || 1;
        precioTotal.value = (unitario * cant).toFixed(moneda.value === 'USD' ? 2 : 0);
    }

    // Calcular precio unitario al cambiar precio total o cantidad
    function actualizarPrecioUnitario() {
        const total = parseFloat(precioTotal.value) || 0;
        const cant = parseInt(cantidad.value) || 1;
        precioUnitario.value = (total / cant).toFixed(moneda.value === 'USD' ? 2 : 0);
    }

    precioUnitario.addEventListener('input', actualizarPrecioTotal);
    cantidad.addEventListener('input', actualizarPrecioTotal);
    precioTotal.addEventListener('input', actualizarPrecioUnitario);

    // Establecer fecha actual
    document.getElementById('compra-fecha').valueAsDate = new Date();

    // Manejar envío del formulario
    form.addEventListener('submit', function(event) {
        event.preventDefault();

        showLoading();

        // Construir objeto de datos según el tipo de compra
        const data = {
            tipoCompra: tipoCompra.value,
            precioUnitario: parseFloat(precioUnitario.value) || 0,
            precioTotal: parseFloat(precioTotal.value) || 0,
            cantidad: parseInt(cantidad.value) || 1,
            pesoTotal: parseFloat(document.getElementById('compra-peso-total').value) || null,
            fecha: document.getElementById('compra-fecha').value,
            descripcion: document.getElementById('compra-descripcion').value,
            moneda: moneda.value
        };

        // Agregar campos específicos según el tipo
        if (tipoCompra.value === 'UNITARIA') {
            data.animalId = parseInt(document.getElementById('compra-animalId').value);
        } else {
            data.tipoAnimalLote = document.getElementById('compra-tipo-animal').value;
            data.razaAnimalLote = document.getElementById('compra-raza-animal').value;
        }

        fetch(`${apiBase}/compras`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
            .then(res => {
                if (!res.ok) {
                    return res.json().then(err => { throw new Error(JSON.stringify(err)) });
                }
                return res.json();
            })
            .then(data => {
                form.reset();
                fetchCompras();
                Toast.success('Compra registrada exitosamente');
                hideLoading();

                // Restablecer valores predeterminados
                document.getElementById('compra-fecha').valueAsDate = new Date();
                tipoCompra.value = 'UNITARIA';
                tipoCompra.dispatchEvent(new Event('change'));
                moneda.value = 'PYG';
                moneda.dispatchEvent(new Event('change'));
            })
            .catch(err => {
                console.error('Error al registrar compra:', err);
                Toast.error('Error al registrar la compra: ' + err.message);
                hideLoading();
            });
    });
}

// Gestionar formulario de venta
function setupVentaForm() {
    const form = document.getElementById('venta-form');
    const tipoVenta = document.getElementById('venta-tipo');
    const precioUnitario = document.getElementById('venta-precio-unitario');
    const precioTotal = document.getElementById('venta-precio-total');
    const cantidad = document.getElementById('venta-cantidad');
    const pesoTotal = document.getElementById('venta-peso-total');
    const precioKilo = document.getElementById('venta-precio-kilo');
    const descuento = document.getElementById('venta-descuento');
    const descuentoMO = document.getElementById('venta-descuento-mo');
    const moneda = document.getElementById('venta-moneda');

    // Cambiar visibilidad según tipo de venta
    tipoVenta.addEventListener('change', function() {
        const isLote = this.value === 'LOTE';
        const isPorKilo = this.value === 'POR_KILO';

        document.querySelector('.animal-unitario').style.display = isLote ? 'none' : 'flex';
        document.querySelector('.animal-lote').style.display = isLote ? 'flex' : 'none';
        document.querySelector('.venta-kilo-group').style.display = isPorKilo ? 'block' : 'none';

        // Si es venta por kilo, hacer obligatorio el peso total
        pesoTotal.required = isPorKilo;

        // Si es venta por kilo, habilitar cálculos automáticos
        if (isPorKilo) {
            pesoTotal.addEventListener('input', calcularPorKilo);
            precioKilo.addEventListener('input', calcularPorKilo);
        } else {
            pesoTotal.removeEventListener('input', calcularPorKilo);
            precioKilo.removeEventListener('input', calcularPorKilo);
        }
    });

    // Función para calcular precio total basado en peso y precio por kilo
    function calcularPorKilo() {
        const peso = parseFloat(pesoTotal.value) || 0;
        const precio = parseFloat(precioKilo.value) || 0;
        let total = peso * precio;

        // Aplicar descuentos
        const descuentoPct = parseFloat(descuento.value) || 0;
        const descuentoMOPct = parseFloat(descuentoMO.value) || 0;

        total = total * (1 - descuentoPct/100) * (1 - descuentoMOPct/100);

        precioTotal.value = total.toFixed(moneda.value === 'USD' ? 2 : 0);

        // Calcular precio unitario si hay cantidad
        const cant = parseInt(cantidad.value) || 1;
        precioUnitario.value = (total / cant).toFixed(moneda.value === 'USD' ? 2 : 0);
    }

    // Actualizar símbolo de moneda
    moneda.addEventListener('change', function() {
        const symbol = this.value === 'USD' ? '$' : '₲';
        document.querySelectorAll('.currency-symbol').forEach(el => {
            el.textContent = symbol;
        });
    });

    // Calcular precio total al cambiar precio unitario o cantidad
    function actualizarPrecioTotal() {
        if (tipoVenta.value === 'POR_KILO') return; // No aplicar este cálculo en venta por kilo

        const unitario = parseFloat(precioUnitario.value) || 0;
        const cant = parseInt(cantidad.value) || 1;
        let total = unitario * cant;

        // Aplicar descuentos
        const descuentoPct = parseFloat(descuento.value) || 0;
        const descuentoMOPct = parseFloat(descuentoMO.value) || 0;

        total = total * (1 - descuentoPct/100) * (1 - descuentoMOPct/100);

        precioTotal.value = total.toFixed(moneda.value === 'USD' ? 2 : 0);
    }

    // Calcular precio unitario al cambiar precio total o cantidad
    function actualizarPrecioUnitario() {
        if (tipoVenta.value === 'POR_KILO') return; // No aplicar este cálculo en venta por kilo

        const total = parseFloat(precioTotal.value) || 0;
        const cant = parseInt(cantidad.value) || 1;

        // Calcular precio antes de descuentos
        const descuentoPct = parseFloat(descuento.value) || 0;
        const descuentoMOPct = parseFloat(descuentoMO.value) || 0;

        const totalSinDescuento = total / ((1 - descuentoPct/100) * (1 - descuentoMOPct/100));

        precioUnitario.value = (totalSinDescuento / cant).toFixed(moneda.value === 'USD' ? 2 : 0);
    }

    // Actualizar precio total cuando cambian los descuentos
    function actualizarDescuentos() {
        if (tipoVenta.value === 'POR_KILO') {
            calcularPorKilo();
        } else {
            actualizarPrecioTotal();
        }
    }

    precioUnitario.addEventListener('input', actualizarPrecioTotal);
    cantidad.addEventListener('input', actualizarPrecioTotal);
    precioTotal.addEventListener('input', actualizarPrecioUnitario);
    descuento.addEventListener('input', actualizarDescuentos);
    descuentoMO.addEventListener('input', actualizarDescuentos);

    // Establecer fecha actual
    document.getElementById('venta-fecha').valueAsDate = new Date();

    // Manejar envío del formulario
    form.addEventListener('submit', function(event) {
        event.preventDefault();

        showLoading();

        // Construir objeto de datos según el tipo de venta
        const data = {
            tipoVenta: tipoVenta.value,
            precioUnitario: parseFloat(precioUnitario.value) || 0,
            precioTotal: parseFloat(precioTotal.value) || 0,
            cantidad: parseInt(cantidad.value) || 1,
            pesoTotal: parseFloat(pesoTotal.value) || null,
            porcentajeDescuento: parseFloat(descuento.value) || 0,
            descuentoMateriaOrganica: parseFloat(descuentoMO.value) || 0,
            fecha: document.getElementById('venta-fecha').value,
            descripcion: document.getElementById('venta-descripcion').value,
            moneda: moneda.value
        };

        // Agregar campos específicos según el tipo
        if (tipoVenta.value === 'UNITARIA') {
            data.animalId = parseInt(document.getElementById('venta-animalId').value);
        } else if (tipoVenta.value === 'LOTE') {
            data.tipoAnimalLote = document.getElementById('venta-tipo-animal').value;
            data.razaAnimalLote = document.getElementById('venta-raza-animal').value;
        } else if (tipoVenta.value === 'POR_KILO') {
            data.precioPorKilo = parseFloat(precioKilo.value) || 0;
            data.animalId = parseInt(document.getElementById('venta-animalId').value);
        }

        fetch(`${apiBase}/ventas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
            .then(res => {
                if (!res.ok) {
                    return res.json().then(err => { throw new Error(JSON.stringify(err)) });
                }
                return res.json();
            })
            .then(data => {
                form.reset();
                fetchVentas();
                Toast.success('Venta registrada exitosamente');
                hideLoading();

                // Restablecer valores predeterminados
                document.getElementById('venta-fecha').valueAsDate = new Date();
                tipoVenta.value = 'UNITARIA';
                tipoVenta.dispatchEvent(new Event('change'));
                moneda.value = 'PYG';
                moneda.dispatchEvent(new Event('change'));
            })
            .catch(err => {
                console.error('Error al registrar venta:', err);
                Toast.error('Error al registrar la venta: ' + err.message);
                hideLoading();
            });
    });
}

// Función para cargar compras
function fetchCompras() {
    showLoading();

    fetch(`${apiBase}/compras`)
        .then(res => {
            if (!res.ok) {
                throw new Error('Error al cargar compras');
            }
            return res.json();
        })
        .then(data => {
            const comprasList = document.getElementById('compras-list');
            comprasList.innerHTML = '';

            if (data && data.length > 0) {
                const table = document.createElement('table');
                table.className = 'data-table';

                // Cabecera de la tabla
                const thead = document.createElement('thead');
                thead.innerHTML = `
                    <tr>
                        <th>ID</th>
                        <th>Fecha</th>
                        <th>Animal/Tipo</th>
                        <th>Cantidad</th>
                        <th>Precio</th>
                        <th>Acciones</th>
                    </tr>
                `;

                // Cuerpo de la tabla
                const tbody = document.createElement('tbody');

                data.forEach(compra => {
                    const tr = document.createElement('tr');

                    // Información del animal/tipo
                    let animalInfo = '';
                    if (compra.tipoCompra === 'UNITARIA' && compra.animal) {
                        animalInfo = `${compra.animal.nombre} (${compra.animal.tipo || ''} ${compra.animal.raza || ''})`;
                    } else {
                        animalInfo = `${compra.tipoAnimalLote || 'Lote'} ${compra.razaAnimalLote || ''}`;
                    }

                    // Formato de moneda
                    const monedaSymbol = compra.moneda === 'USD' ? '$' : '₲';
                    const precio = Currency.format(compra.precioTotal, compra.moneda);

                    tr.innerHTML = `
                        <td>${compra.id}</td>
                        <td>${formatDate(compra.fecha)}</td>
                        <td>${animalInfo}</td>
                        <td>${compra.cantidad}</td>
                        <td>${precio}</td>
                        <td class="actions">
                            <button class="btn-icon edit-btn" title="Editar">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn-icon delete-btn" title="Eliminar">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    `;

                    // Eventos para los botones
                    const editBtn = tr.querySelector('.edit-btn');
                    const deleteBtn = tr.querySelector('.delete-btn');

                    editBtn.addEventListener('click', () => editCompra(compra));
                    deleteBtn.addEventListener('click', () => deleteCompra(compra.id));

                    tbody.appendChild(tr);
                });

                table.appendChild(thead);
                table.appendChild(tbody);
                comprasList.appendChild(table);
            } else {
                comprasList.innerHTML = '<div class="empty-state">No hay compras registradas</div>';
            }

            hideLoading();
        })
        .catch(err => {
            console.error('Error al cargar compras:', err);
            document.getElementById('compras-list').innerHTML = '<div class="error-state">Error al cargar compras</div>';
            Toast.error('No se pudieron cargar las compras');
            hideLoading();
        });
}

// Función para cargar ventas
function fetchVentas() {
    showLoading();

    fetch(`${apiBase}/ventas`)
        .then(res => {
            if (!res.ok) {
                throw new Error('Error al cargar ventas');
            }
            return res.json();
        })
        .then(data => {
            const ventasList = document.getElementById('ventas-list');
            ventasList.innerHTML = '';

            if (data && data.length > 0) {
                const table = document.createElement('table');
                table.className = 'data-table';

                // Cabecera de la tabla
                const thead = document.createElement('thead');
                thead.innerHTML = `
                    <tr>
                        <th>ID</th>
                        <th>Fecha</th>
                        <th>Animal/Tipo</th>
                        <th>Cantidad</th>
                        <th>Peso</th>
                        <th>Precio</th>
                        <th>Acciones</th>
                    </tr>
                `;

                // Cuerpo de la tabla
                const tbody = document.createElement('tbody');

                data.forEach(venta => {
                    const tr = document.createElement('tr');

                    // Información del animal/tipo
                    let animalInfo = '';
                    if ((venta.tipoVenta === 'UNITARIA' || venta.tipoVenta === 'POR_KILO') && venta.animal) {
                        animalInfo = `${venta.animal.nombre} (${venta.animal.tipo || ''} ${venta.animal.raza || ''})`;
                    } else {
                        animalInfo = `${venta.tipoAnimalLote || 'Lote'} ${venta.razaAnimalLote || ''}`;
                    }

                    // Formato de moneda
                    const monedaSymbol = venta.moneda === 'USD' ? '$' : '₲';
                    const precio = Currency.format(venta.precioTotal, venta.moneda);

                    tr.innerHTML = `
                        <td>${venta.id}</td>
                        <td>${formatDate(venta.fecha)}</td>
                        <td>${animalInfo}</td>
                        <td>${venta.cantidad}</td>
                        <td>${venta.pesoTotal ? venta.pesoTotal + ' kg' : '-'}</td>
                        <td>${precio}</td>
                        <td class="actions">
                            <button class="btn-icon edit-btn" title="Editar">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn-icon delete-btn" title="Eliminar">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    `;

                    // Eventos para los botones
                    const editBtn = tr.querySelector('.edit-btn');
                    const deleteBtn = tr.querySelector('.delete-btn');

                    editBtn.addEventListener('click', () => editVenta(venta));
                    deleteBtn.addEventListener('click', () => deleteVenta(venta.id));

                    tbody.appendChild(tr);
                });

                table.appendChild(thead);
                table.appendChild(tbody);
                ventasList.appendChild(table);
            } else {
                ventasList.innerHTML = '<div class="empty-state">No hay ventas registradas</div>';
            }

            hideLoading();
        })
        .catch(err => {
            console.error('Error al cargar ventas:', err);
            document.getElementById('ventas-list').innerHTML = '<div class="error-state">Error al cargar ventas</div>';
            Toast.error('No se pudieron cargar las ventas');
            hideLoading();
        });
}

