package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import core.CryptoBox;

public class Utils {
    private static final String DATA_DIR = "src/data/";
    private static final String DATA_DIR_EXT = DATA_DIR + "extension/";

    public Utils() {
    }

    // Método para listar archivos en un directorio específico
    public static File listFiles(String directoryPath, Scanner scanner) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No hay archivos disponibles en el directorio: " + directoryPath);
            return null;
        }

        System.out.println("Archivos disponibles en " + directoryPath + ":");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        System.out.print("Selecciona el número del archivo: ");
        int fileIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (fileIndex >= 0 && fileIndex < files.length) {
            return files[fileIndex];
        } else {
            System.out.println("Selección inválida.");
            return null;
        }
    }

    // Método para obtener la extensión de un archivo
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "sin extensión" : fileName.substring(lastDotIndex + 1);
    }

    // Método para limpiar la consola
    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Error al limpiar la consola: " + e.getMessage());
        }
    }

    // Método para pausar y esperar la tecla de entrada
    public static void pauseForKeyPress(Scanner scanner) {
        System.out.println("\nPresiona cualquier tecla para continuar...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
        clearConsole();
    }

    // Método para mostrar la animación "BROSGOR"
    public static void animateBrosgor() {
        String logo = """
                ######   ######    #####    #####     ####    #####   ######
                 ##  ##   ##  ##  ##   ##  ##   ##   ##  ##  ##   ##   ##  ##
                 ##  ##   ##  ##  ##   ##  #        ##       ##   ##   ##  ##
                 #####    #####   ##   ##   #####   ##       ##   ##   #####
                 ##  ##   ## ##   ##   ##       ##  ##  ###  ##   ##   ## ##
                 ##  ##   ##  ##  ##   ##  ##   ##   ##  ##  ##   ##   ##  ##
                ######   #### ##   #####    #####     #####   #####   #### ##
                       """;

        String lock = """
                  ____
                 |    |
                 | [] |
                 |____|
                  _||_
                 |____|
                """;

        clearConsole();

        // Mostrar el ícono de la cerradura con retraso
        for (String line : lock.split("\n")) {
            System.out.println(line);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Pausa antes de mostrar el logo
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Mostrar el logo de BROSGOR con efecto de escritura
        for (String line : logo.split("\n")) {
            System.out.println(line);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Pausa antes de limpiar la pantalla
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        clearConsole();
    }

    // Método para leer y editar el archivo si es de texto
    public static void readFileIfText(String extension, File file, Scanner scanner) throws IOException {
        if (extension == null || extension.isEmpty() || "txt".equalsIgnoreCase(extension)
                || "unlocked".equalsIgnoreCase(extension)) {
            System.out.print("El archivo es un "
                    + (extension == null || extension.isEmpty() ? "archivo de texto" : "." + extension)
                    + ", ¿quieres leerlo en la consola? (s/n): ");
            String choice = scanner.nextLine(); // Usar el scanner principal

            if (choice.equalsIgnoreCase("s")) {
                // Leer el contenido codificado en Base64
                byte[] encodedData = Files.readAllBytes(file.toPath());
                byte[] decodedData = Base64.getDecoder().decode(encodedData);
                String content = new String(decodedData);

                // Dividir el contenido en líneas
                List<String> lines = new ArrayList<>(Arrays.asList(content.split("\n")));
                System.out.println("\nContenido del archivo " + file.getName() + ":\n");
                for (int i = 0; i < lines.size(); i++) {
                    System.out.printf("%d: %s\n", i + 1, lines.get(i));
                }
                System.out.println("\n--- Fin del archivo ---\n");

                // Usar el scanner principal para leer la salida del modo lectura
                System.out.println(
                        "Presiona 'x' para salir del modo lectura, 'e' para editar una línea, o 'c' para convertir a la extensión original.");

                while (true) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("x")) {
                        System.out.println("Saliendo del modo lectura...");
                        break;
                    } else if (input.equalsIgnoreCase("e")) {
                        System.out.print("Ingresa el número de línea que deseas editar: ");
                        int lineNumber = Integer.parseInt(scanner.nextLine());
                        if (lineNumber > 0 && lineNumber <= lines.size()) {
                            System.out.print("Ingresa el nuevo contenido para la línea " + lineNumber + ": ");
                            String newContent = scanner.nextLine();
                            lines.set(lineNumber - 1, newContent);
                            String updatedContent = String.join("\n", lines);
                            byte[] updatedEncodedData = Base64.getEncoder().encode(updatedContent.getBytes());
                            Files.write(file.toPath(), updatedEncodedData, StandardOpenOption.TRUNCATE_EXISTING);
                            System.out.println("Línea actualizada exitosamente.");
                        } else {
                            System.out.println("Número de línea inválido.");
                        }
                    } else if (input.equalsIgnoreCase("c")) {
                        System.out.print(
                                "La extensión original del archivo es: " + extension + ". ¿Deseas cambiarla? (s/n): ");
                        String choice2 = scanner.nextLine();
                        if (choice2.equalsIgnoreCase("s")) {
                            try {
                                convertExtension(file, extension);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else {
                        System.out.println(
                                "Entrada no válida. Presiona 'x' para salir, 'e' para editar una línea, o 'c' para convertir a la extensión original.");
                    }
                }
            }
        }
    }

    // Método para convertir un archivo .unlocked a la extensión proporcionada
    // Método para convertir un archivo .unlocked a la extensión proporcionada
    public static void convertExtension(File file, String extension) throws Exception {


        // Obtener el nombre del archivo sin la extensión .unlocked
        String alias = file.getName().substring(0, file.getName().lastIndexOf('.'));
        if (extension == null || extension.isEmpty() || "unlocked".equalsIgnoreCase(extension)){
            CryptoBox cryptoBox = new CryptoBox();

            // Construir la ruta del archivo .extinfo
            String extinfoFileName = alias + ".extinfo";
            File extinfoFile = new File(DATA_DIR_EXT + extinfoFileName);

            // Verificar si el archivo .extinfo existe
            if (!extinfoFile.exists()) {
                System.out.println("El archivo .extinfo no existe: " + extinfoFile.getPath());
                return;
            }

            // Descifrar el archivo .extinfo para obtener la extensión original
            extension = cryptoBox.decryptExtension(alias);
        }

        // Leer y descifrar el contenido del archivo .unlocked
        byte[] encodedData = Files.readAllBytes(file.toPath());
        byte[] decodedData = Base64.getDecoder().decode(encodedData);

        // Crear el nuevo nombre de archivo con la extensión original
        String newFileName = alias + "." + extension;
        File newFile = new File(file.getParent(), newFileName);

        // Escribir el contenido descifrado en el nuevo archivo
        Files.write(newFile.toPath(), decodedData, StandardOpenOption.CREATE);

        System.out.println("Archivo convertido exitosamente a: " + newFile.getPath());
    }

    // Método para crear directorios si no existen
    public static void createDirectories(String... directories) {
        for (String dir : directories) {
            File directory = new File(dir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    System.out.println("Directorio creado: " + dir);
                } else {
                    System.out.println("No se pudo crear el directorio: " + dir);
                }
            }
        }
    }

}