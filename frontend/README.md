# MindCare AI Frontend

Vue 3 + Vite frontend for MindCare AI. See the [root README](../README.md) for architecture, configuration and one-command startup instructions.

```bash
npm ci
npm run dev
```

The development server proxies `/api` to `http://localhost:8080`. Set `VITE_API_BASE_URL` and `VITE_FILE_BASE_URL` only when the frontend and backend use different origins.
