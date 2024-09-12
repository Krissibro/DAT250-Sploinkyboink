import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';

export default defineConfig({
    plugins: [svelte()],
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080', // Your Spring Boot backend
                changeOrigin: true,
                secure: false
            }
        }
    }
});
