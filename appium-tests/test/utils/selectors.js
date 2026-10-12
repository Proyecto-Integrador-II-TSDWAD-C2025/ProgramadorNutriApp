// Localizadores centralizados para Appium (Accessibility IDs)
// En Android, WebdriverIO usa ~ como shortcut para buscar por contentDescription

const SELECTORS = {
    // Login
    inputEmail: '~etEmail',
    inputPassword: '~etPassword',
    btnLogin: '~btnLogin',

    // Dashboard
    saludo: '~tvSaludo',
    cardMiRutina: '~cardMiRutina',

    // Mi Rutina
    tituloMiRutina: '~tvTitulo',
    bannerRevision: '~bannerRevision',
    layoutEmpty: '~layoutEmpty',
    btnVolver: '~btnVolver',

    // Ejercicios (por contentDescription dinamico)
    ejercicioPorNombre: (nombre, estado) => `~Ejercicio: ${nombre}, ${estado}`,
    ejerciciosPendientes: '//android.view.ViewGroup[@content-desc[contains(., "pendiente")]]',
};

module.exports = SELECTORS;
