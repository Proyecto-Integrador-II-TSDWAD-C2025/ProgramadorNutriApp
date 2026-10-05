const { setupUsuarioCompleto } = require('../utils/api-setup');
const SELECTORS = require('../utils/selectors');

describe('Smoke Test - Mi Rutina', () => {
    let testUser;

    before(async () => {
        testUser = await setupUsuarioCompleto();
    });

    it('debe permitir login, navegar a Mi Rutina y completar un ejercicio', async () => {
        // 1. Login UI
        await $(SELECTORS.inputEmail).setValue(testUser.email);
        await $(SELECTORS.inputPassword).setValue(testUser.password);
        await $(SELECTORS.btnLogin).click();

        // Esperar Dashboard
        await $(SELECTORS.saludo).waitForDisplayed({ timeout: 15000 });

        // 2. Navegar a Mi Rutina
        await $(SELECTORS.cardMiRutina).click();

        // Esperar carga de rutina
        await $(SELECTORS.tituloMiRutina).waitForDisplayed({ timeout: 15000 });

        // Validar que no estemos en estado vacio ni revision
        const emptyVisible = await $(SELECTORS.layoutEmpty).isDisplayed().catch(() => false);
        const revisionVisible = await $(SELECTORS.bannerRevision).isDisplayed().catch(() => false);

        if (emptyVisible) {
            throw new Error('La rutina aparece vacia. El usuario de prueba no tiene rutina asignada.');
        }
        if (revisionVisible) {
            throw new Error('La rutina esta en revision. El perfil no genero una rutina automatica.');
        }

        // 3. Completar primer ejercicio pendiente
        const ejerciciosPendientes = await $$(SELECTORS.ejerciciosPendientes);
        if (ejerciciosPendientes.length === 0) {
            throw new Error('No hay ejercicios pendientes para completar.');
        }

        const primerEjercicio = ejerciciosPendientes[0];
        const nombreConEstado = await primerEjercicio.getAttribute('content-desc');
        const nombreEjercicio = nombreConEstado.replace(', pendiente', '').replace('Ejercicio: ', '');

        await primerEjercicio.click();

        // Verificar que cambio a "completado"
        const selectorCompletado = SELECTORS.ejercicioPorNombre(nombreEjercicio, 'completado');
        await $(selectorCompletado).waitForDisplayed({ timeout: 8000 });

        // 4. Verificar persistencia: volver al dashboard y reentrar a Mi Rutina
        await $(SELECTORS.btnVolver).click();
        await $(SELECTORS.saludo).waitForDisplayed({ timeout: 8000 });

        await $(SELECTORS.cardMiRutina).click();
        await $(SELECTORS.tituloMiRutina).waitForDisplayed({ timeout: 15000 });

        // El ejercicio debe seguir apareciendo como completado
        const ejercicioCompletado = await $(selectorCompletado);
        await expect(ejercicioCompletado).toBeDisplayed();
    });
});
