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


# Modelo de Tarjeta sin saldo
class Tarjeta(db.Model):
    __tablename__ = 'tarjeta'
    id = db.Column(db.Integer, primary_key=True)
    numero_tarjeta = db.Column(db.String(16), nullable=False)
    codigo_verificacion = db.Column(db.String(3), nullable=False)

# Modelo de Transacción
class Transaccion(db.Model):
    __tablename__ = 'transacciones'
    id = db.Column(db.Integer, primary_key=True)
    tarjeta_id = db.Column(db.Integer, db.ForeignKey('tarjeta.id'), nullable=False)
    monto = db.Column(db.Integer, nullable=False)
    fecha = db.Column(db.DateTime, default=db.func.current_timestamp())
    tarjeta = db.relationship('Tarjeta', backref='transacciones')


# Endpoint de pago simplificado
@app.route('/api/pagar', methods=['POST'])
def pagar():
    # Obtener los datos de la solicitud como JSON
    data = request.get_json()
    numero_tarjeta = data.get('numero_tarjeta')  
    total_compra = data.get('total_compra')

    # Validar que se recibieron los datos necesarios
    if not numero_tarjeta or not total_compra:
        return jsonify({'mensaje': 'Número de tarjeta y Total de compra son requeridos.'}), 400

    # Verificar que la tarjeta exista
    tarjeta = Tarjeta.query.filter_by(numero_tarjeta=numero_tarjeta).first()
    if not tarjeta:
        return jsonify({'mensaje': f'Tarjeta no encontrada: Número {numero_tarjeta}'}), 404

    # Registrar la transacción
    nueva_transaccion = Transaccion(tarjeta_id=tarjeta.id, monto=total_compra)
    db.session.add(nueva_transaccion)
    db.session.commit()

    return jsonify({'mensaje': 'Pago realizado con éxito'}), 200




if __name__ == '__main__':
    app.run(port=5003)
