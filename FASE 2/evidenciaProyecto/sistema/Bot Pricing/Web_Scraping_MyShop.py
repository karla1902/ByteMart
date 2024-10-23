from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import json
import mysql.connector
import re

# ---------------Configuración del WebDriver-------------------
service = Service()
options = webdriver.ChromeOptions()
options.add_argument("---disable-blink-features=AutomaticControlled")
options.add_argument("--start-maximized")
options.add_experimental_option("prefs", {
    "download.prompt_for_download": False,
    "download.directory_upgrade": True,
    "safebrowsing.enabled": True
})

driver = webdriver.Chrome(options=options)

# Leer datos del archivo JSON
with open(r'config.json') as f:
    data = json.load(f)

# Conexión a la base de datos
connection = mysql.connector.connect(
    user='root',
    password='system',
    host='localhost',
    database='bot_pricing'
)

cursor = connection.cursor()

# Lista de enlaces de productos de MyShop
myShop_data = data["retailers"]["MyShop"]["product_links"]

# Función para convertir stock y precio
def convertir_stock(stock_text):
    # Extraer el número del texto, ejemplo: "5 disponibles" -> 5
    stock_numerico = int(re.search(r'\d+', stock_text).group())
    return stock_numerico

def convertir_precio(precio_text):
    # Eliminar símbolos como "$" y convertir a número
    precio_numerico = int(re.sub(r'[^0-9]', '', precio_text))
    return precio_numerico

def limpiar_nombre(nombre):
    # Convertir a minúsculas
    nombre = nombre.lower()
    # Lista de palabras a remover
    palabras_a_remover = ["4x", "micro","atx","2x","ddr","asus","amd", "cpu", "socket", "core", "ghz", "-", "modelo", "nueva", "versión", "disponible", "sam4", "am4", "3.6", "4", "3.7", "3.8", "s/cooler", "3.0"]
    for palabra in palabras_a_remover:
        nombre = nombre.replace(palabra, "")
    
    # Eliminar paréntesis y su contenido
    nombre = re.sub(r'\(.*?\)', '', nombre)
    
    # Eliminar el número al final (si existe), que puede estar precedido por un espacio
    nombre = re.sub(r'\s+\d+\s*$', '', nombre)  # Esto eliminará un número al final de la cadena, precedido de un espacio

    # Eliminar espacios extras
    nombre = " ".join(nombre.split())
    
    return nombre.capitalize()  # Convertir solo la primera letra en mayúscula

# Ejemplo
nombre_limpio = limpiar_nombre("Ryzen 5 5600x 6")
print(nombre_limpio)  # Salida esperada: "Ryzen 5 5600x"


def limpiar_marca(marca):
    # Convertir a minúsculas
    marca = marca.lower()
    # Lista de palabras a remover
    palabras_a_remover = ["marca:"]
    for palabra in palabras_a_remover:
        marca = marca.replace(palabra, "")
    # Eliminar paréntesis y su contenido
    marca = re.sub(r'\(.*?\)', '', marca)
    # Eliminar espacios extras
    marca = " ".join(marca.split())
    
    return marca.capitalize() 

def limpiar_categoria(categoria):
    # Convertir a minúsculas
    categoria = categoria.lower()
    # Lista de palabras a remover
    palabras_a_remover = ["amd"]
    for palabra in palabras_a_remover:
        categoria = categoria.replace(palabra, "")
    # Eliminar paréntesis y su contenido
    categoria = re.sub(r'\(.*?\)', '', categoria)
    # Eliminar espacios extras
    categoria = " ".join(categoria.split())
    
    return categoria.capitalize() 

# Tienda
tienda = "MyShop"

# Visitar cada enlace de la lista uno por uno
for link in myShop_data:
    driver.get(link)
    
    try:
        # Extraer los datos
        nombre = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located(
                (By.XPATH, '/html/body/div[4]/div[1]/div/div/div[2]/div/form/h1'))
        ).text

        categoria = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located(
                (By.XPATH, '/html/body/div[3]/div/div/div/div/ul/li[2]/a'))
        ).text

        marca = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located(
                (By.XPATH, '/html/body/div[4]/div[1]/div/div/div[2]/div/form/div[4]/p[3]/span'))
        ).text

        stock_texto = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located(
                (By.XPATH, '/html/body/div[4]/div[1]/div/div/div[2]/div/form/div[5]/ul/li[1]'))
        ).text

        precio_texto = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located(
                (By.XPATH, '/html/body/div[4]/div[1]/div/div/div[2]/div/form/div[2]/span[1]'))
        ).text

        # Convertir stock y precio a valores numéricos
        stock = convertir_stock(stock_texto)
        precio = convertir_precio(precio_texto)
        nombre = limpiar_nombre(nombre)
        marca = limpiar_marca(marca)
        categoria = limpiar_categoria(categoria)
        print(f"Título del producto: {nombre}, Categoría: {categoria}, Marca: {marca}, Stock: {stock}, Precio: {precio}")

        # Insertar los datos en la tabla retail_data
        query = """
        INSERT INTO tienda_data (nombre, categoria, marca, stock, precio, tienda)
        VALUES (%s, %s, %s, %s, %s, %s)
        """
        valores = (nombre, categoria, marca, stock, precio, tienda)
        cursor.execute(query, valores)

        # Confirmar la inserción en la base de datos
        connection.commit()

    except Exception as e:
        print(f"Error al extraer datos de {link}: {e}")

# Cerrar el navegador y la conexión a la base de datos
driver.quit()
cursor.close()
connection.close()
