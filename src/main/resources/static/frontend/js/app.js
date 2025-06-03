const apiBase = '/api';

// --- Animales ---

const animalesList = document.getElementById('animales-list');
const animalForm = document.getElementById('animal-form');

function fetchAnimales() {
    fetch(`${apiBase}/animales`)
        .then(res => res.json())
        .then(data => {
            animalesList.innerHTML = '';
            data.forEach(animal => {
                const li = document.createElement('li');
                li.textContent = `ID: ${animal.id} - ${animal.nombre} (${animal.raza}), Peso: ${animal.peso}`;
                const delBtn = document.createElement('button');
                delBtn.textContent = 'Eliminar';
                delBtn.onclick = () => deleteAnimal(animal.id);
                li.appendChild(delBtn);
                animalesList.appendChild(li);
            });
        });
}

function addAnimal(event) {
    event.preventDefault();
    const nombre = document.getElementById('animal-nombre').value;
    const raza = document.getElementById('animal-raza').value;
    const peso = parseFloat(document.getElementById('animal-peso').value);

    fetch(`${apiBase}/animales`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombre, raza, peso })
    })
        .then(res => {
            if (res.ok) {
                animalForm.reset();
                fetchAnimales();
            } else {
                alert('Error al agregar animal');
            }
        });
}

function deleteAnimal(id) {
    fetch(`${apiBase}/animales/${id}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) fetchAnimales();
            else alert('Error al eliminar animal');
        });
}

animalForm.addEventListener('submit', addAnimal);

// --- Compras ---

const comprasList = document.getElementById('compras-list');
const compraForm = document.getElementById('compra-form');

function fetchCompras() {
    fetch(`${apiBase}/compras`)
        .then(res => res.json())
        .then(data => {
            comprasList.innerHTML = '';
            data.forEach(compra => {
                const li = document.createElement('li');
                li.textContent = `ID: ${compra.id} - Animal ID: ${compra.animalId}, Cantidad: ${compra.cantidad}, Precio: ${compra.precio}`;
                const delBtn = document.createElement('button');
                delBtn.textContent = 'Eliminar';
                delBtn.onclick = () => deleteCompra(compra.id);
                li.appendChild(delBtn);
                comprasList.appendChild(li);
            });
        });
}

function addCompra(event) {
    event.preventDefault();
    const animalId = parseInt(document.getElementById('compra-animalId').value);
    const cantidad = parseInt(document.getElementById('compra-cantidad').value);
    const precio = parseFloat(document.getElementById('compra-precio').value);

    fetch(`${apiBase}/compras`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ animalId, cantidad, precio })
    })
        .then(res => {
            if (res.ok) {
                compraForm.reset();
                fetchCompras();
            } else {
                alert('Error al agregar compra');
            }
        });
}

function deleteCompra(id) {
    fetch(`${apiBase}/compras/${id}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) fetchCompras();
            else alert('Error al eliminar compra');
        });
}

compraForm.addEventListener('submit', addCompra);

// --- Ventas ---

const ventasList = document.getElementById('ventas-list');
const ventaForm = document.getElementById('venta-form');

function fetchVentas() {
    fetch(`${apiBase}/ventas`)
        .then(res => res.json())
        .then(data => {
            ventasList.innerHTML = '';
            data.forEach(venta => {
                const li = document.createElement('li');
                li.textContent = `ID: ${venta.id} - Animal ID: ${venta.animalId}, Cantidad: ${venta.cantidad}, Precio: ${venta.precio}`;
                const delBtn = document.createElement('button');
                delBtn.textContent = 'Eliminar';
                delBtn.onclick = () => deleteVenta(venta.id);
                li.appendChild(delBtn);
                ventasList.appendChild(li);
            });
        });
}

function addVenta(event) {
    event.preventDefault();
    const animalId = parseInt(document.getElementById('venta-animalId').value);
    const cantidad = parseInt(document.getElementById('venta-cantidad').value);
    const precio = parseFloat(document.getElementById('venta-precio').value);

    fetch(`${apiBase}/ventas`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ animalId, cantidad, precio })
    })
        .then(res => {
            if (res.ok) {
                ventaForm.reset();
                fetchVentas();
            } else {
                alert('Error al agregar venta');
            }
        });
}

function deleteVenta(id) {
    fetch(`${apiBase}/ventas/${id}`, { method: 'DELETE' })
        .then(res => {
            if (res.ok) fetchVentas();
            else alert('Error al eliminar venta');
        });
}

ventaForm.addEventListener('submit', addVenta);

// --- Inicialización ---

function init() {
    fetchAnimales();
    fetchCompras();
    fetchVentas();
}

window.onload = init;