# Use the official Node.js image for building and running the frontend
FROM node:18 AS frontend

# Set the working directory inside the container
WORKDIR /app

# Copy the wait-for-it script
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Copy the package.json and package-lock.json to install dependencies
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application files
COPY . .

# Expose the port Vite will run on
EXPOSE 3000

# Use wait-for-it.sh to wait for backend to be ready before starting the frontend
CMD ["/wait-for-it.sh", "backend:8080", "--timeout=60", "--", "npm", "run", "dev", "--", "--host"]
