import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';


window.addEventListener('unhandledrejection', function(event) {
  if (event.reason && event.reason.name === 'HttpErrorResponse') {
    const error = event.reason;
    if (error.status === 0) {
      console.error('API Connectivity Error:', {
        message: 'Cannot connect to backend server. Please ensure it is running.',
        url: error.url,
        error: error
      });
    }
  }
});

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => {
    console.error('Application bootstrap error:', err);
    
    // Check if it's an API connectivity error
    if (err.name === 'HttpErrorResponse' && err.status === 0) {
      const errorElement = document.createElement('div');
      errorElement.innerHTML = `
        <div style="padding: 20px; background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; border-radius: 5px; margin: 20px;">
          <h3>Backend Server Connection Error</h3>
          <p>Cannot connect to the backend server at <strong>${err.url}</strong>.</p>
          <p>Please make sure the backend server is running at <strong>http://localhost:5454</strong>.</p>
        </div>
      `;
      document.body.appendChild(errorElement);
    }
  });