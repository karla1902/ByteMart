from flask import Flask, jsonify
from flask_sqlalchemy import SQLAlchemy
import json
from flask_migrate import Migrate
from flask_cors import CORS
from sqlalchemy import text
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

# Función para cargar datos JSON
def cargar_datos_json(nombre_archivo):
    with open(nombre_archivo, encoding='utf-8') as archivo:
        return json.load(archivo)

# Función para cargar las regiones
def cargar_regiones():
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
        app.logger.info('Regiones cargadas correctamente.')
    except Exception as e:
        db.session.rollback()
        app.logger.error(f"Error al cargar regiones: {e}")

# Función para cargar las provincias
def cargar_provincias():
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
        app.logger.info('Provincias cargadas correctamente.')
    except Exception as e:
        db.session.rollback()
        app.logger.error(f"Error al cargar provincias: {e}")

# Función para cargar las comunas
def cargar_comunas():
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
        app.logger.info('Comunas cargadas correctamente.')
    except Exception as e:
        db.session.rollback()
        app.logger.error(f"Error al cargar comunas: {e}")


@app.route('/test', methods=['GET'])
def test_connection():
    try:
        db.session.execute(text('SELECT 1'))
        return 'Conexión exitosa a la base de datos', 200
    except Exception as e:
        return f'Error en la conexión a la base de datos: {e}', 500

# Rutas GET
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
    with app.app_context():
        db.create_all()
        cargar_regiones()   # Cargar las regiones
        cargar_provincias() # Cargar las provincias
        cargar_comunas()    # Cargar las comunas
    app.run(debug=True)
