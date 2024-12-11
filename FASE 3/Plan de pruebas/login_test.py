from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import json
import mysql.connector
import re
import time
# ---------------Configuración del WebDriver-------------------
service = Service()
options = webdriver.ChromeOptions()
options.add_argument("---disable-blink-features=AutomaticControlled")
options.add_argument("--start-maximized")
options.add_argument("--ignore-certificate-errors")  # Ignorar errores de certificado
options.add_argument("--ignore-ssl-errors")  
options.add_experimental_option("prefs", {
    "download.prompt_for_download": False,
    "download.directory_upgrade": True,
    "safebrowsing.enabled": True
})


driver = webdriver.Chrome(options=options)


driver.get("https://192.168.0.17:5000/login")


try:
    # Esperar hasta que el elemento esté presente y sea interactivo
    wait = WebDriverWait(driver, 10)  # Espera máxima de 10 segundos
    username_input = wait.until(EC.element_to_be_clickable((By.XPATH, '//*[@id="login-username"]')))

    # Enviar texto al campo
    username_input.clear()  # Limpiar cualquier texto preexistente
    username_input.send_keys('admin')

    # Repetir para el campo de contraseña
    password_input = wait.until(EC.element_to_be_clickable((By.XPATH, '//*[@id="login-password"]')))
    password_input.clear()
    password_input.send_keys('admin1234')

    # Hacer clic en el botón de login
    login_button = wait.until(EC.element_to_be_clickable((By.XPATH, '//*[@id="container"]/div[2]/form/button')))
    login_button.click()
    time.sleep(2)  # Pequeña espera para permitir la carga de la nueva página

    # Hacer clic en la alerta
    login_button = wait.until(EC.element_to_be_clickable((By.XPATH, '/html/body/div[9]/div/div[6]/button[1]')))
    login_button.click()

    # Hacer clic en cerrar sesion
    login_button = wait.until(EC.element_to_be_clickable((By.XPATH, '//*[@id="top-header"]/div/ul[2]/li[4]/a')))
    login_button.click()
    time.sleep(2)  # Pequeña espera para permitir la carga de la nueva página

        # Hacer clic en la alerta
    login_button = wait.until(EC.element_to_be_clickable((By.XPATH, '/html/body/div[9]/div/div[6]/button[1]')))
    login_button.click()

        # Hacer clic login
    login_button = wait.until(EC.element_to_be_clickable((By.XPATH, '//*[@id="top-header"]/div/ul[2]/li/a')))
    login_button.click()

    # Esperar hasta que el elemento esté presente y sea interactivo
    wait = WebDriverWait(driver, 10)  # Espera máxima de 10 segundos
    username_input = wait.until(EC.element_to_be_clickable((By.XPATH, '//*[@id="login-username"]')))

    # Enviar texto al campo
    username_input.clear()  # Limpiar cualquier texto preexistente
    username_input.send_keys('admin')

    # Repetir para el campo de contraseña
    password_input = wait.until(EC.element_to_be_clickable((By.XPATH, '//*[@id="login-password"]')))
    password_input.clear()
    password_input.send_keys('admin')

    # Hacer clic en el botón de login
    login_button = wait.until(EC.element_to_be_clickable((By.XPATH, '//*[@id="container"]/div[2]/form/button')))
    login_button.click()
    time.sleep(2)  # Pequeña espera para permitir la carga de la nueva página

    # Hacer clic en la alerta
    login_button = wait.until(EC.element_to_be_clickable((By.XPATH, '/html/body/div[9]/div/div[6]/button[1]')))
    login_button.click()

    # Hacer clic en cerrar sesion
    login_button = wait.until(EC.element_to_be_clickable((By.XPATH, '//*[@id="top-header"]/div/ul[2]/li[4]/a')))
    login_button.click()
    time.sleep(2)  # Pequeña espera para permitir la carga de la nueva página

        # Hacer clic en la alerta
    login_button = wait.until(EC.element_to_be_clickable((By.XPATH, '/html/body/div[9]/div/div[6]/button[1]')))
    login_button.click()

        # Hacer clic login
    login_button = wait.until(EC.element_to_be_clickable((By.XPATH, '//*[@id="top-header"]/div/ul[2]/li/a')))
    login_button.click()

    # Validar que redirigió a la página correcta
    time.sleep(2)  # Pequeña espera para permitir la carga de la nueva página

except Exception as e:
    print(f"Error durante la automatización: {e}")
    
finally:
    driver.quit()

