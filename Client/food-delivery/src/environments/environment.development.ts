// environment.development.ts - Local development environment
export const environment = {
    production: false,
    apiUrl: '', // Use empty URL for proxy configuration
    tokenExpiryCheck: 300000, // Check token validity every 5 minutes
    allowMockData: true, // Allow mock data in development mode
    debugMode: true // Enable detailed console logging
};