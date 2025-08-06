from db import connection

def crear_tabla_si_no_existe():
    with connection.cursor() as cursor:
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS facturas (
                id INT AUTO_INCREMENT PRIMARY KEY,
                cosecha_id VARCHAR(255),
                producto VARCHAR(255),
                toneladas FLOAT,
                total FLOAT,
                fecha DATETIME
            )
        """)
    connection.commit()
    print("✅ Tabla 'facturas' verificada o creada.")

if __name__ == '__main__':
    crear_tabla_si_no_existe()
