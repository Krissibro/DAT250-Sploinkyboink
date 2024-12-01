# Stage 1: Build the SvelteKit app
FROM node:18 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json for dependency installation
COPY package*.json ./

# Install only production dependencies
RUN npm ci --omit=dev

# Copy the rest of the app files into the container
COPY . .

# Build the app for production
RUN npm run build

# Stage 2: Serve the production build using a lightweight server
FROM node:18-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the production build from the builder stage
COPY --from=builder /app/.svelte-kit ./build

# Install a lightweight HTTP server to serve the app
RUN npm install -g serve

# Expose the port the app will run on
EXPOSE 3000

# Serve the app in production mode
CMD ["serve", "-s", "build"]
