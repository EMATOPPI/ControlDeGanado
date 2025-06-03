// En un archivo nuevo: js/utils/currency.js
const Currency = {
    // Formato para guaraníes (sin decimales)
    formatGuarani(amount) {
        return new Intl.NumberFormat('es-PY', {
            style: 'currency',
            currency: 'PYG',
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount);
    },

    // Formato para dólares (con dos decimales)
    formatDollar(amount) {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(amount);
    },

    // Formato personalizado
    format(amount, currency = 'PYG') {
        switch (currency) {
            case 'USD':
                return this.formatDollar(amount);
            case 'PYG':
            default:
                return this.formatGuarani(amount);
        }
    },

    // Parsear valor de moneda a número
    parse(value, currency = 'PYG') {
        if (!value) return 0;

        // Eliminar símbolos de moneda y separadores
        const cleanValue = value.replace(/[^\d,-]/g, '')
            .replace(',', '.');

        return parseFloat(cleanValue) || 0;
    }
};