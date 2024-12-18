from flask import Flask, abort, render_template, request, jsonify, redirect, url_for, session, flash, g
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
import logging
from sqlalchemy import Sequence
import os
from werkzeug.utils import secure_filename
from sqlalchemy.orm import joinedload
from sqlalchemy import or_
from flask_mail import Mail, Message
import logging, random, string
from datetime import datetime, timedelta
from sqlalchemy import Sequence
import requests
from flask_cors import CORS
from sqlalchemy import text
import locale
from functools import wraps
from werkzeug.security import generate_password_hash, check_password_hash
import bcrypt

app = Flask(__name__)
CORS(app)
# csrf = CSRFProtect(app)
app.config['SECRET_KEY'] = 'mi_clave_secreta'  # Necesario para las sesiones
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+mysqlconnector://root:system@localhost/proyecto'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False


UPLOAD_FOLDER = 'static/uploads/'
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

# Envio Correo // Configuración de Flask-Mail
app.config['MAIL_SERVER'] = 'smtp.gmail.com' 
app.config['MAIL_PORT'] = 587  # Generalmente 587 para TLS
app.config['MAIL_USE_TLS'] = True
app.config['MAIL_USE_SSL'] = False
app.config['MAIL_USERNAME'] = 'kar.v.prueba@gmail.com' 
app.config['MAIL_PASSWORD'] = 'dgnh jefs dgxu dqwx'
app.config['MAIL_DEFAULT_SENDER'] = 'kar.v.prueba@gmail.com' 
app.config['SESSION_COOKIE_SECURE'] = True  # Solo permite cookies a través de HTTPS
app.config['SESSION_COOKIE_HTTPONLY'] = True  # Bloquea acceso a cookies desde JavaScript
app.config['SESSION_COOKIE_SAMESITE'] = 'Strict'  # Previene el uso de cookies en requests de otros sitios


# Inicializar SQLAlchemy y Bcrypt
mail = Mail(app)
db = SQLAlchemy(app)
migrate = Migrate(app, db)


# Configuración del registro
logging.basicConfig(level=logging.INFO)

# Definir secuencia
user_sequence = Sequence('user_id_seq', start=1, increment=1)
product_sequence = Sequence('product_id_seq', start=1, increment=1)
category_sequence = Sequence('category_id_seq', start=1, increment=1)

from werkzeug.security import generate_password_hash

def create_default_admin():
    with app.app_context():
        admin_rol = Rol.query.filter_by(nombre='Administrador').first()
        if not admin_rol:
            admin_rol = Rol(nombre='Administrador')
            db.session.add(admin_rol)
            db.session.commit()

        admin_user = Usuario.query.filter_by(username='admin').first()
        if not admin_user:
            # Crear un hash de contraseña con bcrypt
            hashed_password = bcrypt.hashpw('admin1234'.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

            admin_user = Usuario(
                username='admin',
                password=hashed_password,
                nombre='Admin',
                apellido='Administrador',
                email='email@email.com',
                direccion='Dirección de administrador',
                is_admin=True
            )

            admin_user.roles = [admin_rol]
            db.session.add(admin_user)
            db.session.commit()
            app.logger.info('Usuario administrador creado con éxito.')


def estados_orden():
    """
    Crea los estados de orden predeterminados si no existen.
    """
    with app.app_context():  # Asegúrate de usar el contexto de la aplicación
        estados = ['Pendiente', 'Pagado', 'En Proceso', 'Enviado', 'Entregado', 'Cancelado']

        for estado in estados:
            # Verificar si el estado ya existe en la base de datos
            estado_existente = EstadoOrden.query.filter_by(nombre=estado).first()
            if not estado_existente:
                # Si no existe, lo creamos
                nuevo_estado = EstadoOrden(nombre=estado)
                db.session.add(nuevo_estado)

        # Confirmar los cambios en la base de datos
        db.session.commit()
        app.logger.info('Estados de orden creados o ya existentes.')


def test_connection():
    try:
        with app.app_context():
            # Intenta ejecutar una consulta simple para verificar la conexión
            db.session.execute(text('SELECT 1'))
            app.logger.info('Conexión a la base de datos exitosa!')
    except Exception as e:
        app.logger.error(f'Error en la conexión a la base de datos: {e}')


# Establecer la configuración regional para la moneda
locale.setlocale(locale.LC_ALL, 'es_ES.UTF-8')  # Cambia según tus necesidades

# Función para formatear el saldo
def formatear_saldo(saldo):
    return f"${saldo:,.0f}".replace(',', '.').replace('$', '$ ')


# Modelo de Usuario
class Usuario(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(50), unique=True, nullable=False)
    password = db.Column(db.String(200), nullable=False)
    nombre = db.Column(db.String(100), nullable=False)
    apellido = db.Column(db.String(100), nullable=False)
    email = db.Column(db.String(120), nullable=False)
    direccion = db.Column(db.String(100), nullable=True)
    reset_code = db.Column(db.String(6), nullable=True)
    reset_code_expiration = db.Column(db.DateTime, nullable=True)
    is_admin = db.Column(db.Boolean, default=False)

    roles = db.relationship('Rol', secondary='usuario_rol', backref='usuarios')
    tarjetas = db.relationship('Tarjeta', backref='usuario', lazy=True)
    carrito = db.relationship('Carrito', backref='usuario_carrito', lazy=True, uselist=False)
    ordenes = db.relationship('Orden', backref='usuario_orden', lazy=True)

    def __repr__(self):
        return f'<Usuario {self.username}>'

    def set_reset_code(self, code):
        self.reset_code = code
        self.reset_code_expiration = datetime.utcnow() + timedelta(hours=1)




class Categoria(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(50), unique=True, nullable=False)
    fecha_creacion = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)

    def __repr__(self):
        return f'<Categoria {self.name}>'

        
class Producto(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(155), unique=True, nullable=False)
    price = db.Column(db.Integer, nullable=False)
    marca_id = db.Column(db.Integer, db.ForeignKey('marca.id'), nullable=False)  # Cambiado a referencia de marca
    descripcion = db.Column(db.String(1000), nullable=False)
    stock = db.Column(db.Integer, nullable=False)
    category_id = db.Column(db.Integer, db.ForeignKey('categoria.id'), nullable=False)
    en_oferta = db.Column(db.Boolean, default=False)
    destacado = db.Column(db.Boolean, default=False)
    fecha_creacion = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)

    categoria = db.relationship('Categoria', backref=db.backref('productos', lazy=True))
    imagenes = db.relationship('Imagen', backref='producto', lazy=True, cascade="all, delete-orphan")

    def __repr__(self):
        return f'<Producto {self.name}>'




# Modelo de Imagen
class Imagen(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    image_url = db.Column(db.String(255), nullable=False)
    producto_id = db.Column(db.Integer, db.ForeignKey('producto.id'), nullable=False)

    def __repr__(self):
        return f'<Imagen {self.image_url}>'

# Modelo de Marcas
class Marca(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), unique=True, nullable=False)

    productos = db.relationship('Producto', backref='marca', lazy=True)

    def __repr__(self):
        return f'<Marca {self.name}>'



