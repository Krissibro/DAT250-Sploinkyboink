#!/bin/bash

# Run the backend in the background
echo "Starting backend (Spring Boot)"
cd backend
./gradlew bootRun &
backend_pid=$!

# Run the frontend
echo "Starting frontend (Svelte)"
cd ../frontend
npm run dev &

# Wait for both processes
wait $backend_pid

echo "Done!"