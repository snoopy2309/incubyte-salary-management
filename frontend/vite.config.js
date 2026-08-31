import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Forward API calls to the Spring Boot backend during development,
    // so the frontend can call "/api/..." with no CORS configuration.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
  test: {
    // Simulate a browser DOM so components can be rendered in tests.
    environment: 'jsdom',
    // Allow `describe`/`it`/`expect` without importing them everywhere.
    globals: true,
    // Registers jest-dom matchers (e.g. toBeInTheDocument) before each test.
    setupFiles: './src/test/setup.js',
  },
})
