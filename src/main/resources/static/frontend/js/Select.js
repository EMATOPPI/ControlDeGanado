// En un nuevo archivo: js/components/Select.js
class CustomSelect {
    constructor(element, options = {}) {
        this.element = element;
        this.options = {
            placeholder: options.placeholder || 'Seleccione una opción',
            emptyMessage: options.emptyMessage || 'No hay opciones disponibles',
            fetchUrl: options.fetchUrl || null,
            valueField: options.valueField || 'id',
            textField: options.textField || 'nombre',
            onSelect: options.onSelect || function() {},
            onLoad: options.onLoad || function() {}
        };

        this.data = options.data || [];
        this.selectedValue = null;

        this.init();
    }

    init() {
        // Crear estructura HTML
        this.createElements();

        // Cargar datos si la URL está disponible
        if (this.options.fetchUrl) {
            this.fetchData();
        } else if (this.data.length > 0) {
            this.renderOptions();
        }

        // Eventos
        this.setupEvents();
    }

    createElements() {
        // Contenedor principal
        this.container = document.createElement('div');
        this.container.className = 'custom-select';

        // Campo visible
        this.field = document.createElement('div');
        this.field.className = 'custom-select-field';
        this.field.innerHTML = `
            <span class="custom-select-text">${this.options.placeholder}</span>
            <span class="custom-select-arrow">▼</span>
        `;

        // Dropdown
        this.dropdown = document.createElement('div');
        this.dropdown.className = 'custom-select-dropdown';

        // Insertar en el DOM
        this.container.appendChild(this.field);
        this.container.appendChild(this.dropdown);

        // Reemplazar el elemento original
        this.element.parentNode.replaceChild(this.container, this.element);

        // Guardar referencia al elemento original
        this.container.appendChild(this.element);
        this.element.style.display = 'none';
    }

    fetchData() {
        // Mostrar estado de carga
        this.field.classList.add('loading');
        this.dropdown.innerHTML = '<div class="custom-select-loading">Cargando opciones...</div>';

        fetch(this.options.fetchUrl)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al cargar datos');
                }
                return response.json();
            })
            .then(data => {
                this.data = data;
                this.renderOptions();
                this.field.classList.remove('loading');
                this.options.onLoad(data);
            })
            .catch(error => {
                console.error('Error fetching data:', error);
                this.dropdown.innerHTML = `<div class="custom-select-error">Error: ${error.message}</div>`;
                this.field.classList.remove('loading');
            });
    }

    renderOptions() {
        if (this.data.length === 0) {
            this.dropdown.innerHTML = `<div class="custom-select-empty">${this.options.emptyMessage}</div>`;
            return;
        }

        const optionsList = document.createElement('ul');

        this.data.forEach(item => {
            const li = document.createElement('li');
            const value = item[this.options.valueField];
            const text = item[this.options.textField];

            // Si hay campos adicionales, añadirlos como data-attributes
            let additionalText = '';
            if (item.tipo || item.raza) {
                additionalText = item.tipo && item.raza
                    ? ` (${item.tipo} - ${item.raza})`
                    : ` (${item.tipo || item.raza})`;
            }

            li.dataset.value = value;
            li.textContent = text + additionalText;
            li.addEventListener('click', () => this.selectOption(value, text + additionalText));

            optionsList.appendChild(li);
        });

        this.dropdown.innerHTML = '';
        this.dropdown.appendChild(optionsList);

        // Actualizar también el select original
        this.element.innerHTML = '';
        const placeholderOption = document.createElement('option');
        placeholderOption.value = '';
        placeholderOption.textContent = this.options.placeholder;
        this.element.appendChild(placeholderOption);

        this.data.forEach(item => {
            const option = document.createElement('option');
            option.value = item[this.options.valueField];
            option.textContent = item[this.options.textField];
            this.element.appendChild(option);
        });
    }

    selectOption(value, text) {
        this.selectedValue = value;
        this.field.querySelector('.custom-select-text').textContent = text;
        this.element.value = value;

        // Disparar evento change en el select original
        const event = new Event('change', { bubbles: true });
        this.element.dispatchEvent(event);

        // Callback
        this.options.onSelect(value, text);

        // Cerrar dropdown
        this.closeDropdown();
    }

    openDropdown() {
        this.dropdown.style.display = 'block';
        this.field.classList.add('active');
    }

    closeDropdown() {
        this.dropdown.style.display = 'none';
        this.field.classList.remove('active');
    }

    setupEvents() {
        // Abrir/cerrar dropdown
        this.field.addEventListener('click', (e) => {
            e.stopPropagation();

            if (this.dropdown.style.display === 'block') {
                this.closeDropdown();
            } else {
                this.openDropdown();
            }
        });

        // Cerrar al hacer clic fuera
        document.addEventListener('click', () => {
            this.closeDropdown();
        });

        // Evitar que se cierre al hacer clic en el dropdown
        this.dropdown.addEventListener('click', (e) => {
            e.stopPropagation();
        });
    }

    // Métodos públicos
    getValue() {
        return this.selectedValue;
    }

    setValue(value) {
        const item = this.data.find(item => item[this.options.valueField] == value);
        if (item) {
            const text = item[this.options.textField];
            let additionalText = '';
            if (item.tipo || item.raza) {
                additionalText = item.tipo && item.raza
                    ? ` (${item.tipo} - ${item.raza})`
                    : ` (${item.tipo || item.raza})`;
            }
            this.selectOption(value, text + additionalText);
        }
    }

    reload() {
        if (this.options.fetchUrl) {
            this.fetchData();
        } else {
            this.renderOptions();
        }
    }
}