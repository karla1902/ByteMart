from flask import Flask, jsonify
import mysql.connector
from config import db_config

app = Flask(__name__)

# Ruta para obtener productos destacados
@app.route('/api/productos_destacados', methods=['GET'])
def productos_destacados():
    conexion = mysql.connector.connect(**db_config)
    cursor = conexion.cursor(dictionary=True)
    query = """
        SELECT p.id, p.name, p.stock, p.descripcion, p.price, 
               (SELECT GROUP_CONCAT(i.image_url) FROM imagen i WHERE i.producto_id = p.id) as imagenes
        FROM producto p 
        WHERE p.destacado = TRUE
    """
    cursor.execute(query)
    productos = cursor.fetchall()
    
    # Convertir la cadena de imágenes a una lista
    for producto in productos:
        producto['imagenes'] = producto['imagenes'].split(',') if producto['imagenes'] else []
        
    cursor.close()
    conexion.close()

    return jsonify(productos)

