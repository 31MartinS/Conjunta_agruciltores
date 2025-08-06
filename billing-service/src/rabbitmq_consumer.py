import pika
import json
import os
import requests
from dotenv import load_dotenv
from db import insertar_factura

load_dotenv()

def callback(ch, method, properties, body):
    try:
        mensaje = json.loads(body)
        print("📨 Evento recibido:", mensaje)

        cosecha_id = mensaje.get("cosecha_id")
        producto = mensaje.get("producto")
        toneladas = mensaje.get("toneladas")
        fecha = mensaje.get("timestamp")

        if cosecha_id and producto and toneladas:
            # Leer precio dinámico desde .env
            precio_unitario = float(os.getenv("PRECIO_UNITARIO", 25.0))
            total = toneladas * precio_unitario

            # Guardar en base de datos
            insertar_factura(cosecha_id, producto, toneladas, total, fecha)
            print(f"✅ Factura registrada para {producto} ({toneladas} t) = ${total}")

            # PUT al servicio central
            central_url = os.getenv("CENTRAL_URL") + f"/{cosecha_id}/estado"
            response = requests.put(central_url, json={"estado": "FACTURADA"})


            if response.status_code == 200:
                print(f"🟢 Estado actualizado en Central para {cosecha_id}")
            else:
                print(f"🟡 Error al actualizar estado en Central: {response.status_code} - {response.text}")

        else:
            print("⚠️ Datos incompletos, no se registró factura.")

    except Exception as e:
        print("❌ Error procesando mensaje:", e)

def iniciar_consumidor():
    connection = pika.BlockingConnection(pika.URLParameters(os.getenv("RABBITMQ_URL")))
    channel = connection.channel()

    channel.exchange_declare(exchange='cosechas', exchange_type='direct', durable=True)
    channel.queue_declare(queue='facturacion', durable=True)
    channel.queue_bind(exchange='cosechas', queue='facturacion', routing_key='nueva')

    print("🟢 Esperando mensajes en 'cosechas' (routing key: nueva)")
    channel.basic_consume(queue='facturacion', on_message_callback=callback, auto_ack=True)
    channel.start_consuming()
