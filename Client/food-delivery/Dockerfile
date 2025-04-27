FROM node:18

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy project files
COPY . .

# Expose port
EXPOSE 4200

# Start the app
CMD ["npm", "start", "--", "--host", "0.0.0.0", "--disable-host-check"]