from flask import Flask, render_template, request, jsonify, redirect, url_for, session, flash, g
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_bcrypt import Bcrypt
import logging
from sqlalchemy import Sequence
import os
from werkzeug.utils import secure_filename
from sqlalchemy.orm import joinedload
from sqlalchemy import or_
from flask_mail import Mail, Message
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_bcrypt import Bcrypt
import logging, random, string
from datetime import datetime, timedelta
from sqlalchemy import Sequence
import requests
import urllib.request
import json
from flask_cors import CORS
from flask_wtf.csrf import CSRFProtect  



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

mail = Mail(app)


# Inicializar SQLAlchemy y Bcrypt
db = SQLAlchemy(app)
migrate = Migrate(app, db)
bcrypt = Bcrypt(app)

# Definir secuencia
user_sequence = Sequence('user_id_seq', start=1, increment=1)
product_sequence = Sequence('product_id_seq', start=1, increment=1)
category_sequence = Sequence('category_id_seq', start=1, increment=1)

# Configuración del registro
logging.basicConfig(level=logging.INFO)




def create_default_admin():
    # Crear un usuario admin por defecto si no existe
    with app.app_context():  # Asegúrate de que el contexto de la aplicación esté disponible
        admin_user = Usuario.query.filter_by(username='admin').first()
        if not admin_user:
            # hashed_password = bcrypt.generate_password_hash('admin123').decode('utf-8')
            admin_user = Usuario(username='admin', password='admin1234', email='email@email.com', is_admin=True)
            db.session.add(admin_user)
            db.session.commit()
            app.logger.info('Usuario administrador creado con éxito.')

def test_connection():
    try:
        with app.app_context():
            # Intenta ejecutar una consulta simple para verificar la conexión
            db.session.execute('SELECT 1')
            app.logger.info('Conexión a la base de datos exitosa!')
    except Exception as e:
        app.logger.error(f'Error en la conexión a la base de datos: {e}')

# Modelo de Usuario
class Usuario(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, user_sequence, primary_key=True)
    username = db.Column(db.String(50), unique=True, nullable=False)
    password = db.Column(db.String(100), nullable=False)
    nombre = db.Column(db.String(100), nullable=False)
    apellido = db.Column(db.String(100), nullable=False)
    email = db.Column(db.String(120), nullable=False)
    direccion = db.Column(db.String(50), unique=True, nullable=True)
    is_admin = db.Column(db.Boolean, default=False)  
    reset_code = db.Column(db.String(6), nullable=True)
    reset_code_expiration = db.Column(db.DateTime, nullable=True)
    

    def __repr__(self):
        return f'<Usuario {self.username}>'


# Modelo de Categoria
class Categoria(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, category_sequence, primary_key=True)
    name = db.Column(db.String(50), unique=True, nullable=False)

    def __repr__(self):
        return f'<Categoria {self.name}>'
        
# Modelo de Producto
class Producto(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, product_sequence, primary_key=True)
    name = db.Column(db.String(155), unique=True, nullable=False)
    price = db.Column(db.Integer, nullable=False)
    marca = db.Column(db.String(100), nullable=False)
    descripcion = db.Column(db.String(1000), nullable=False)
    stock = db.Column(db.Integer, nullable=False)
    category_id = db.Column(db.Integer, db.ForeignKey('categoria.id'), nullable=False)
    en_oferta = db.Column(db.Boolean, default=False)  # Nuevo atributo para indicar si el producto está en oferta
    destacado = db.Column(db.Boolean, default=False) 

    # Relación con Categoria
    categoria = db.relationship('Categoria', backref=db.backref('productos', lazy=True))

    # Relación con Imagen con eliminación en cascada
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


