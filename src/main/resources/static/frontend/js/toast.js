/**
 * Sistema de mensajes toast para la aplicación
 */
const Toast = {
    // Tipos de mensajes
    TYPES: {
        SUCCESS: 'success',
        ERROR: 'error',
        WARNING: 'warning',
        INFO: 'info'
    },

    /**
     * Muestra un mensaje toast
     * @param {string} message - El mensaje a mostrar
     * @param {string} type - El tipo de mensaje (success, error, warning, info)
     * @param {number} duration - Duración en milisegundos (por defecto 3000ms)
     */
    show(message, type = this.TYPES.INFO, duration = 3000) {
        // Crear el contenedor si no existe
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
        }

        // Crear el toast
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;

        // Crear el ícono según el tipo
        const icon = document.createElement('span');
        icon.className = 'toast-icon';
        switch (type) {
            case this.TYPES.SUCCESS:
                icon.innerHTML = '✓';
                break;
            case this.TYPES.ERROR:
                icon.innerHTML = '✗';
                break;
            case this.TYPES.WARNING:
                icon.innerHTML = '⚠';
                break;
            case this.TYPES.INFO:
            default:
                icon.innerHTML = 'ℹ';
                break;
        }

        // Crear el contenido
        const content = document.createElement('div');
        content.className = 'toast-content';
        content.textContent = message;

        // Agregar ícono y contenido al toast
        toast.appendChild(icon);
        toast.appendChild(content);

        // Agregar el toast al contenedor
        container.appendChild(toast);

        // Animar entrada
        setTimeout(() => {
            toast.classList.add('show');
        }, 10);

        // Eliminar después de la duración
        setTimeout(() => {
            toast.classList.remove('show');
            toast.classList.add('hide');

            // Eliminar del DOM después de la animación
            setTimeout(() => {
                if (container.contains(toast)) {
                    container.removeChild(toast);
                }

                // Eliminar el contenedor si está vacío
                if (container.children.length === 0) {
                    document.body.removeChild(container);
                }
            }, 300);
        }, duration);
    },

    /**
     * Muestra un mensaje de éxito
     * @param {string} message - El mensaje a mostrar
     * @param {number} duration - Duración en milisegundos
     */
    success(message, duration = 3000) {
        this.show(message, this.TYPES.SUCCESS, duration);
    },

    /**
     * Muestra un mensaje de error
     * @param {string} message - El mensaje a mostrar
     * @param {number} duration - Duración en milisegundos
     */
    error(message, duration = 5000) {
        this.show(message, this.TYPES.ERROR, duration);
    },

    /**
     * Muestra un mensaje de advertencia
     * @param {string} message - El mensaje a mostrar
     * @param {number} duration - Duración en milisegundos
     */
    warning(message, duration = 4000) {
        this.show(message, this.TYPES.WARNING, duration);
    },

    /**
     * Muestra un mensaje informativo
     * @param {string} message - El mensaje a mostrar
     * @param {number} duration - Duración en milisegundos
     */
    info(message, duration = 3000) {
        this.show(message, this.TYPES.INFO, duration);
    }
};