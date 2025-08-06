from rabbitmq_consumer import iniciar_consumidor
from init_db import crear_tabla_si_no_existe

if __name__ == '__main__':
    crear_tabla_si_no_existe()
    iniciar_consumidor()
