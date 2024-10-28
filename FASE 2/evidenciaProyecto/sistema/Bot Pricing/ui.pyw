import customtkinter as ctk
import subprocess
import threading
import time
from PIL import Image, ImageTk

# Configuración de la aplicación
ctk.set_appearance_mode("dark")
ctk.set_default_color_theme("blue")

# Crear la ventana principal
app = ctk.CTk()
app.title("Ejecutar Bot Pricing")
app.geometry("400x250")
app.resizable(False, False)  # Desactivar maximización

# Centrar la ventana en la pantalla
app.update_idletasks()
width, height = 400, 250
x = (app.winfo_screenwidth() // 2) - (width // 2)
y = (app.winfo_screenheight() // 2) - (height // 2)
app.geometry(f"{width}x{height}+{x}+{y}")

# Cargar y configurar la imagen de fondo
def load_static_image():
    global background_image, background_label
    image_path = "static/stars.gif"  # Cambia esta ruta a la ubicación de tu imagen estática
    background_image = Image.open(image_path)
    background_image = background_image.resize((400, 250), Image.LANCZOS)
    background_photo = ImageTk.PhotoImage(background_image)

    background_label = ctk.CTkLabel(app, image=background_photo, text="")  # Evitar texto en el label
    background_label.place(relwidth=1, relheight=1)
    background_label.image = background_photo

# Variables globales para el cronómetro
running = False
start_time = 0
stop_event = threading.Event()

# Función para iniciar los scripts en segundo plano
def iniciar_scripts():
    global running, start_time, stop_event
    running = True
    start_time = time.time()
    stop_event.clear()  # Limpiar el evento de parada

    def ejecutar_script(script_name):
        subprocess.run([r"C:\Users\Vicente\AppData\Local\Programs\Python\Python312\pythonw.exe", script_name])

    # Crear hilos para cada script
    scripts = ["Web_Scraping_MyShop.py", "Web_Scraping_Pc_Factory.py", "Web_Scraping_Bytemart.py"]
    threads = [threading.Thread(target=ejecutar_script, args=(script,)) for script in scripts]

    # Iniciar cada hilo
    for thread in threads:
        thread.start()

    # Iniciar el cronómetro de forma segura
    threading.Thread(target=check_scripts_completion, args=(threads,)).start()

# Función para detener los scripts
def detener_scripts():
    global running, stop_event
    running = False
    stop_event.set()  # Activar el evento de parada
    timer_label.configure(text="Tiempo transcurrido: 00:00")  # Reiniciar el cronómetro

# Función para actualizar el cronómetro
def update_timer():
    if running:
        elapsed_time = int(time.time() - start_time)
        mins, secs = divmod(elapsed_time, 60)
        timer_label.configure(text=f"Tiempo transcurrido: {mins:02}:{secs:02}")
    app.after(1000, update_timer)

# Función para verificar cuando todos los scripts terminen
def check_scripts_completion(threads):
    global running
    for thread in threads:
        thread.join()  # Espera a que cada hilo termine
    running = False  # Detener el cronómetro cuando el último hilo termine

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
