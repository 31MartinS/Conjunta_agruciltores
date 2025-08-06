const amqp = require('amqplib');

const RABBITMQ_URL = process.env.RABBITMQ_URL || 'amqp://localhost';
const EXCHANGE = 'cosechas'; // Usamos el mismo exchange que el central
const ROUTING_KEY = 'inventario.ajustado';

async function publicarConfirmacion(cosechaId) {
  try {
    const conn = await amqp.connect(RABBITMQ_URL);
    const channel = await conn.createChannel();

    await channel.assertExchange(EXCHANGE, 'direct', { durable: true });

    const mensaje = {
      evento: 'inventario_ajustado',
      cosecha_id: cosechaId,
      status: 'OK'
    };

    channel.publish(
      EXCHANGE,
      ROUTING_KEY,
      Buffer.from(JSON.stringify(mensaje))
    );

    console.log(`📤 Confirmación enviada: ${JSON.stringify(mensaje)}`);

    setTimeout(() => {
      channel.close();
      conn.close();
    }, 500);
  } catch (err) {
    console.error('❌ Error al publicar confirmación:', err.message);
  }
}

module.exports = { publicarConfirmacion };