# Modelo de Carrito
class Carrito(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    usuario_id = db.Column(db.Integer, db.ForeignKey('usuario.id'), nullable=False)
    usuario = db.relationship('Usuario', backref=db.backref('carrito', lazy=True))

# Modelo de ItemCarrito
class ItemCarrito(db.Model):
    __table_args__ = {'extend_existing': True}
    id = db.Column(db.Integer, primary_key=True)
    carrito_id = db.Column(db.Integer, db.ForeignKey('carrito.id'), nullable=False)
    producto_id = db.Column(db.Integer, db.ForeignKey('producto.id'), nullable=False)
    cantidad = db.Column(db.Integer, nullable=False, default=1)
    
    # Relaciones
    carrito = db.relationship('Carrito', backref=db.backref('items', lazy=True))
    producto = db.relationship('Producto', backref=db.backref('items', lazy=True))

# Modelo de Tarjeta
class Tarjeta(db.Model):
    __tablename__ = 'tarjetas'
    __table_args__ = {'extend_existing': True}
    
    id = db.Column(db.Integer, primary_key=True)
    usuario_id = db.Column(db.Integer, db.ForeignKey('usuario.id'), nullable=False)
    saldo = db.Column(db.Integer, nullable=False)  
    numero_tarjeta = db.Column(db.String(16), nullable=False)  # Cambiar a String para manejar ceros a la izquierda
    codigo_verificacion = db.Column(db.String(3), nullable=False)  # Cambiar a String

    # Relación con Usuario
    usuario = db.relationship('Usuario', backref='tarjetas')  # Opcional, para acceder a las tarjetas desde el usuario

    def __repr__(self):
        return f'<Tarjeta {self.numero_tarjeta}, Saldo: {self.saldo}>'


@app.before_request
def calcular_subtotal():
    carrito = session.get('carrito', [])
    # Sumar los precios como enteros sin usar replace
    subtotal = sum([producto['precio'] for producto in carrito])  # Asegúrate de que 'precio' sea un entero

    # Almacena el subtotal y el carrito en el contexto de la plantilla
    app.jinja_env.globals['subtotal'] = subtotal
    app.jinja_env.globals['carrito'] = carrito  # También almacena el carrito en el contexto global


@app.route('/')
def index():
    categorias = Categoria.query.all()
    productos = Producto.query.all()  # Obtén todos los productos de la base de datos
    # lógica de la vista
    return render_template('index.html',categorias=categorias,productos=productos)



# Rutas de la página
@app.route('/')
def home():
    categorias = Categoria.query.all()
    return render_template('index.html',categorias=categorias)

@app.route('/login', methods=['GET', 'POST'])
def login():
    categorias = Categoria.query.all()
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        
        user = Usuario.query.filter_by(username=username).first()
        
        if user and user.password == password:
            session['user_id'] = user.id
            # Crear un carrito si no existe
            if not Carrito.query.filter_by(usuario_id=user.id).first():
                nuevo_carrito = Carrito(usuario_id=user.id)
                db.session.add(nuevo_carrito)
                db.session.commit()
            
            flash('Login exitoso', 'success')
            return redirect(url_for('home'))
        else:
            flash('Usuario o contraseña incorrectos', 'danger')

    
    return render_template('login.html', categorias=categorias)


@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        username = request.form['username']
        nombre = request.form['nombre']
        apellido = request.form['apellido']
        email = request.form['email']
        direccion = request.form.get('direccion', None)  # Opcional
        password = request.form['password']
        # hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')

        # Verificar si el nombre de usuario ya existe
        if Usuario.query.filter_by(username=username).first():
            flash('El nombre de usuario ya existe', 'warning')
            return render_template('register.html')

        # Verificar si el correo ya existe
        if Usuario.query.filter_by(email=email).first():
            flash('El correo electrónico ya está en uso', 'warning')
            return render_template('register.html')

        # Crear un nuevo usuario
        new_user = Usuario(username=username, nombre=nombre, apellido=apellido,
                           email=email, direccion=direccion, password=password)
        db.session.add(new_user)
        db.session.commit()
        flash('Usuario registrado exitosamente', 'success')
        return redirect(url_for('login'))

    return render_template('register.html')




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
    if 'user_id' not in session:
        return redirect(url_for('login'))  # Redirigir si el usuario no está logueado

    user = Usuario.query.get(session['user_id'])  # Obtener el usuario actual desde la sesión
    tarjetas = Tarjeta.query.filter_by(usuario_id=user.id).all()  # Obtener las tarjetas del usuario

    if request.method == 'POST':
        # Actualizar el nombre de usuario y el correo electrónico desde el formulario
        user.email = request.form['email']
        user.username = request.form['username']
        db.session.commit()  # Guardar los cambios en la base de datos
        flash('Datos actualizados correctamente', 'success')
        return redirect(url_for('profile'))

    return render_template('profile.html', user=user, tarjetas=tarjetas)  # Pasa las tarjetas a la plantilla


@app.route('/logout')
def logout():
    session.pop('user_id', None)
    flash('Has salido de la sesión', 'info')
    return redirect(url_for('home'))

@app.route('/product')
def product():
    return render_template('product.html')

@app.route('/admin/configuracion')
def configuracion():
    categorias = Categoria.query.all()
    return render_template('configuracion.html', categorias = categorias)

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




@app.route('/admin/productos', methods=['GET', 'POST'])
@app.route('/admin/productos/<int:id>', methods=['GET', 'POST'])
def gestionar_productos(id=None):
    categorias = Categoria.query.all()
    producto = None  # Inicializamos como None por si estamos creando un producto nuevo
    
    if id:
        # Si hay un ID, obtenemos el producto de la base de datos
        producto = Producto.query.get_or_404(id)

    if request.method == 'POST':
        if producto:  # Editar producto existente
            producto.name = request.form['name']
            producto.price = request.form['price']
            producto.marca = request.form['marca']
            producto.descripcion = request.form['descripcion']
            producto.stock = request.form['stock']
            producto.category_id = request.form['category_id']
            producto.en_oferta = 'en_oferta' in request.form
            producto.destacado = 'destacado' in request.form  # Convertir 'on' a True

        else:  # Crear un nuevo producto
            nombre_producto = request.form['name']
            precio_producto = request.form['price']
            marca = request.form['marca']
            descripcion = request.form['descripcion']
            stock = request.form['stock']
            categoria_id = request.form['category_id']
            en_oferta = 'en_oferta' in request.form
            destacado = 'destacado' in request.form  # Convertir 'on' a True

            # Validar precios y stock
            try:
                precio_producto = int(precio_producto.replace('$', '').replace('.', '').strip())
                stock = int(stock)
            except ValueError:
                flash('El precio o el stock no son válidos', 'danger')
                return redirect(url_for('gestionar_productos', id=id))

            producto = Producto(name=nombre_producto, price=precio_producto, marca=marca, 
                                stock=stock, descripcion=descripcion, 
                                category_id=categoria_id, en_oferta=en_oferta, 
                                destacado=destacado)  # Agrega el nuevo campo al crear el producto
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
    return render_template('admin_productos.html', productos=productos, categorias=categorias, producto=producto)





@app.route('/admin/productos/eliminar/<int:id>', methods=['POST'])
def eliminar_producto(id):
    producto = Producto.query.get_or_404(id)
    
    # Eliminar archivos de imagen físicos
    for imagen in producto.imagenes:
        try:
            os.remove(imagen.image_url)
        except OSError as e:
            # Si ocurre un error, podrías manejarlo aquí, como registrar un mensaje de error
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
    
    # Asegúrate de que el usuario esté autenticado
    carrito = Carrito.query.filter_by(usuario_id=session['user_id']).first()
    items = []

    if carrito:
        items = ItemCarrito.query.filter_by(carrito_id=carrito.id).all()

    subtotal = sum(item.producto.price * item.cantidad for item in items)  # Total por cantidad
    
    # Agregar la URL de la imagen al carrito
    for item in items:
        item.image_url = item.producto.imagenes[0].image_url if item.producto.imagenes else None

    return render_template('carrito.html', carrito=items, subtotal=subtotal, categorias=categorias)




@app.route('/checkout', methods=['POST'])
def checkout():
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
    total = calcular_total(items)  # Calcular el total usando la función ajustada

    for item in items:
        # Obtener la primera imagen del producto, si existe
        imagen = item.producto.imagenes[0].image_url if item.producto.imagenes else None

        checkout_items.append({
            'id': item.producto.id,
            'name': item.producto.name,  # Asegúrate de usar el atributo correcto
            'cantidad': item.cantidad,
            'precio': item.producto.price,  # Asegúrate de que el precio está en el formato adecuado
            'imagen': imagen  # Usar la imagen si existe
        })



    # Almacenar datos en la sesión para usarlo en la página de checkout
    session['checkout_items'] = checkout_items
    session['checkout_total'] = total

    return redirect(url_for('checkout_view'))  # Redirigir a la vista de checkout


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
    # Aquí va la lógica para procesar la compra
    # Asegúrate de que el usuario esté autenticado
    carrito = Carrito.query.filter_by(usuario_id=session['user_id']).first()
    
    if not carrito:
        flash('Tu carrito está vacío', 'warning')
        return redirect(url_for('ver_carrito'))

    items = ItemCarrito.query.filter_by(carrito_id=carrito.id).all()
    if not items:
        flash('Tu carrito está vacío', 'warning')
        return redirect(url_for('ver_carrito'))

    total = calcular_total(items)  # Calcular el total
    tarjeta_id = request.form.get('tarjeta_id')  # Asegúrate de obtener el ID de la tarjeta del formulario

    # Realizar la solicitud de pago a la API de pago
    try:
        response = requests.post('http://localhost:5003/api/pagar', json={
            'tarjeta_id': tarjeta_id,
            'total_compra': total
        })
        response.raise_for_status()  # Esto lanzará un error si la respuesta no es 200

        # Aquí puedes manejar la respuesta de la API, como confirmar la transacción
        flash('Pago realizado con éxito', 'success')
        
        # Aquí podrías guardar la orden en tu base de datos
        # (esto es solo un ejemplo, deberías ajustar según tu lógica de negocio)
        
    except requests.exceptions.RequestException as e:
        flash('Error al procesar el pago: {}'.format(e), 'danger')
        return redirect(url_for('ver_carrito'))

    return redirect(url_for('success_page'))  # Redirigir a una página de éxito




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
    try:
        response = requests.get('http://localhost:5001/api/productos_destacados')
        response.raise_for_status()
        productos_destacados = response.json()
        print(productos_destacados)  # Añadir esta línea para verificar la estructura de los datos
    except requests.exceptions.RequestException as e:
        flash('Error al obtener los productos destacados', 'danger')
        productos_destacados = []

    return render_template('destacados.html', productos=productos_destacados)






@app.route('/admin/tarjetas')
def administrar_tarjetas():
    if 'user_id' not in session:
        flash('Por favor, inicie sesión para acceder a esta página.', 'danger')
        return redirect(url_for('login'))

    user_id = session['user_id']
    usuario_actual = Usuario.query.get(user_id)

    # Verifica si el usuario actual es administrador
    if not usuario_actual.is_admin:
        flash('No tienes permisos para acceder a esta página.', 'danger')
        return redirect(url_for('home'))  # Redirige al home o donde prefieras

    # Si es administrador, obtiene todas las tarjetas
    tarjetas = Tarjeta.query.all()

    return render_template('admin_tarjetas.html', tarjetas=tarjetas)


@app.route('/add_card', methods=['POST'])
def add_card():
    # Obtén los datos del formulario
    numero_tarjeta = request.form['numero_tarjeta']
    codigo_verificacion = request.form['codigo_verificacion']

    # Validar el número de tarjeta y el código de verificación
    if len(numero_tarjeta) != 16 or not numero_tarjeta.isdigit():
        flash('El número de tarjeta debe tener 16 dígitos y solo contener números.', 'danger')
        return redirect(url_for('profile'))  # Redirige al perfil del usuario
    
    if len(codigo_verificacion) != 3 or not codigo_verificacion.isdigit():
        flash('El código de verificación debe tener 3 dígitos y solo contener números.', 'danger')
        return redirect(url_for('profile'))  # Redirige al perfil del usuario

    # Obtén el ID del usuario de la sesión
    user_id = session['user_id']

    # Verificar si la tarjeta ya existe
    tarjeta_existente = Tarjeta.query.filter_by(numero_tarjeta=numero_tarjeta, usuario_id=user_id).first()
    if tarjeta_existente:
        flash('Ya existe una tarjeta con ese número para este usuario.', 'danger')
        return redirect(url_for('profile'))  # Redirige al perfil del usuario

    # Crea una nueva instancia de la tarjeta con saldo 0
    nueva_tarjeta = Tarjeta(usuario_id=user_id, 
                             numero_tarjeta=numero_tarjeta, 
                             codigo_verificacion=codigo_verificacion, 
                             saldo=0)  # Asigna un saldo inicial de 0

    # Agrega la tarjeta a la base de datos
    db.session.add(nueva_tarjeta)
    db.session.commit()

    flash('Tarjeta agregada exitosamente.', 'success')
    return redirect(url_for('perfil'))  # Redirige al perfil del usuario

@app.route('/delete_card/<int:card_id>', methods=['POST'])
def delete_card(card_id):
    if 'user_id' not in session:
        return redirect(url_for('login'))  # Redirigir si el usuario no está logueado

    # Obtener la tarjeta a eliminar
    tarjeta = Tarjeta.query.get(card_id)

    if tarjeta:
        db.session.delete(tarjeta)  # Eliminar la tarjeta de la base de datos
        db.session.commit()  # Guardar cambios
        flash('Tarjeta eliminada correctamente.', 'success')
    else:
        flash('Tarjeta no encontrada.', 'danger')

    return redirect(url_for('profile'))  # Redirigir al perfil


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

    # Leer el nuevo saldo desde el cuerpo de la solicitud
    data = request.get_json()
    nuevo_saldo = data.get('saldo')

    if nuevo_saldo is None or nuevo_saldo < 0:
        return jsonify({'success': False, 'message': 'Saldo inválido'}), 400

    # Actualizar el saldo de la tarjeta
    tarjeta.saldo = nuevo_saldo
    db.session.commit()

    return jsonify({'success': True, 'message': 'Saldo actualizado correctamente'})







if __name__ == '__main__':
    # Verificar la conexión antes de iniciar el servidor
    test_connection()
    # Crear el usuario admin por defecto
    create_default_admin()
    
    app.run(debug=True)

