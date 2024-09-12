package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class Utils {
    public Utils() {
    }

    // Método para listar archivos en un directorio
    public static File listFiles(String directoryPath, Scanner scanner) {
        File dir = new File(directoryPath);
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            System.out.println("\nArchivos disponibles:");
            for (int i = 0; i < files.length; i++) {
                System.out.println((i + 1) + ". " + files[i].getName());
            }
            System.out.print("Selecciona el número del archivo: ");
            int selection = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después de nextInt()
            return files[selection - 1];
        } else {
            System.out.println("No hay archivos en el directorio.");
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

    public static void readFileIfText(String extension, File file, Scanner scanner) throws IOException {
        if ("txt".equalsIgnoreCase(extension) ) {
            System.out.print("El archivo es un .txt, ¿quieres leerlo en la consola? (s/n): ");
            String choice = scanner.nextLine(); // Usar el scanner principal

            if (choice.equalsIgnoreCase("s")) {
                String content = new String(Files.readAllBytes(file.toPath()));
                System.out.println("\nContenido del archivo " + file.getName() + ":\n");
                System.out.println(content);
                System.out.println("\n--- Fin del archivo ---\n");

                // Usar el scanner principal para leer la salida del modo lectura
                System.out.println("Presiona 'x' para salir del modo lectura.");

                while (true) {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("x")) {
                        System.out.println("Saliendo del modo lectura...");
                        break;
                    }
                    System.out.println("Entrada no válida. Presiona 'x' para salir.");
                }
            }
        }
    }

}