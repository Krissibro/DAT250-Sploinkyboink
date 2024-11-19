# Use the official Node.js image for building and running the frontend
FROM node:18 AS frontend

# Set the working directory inside the container
WORKDIR /app

# Copy only package.json and package-lock.json initially
COPY package*.json ./

# Install all dependencies (both production and development)
RUN npm ci

# Install esbuild for linux-x64
RUN npm install esbuild

# Copy the rest of the application files after installing dependencies
COPY . .

# Expose the port Vite will run on
EXPOSE 5173

CMD ["npm", "run", "dev", "--", "--host"]
