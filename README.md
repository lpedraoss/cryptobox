# Sistema de Cifrado Híbrido Brosgor (AES-256 + RSA)

Este proyecto es un sistema de cifrado híbrido que combina cifrado simétrico (AES-256) y cifrado asimétrico (RSA) para proteger archivos. Utiliza la biblioteca de criptografía de Java (`javax.crypto`) y proporciona una interfaz de línea de comandos (CLI) para cifrar y descifrar archivos.

## Descripción del proyecto

El sistema permite:

- **Cifrar archivos**: Los archivos se cifran con una clave AES-256 generada aleatoriamente. La clave AES se cifra utilizando RSA con claves públicas y privadas generadas dinámicamente. El archivo cifrado resultante tiene la extensión `.lock`, mientras que la clave AES cifrada se guarda en un archivo con la extensión `.key`.
- **Descifrar archivos**: Utilizando la clave privada RSA, el sistema descifra la clave AES y luego descifra el archivo original, devolviéndolo a su formato original.

## Requisitos

Antes de ejecutar el proyecto, asegúrate de tener instalado lo siguiente:

- Java Development Kit (JDK) 8 o superior

## Instalación

1. Clona este repositorio:

    ```bash
    git clone https://github.com/lpedraoss/CipherBox.git
    cd cipherbox
    ```

2. Compila el proyecto:

    ```bash
    javac -d bin -sourcepath src src/Main.java
    ```

3. Ejecuta el proyecto:

    ```bash
    java -cp bin Main
    ```

## Uso

### Cifrar un archivo

1. Coloca los archivos que deseas cifrar en la carpeta `src/data`.
2. Ejecuta el programa:

    ```bash
    java -cp bin Main
    ```

3. Selecciona la opción **"1. Cifrar un archivo"** y sigue las instrucciones en pantalla para elegir un archivo y especificar un alias para las claves y archivos cifrados.

### Descifrar un archivo

1. Ejecuta el programa:

    ```bash
    java -cp bin Main
    ```

2. Selecciona la opción **"2. Descifrar un archivo"** y elige el archivo `.lock` que deseas descifrar.

### Limpiar la consola

El programa limpiará automáticamente la consola según el sistema operativo que utilices:

- **Windows**: Utiliza `cls`.
- **Linux/MacOS**: Utiliza `clear`.

## Archivos generados

El programa generará varios archivos:

- `.lock`: El archivo cifrado.
- `.key`: La clave AES cifrada con RSA.
- `.extinfo`: Información adicional cifrada, como la extensión original del archivo.
- `.private.key`: La clave privada RSA.
- `.public.key`: La clave pública RSA.

## Dependencias

El proyecto depende de las bibliotecas estándar de Java para criptografía. No se requieren dependencias externas adicionales.

## Estructura del Proyecto

El proyecto está organizado en varios paquetes y clases, cada uno con responsabilidades específicas:

### Paquete `core`

#### Clase `CryptoBox`

`CryptoBox` es el componente central del proyecto, encargado de manejar la criptografía de archivos y la gestión de extensiones. Esta clase proporciona métodos para encriptar y desencriptar archivos, así como para gestionar las extensiones de archivos seguros.

#### Clase `DataFile`

`DataFile` es una clase de modelo que representa un archivo con su extensión correspondiente. Se utiliza para manejar y almacenar información sobre archivos cifrados y descifrados.

### Paquete `utils`

#### Clase `Utils`

`Utils` proporciona métodos de utilidad para diversas operaciones del sistema, como listar archivos en un directorio, obtener la extensión de un archivo, limpiar la consola, pausar la ejecución para esperar una entrada del usuario, y mostrar animaciones en la consola. También incluye métodos para leer y editar archivos de texto, y convertir archivos cifrados a su extensión original.

## Contribución

Si deseas contribuir a este proyecto, puedes hacer un fork del repositorio y enviar un pull request con tus mejoras.

---

Este proyecto fue desarrollado para demostrar la implementación de un sistema de cifrado híbrido en Java, utilizando las bibliotecas de criptografía estándar. Cualquier mejora o sugerencia es bienvenida.
