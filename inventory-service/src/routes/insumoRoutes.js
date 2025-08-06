const express = require('express');
const router = express.Router();
const controller = require('../controllers/insumoController');

router.get('/', controller.getAllInsumos);
router.get('/:id', controller.getInsumoById);
router.post('/', controller.createInsumo);
router.put('/:id', controller.updateInsumo);
router.delete('/:id', controller.deleteInsumo);

module.exports = router;
