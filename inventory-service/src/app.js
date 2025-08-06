const express = require('express');
const app = express();
require('dotenv').config();
const listenToQueue = require('./rabbitmq');
const insumoRoutes = require('./routes/insumoRoutes');

app.use(express.json()); // Middleware para JSON
app.use('/insumos', insumoRoutes); // Monta las rutas

const PORT = process.env.PORT || 3001;

app.get('/', (req, res) => {
  res.send('🛒 Microservicio de Inventario funcionando');
});

app.listen(PORT, () => {
  console.log(`🚀 Inventario corriendo en http://localhost:${PORT}`);
  listenToQueue();
});
