const axios = require('axios');

const BASE_URL = process.env.API_BASE_URL || 'https://nutriapp-appmovil.up.railway.app/api/';

const api = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

async function crearUsuarioTest(email, password) {
    const response = await api.post('register/', {
        nombre: 'Appium',
        apellido: 'Test',
        email,
        contrasena: password,
    });
    return response.data;
}

async function crearPerfilTest(token, perfilData) {
    const response = await api.put('perfil/', perfilData, {
        headers: {
            Authorization: `Token ${token}`,
        },
    });
    return response.data;
}

async function setupUsuarioCompleto() {
    const timestamp = Date.now();
    const email = `appium-test-${timestamp}@example.com`;
    const password = 'TestPass123!';

    const registro = await crearUsuarioTest(email, password);
    const token = registro.token;

    await crearPerfilTest(token, {
        sexo: 'm',
        edad: 25,
        peso_actual: 75.0,
        altura_cm: 175,
        peso_objetivo: 70.0,
        objetivo: 'bajar_grasa',
        actividad: 'moderado',
        preferencia: 'sin_preferencia',
        dias_entrenamiento: 3,
        limitaciones: '',
        consideraciones_alimentarias: '',
    });

    return { email, password, token };
}

module.exports = {
    crearUsuarioTest,
    crearPerfilTest,
    setupUsuarioCompleto,
};
