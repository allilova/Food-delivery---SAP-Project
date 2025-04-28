FROM node:18 as build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# NOW copy the rest of the application (src/, angular.json, etc.)
COPY . .

# Create the environments directory if it doesn't exist
RUN mkdir -p src/environments

# Set the environment for production build
RUN echo "export const environment = { production: true, apiUrl: 'http://localhost:5454' };" > src/environments/environment.ts

# Build the app
RUN npm run build -- --configuration production

# Use Nginx for serving the app
FROM nginx:alpine

# Copy the build output to the nginx server
COPY --from=build /app/dist/food-delivery/browser /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.config /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 4200

# Run nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
