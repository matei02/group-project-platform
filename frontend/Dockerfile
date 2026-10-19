# Build stage
FROM node:18.19-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package files
COPY package.json package-lock.json ./

# Clean install dependencies
RUN npm ci --legacy-peer-deps

# Copy project files
COPY . .

# Build the application
RUN npm run build -- --configuration production

# Production stage
FROM nginx:alpine

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built assets
COPY --from=builder /app/dist/groupproject_frontend/browser /usr/share/nginx/html

# Make port 8080 available (Azure requirement)
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
