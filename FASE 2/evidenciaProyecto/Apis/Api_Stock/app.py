from flask import Flask, jsonify
import mysql.connector
from flask_mail import Mail, Message
from config import db_config, MAIL_SERVER, MAIL_PORT, MAIL_USE_SSL, MAIL_USERNAME, MAIL_PASSWORD, MAIL_DEFAULT_SENDER

app = Flask(__name__)

# Configurar Flask-Mail
app.config['MAIL_SERVER'] = MAIL_SERVER
app.config['MAIL_PORT'] = MAIL_PORT
app.config['MAIL_USE_SSL'] = MAIL_USE_SSL
app.config['MAIL_USERNAME'] = MAIL_USERNAME
app.config['MAIL_PASSWORD'] = MAIL_PASSWORD
app.config['MAIL_DEFAULT_SENDER'] = MAIL_DEFAULT_SENDER

mail = Mail(app)

# Ruta para productos con stock bajo
@app.route('/api/stock_bajo', methods=['GET'])
def productos_con_stock_bajo():
    stock_minimo = 2  # Definir stock mínimo aquí en Python
    conexion = mysql.connector.connect(**db_config)
    cursor = conexion.cursor(dictionary=True)
    query = "SELECT id, name, stock, marca FROM producto"
    cursor.execute(query)
    productos = cursor.fetchall()
    cursor.close()
    conexion.close()

    # Filtrar productos con stock menor que stock_minimo
    productos_bajo_stock = [p for p in productos if p['stock'] < stock_minimo]
    
    # Si hay productos con stock bajo, enviar notificación
    if productos_bajo_stock:
        enviar_correo_stock_bajo(productos_bajo_stock)
    
    return jsonify(productos_bajo_stock)

def enviar_correo_stock_bajo(productos):
    with app.app_context():
        # Preparar el contenido del correo
        lista_productos = "".join(
            [f"<li>{p['name']} - Marca: {p['marca']} - Stock: {p['stock']}</li>" for p in productos]
        )
        
        asunto = "Alerta!, Hay pocas existencias de stock!"
        cuerpo = f"""
        <html>
            <head>
                <style>
                    body {{
                        font-family: Arial, sans-serif;
                        color: #333;
                    }}
                    h2 {{
                        color: #4CAF50;  /* Color verde */
                    }}
                    ul {{
                        list-style-type: none;  /* Quita los puntos de la lista */
                    }}
                    li {{
                        background: #f4f4f4;  /* Color de fondo */
                        padding: 10px;
                        margin-bottom: 5px;
                        border-radius: 5px;
                    }}
                    .ByteMart{{
                    color: #ffffff;
                    padding: 10px;
                    font-weight: 700;
                    margin: 0 0 10px;
                    }}
                </style>
            </head>
            <body>
                <h2>Notificación de Stock Bajo</h2>
                <p>Los siguientes productos se encuentran con un stock bajo:</p>
                <ul>
                    {lista_productos}
                </ul>
                <p>Por favor, toma las medidas necesarias.</p>
                <footer style="display: flex; flex-direction: column; align-items: center; margin: 10px 0;">
                    <div style="display: flex; align-items: center;">
                        <h1 class="ByteMart" style="margin: 0; padding: 0; font-size: 24px;">Bytemart</h1>
                        <img src="http://127.0.0.1:5000/static/img/shopping-basket.png" alt="Logotipo" style="width: 64px; margin-left: 10px;">
                    </div>
                    <p style="margin: 5px 0; color: white; font-size: 12px; text-align: center;">
                        Este mensaje es generado automáticamente, favor no responder.
                    </p>
                </footer>
            </body>
        </html>
        """
        
        # Crear mensaje en HTML
        msg = Message(asunto, recipients=["soyite4274@skrank.com"], html=cuerpo)
        
        # Enviar correo
        mail.send(msg)


