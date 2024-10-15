from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

# Inicializa la aplicación Flask
app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+mysqlconnector://root:system@localhost/api_pago'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# Inicializa SQLAlchemy y Flask-Migrate
db = SQLAlchemy(app)
migrate = Migrate(app, db)

# Modelo de Tarjeta
class Tarjeta(db.Model):
    __tablename__ = 'tarjeta'
    id = db.Column(db.Integer, primary_key=True)
    saldo = db.Column(db.Integer, nullable=False)
    numero_tarjeta = db.Column(db.String(16), nullable=False)  # Cambiar a String
    codigo_verificacion = db.Column(db.String(3), nullable=False)  # Cambiar a String


# Modelo de Transacción
class Transaccion(db.Model):
    __tablename__ = 'transacciones'
    id = db.Column(db.Integer, primary_key=True)
    tarjeta_id = db.Column(db.Integer, db.ForeignKey('tarjeta.id'), nullable=False)
    monto = db.Column(db.Integer, nullable=False)
    fecha = db.Column(db.DateTime, default=db.func.current_timestamp())

    tarjeta = db.relationship('Tarjeta', backref='transacciones')


# Endpoint para procesar el pago
@app.route('/api/pagar', methods=['POST'])
def pagar():
    # Si usas 'request.form' en lugar de 'request.json' para formularios HTML
    tarjeta_id = request.form.get('tarjeta_id')
    total_compra = request.form.get('total_compra')

    # Resto del código permanece igual...


    # Obtener la tarjeta
    tarjeta = Tarjeta.query.get(tarjeta_id)
    if not tarjeta:
        return jsonify({'mensaje': 'Tarjeta no encontrada'}), 404

    # Verificar saldo
    if tarjeta.saldo < total_compra:
        return jsonify({'mensaje': 'Saldo insuficiente'}), 400

    # Realizar el pago
    tarjeta.saldo -= total_compra

    # Registrar la transacción
    nueva_transaccion = Transaccion(tarjeta_id=tarjeta.id, monto=total_compra)
    db.session.add(nueva_transaccion)
    
    # Guardar cambios en la base de datos
    db.session.commit()

    return jsonify({'mensaje': 'Pago realizado con éxito', 'nuevo_saldo': tarjeta.saldo}), 200

if __name__ == '__main__':
    app.run(port=5003)  # Cambia el puerto si es necesario
