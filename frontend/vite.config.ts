import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vitest/config';

export default defineConfig({
	plugins: [sveltekit()],
	test: {
		include: ['src/**/*.{test,spec}.{js,ts}']
	},
	server: {
		proxy: {
			'/api': {
				target: process.env.VITE_BACKEND_URL || 'http://localhost:8080', // Default to localhost for local dev
				changeOrigin: true,
				secure: false
			}
		}
	}
});