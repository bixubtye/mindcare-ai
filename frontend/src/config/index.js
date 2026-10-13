// Vite 本地开发时文件由 8080 端口的 Spring Boot 提供；生产构建保持同源访问。
export const fileBaseURL = import.meta.env.VITE_FILE_BASE_URL
  || (import.meta.env.DEV ? 'http://localhost:8080' : '')
