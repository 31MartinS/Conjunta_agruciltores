const amqp = require('amqplib');
const { descontarStock } = require('./controllers/inventoryController');
const { publicarConfirmacion } = require('./services/publisher');

const RABBITMQ_URL = process.env.RABBITMQ_URL || 'amqp://localhost';
const EXCHANGE = 'cosechas';
const ROUTING_KEY = 'nueva';
const QUEUE = 'cola_inventario';

async function listenToQueue() {
  try {
    const conn = await amqp.connect(RABBITMQ_URL);
    const channel = await conn.createChannel();

    await channel.assertExchange(EXCHANGE, 'direct', { durable: true });
    await channel.assertQueue(QUEUE, { durable: true });
    await channel.bindQueue(QUEUE, EXCHANGE, ROUTING_KEY);

    console.log(`📥 Esperando mensajes en la cola '${QUEUE}'...`);

    channel.consume(QUEUE, async (msg) => {
      if (msg !== null) {
        try {
          const message = JSON.parse(msg.content.toString());
          console.log('📨 Mensaje recibido:', message);

          const { producto, toneladas, requiere_insumos, cosecha_id } = message;

          if (!producto || !toneladas || !cosecha_id) {
            console.warn('❌ Mensaje incompleto o inválido:', message);
            channel.ack(msg);
            return;
          }

          // Si no se especifican insumos, usar por defecto para prueba
          const insumos = Array.isArray(requiere_insumos)
            ? requiere_insumos
            : ['Semilla Maíz H-519', 'Fertilizante N-PK'];

          const cantidades = {
            semilla: toneladas * 5,
            fertilizante: toneladas * 2,
          };

          for (const insumo of insumos) {
            const tipo = insumo.toLowerCase().includes('semilla') ? 'semilla' : 'fertilizante';
            const cantidad = cantidades[tipo];

            await descontarStock(insumo, cantidad);
          }

          // ✅ Publicar confirmación de ajuste de inventario
          await publicarConfirmacion(cosecha_id);

          console.log(`✅ Inventario actualizado para ${producto}`);
          channel.ack(msg);
        } catch (err) {
          console.error('❌ Error al procesar el mensaje:', err.message);
          channel.nack(msg, false, false); // descarta el mensaje
        }
      }
    });
  } catch (err) {
    console.error('❌ Error al conectar a RabbitMQ:', err.message);
  }
}

module.exports = listenToQueue;
