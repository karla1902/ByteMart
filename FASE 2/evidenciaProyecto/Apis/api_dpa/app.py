from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
import json
from flask_migrate import Migrate
from flask_cors import CORS
app = Flask(__name__)
CORS(app)

# Configuración de la base de datos
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:system@localhost/api_dpa'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)
migrate = Migrate(app, db)

class Region(db.Model):
    __tablename__ = 'regiones'
    id = db.Column(db.String(10), primary_key=True)
    tipo = db.Column(db.String(50), nullable=False)
    nombre = db.Column(db.String(100), nullable=False)
    lat = db.Column(db.Float, nullable=True)
    lng = db.Column(db.Float, nullable=True)
    url = db.Column(db.String(255), nullable=True)
    codigo_padre = db.Column(db.String(10), nullable=True)

class Provincia(db.Model):
    __tablename__ = 'provincias'
    id = db.Column(db.String(10), primary_key=True)
    tipo = db.Column(db.String(50), nullable=False)
    nombre = db.Column(db.String(100), nullable=False)
    lat = db.Column(db.Float, nullable=True)
    lng = db.Column(db.Float, nullable=True)
    url = db.Column(db.String(255), nullable=True)
    region_id = db.Column(db.String(10), db.ForeignKey('regiones.id'), nullable=False)

    region = db.relationship('Region', backref='provincias')

class Comuna(db.Model):
    __tablename__ = 'comunas'
    id = db.Column(db.String(10), primary_key=True)
    tipo = db.Column(db.String(50), nullable=False)
    nombre = db.Column(db.String(100), nullable=False)
    lat = db.Column(db.Float, nullable=True)
    lng = db.Column(db.Float, nullable=True)
    url = db.Column(db.String(255), nullable=True)
    provincia_id = db.Column(db.String(10), db.ForeignKey('provincias.id'), nullable=False)

    provincia = db.relationship('Provincia', backref='comunas')

def cargar_datos_json(nombre_archivo):
    with open(nombre_archivo, encoding='utf-8') as archivo:
        return json.load(archivo)

# Cambiando a métodos POST
@app.route('/populate/regiones', methods=['POST'])
def populate_regiones():
    try:
        regiones_data = cargar_datos_json('regiones.json')
        for region in regiones_data:
            nueva_region = Region(
                id=region['codigo'],
                tipo=region['tipo'],
                nombre=region['nombre'],
                lat=region['lat'],
                lng=region['lng'],
                url=region['url'],
                codigo_padre=region['codigo_padre']
            )
            db.session.add(nueva_region)
        db.session.commit()
        return jsonify({"message": "Regiones cargadas correctamente"}), 201
    except Exception as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 500

@app.route('/populate/provincias', methods=['POST'])
def populate_provincias():
    try:
        provincias_data = cargar_datos_json('provincias.json')
        for provincia in provincias_data:
            region = Region.query.filter_by(id=provincia['codigo_padre']).first()
            if region:
                nueva_provincia = Provincia(
                    id=provincia['codigo'],
                    tipo=provincia['tipo'],
                    nombre=provincia['nombre'],
                    lat=provincia['lat'],
                    lng=provincia['lng'],
                    url=provincia['url'],
                    region_id=provincia['codigo_padre']
                )
                db.session.add(nueva_provincia)
            else:
                app.logger.error(f"Región con código {provincia['codigo_padre']} no encontrada")
        db.session.commit()
        return jsonify({"message": "Provincias cargadas correctamente"}), 201
    except Exception as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 500

@app.route('/populate/comunas', methods=['POST'])
def populate_comunas():
    try:
        comunas_data = cargar_datos_json('comunas.json')
        for comuna in comunas_data:
            provincia = Provincia.query.filter_by(id=comuna['codigo_padre']).first()
            if provincia:
                nueva_comuna = Comuna(
                    id=comuna['codigo'],
                    tipo=comuna['tipo'],
                    nombre=comuna['nombre'],
                    lat=comuna['lat'],
                    lng=comuna['lng'],
                    url=comuna['url'],
                    provincia_id=comuna['codigo_padre']
                )
                db.session.add(nueva_comuna)
            else:
                app.logger.error(f"Provincia con código {comuna['codigo_padre']} no encontrada")
        db.session.commit()
        return jsonify({"message": "Comunas cargadas correctamente"}), 201
    except Exception as e:
        db.session.rollback()
        return jsonify({"error": str(e)}), 500

@app.route('/regiones', methods=['GET'])
def get_regiones():
    regiones = Region.query.all()
    return jsonify({'regiones': [{'codigo': region.id, 'nombre': region.nombre} for region in regiones]})

@app.route('/provincias/<region_id>', methods=['GET'])
def get_provincias(region_id):
    provincias = Provincia.query.filter_by(region_id=region_id).all()
    return jsonify({'provincias': [{'codigo': provincia.id, 'nombre': provincia.nombre} for provincia in provincias]})

@app.route('/comunas/<provincia_id>', methods=['GET'])
def get_comunas(provincia_id):
    comunas = Comuna.query.filter_by(provincia_id=provincia_id).all()
    return jsonify({'comunas': [{'codigo': comuna.id, 'nombre': comuna.nombre} for comuna in comunas]})

@app.route('/datos_completos', methods=['GET'])
def get_datos_completos():
    regiones = Region.query.all()
    provincias = Provincia.query.all()
    comunas = Comuna.query.all()

    return jsonify({
        'regiones': [{'codigo': region.id, 'nombre': region.nombre} for region in regiones],
        'provincias': [{'codigo': provincia.id, 'nombre': provincia.nombre, 'region_id': provincia.region_id} for provincia in provincias],
        'comunas': [{'codigo': comuna.id, 'nombre': comuna.nombre, 'provincia_id': comuna.provincia_id} for comuna in comunas]
    })


if __name__ == '__main__':
    db.create_all()
    app.run(debug=True)
