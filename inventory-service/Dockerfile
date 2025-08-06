# Etapa de construcción (opcional si no usas herramientas como TypeScript)
FROM node:20-alpine

# Crear directorio de trabajo
WORKDIR /app

# Copiar dependencias
COPY package*.json ./

# Instalar dependencias
RUN npm install

# Copiar el resto del código
COPY . .

# Exponer el puerto
EXPOSE 3001

# Comando por defecto
CMD ["node", "src/app.js"]
