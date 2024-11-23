import os
import sys
import customtkinter as ctk
import subprocess
import threading
import time
from PIL import Image, ImageTk
import signal
import psutil

# Función para obtener rutas de recursos
def resource_path(relative_path):
    """Obtiene la ruta del recurso, incluso si está empaquetado con PyInstaller."""
    if hasattr(sys, '_MEIPASS'):  # PyInstaller usa esta variable para los recursos empaquetados
        return os.path.join(sys._MEIPASS, relative_path)
    return os.path.join(os.path.abspath("."), relative_path)

# Configuración de la aplicación
ctk.set_appearance_mode("dark")
ctk.set_default_color_theme("blue")

# Crear la ventana principal
app = ctk.CTk()
app.title("Ejecutar Bot Pricing")
app.geometry("400x250")
app.resizable(False, False)

# Centrar la ventana en la pantalla
app.update_idletasks()
width, height = 400, 250
x = (app.winfo_screenwidth() // 2) - (width // 2)
y = (app.winfo_screenheight() // 2) - (height // 2)
app.geometry(f"{width}x{height}+{x}+{y}")

# Cargar y configurar la imagen de fondo
def load_static_image():
    global background_image, background_label
    image_path = resource_path("dist/static/stars.gif")  # Cambiar a ruta relativa
    background_image = Image.open(image_path)
    background_image = background_image.resize((400, 250), Image.LANCZOS)
    background_photo = ImageTk.PhotoImage(background_image)

    background_label = ctk.CTkLabel(app, image=background_photo, text="")  # Evitar texto en el label
    background_label.place(relwidth=1, relheight=1)
    background_label.image = background_photo

# Variables globales para el cronómetro y el proceso
running = False
start_time = 0
stop_event = threading.Event()
process = None  # Variable para almacenar el proceso del driver

# Función para ejecutar los scripts en secuencia (en un hilo separado)
def ejecutar_scripts_en_hilo():
    global running, start_time, stop_event, process
    running = True
    start_time = time.time()
    stop_event.clear()  # Limpiar el evento de parada

    def ejecutar_script(script_name):
        script_path = resource_path(script_name)
        print(f"Ejecutando {script_name}...")
        
        # Usar subprocess.Popen en lugar de subprocess.run para tener control sobre el proceso
        global process
        process = subprocess.Popen(["python", script_path])  # Ejecutar el script en segundo plano

        process.communicate()  # Espera a que el proceso termine antes de continuar

    # Lista de scripts para ejecutar en secuencia
    scripts = ["dist/static/Web_Scraping_MyShop.py", "dist/static/Web_Scraping_Pc_Factory.py", "dist/static/Web_Scraping_Bytemart.py"]

    # Ejecutar cada script en secuencia
    for script in scripts:
        if stop_event.is_set():
            break  # Si se ha detenido el proceso, romper el ciclo
        ejecutar_script(script)

    # Detener el cronómetro cuando todos los scripts hayan terminado
    running = False
    timer_label.configure(text="Tiempo transcurrido: 00:00")

# Función para iniciar los scripts en un hilo separado
def iniciar_scripts():
    # Crear un hilo para ejecutar los scripts sin bloquear la interfaz gráfica
    thread = threading.Thread(target=ejecutar_scripts_en_hilo)
    thread.start()

# Función para detener los scripts y cerrar el driver
def detener_scripts():
    global running, stop_event, process
    running = False
    stop_event.set()  # Activar el evento de parada
    timer_label.configure(text="Tiempo transcurrido: 00:00")  # Reiniciar el cronómetro

    if process:
        # Si el proceso del script está en ejecución, intentar terminarlo
        print("Deteniendo proceso...")
        try:
            process.terminate()  # Terminar el proceso
            process.wait()  # Esperar a que el proceso termine
        except Exception as e:
            print(f"Error al detener el proceso: {e}")

    # Aquí es donde podrías cerrar el driver de Selenium si lo estás utilizando
    # Ejemplo (si tienes un objeto de Selenium `driver`):
    # if driver:
    #     driver.quit()

# Función para actualizar el cronómetro
def update_timer():
    if running:
        elapsed_time = int(time.time() - start_time)
        mins, secs = divmod(elapsed_time, 60)
        timer_label.configure(text=f"Tiempo transcurrido: {mins:02}:{secs:02}")
    app.after(1000, update_timer)

# Cargar la imagen de fondo
load_static_image()

# Crear los botones y la etiqueta del cronómetro
boton_iniciar = ctk.CTkButton(app, text="Comenzar!", command=iniciar_scripts)
boton_iniciar.place(relx=0.5, rely=0.3, anchor='center')

boton_detener = ctk.CTkButton(app, text="Detener", command=detener_scripts)
boton_detener.place(relx=0.5, rely=0.5, anchor='center')

timer_label = ctk.CTkLabel(app, text="Tiempo transcurrido: 00:00", font=("Arial", 20))
timer_label.place(relx=0.5, rely=0.8, anchor='center')  # Ajustado para mayor separación

# Iniciar la actualización del cronómetro
update_timer()

# Ejecutar la app
app.mainloop()