# Modelo de Carrito
class Carrito(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    usuario_id = db.Column(db.Integer, db.ForeignKey('usuario.id'), nullable=False)

    def __repr__(self):
        return f'<Carrito {self.id}>'

# Modelo de ItemCarrito
class ItemCarrito(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    carrito_id = db.Column(db.Integer, db.ForeignKey('carrito.id'), nullable=False)
    producto_id = db.Column(db.Integer, db.ForeignKey('producto.id'), nullable=False)
    cantidad = db.Column(db.Integer, nullable=False, default=1)
    
    carrito = db.relationship('Carrito', backref=db.backref('items', lazy=True))
    producto = db.relationship('Producto', backref='item_carrito', lazy=True)


# Modelo de Tarjeta ajustado
class Tarjeta(db.Model):
    __tablename__ = 'tarjetas'
    __table_args__ = (db.UniqueConstraint('numero_tarjeta', 'usuario_id', name='unique_tarjeta_usuario'),)
    
    id = db.Column(db.Integer, primary_key=True)
    usuario_id = db.Column(db.Integer, db.ForeignKey('usuario.id'), nullable=False)
    numero_tarjeta = db.Column(db.String(16), nullable=False)
    mes_vencimiento = db.Column(db.Integer, nullable=False)
    anio_vencimiento = db.Column(db.Integer, nullable=False)
    codigo_verificacion = db.Column(db.String(3), nullable=False)
    saldo = db.Column(db.Integer, nullable=True)

    proceso_pagos = db.relationship('ProcesoPago', backref='tarjeta_rel', lazy=True)

    def __repr__(self):
        return f'<Tarjeta {self.numero_tarjeta}, Saldo: {self.saldo}, Vencimiento: {self.mes_vencimiento}/{self.anio_vencimiento}>'



# Modelo de Rol
class Rol(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(50), unique=True, nullable=False)

    def __repr__(self):
        return f'<Rol {self.nombre}>'


# Tabla intermedia para la relación muchos a muchos entre Usuario y Rol
class Usuario_Rol(db.Model):
    __tablename__ = 'usuario_rol'
    id = db.Column(db.Integer, primary_key=True)
    usuario_id = db.Column(db.Integer, db.ForeignKey('usuario.id'), nullable=False)
    rol_id = db.Column(db.Integer, db.ForeignKey('rol.id'), nullable=False)

    def __repr__(self):
        return f'<Usuario_Rol usuario_id={self.usuario_id}, rol_id={self.rol_id}>'



class OrdenItem(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    orden_id = db.Column(db.Integer, db.ForeignKey('orden.id'), nullable=False)
    producto_id = db.Column(db.Integer, db.ForeignKey('producto.id'), nullable=False)
    cantidad = db.Column(db.Integer, nullable=False)
    fecha_creacion = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)

    orden = db.relationship('Orden', backref='items', lazy=True)
    producto = db.relationship('Producto', backref='orden_items', lazy=True)

    def __repr__(self):
        return f'<OrdenItem producto_id={self.producto_id}, cantidad={self.cantidad}, fecha_creacion={self.fecha_creacion}>'


class EstadoOrden(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(50), unique=True, nullable=False)

    def __repr__(self):
        return f'<EstadoOrden {self.nombre}>'


class Direccion(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    
    # Relación con Usuario
    usuario_id = db.Column(db.Integer, db.ForeignKey('usuario.id'), nullable=False)
    
    # Campos del modelo
    nombre = db.Column(db.String(100), nullable=False)  # Nombre del destinatario o usuario
    numero_domicilio = db.Column(db.String(10), nullable=False)  # Número de la vivienda
    
    # Campo para almacenar el ID de comuna
    comuna_id = db.Column(db.Integer, nullable=False)  # Solo almacenamos el ID, sin relación directa
    
    # Relación con Usuario
    usuario = db.relationship('Usuario', backref='direcciones', lazy=True)
    
    def __repr__(self):
        return f'<Direccion {self.nombre}, Número: {self.numero_domicilio}>'
    
    
class Orden(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    usuario_id = db.Column(db.Integer, db.ForeignKey('usuario.id'), nullable=False)
    estado_id = db.Column(db.Integer, db.ForeignKey('estado_orden.id'), nullable=False)
    fecha_creacion = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)

    usuario = db.relationship('Usuario', backref='ordenes_usuario', lazy=True)
    estado = db.relationship('EstadoOrden', backref='ordenes', lazy=True)
    
    # Relación con la factura (una factura por orden)
    factura = db.relationship('Factura', backref='orden_factura', uselist=False)  # Cambiar el backref aquí

    def __repr__(self):
        return f'<Orden id={self.id}, usuario_id={self.usuario_id}>'



# Modelo de Factura actualizado
class Factura(db.Model):
    __tablename__ = 'factura'
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    orden_id = db.Column(db.Integer, db.ForeignKey('orden.id'), nullable=False)
    monto = db.Column(db.Integer, nullable=False)
    fecha_creacion = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)

    orden = db.relationship('Orden', backref='factura_inversa', lazy=True)  # Aquí también puedes cambiar el backref

    def __repr__(self):
        return f'<Factura id={self.id}, orden_id={self.orden_id}>'

class ProcesoPago(db.Model):
    __tablename__ = 'proceso_pago'
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    tarjeta_id = db.Column(db.Integer, db.ForeignKey('tarjetas.id'), nullable=False)
    fecha_pago = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)
    monto = db.Column(db.Integer, nullable=False)

    def __repr__(self):
        return f'<ProcesoPago tarjeta_id={self.tarjeta_id}, monto={self.monto}>'

@app.before_request

def calcular_subtotal():
    carrito = session.get('carrito', [])
    # Sumar los precios como enteros sin usar replace
    subtotal = sum([producto['precio'] for producto in carrito])  # Asegúrate de que 'precio' sea un entero

    # Almacena el subtotal y el carrito en el contexto de la plantilla
    app.jinja_env.globals['subtotal'] = subtotal
    app.jinja_env.globals['carrito'] = carrito  # También almacena el carrito en el contexto global

def crear_roles():
    # Verifica si los roles ya existen para no duplicarlos
    roles = ['Administrador', 'Jefe de tienda', 'Vendedor', 'Cliente']
    for rol_nombre in roles:
        if not Rol.query.filter_by(nombre=rol_nombre).first():
            rol = Rol(nombre=rol_nombre)
            db.session.add(rol)
    db.session.commit()

def roles_required(*roles):
    def decorator(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
            # Verificar si el usuario está en sesión
            if 'user_id' not in session:
                flash('Por favor, inicia sesión primero.', 'warning')
                return redirect(url_for('login'))
            
            # Obtener el usuario de la base de datos
            user = Usuario.query.get(session['user_id'])
            if not user:
                flash('Usuario no encontrado', 'danger')
                return redirect(url_for('login'))
            
            # Comprobar si tiene al menos uno de los roles requeridos
            user_roles = {rol.nombre for rol in user.roles}  # Convertir a set para optimizar la comparación
            if not any(role in user_roles for role in roles):
                flash('No tienes permiso para acceder a esta página', 'danger')
                return redirect(url_for('index'))
            
            return f(*args, **kwargs)
        return decorated_function
    return decorator

@app.route('/access_denied')
def access_denied():
    return render_template('access_denied.html'), 403



@app.route('/')
def index():
    categorias = Categoria.query.all()
    productos = Producto.query.all()  # Obtén todos los productos de la base de datos
    # lógica de la vista
    return render_template('index.html',categorias=categorias,productos=productos)



@app.route('/login', methods=['GET', 'POST'])
def login():
    categorias = Categoria.query.all()
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        
        # Buscar al usuario por nombre de usuario
        user = Usuario.query.filter_by(username=username).first()
        
        # Verificar la contraseña usando bcrypt
        if user and bcrypt.checkpw(password.encode('utf-8'), user.password.encode('utf-8')):
            session['user_id'] = user.id
            
            # Crear un carrito si no existe
            if not Carrito.query.filter_by(usuario_id=user.id).first():
                nuevo_carrito = Carrito(usuario_id=user.id)
                db.session.add(nuevo_carrito)
                db.session.commit()
            
            flash('Login exitoso', 'success')
            return redirect(url_for('index'))
        else:
            flash('Usuario o contraseña incorrectos', 'danger')

    return render_template('login.html', categorias=categorias)





@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        confirm_password = request.form['confirm-password']  # Obtener la confirmación de la contraseña
        nombre = request.form['nombre']  # Obtener nombre
        apellido = request.form['apellido']  # Obtener apellido
        email = request.form['email']  # Obtener email
        direccion = request.form.get('direccion')  # Obtener dirección (opcional)

        # Verificar si el nombre de usuario ya existe
        if Usuario.query.filter_by(username=username).first():
            flash('El nombre de usuario ya existe', 'warning')
            return render_template('login.html')

        # Verificar que las contraseñas coincidan
        if password != confirm_password:
            flash('Las contraseñas no coinciden', 'danger')
            return render_template('login.html')

        try:
            # Crear un hash de contraseña con bcrypt
            hashed_password = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

            # Crear el nuevo usuario con todos los campos
            new_user = Usuario(
                username=username, 
                password=hashed_password, 
                nombre=nombre, 
                apellido=apellido, 
                email=email, 
                direccion=direccion, 
                reset_code=None,  # Si tu base de datos tiene estos campos, puedes inicializarlos aquí
                reset_code_expiration=None, 
                is_admin=0  # Suponiendo que el valor por defecto sea 0
            )
            db.session.add(new_user)
            db.session.commit()

            flash('Usuario registrado exitosamente', 'success')
            return redirect(url_for('login'))

        except Exception as e:
            db.session.rollback()
            flash(f'Error al registrar usuario: {str(e)}', 'danger')
            return render_template('login.html')

    return render_template('login.html')








# Restablecer Contraseña
def generate_reset_code(length=6):
    return ''.join(random.choices(string.ascii_uppercase + string.digits, k=length))

def send_reset_email(recipient_email, reset_code):
    try:
        msg = Message('Código de Restablecimiento de Contraseña',
                      sender=app.config['MAIL_USERNAME'],
                      recipients=[recipient_email])
        msg.body = f'Tu código de restablecimiento de contraseña es: {reset_code}'
        mail.send(msg)
        return True
    except Exception as e:
        print(f'Error al enviar correo: {e}')
        return False

@app.route('/sendResetCode', methods=['POST'])
def send_reset_code():
    data = request.get_json()
    user_email = data.get('email')

    if not user_email:
        return jsonify({'success': False, 'message': 'El correo electrónico es obligatorio'}), 400
    
    user = Usuario.query.filter_by(email=user_email).first()
    if not user:
        return jsonify({'success': False, 'message': 'El usuario no existe'}), 400

    # Generar un código de verificación aleatorio
    reset_code = generate_reset_code()
    expiration_time = datetime.now() + timedelta(hours=1)  # Código válido por 1 hora

    user.reset_code = reset_code
    user.reset_code_expiration = expiration_time
    db.session.commit()

    # Enviar el correo con el código de restablecimiento
    if send_reset_email(user_email, reset_code):
        return jsonify({'success': True, 'message': 'Código enviado con éxito'})
    else:
        return jsonify({'success': False, 'message': 'Error al enviar el código'}), 500


@app.route('/resetPassword', methods=['POST'])
def reset_password():
    data = request.get_json()
    reset_code = data.get('resetCode')
    new_password = data.get('newPassword')
    confirm_password = data.get('confirmPassword')
    
    if new_password != confirm_password:
        return jsonify({'success': False, 'message': 'Las contraseñas no coinciden'}), 400
    
    # Buscar el usuario con el código de restablecimiento
    user = Usuario.query.filter_by(reset_code=reset_code).first()

    if not user:
        return jsonify({'success': False, 'message': 'Código de restablecimiento inválido'}), 400

    # Verificar si el código ha expirado
    if user.reset_code_expiration < datetime.now():
        return jsonify({'success': False, 'message': 'El código ha expirado'}), 400

    # Actualizar la contraseña del usuario
    hashed_password = bcrypt.generate_password_hash(new_password).decode('utf-8')
    user.password = hashed_password
    user.reset_code = None  # Limpiar el código de restablecimiento después de usarlo
    user.reset_code_expiration = None  # Limpiar la expiración del código

    db.session.commit()

    return jsonify({'success': True, 'message': 'Contraseña restablecida con éxito'})

@app.route('/resetPassword', methods=['GET'])
def reset_password_get():
    return render_template('resetPassword.html')

@app.route('/profile', methods=['GET', 'POST'])
def profile():
    categorias = Categoria.query.all()
    if 'user_id' not in session:
        return redirect(url_for('login'))  # Redirigir si el usuario no está logueado

    user = Usuario.query.get(session['user_id'])  # Obtener el usuario actual desde la sesión
    tarjetas = Tarjeta.query.filter_by(usuario_id=user.id).all()  # Obtener las tarjetas del usuario

    if request.method == 'POST':
        # Actualizar los campos desde el formulario
        user.email = request.form['email']
        user.username = request.form['username']
        user.nombre = request.form['nombre']  # Nuevo campo
        user.apellido = request.form['apellido']  # Nuevo campo
        user.direccion = request.form['direccion']  # Nuevo campo
        db.session.commit()  # Guardar los cambios en la base de datos
        flash('Datos actualizados correctamente', 'success')
        return redirect(url_for('profile'))

    return render_template('profile.html', user=user, tarjetas=tarjetas,categorias = categorias)  # Pasa las tarjetas a la plantilla



@app.route('/logout')
def logout():
    session.pop('user_id', None)
    flash('Has salido de la sesión', 'info')
    return redirect(url_for('index'))

@app.route('/product')
def product():
    return render_template('product.html')

@app.route('/admin/configuracion')
def configuracion():
    categorias = Categoria.query.all()
    return render_template('configuracion.html', categorias = categorias)

# Ruta para administrar usuarios
@app.route('/admin/usuarios', methods=['GET', 'POST'])
@roles_required('Administrador')
def gestionar_usuarios():
    if 'user_id' not in session or not Usuario.query.get(session['user_id']).is_admin:
        flash('Acceso denegado. No tienes permisos para acceder a esta página.', 'danger')
        return redirect(url_for('login'))

    roles = Rol.query.all()  
    app.logger.info("ROLES: %s", roles)  # Imprimir roles para depuración
    
    if not roles:
        flash('No hay roles disponibles en la base de datos.', 'error')

    # Inicializar la variable usuario como None
    usuario = None  

    if request.method == 'POST':
        # Lógica para agregar o actualizar usuarios
        usuario_id = request.form.get('id')
        usuario = Usuario.query.get(usuario_id) if usuario_id else Usuario()

        # Asignación de campos del formulario a la instancia de usuario
        usuario.nombre = request.form['nombre']
        usuario.apellido = request.form['apellido']
        usuario.username = request.form['username']
        usuario.email = request.form['email']
        usuario.direccion = request.form['direccion']

        if request.form['password']:
            usuario.password = request.form['password']

        # Manejo de roles
        rol_id = request.form['rol_id']
        if rol_id:
            rol = Rol.query.get(rol_id)
            if rol:
                usuario.roles.clear()  # Limpiar roles anteriores
                usuario.roles.append(rol)  # Asigna el nuevo rol
            else:
                flash('Rol no encontrado', 'error')
                return redirect(url_for('gestionar_usuarios'))
        else:
            flash('No se seleccionó un rol', 'error')
            return redirect(url_for('gestionar_usuarios'))

        db.session.add(usuario)
        db.session.commit()

        flash('Usuario {} con éxito'.format('actualizado' if usuario_id else 'creado'), 'success')
        return redirect(url_for('gestionar_usuarios'))

    # Filtrar usuarios por rol si se proporciona un filtro
    filtro_rol = request.args.get('filtro_rol')
    usuarios = Usuario.query.filter(Usuario.roles.any(id=filtro_rol)).all() if filtro_rol else Usuario.query.all()

    # Si se está editando un usuario, cargar el usuario correspondiente
    usuario_id = request.args.get('id')
    if usuario_id:
        usuario = Usuario.query.get(usuario_id)

    # Renderizar la plantilla, asegurando que la variable usuario siempre tenga un valor
    return render_template('admin_usuarios.html', usuarios=usuarios, roles=roles, usuario=usuario)


@app.route('/eliminar_usuario/<int:id>', methods=['POST'])
def eliminar_usuario(id):
    # Obtener el usuario por su ID
    usuario = Usuario.query.get(id)
    if usuario:
        # Primero, eliminar las tarjetas asociadas
        if usuario.tarjetas:  # Verifica que existan tarjetas
            for tarjeta in usuario.tarjetas:
                db.session.delete(tarjeta)  # Eliminar cada tarjeta

        # Luego, eliminar el carrito asociado
        if usuario.carrito:  # Verifica que el carrito exista
            # Eliminar todos los ítems en el carrito
            for item in usuario.carrito.items:
                db.session.delete(item)  # Eliminar cada ítem del carrito

            db.session.delete(usuario.carrito)  # Luego eliminar el carrito

        # Ahora eliminar el usuario
        db.session.delete(usuario)
        db.session.commit()
        flash('Usuario eliminado con éxito.', 'success')
    else:
        flash('Usuario no encontrado.', 'error')

    return redirect(url_for('gestionar_usuarios'))



#Administración de categorias
@app.route('/admin/categorias', methods=['GET', 'POST'])
def gestionar_categorias():
    if request.method == 'POST':
        if 'name' in request.form:  # Crear categoría
            nombre_categoria = request.form['name']
            nueva_categoria = Categoria(name=nombre_categoria)
            db.session.add(nueva_categoria)
            db.session.commit()
            flash('Categoría creada con éxito', 'success')
            return redirect(url_for('gestionar_categorias'))

        # Lógica para actualización de categoría
        elif 'id' in request.form:  # Actualizar categoría
            categoria_id = request.form['id']
            categoria = Categoria.query.get(categoria_id)
            categoria.name = request.form['name']
            db.session.commit()
            flash('Categoría actualizada con éxito', 'success')
            return redirect(url_for('gestionar_categorias'))

    categorias = Categoria.query.all()
    return render_template('admin_categorias.html', categorias=categorias)

@app.route('/admin/categorias/<int:id>', methods=['POST'])
def eliminar_categoria(id):
    categoria = Categoria.query.get(id)
    if categoria:
        db.session.delete(categoria)
        db.session.commit()
        flash('Categoría eliminada con éxito', 'success')
    return redirect(url_for('gestionar_categorias'))

@app.route('/admin/categorias/<int:id>/update', methods=['POST'])
def actualizar_categoria(id):
    categoria = Categoria.query.get(id)
    if categoria:
        categoria.name = request.form['name']
        db.session.commit()
        flash('Categoría actualizada con éxito', 'success')
    return redirect(url_for('gestionar_categorias'))

@app.route('/categorias', methods=['GET'])
def mostrar_categorias():
    categorias = Categoria.query.all()
    return render_template('categorias.html', categorias=categorias)

@app.route('/categorias/<categoria_name>', methods=['GET'])
def categoria_producto(categoria_name):
    # Obtener la categoría por nombre
    categorias = Categoria.query.all()
    categoria = Categoria.query.filter_by(name=categoria_name).first()
    if not categoria:
        abort(404)  # Maneja el caso donde la categoría no existe
    
    # Filtrar los productos por la instancia de categoría
    productos = Producto.query.filter_by(categoria=categoria).all()
    return render_template('resultados_busqueda.html', categoria=categoria, productos=productos,categorias=categorias)

@app.route('/admin/marcas', methods=['GET', 'POST'])
def gestionar_marcas():
    if request.method == 'POST':
        if 'name' in request.form:  # Crear marca
            nombre_marca = request.form['name']
            nueva_marca = Marca(name=nombre_marca)
            db.session.add(nueva_marca)
            db.session.commit()
            flash('Marca creada con éxito', 'success')
            return redirect(url_for('gestionar_marcas'))

        # Lógica para actualización de marca
        elif 'id' in request.form:  # Actualizar marca
            marca_id = request.form['id']
            marca = Marca.query.get(marca_id)
            marca.name = request.form['name']
            db.session.commit()
            flash('Marca actualizada con éxito', 'success')
            return redirect(url_for('gestionar_marcas'))

    marcas = Marca.query.all()
    return render_template('admin_marcas.html', marcas=marcas)

@app.route('/admin/marcas/<int:id>', methods=['POST'])
def eliminar_marca(id):
    marca = Marca.query.get(id)
    if marca:
        db.session.delete(marca)
        db.session.commit()
        flash('Marca eliminada con éxito', 'success')
    return redirect(url_for('gestionar_marcas'))

@app.route('/admin/marcas/<int:id>/update', methods=['POST'])
def actualizar_marca(id):
    marca = Marca.query.get(id)
    if marca:
        marca.name = request.form['name']
        db.session.commit()
        flash('Marca actualizada con éxito', 'success')
    return redirect(url_for('gestionar_marcas'))





@app.route('/admin/productos', methods=['GET', 'POST'])
@app.route('/admin/productos/<int:id>', methods=['GET', 'POST'])
@roles_required('Administrador', 'Jefe de tienda', 'Vendedor')
def gestionar_productos(id=None):
    categorias = Categoria.query.all()
    marcas = Marca.query.all()  # Obtener todas las marcas
    producto = None  # Inicializamos como None por si estamos creando un producto nuevo
    
    if id:
        # Si hay un ID, obtenemos el producto de la base de datos
        producto = Producto.query.get_or_404(id)

    if request.method == 'POST':
        if producto:  # Editar producto existente
            producto.name = request.form['name']
            producto.price = request.form['price']
            producto.marca_id = request.form['marca_id']  # Almacenamos el ID de la marca
            producto.descripcion = request.form['descripcion']
            producto.stock = request.form['stock']
            producto.category_id = request.form['category_id']
            producto.en_oferta = 'en_oferta' in request.form
            producto.destacado = 'destacado' in request.form

        else:  # Crear un nuevo producto
            nombre_producto = request.form['name']
            precio_producto = request.form['price']
            marca_id = request.form['marca_id']  # Tomamos el ID de la marca seleccionada
            descripcion = request.form['descripcion']
            stock = request.form['stock']
            categoria_id = request.form['category_id']
            en_oferta = 'en_oferta' in request.form
            destacado = 'destacado' in request.form

            # Validar precios y stock
            try:
                precio_producto = int(precio_producto.replace('$', '').replace('.', '').strip())
                stock = int(stock)
            except ValueError:
                flash('El precio o el stock no son válidos', 'danger')
                return redirect(url_for('gestionar_productos', id=id))

            producto = Producto(name=nombre_producto, price=precio_producto, marca_id=marca_id, 
                                stock=stock, descripcion=descripcion, 
                                category_id=categoria_id, en_oferta=en_oferta, 
                                destacado=destacado)
            db.session.add(producto)
            db.session.commit()  # Guardar el producto primero para obtener el ID

        # Manejo de imágenes, tanto para nuevos como para productos editados
        files = request.files.getlist('images')
        for file in files:
            if file:
                filename = secure_filename(file.filename)
                file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
                file.save(file_path)
                
                nueva_imagen = Imagen(image_url=file_path, producto_id=producto.id)
                db.session.add(nueva_imagen)

        db.session.commit()
        flash(f'Producto {"editado" if id else "creado"} con éxito', 'success')
        return redirect(url_for('gestionar_productos'))

    productos = Producto.query.all()
    return render_template('admin_productos.html', productos=productos, categorias=categorias, marcas=marcas, producto=producto)






@app.route('/admin/productos/eliminar/<int:id>', methods=['POST'])
def eliminar_producto(id):
    producto = Producto.query.get_or_404(id)

    # Eliminar ítems del carrito relacionados
    item_carritos = ItemCarrito.query.filter_by(producto_id=producto.id).all()
    for item in item_carritos:
        db.session.delete(item)

    # Eliminar archivos de imagen físicos
    for imagen in producto.imagenes:
        try:
            os.remove(imagen.image_url)
        except OSError as e:
            print(f"Error al eliminar la imagen: {e}")

    # Luego, eliminar el producto de la base de datos
    db.session.delete(producto)
    db.session.commit()
    flash('Producto e imágenes eliminados con éxito', 'success')
    return redirect(url_for('gestionar_productos'))


@app.route('/buscar/<int:categoria_id>')
def buscar_por_categoria(categoria_id):
    categorias = Categoria.query.all()
    categoria = Categoria.query.get_or_404(categoria_id)
    productos = Producto.query.filter_by(category_id=categoria_id).all()
    return render_template('buscar.html', categoria=categoria, productos=productos,categorias=categorias)


@app.route('/producto/<int:product_id>')
def product_detail(product_id):
    categorias = Categoria.query.all()
    producto = Producto.query.get_or_404(product_id)
    return render_template('product_detail.html', producto=producto,categorias=categorias)


@app.before_request
def load_user():
    """Carga el usuario actual en el contexto global antes de cada solicitud."""
    if 'user_id' in session:
        g.user = Usuario.query.get(session['user_id'])
    else:
        g.user = None



@app.route('/buscar', methods=['GET'])
def buscar_productos():
    categorias = Categoria.query.all()
    query = request.args.get('query', '').strip()
    
    if query:
        # Filtrar productos por nombre o categoría
        productos = Producto.query.join(Categoria).options(joinedload(Producto.categoria)).filter(
            or_(
                Producto.name.ilike(f'%{query}%'),
                Categoria.name.ilike(f'%{query}%')
            )
        ).all()
    else:
        productos = []

    return render_template('resultados_busqueda.html', productos=productos, categorias=categorias)

@app.route('/producto/<int:id>', methods=['GET'])
def detalle_producto(id):
    producto = Producto.query.get_or_404(id)
    return render_template('detalle_producto.html', producto=producto)


from flask import jsonify

@app.route('/agregar_carrito/<int:product_id>', methods=['POST'])
def agregar_carrito(product_id):
    producto = Producto.query.get_or_404(product_id)

    # Obtén el carrito existente del usuario en la base de datos
    carrito = Carrito.query.filter_by(usuario_id=session['user_id']).first()
    
    if carrito is None:
        # Si no existe un carrito, crea uno nuevo
        carrito = Carrito(usuario_id=session['user_id'])
        db.session.add(carrito)
        db.session.commit()

    # Verifica si el producto ya existe en el carrito
    item = ItemCarrito.query.filter_by(carrito_id=carrito.id, producto_id=producto.id).first()
    
    if item:
        # Verifica si al agregar uno más, se excede el stock
        if item.cantidad + 1 > producto.stock:
            flash('Sin stock disponible.', 'error')
            return redirect(url_for('ver_carrito'))
        
        # Incrementa la cantidad si no se excede el stock
        item.cantidad += 1
    else:
        # Si el producto no está en el carrito, lo agrega
        nuevo_item = ItemCarrito(carrito_id=carrito.id, producto_id=producto.id, cantidad=1)
        db.session.add(nuevo_item)

    db.session.commit()  # Guardar cambios en la base de datos
    flash(f'{producto.name} fue añadido al carrito.')
    return redirect(url_for('ver_carrito'))






@app.route('/editar_producto/<int:id>', methods=['GET', 'POST'])
def editar_producto(id):
    producto = Producto.query.get(id)
    if request.method == 'POST':
        # Obtener datos del formulario
        producto.name = request.form['name']
        producto.price = int(request.form['price'])
        producto.marca = request.form['marca']
        producto.stock = int(request.form['stock'])
        producto.descripcion = request.form['descripcion']
        producto.category_id = request.form['category_id']
        producto.en_oferta = 'en_oferta' in request.form  # Si está marcado, será True

        # Guardar los cambios en la base de datos
        db.session.commit()
        flash('Producto actualizado exitosamente', 'success')
        return redirect(url_for('tu_funcion_para_listar_productos'))  # Cambia esto por la ruta adecuada

    # En caso de que sea un GET, renderiza el formulario con los datos actuales
    categorias = Categoria.query.all()  # Obtén todas las categorías
    return render_template('tu_template_para_editar_producto.html', producto=producto, categorias=categorias)





@app.route('/carrito')
def ver_carrito():
    categorias = Categoria.query.all()
    
    if 'user_id' not in session:
        return redirect(url_for('login'))
    
    carrito = Carrito.query.filter_by(usuario_id=session['user_id']).first()
    items = []

    if carrito:
        items = ItemCarrito.query.filter_by(carrito_id=carrito.id).all()

    subtotal = sum(item.producto.price * item.cantidad for item in items)

    for item in items:
        if item.producto.imagenes:
            item.image_url = item.producto.imagenes[0].image_url
        else:
            item.image_url = None

    return render_template('carrito.html', carrito=items, subtotal=subtotal, categorias=categorias)





@app.route('/checkout', methods=['GET', 'POST'])
def checkout():
    categorias = Categoria.query.all()
    
    if request.method == 'POST':
        carrito = Carrito.query.filter_by(usuario_id=session['user_id']).first()

        # Validar si hay un carrito
        if not carrito:
            flash('Tu carrito está vacío', 'warning')
            return redirect(url_for('ver_carrito'))

        # Obtener los ítems del carrito
        items = ItemCarrito.query.filter_by(carrito_id=carrito.id).all()

        # Validar si hay ítems en el carrito
        if not items:
            flash('Tu carrito está vacío', 'warning')
            return redirect(url_for('ver_carrito'))

        # Preparar datos para el checkout
        checkout_items = []
        total = calcular_total(items)

        for item in items:
            imagen = item.producto.imagenes[0].image_url if item.producto.imagenes else None

            checkout_items.append({
                'id': item.producto.id,
                'name': item.producto.name,
                'cantidad': item.cantidad,
                'precio': item.producto.price,
                'imagen': imagen
            })

        # Almacenar datos en la sesión
        session['checkout_items'] = checkout_items
        session['checkout_total'] = total

        return redirect(url_for('checkout_view'))

    else:  # Manejo del GET para mostrar la vista de checkout
        # Obtener las tarjetas del usuario
        tarjetas = Tarjeta.query.filter_by(usuario_id=session['user_id']).all()
        usuario = Usuario.query.get(session['user_id'])  # Recuperar el usuario logueado

        try:
            response = requests.get('http://localhost:5005/datos_completos')
            response.raise_for_status()

            data = response.json()
            regiones = data.get('regiones', [])
            provincias = data.get('provincias', [])
            comunas = data.get('comunas', [])

            total = session.get('checkout_total', 0)
            checkout_items = session.get('checkout_items', [])

            # Enviar los datos del usuario a la plantilla si existen
            usuario_data = {
                "nombre": usuario.nombre if usuario else "",
                "apellido": usuario.apellido if usuario else "",
                "email": usuario.email if usuario else "",
                "direccion": usuario.direccion if usuario else "",
            }

            return render_template(
                'vista_checkout.html',
                total_price=total,
                checkout_items=checkout_items,
                regiones=regiones,
                provincias=provincias,
                comunas=comunas,
                tarjetas=tarjetas,
                categorias=categorias,
                usuario_data=usuario_data
            )

        except requests.exceptions.RequestException as e:
            print(f"Error en la solicitud: {e}")
            return "Error al obtener datos. Por favor, inténtelo más tarde.", 500
        except ValueError as e:
            print(f"Error al procesar JSON: {e}")
            return "Error al procesar datos. Por favor, inténtelo más tarde.", 500




@app.route('/checkout', methods=['GET'])
def checkout_view():
    try:
        # Obtén todos los datos (regiones, provincias, comunas) de una sola vez
        response = requests.get('http://localhost:5005/datos_completos')
        response.raise_for_status()  # Lanza un error para códigos de estado HTTP 4xx o 5xx
        
        data = response.json()  # Obtiene los datos JSON de la respuesta

        # Asigna las regiones, provincias y comunas desde los datos obtenidos
        regiones = data.get('regiones', [])
        provincias = data.get('provincias', [])
        comunas = data.get('comunas', [])

        # Obtén el total y los ítems del checkout de la sesión
        total = session.get('checkout_total', 0)
        checkout_items = session.get('checkout_items', [])

        return render_template(
            'vista_checkout.html',
            total_price=total,
            checkout_items=checkout_items,
            regiones=regiones,
            provincias=provincias,
            comunas=comunas
        )
    
    except requests.exceptions.RequestException as e:
        print(f"Error en la solicitud: {e}")
        return "Error al obtener datos. Por favor, inténtelo más tarde.", 500
    except ValueError as e:
        print(f"Error al procesar JSON: {e}")
        return "Error al procesar datos. Por favor, inténtelo más tarde.", 500



@app.route('/process_checkout', methods=['POST'])
def process_checkout():
    try:
        # Validar datos del formulario
        first_name = request.form.get('first-name')
        last_name = request.form.get('last-name')
        email = request.form.get('email')
        address = request.form.get('address')
        region = request.form.get('region')
        provincia = request.form.get('provincia')
        comuna = request.form.get('comuna')
        tel = request.form.get('tel')
        tarjeta_id = request.form.get('tarjeta_id')
        total_compra = request.form.get('total_compra')

        if not tarjeta_id or not total_compra:
            flash('Por favor, selecciona una tarjeta y asegúrate de que el total de compra sea correcto.', 'error')
            return redirect(url_for('checkout'))

        try:
            total_compra = int(total_compra)
        except ValueError:
            flash('El total de compra no es válido.', 'error')
            return redirect(url_for('checkout'))

        # Verificar tarjeta
        tarjeta = Tarjeta.query.get(tarjeta_id)
        if not tarjeta:
            flash('Tarjeta no válida.', 'error')
            return redirect(url_for('checkout'))

        if tarjeta.saldo < total_compra:
            flash('Saldo insuficiente en la tarjeta.', 'error')
            return redirect(url_for('checkout'))

        # Procesar el pago
        pago_exitoso = procesar_pago_externo(tarjeta, total_compra)
        if not pago_exitoso:
            flash('El pago no se pudo procesar. Intenta nuevamente.', 'error')
            return redirect(url_for('checkout'))

        # Actualizar modelos: Orden, Factura, Carrito
        exito, mensaje = actualizar_modelos_post_pago(session['user_id'], tarjeta_id, total_compra)
        if not exito:
            flash(mensaje, 'error')
            return redirect(url_for('checkout'))

        flash('Tu pedido ha sido realizado con éxito!', 'success')
        return redirect(url_for('confirmacion_compra'))  # Redirigir a una página de confirmación personalizada

    except Exception as e:
        db.session.rollback()  # Revertir cualquier cambio en caso de error
        print(f"Error en el proceso de checkout: {e}")
        flash('Ocurrió un error inesperado durante el proceso. Por favor, intenta nuevamente.', 'error')
        return redirect(url_for('checkout'))


# Función para procesar el pago en la API externa
def procesar_pago_externo(tarjeta, total_compra):
    url_pago = 'http://localhost:5003/api/pagar'
    try:
        response = requests.post(url_pago, json={
            'numero_tarjeta': tarjeta.numero_tarjeta,
            'total_compra': total_compra
        }, headers={'Content-Type': 'application/json'})

        response.raise_for_status()
        if response.status_code == 200:
            tarjeta.saldo -= total_compra  # Actualizar saldo de la tarjeta localmente
            db.session.commit()
            return True
        else:
            mensaje_error = response.json().get('mensaje', 'Error desconocido')
            flash(mensaje_error, 'error')
            return False
    except requests.exceptions.RequestException as e:
        print(f"Error al conectarse a la API de pago: {e}")
        return False


# Función para actualizar los modelos después del pago
def actualizar_modelos_post_pago(usuario_id, tarjeta_id, total_compra):
    try:
        # Crear la orden con estado "Pagado"
        estado_pagado = EstadoOrden.query.filter_by(nombre='Pagado').first()
        if not estado_pagado:
            return False, 'El estado "Pagado" no está configurado en el sistema.'

        nueva_orden = Orden(
            usuario_id=usuario_id,
            estado_id=estado_pagado.id
        )
        db.session.add(nueva_orden)
        db.session.commit()

        # Asociar ítems del carrito a la orden
        carrito = Carrito.query.filter_by(usuario_id=usuario_id).first()
        if not carrito:
            return False, 'No se encontró el carrito del usuario.'

        items_carrito = ItemCarrito.query.filter_by(carrito_id=carrito.id).all()
        for item in items_carrito:
            producto = Producto.query.get(item.producto_id)
            if producto and producto.stock >= item.cantidad:
                producto.stock -= item.cantidad
                orden_item = OrdenItem(
                    orden_id=nueva_orden.id,
                    producto_id=item.producto_id,
                    cantidad=item.cantidad
                )
                db.session.add(orden_item)
            else:
                return False, f'Stock insuficiente para {producto.name}.'

        # Crear factura asociada a la orden
        nueva_factura = Factura(
            orden_id=nueva_orden.id,
            monto=total_compra
        )
        db.session.add(nueva_factura)

        # Vaciar carrito
        for item in items_carrito:
            db.session.delete(item)
        db.session.delete(carrito)

        db.session.commit()
        return True, 'Modelos actualizados con éxito.'

    except Exception as e:
        db.session.rollback()
        print(f"Error al actualizar los modelos: {e}")
        return False, 'Error al actualizar los datos de la orden. Intenta nuevamente.'


# @app.route('/gracias')
# def gracias():
#     return render_template('checkout')  # Asegúrate de tener una plantilla de agradecimiento






def calcular_total(items):
    total = 0
    for item in items:
        total += item.producto.price * item.cantidad  # Usa item.producto.price para acceder al precio
    return total



@app.route('/remove_from_cart/<int:item_id>', methods=['POST'])
def remove_from_cart(item_id):
    carrito = session.get('carrito', [])
    
    for item in carrito:
        if item['id'] == item_id:
            item['cantidad'] -= 1  # Reducir la cantidad en lugar de eliminarlo
            if item['cantidad'] <= 0:
                carrito.remove(item)  # Eliminar el item si la cantidad es 0 o menos
            break

    session['carrito'] = carrito  # Actualizar la sesión con el carrito modificado
    flash('Ítem eliminado del carrito.' if item['cantidad'] > 0 else 'Ítem eliminado completamente del carrito.')
    return redirect(url_for('ver_carrito'))



@app.route('/update_cart/<int:item_id>', methods=['POST'])
def update_cart(item_id):
    carrito = session.get('carrito', [])
    new_quantity = request.form.get('quantity', type=int)

    # Obtener el producto desde la base de datos para verificar el stock
    producto = Producto.query.get(item_id)
    
    # Verificar si el item está en el carrito y si la nueva cantidad no excede el stock
    for item in carrito:
        if item['id'] == item_id:
            if new_quantity > 0 and new_quantity <= producto.stock:
                item['cantidad'] = new_quantity
                flash('Cantidad actualizada.', 'success')  # Mensaje de éxito
            elif new_quantity > producto.stock:
                flash(f'Solo hay {producto.stock} unidades disponibles.', 'error')  # Mensaje de error
            else:
                carrito.remove(item)  # Eliminar el item si la cantidad es 0 o menos
            break

    session['carrito'] = carrito  # Actualizar la sesión con el carrito modificado
    return redirect(url_for('ver_carrito'))  # Asegúrate de redirigir correctamente



@app.route('/eliminar_item/<int:item_id>', methods=['POST'])
def eliminar_item(item_id):
    # Verificar si el usuario está autenticado
    if 'user_id' not in session:
        flash('Debes iniciar sesión para realizar esta acción.', 'error')
        return redirect(url_for('login'))  # Redirigir a la página de login si no está autenticado

    # Obtener el carrito del usuario actual
    carrito = Carrito.query.filter_by(usuario_id=session['user_id']).first()
    
    if carrito:
        # Buscar el ítem en la tabla ItemCarrito
        item_carrito = ItemCarrito.query.filter_by(carrito_id=carrito.id, producto_id=item_id).first()
        
        if item_carrito:
            # Eliminar el ítem del carrito
            db.session.delete(item_carrito)
            db.session.commit()
            flash(f'El ítem fue eliminado del carrito.', 'success')
        else:
            flash('El ítem no se encuentra en el carrito.', 'error')
    else:
        flash('No hay carrito asociado al usuario.', 'error')

    return redirect(url_for('ver_carrito'))  # Redirigir a la vista del carrito




@app.route('/vaciar_carrito', methods=['POST'])
def vaciar_carrito():
    # Asegúrate de que el usuario esté autenticado
    carrito = Carrito.query.filter_by(usuario_id=session['user_id']).first()
    
    if carrito:
        # Eliminar todos los ítems del carrito
        ItemCarrito.query.filter_by(carrito_id=carrito.id).delete()
        
        # Opcional: puedes eliminar el carrito si también lo deseas
        # db.session.delete(carrito)
        
        db.session.commit()  # Confirmar los cambios en la base de datos
    
    flash("El carrito ha sido vaciado.", "success")  # Mensaje de éxito
    return redirect(url_for('ver_carrito'))  # Redirigir a la vista del carrito


@app.route('/destacados')
def destacados():
    categorias = Categoria.query.all()
    try:
        response = requests.get('http://localhost:5001/api/productos_destacados')
        response.raise_for_status()
        productos_destacados = response.json()
        print(productos_destacados)  # Añadir esta línea para verificar la estructura de los datos
    except requests.exceptions.RequestException as e:
        flash('Error al obtener los productos destacados', 'danger')
        productos_destacados = []

    return render_template('destacados.html', productos=productos_destacados, categorias = categorias)






@app.route('/admin/tarjetas')
def administrar_tarjetas():
    categorias = Categoria.query.all()
    if 'user_id' not in session:
        flash('Por favor, inicie sesión para acceder a esta página.', 'danger')
        return redirect(url_for('login'))

    user_id = session['user_id']
    usuario_actual = Usuario.query.get(user_id)

    # Verifica si el usuario actual es administrador
    if not usuario_actual.is_admin:
        flash('No tienes permisos para acceder a esta página.', 'danger')
        return redirect(url_for('index'))  # Redirige al  o donde prefieras

    # Si es administrador, obtiene todas las tarjetas
    tarjetas = Tarjeta.query.all()

    return render_template('admin_tarjetas.html', tarjetas=tarjetas, categorias=categorias)




@app.route('/add_card', methods=['POST'])
def add_card():
    if 'user_id' not in session:
        return redirect(url_for('login'))  # Redirigir si el usuario no está logueado

    # Obtener el ID del usuario de la sesión
    usuario_id = session['user_id']
    
    numero_tarjeta = request.form['numero_tarjeta']
    codigo_verificacion = request.form['codigo_verificacion']
    fecha_vencimiento = request.form['fecha_vencimiento']

    # Separar mes y año
    mes_vencimiento, anio_vencimiento = map(int, fecha_vencimiento.split('/'))
    anio_vencimiento += 2000  # Convertir el año a cuatro dígitos (asumiendo que es del 2000 en adelante)

    # Validar mes de vencimiento
    if mes_vencimiento < 1 or mes_vencimiento > 12:
        flash('El mes de vencimiento debe estar entre 1 y 12.', 'danger')
        return redirect('/ruta_deseada')  # O redirigir a la página de donde vino

    # Verificar si la tarjeta ya existe
    tarjeta_existente = Tarjeta.query.filter_by(usuario_id=usuario_id, numero_tarjeta=numero_tarjeta).first()
    if tarjeta_existente:
        flash('La tarjeta ya está registrada.', 'danger')
        return redirect('/ruta_deseada')  # O redirigir a la página de donde vino

    # Crear la tarjeta en la base de datos
    nueva_tarjeta = Tarjeta(
        usuario_id=usuario_id,  # Agregar el usuario_id aquí
        numero_tarjeta=numero_tarjeta,
        codigo_verificacion=codigo_verificacion,
        mes_vencimiento=mes_vencimiento,
        anio_vencimiento=anio_vencimiento,
        saldo=0  # O el valor que desees para el saldo inicial
    )
    db.session.add(nueva_tarjeta)
    db.session.commit()

    return redirect('/profile')  # Redirige a donde quieras después de agregar la tarjeta


@app.route('/delete_card/<int:tarjeta_id>', methods=['POST'])
def delete_card(tarjeta_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))  # Redirigir si el usuario no está logueado

    # Verificar si la tarjeta existe
    tarjeta = Tarjeta.query.get_or_404(tarjeta_id)
    
    # Verificar si el usuario es el propietario de la tarjeta o administrador
    usuario_id = session['user_id']
    usuario_actual = Usuario.query.get(usuario_id)
    
    if tarjeta.usuario_id != usuario_id and not usuario_actual.is_admin:
        flash('No tienes permiso para eliminar esta tarjeta.', 'danger')
        return redirect('/profile')  # O redirigir a donde prefieras
    
    # Eliminar la tarjeta
    db.session.delete(tarjeta)
    db.session.commit()

    flash('Tarjeta eliminada con éxito.', 'success')
    return redirect('/profile')  # O redirigir a la página de administración o perfil




@app.route('/update_saldo/<int:tarjeta_id>', methods=['POST'])
def update_saldo(tarjeta_id):
    if 'user_id' not in session:
        return jsonify({'success': False, 'message': 'Debe iniciar sesión'}), 403

    user_id = session['user_id']
    usuario_actual = Usuario.query.get(user_id)

    # Verificar si el usuario es administrador
    if not usuario_actual.is_admin:
        return jsonify({'success': False, 'message': 'No tienes permisos para realizar esta acción'}), 403

    tarjeta = Tarjeta.query.get(tarjeta_id)
    
    if not tarjeta:
        return jsonify({'success': False, 'message': 'Tarjeta no encontrada'}), 404

    # Leer el monto a aumentar desde el cuerpo de la solicitud
    data = request.get_json()
    monto_aumentar = data.get('saldo')

    if monto_aumentar is None or monto_aumentar < 0:
        return jsonify({'success': False, 'message': 'Monto inválido'}), 400

    # Sumar el monto al saldo actual
    tarjeta.saldo += monto_aumentar
    db.session.commit()

    return jsonify({'success': True, 'message': 'Saldo actualizado correctamente'})



@app.context_processor
def inject_carrito():
    carrito_data = {
        'carrito': [],
        'subtotal_carrito': 0,
        'total_items_carrito': 0,
        'user_roles': set()
    }

    if 'user_id' in session:
        carrito = Carrito.query.filter_by(usuario_id=session['user_id']).first()
        items = []
        subtotal = 0
        total_items = 0

        # Lógica para el carrito
        if carrito:
            items = ItemCarrito.query.filter_by(carrito_id=carrito.id).all()
            subtotal = sum(item.producto.price * item.cantidad for item in items)
            total_items = sum(item.cantidad for item in items)

            for item in items:
                item.image_url = item.producto.imagenes[0].image_url if item.producto.imagenes else None

        # Agregar datos del carrito
        carrito_data['carrito'] = items
        carrito_data['subtotal_carrito'] = subtotal
        carrito_data['total_items_carrito'] = total_items

        # Lógica para los roles del usuario
        user = Usuario.query.get(session['user_id'])
        if user:
            carrito_data['user_roles'] = {rol.nombre for rol in user.roles}

    return carrito_data


@app.route('/confirmacion_compra')
def confirmacion_compra():
    # Obtener datos de la sesión
    checkout_items = session.get('checkout_items', [])
    total_price = session.get('checkout_total', 0)
    email = session.get('user_email', 'tu@correo.com')  # Ajustar según cómo almacenes el email

    return render_template(
        'confirmacion_compra.html',
        checkout_items=checkout_items,
        total_price=total_price,
        email=email
    )

@app.route('/pedidos')
def pedidos():
    categorias = Categoria.query.all()
    productos = Producto.query.all()  # Obtén todos los productos de la base de datos
    usuario_id = session.get('user_id')  # Obtén el ID del usuario desde la sesión

    # Obtener todos los pedidos del usuario
    pedidos = Orden.query.filter_by(usuario_id=usuario_id).all()  # Obtener los pedidos del usuario

    for pedido in pedidos:
        # Cargar el estado del pedido
        pedido.estado_orden = EstadoOrden.query.get(pedido.estado_id)
        
        # Obtener la tarjeta asociada al usuario (última tarjeta, si es necesario)
        pedido.tarjeta = Tarjeta.query.filter_by(usuario_id=usuario_id).first()  # Aquí seleccionas la primera tarjeta
        
        # Obtener la factura asociada al pedido (solo uno)
        pedido.factura = Factura.query.filter_by(orden_id=pedido.id).first()  # Correcto para obtener un solo registro

        # Obtener los productos asociados a la orden (a través de OrdenItem)
        pedido.items = OrdenItem.query.filter_by(orden_id=pedido.id).all()

    return render_template('pedidos.html', categorias=categorias, productos=productos, pedidos=pedidos)

@app.route('/pedido/<int:pedido_id>', methods=['GET'])
def detalle_pedido(pedido_id):
    # Lógica para obtener los detalles del pedido
    pedido = Orden.query.get_or_404(pedido_id)
    return render_template('detalle_pedido.html', pedido=pedido)



if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    with app.app_context():
        db.create_all()
        # Verificar la conexión antes de iniciar el servidor
        test_connection()
        # Crear rol por defecto
        crear_roles()
        # Crear el usuario admin por defecto
        create_default_admin()
        # Estados de orden
        estados_orden()

    # Iniciar el servidor con soporte para HTTPS
    app.run(
        debug=True, 
        host="0.0.0.0", 
        port=5000, 
        ssl_context=("server.crt", "server.key")  # Rutas a los archivos del certificado y clave
    )

