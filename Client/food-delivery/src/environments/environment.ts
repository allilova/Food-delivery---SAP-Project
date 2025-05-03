// environment.ts - Production environment
export const environment = {
    production: true,
    apiUrl: 'http://localhost:5454', // Change this to your production API URL when deploying
    tokenExpiryCheck: 600000, // Check token validity every 10 minutes
    allowMockData: false // Mock data not allowed in production
};