const pool = require('../db');

async function descontarStock(producto, cantidad) {
  if (!producto || typeof producto !== 'string') {
    console.error('❌ Nombre de producto inválido');
    return;
  }

  if (cantidad == null || isNaN(cantidad) || cantidad <= 0) {
    console.error('❌ Cantidad inválida para descontar');
    return;
  }

  const [rows] = await pool.query(
    'SELECT stock FROM productos WHERE nombre = ?',
    [producto]
  );

  if (rows.length === 0) {
    console.warn(`⚠️ Producto no encontrado: ${producto}`);
    return;
  }

  const stockActual = rows[0].stock;
  const nuevoStock = stockActual - cantidad;

  if (nuevoStock < 0) {
    console.warn(`⚠️ Stock insuficiente para ${producto}. Disponible: ${stockActual}, requerido: ${cantidad}`);
    return;
  }

  await pool.query(
    'UPDATE productos SET stock = ? WHERE nombre = ?',
    [nuevoStock, producto]
  );

  console.log(`✅ Stock actualizado para ${producto}: ${stockActual} → ${nuevoStock}`);
}


module.exports = {
  descontarStock,
};
