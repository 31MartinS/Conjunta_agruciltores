import pymysql
import os
from dotenv import load_dotenv

load_dotenv()

connection = pymysql.connect(
    host=os.getenv("MYSQL_HOST"),
    port=int(os.getenv("MYSQL_PORT")),
    user=os.getenv("MYSQL_USER"),
    password=os.getenv("MYSQL_PASSWORD"),
    database=os.getenv("MYSQL_DB"),
    cursorclass=pymysql.cursors.DictCursor
)

def insertar_factura(cosecha_id, producto, toneladas, total, fecha):
    with connection.cursor() as cursor:
        sql = """
            INSERT INTO facturas (cosecha_id, producto, toneladas, total, fecha)
            VALUES (%s, %s, %s, %s, %s)
        """
        cursor.execute(sql, (cosecha_id, producto, toneladas, total, fecha))
        connection.commit()
