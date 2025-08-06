const pool = require('../db');

exports.getAllInsumos = async (req, res) => {
  const [rows] = await pool.query('SELECT * FROM insumos');
  res.json(rows);
};

exports.getInsumoById = async (req, res) => {
  const [rows] = await pool.query('SELECT * FROM insumos WHERE id = ?', [req.params.id]);
  if (rows.length === 0) return res.status(404).json({ error: 'Insumo no encontrado' });
  res.json(rows[0]);
};

exports.createInsumo = async (req, res) => {
  const { nombre_insumo, descripcion, stock, unidad } = req.body;

  if (!nombre_insumo || nombre_insumo.trim() === '') {
    return res.status(400).json({ error: 'El nombre del insumo es obligatorio' });
  }
  if (!descripcion || descripcion.trim() === '') {
    return res.status(400).json({ error: 'La descripción es obligatoria' });
  }
  if (stock == null || isNaN(stock) || stock < 0) {
    return res.status(400).json({ error: 'El stock debe ser un número positivo o cero' });
  }
  if (!unidad || unidad.trim() === '') {
    return res.status(400).json({ error: 'La unidad de medida es obligatoria' });
  }

  await pool.query(
    'INSERT INTO insumos (nombre_insumo, descripcion, stock, unidad) VALUES (?, ?, ?, ?)',
    [nombre_insumo, descripcion, stock, unidad]
  );

  res.status(201).json({ message: 'Insumo creado' });
};


exports.updateInsumo = async (req, res) => {
  const { nombre_insumo, descripcion, stock, unidad } = req.body;

  if (!nombre_insumo || nombre_insumo.trim() === '') {
    return res.status(400).json({ error: 'El nombre del insumo es obligatorio' });
  }
  if (!descripcion || descripcion.trim() === '') {
    return res.status(400).json({ error: 'La descripción es obligatoria' });
  }
  if (stock == null || isNaN(stock) || stock < 0) {
    return res.status(400).json({ error: 'El stock debe ser un número positivo o cero' });
  }
  if (!unidad || unidad.trim() === '') {
    return res.status(400).json({ error: 'La unidad de medida es obligatoria' });
  }

  await pool.query(
    'UPDATE insumos SET nombre_insumo = ?, descripcion = ?, stock = ?, unidad = ? WHERE id = ?',
    [nombre_insumo, descripcion, stock, unidad, req.params.id]
  );

  res.json({ message: 'Insumo actualizado' });
};


exports.deleteInsumo = async (req, res) => {
  await pool.query('DELETE FROM insumos WHERE id = ?', [req.params.id]);
  res.json({ message: 'Insumo eliminado' });
};
