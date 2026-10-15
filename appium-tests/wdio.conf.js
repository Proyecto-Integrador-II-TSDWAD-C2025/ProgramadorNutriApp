require('dotenv').config();

const path = require('path');

exports.config = {
    runner: 'local',
    port: 4723,
    specs: [
        path.join(__dirname, 'test', 'specs', '**', '*.e2e.js')
    ],
    exclude: [],
    maxInstances: 1,
    capabilities: [{
        platformName: 'Android',
        'appium:automationName': 'UiAutomator2',
        'appium:deviceName': process.env.DEVICE_NAME || 'Android Emulator',
        'appium:platformVersion': process.env.PLATFORM_VERSION || '13',
        'appium:appPackage': 'com.example.nutriappmovil',
        'appium:appActivity': 'com.example.nutriappmovil.LoginActivity',
        'appium:noReset': false,
        'appium:fullReset': false,
        'appium:app': process.env.APP_PATH || undefined,
        'appium:newCommandTimeout': 300,
    }],
    logLevel: 'info',
    bail: 0,
    baseUrl: '',
    waitforTimeout: 10000,
    connectionRetryTimeout: 120000,
    connectionRetryCount: 3,
    services: [
        ['appium', {
            args: {
                address: 'localhost',
                port: 4723,
                relaxedSecurity: true,
            },
            logPath: './logs',
        }]
    ],
    framework: 'mocha',
    reporters: ['spec'],
    mochaOpts: {
        ui: 'bdd',
        timeout: 120000,
    },
};
