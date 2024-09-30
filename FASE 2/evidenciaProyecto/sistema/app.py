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


app = Flask(__name__)
app.config['SECRET_KEY'] = 'mi_clave_secreta'  # Necesario para las sesiones
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+mysqlconnector://root:Mysql123.@localhost/proyecto'
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
            hashed_password = bcrypt.generate_password_hash('admin123').decode('utf-8')
            admin_user = Usuario(username='admin', password=hashed_password, is_admin=True)
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
    email = db.Column(db.String(120), nullable=False)
    is_admin = db.Column(db.Boolean, default=False)  # Añadido para diferenciar entre admin y usuario normal
    reset_code = db.Column(db.String(6), nullable=True)  # Código de restablecimiento
    reset_code_expiration = db.Column(db.DateTime, nullable=True)  # Expiración del código
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
    name = db.Column(db.String(50), unique=True, nullable=False)
    price = db.Column(db.String(100), nullable=False)
    marca = db.Column(db.String(100), nullable=False)
    descripcion = db.Column(db.String(1000), nullable=False)
    stock = db.Column(db.Integer, nullable=False)
    category_id = db.Column(db.Integer, db.ForeignKey('categoria.id'), nullable=False)

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
        
        if user and bcrypt.check_password_hash(user.password, password):
            session['user_id'] = user.id
            flash('Login exitoso', 'success')
            return redirect(url_for('home'))
        else:
            flash('Usuario o contraseña incorrectos', 'danger')
    
    return render_template('login.html', categorias = categorias)

@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        username = request.form['username']
        email = request.form['email']
        password = request.form['password']
        hashed_password = bcrypt.generate_password_hash(password).decode('utf-8')
        
        # Determinar si el usuario es admin
        is_admin = 'is_admin' in request.form  # Por ejemplo, si hay un campo en el formulario para esto

        # Verificar si el usuario ya existe
        if Usuario.query.filter_by(username=username).first():
            flash('El nombre de usuario ya existe', 'warning')
        else:
            new_user = Usuario(username=username, email=email, password=hashed_password, is_admin=is_admin)
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
    categorias = Categoria.query.all()
    if 'user_id' not in session:
        return redirect(url_for('login'))

    user = Usuario.query.get(session['user_id'])
    
    if request.method == 'POST':
        user.email = request.form['email']
        user.username = request.form['username']
        db.session.commit()
        flash('Datos actualizados correctamente', 'success')
        return redirect(url_for('profile'))

    return render_template('profile.html', user=user, categorias = categorias)

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
        nombre_categoria = request.form['name']
        nueva_categoria = Categoria(name=nombre_categoria)
        db.session.add(nueva_categoria)
        db.session.commit()
        flash('Categoría creada con éxito', 'success')
        return redirect(url_for('gestionar_categorias'))
    
    categorias = Categoria.query.all()
    return render_template('admin_categorias.html', categorias=categorias)

@app.route('/admin/categorias/<int:id>', methods=['POST'])
def eliminar_categoria(id):
    categoria = Categoria.query.get(id)
    if categoria:
        db.session.delete(categoria)
        db.session.commit()
        if request.headers.get('X-Requested-With') == 'XMLHttpRequest':  # Verifica si es una solicitud AJAX
            return '', 204  # Respuesta vacía con código de estado 204 (No Content)
    return redirect(url_for('gestionar_categorias'))



@app.route('/admin/productos', methods=['GET', 'POST'])
def gestionar_productos():
    categorias = Categoria.query.all()
    if request.method == 'POST':
        nombre_producto = request.form['name']
        precio_producto = request.form['price']
        marca = request.form['marca']
        descripcion = request.form['descripcion']
        stock = request.form['stock']
        categoria_id = request.form['category_id']
        files = request.files.getlist('images')
        
        nuevo_producto = Producto(name=nombre_producto, price=precio_producto, marca=marca,stock=stock, descripcion=descripcion, category_id=categoria_id)
        db.session.add(nuevo_producto)
        db.session.commit()  # Primero guarda el producto para tener el ID

        for file in files:
            if file:
                filename = secure_filename(file.filename)
                file_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
                file.save(file_path)
                
                nueva_imagen = Imagen(image_url=file_path, producto_id=nuevo_producto.id)
                db.session.add(nueva_imagen)

        db.session.commit()
        flash('Producto creado con éxito y imágenes cargadas', 'success')
        return redirect(url_for('gestionar_productos'))

    productos = Producto.query.all()
    return render_template('admin_productos.html', productos=productos, categorias=categorias)

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


if __name__ == '__main__':
    # Verificar la conexión antes de iniciar el servidor
    test_connection()
    # Crear el usuario admin por defecto
    create_default_admin()
    app.run(debug=True)

