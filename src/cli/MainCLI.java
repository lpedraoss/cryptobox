package cli;

import core.CipherBox;
import java.io.File;
import java.util.Scanner;

public class MainCLI {
    private static final String DATA_DIR = "src/data/";
    private static final String ORIGINALS_DIR = "src/central/";

    public static void main(String[] args) {
        animateBrosgor();
        CipherBox cipherBox = new CipherBox(DATA_DIR);
        try (Scanner scanner = new Scanner(System.in)) {
            clearConsole();
            System.out.println("Bienvenido al sistema de cifrado híbrido BROSGOR.");
            System.out.println("Este programa utiliza un método de cifrado híbrido que combina RSA y AES.");
            System.out.println("Puedes cifrar y descifrar archivos con alta seguridad.");

            while (true) {
                System.out.println("\nSelecciona una opción:");
                System.out.println("1. Cifrar un archivo");
                System.out.println("2. Descifrar un archivo");
                System.out.println("3. Salir");

                String option = scanner.nextLine();

                switch (option) {
                    case "1":
                        clearConsole();
                        File sourceFile = listFiles(ORIGINALS_DIR, scanner);
                        if (sourceFile != null) {
                            System.out.print("Ingresa el nombre del archivo cifrado (sin extensión): ");
                            String encryptedFileName = scanner.nextLine();
                            cipherBox.lockFile(sourceFile.getPath(), encryptedFileName);
                        }
                        break;
                    case "2":
                        clearConsole();
                        File encryptedFile = listFiles(DATA_DIR, scanner);
                        if (encryptedFile != null) {
                            System.out.print("Ingresa el nombre del archivo descifrado (con extensión): ");
                            String decryptedFileName = scanner.nextLine();
                            cipherBox.unlockFile(encryptedFile.getPath(), decryptedFileName);
                        }
                        break;
                    case "3":
                        System.out.println("Saliendo del programa.");
                        return;
                    default:
                        clearConsole();
                        System.out.println("Opción inválida. Por favor, intenta nuevamente.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para listar archivos en un directorio
    private static File listFiles(String directoryPath, Scanner scanner) {
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

    // Método para limpiar la consola
    private static void clearConsole() {
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

    // Método para mostrar la animación "BROSGOR"
    private static void animateBrosgor() {
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
}
