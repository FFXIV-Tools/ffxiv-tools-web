import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

const localApiProxy = {
    target: "http://localhost:8080",
    changeOrigin: true,
};

export default defineConfig({
    plugins: [
        react(),
    ],
    server: {
        port: 3000,
        proxy: {
            "/api": localApiProxy,
            "/auth": localApiProxy,
        },
    },
});
