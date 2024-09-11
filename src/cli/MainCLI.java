package cli;

import core.CipherBox;
import java.io.File;
import java.util.Scanner;

public class MainCLI {
    private static final String DATA_DIR = "src/data/";
    private static final String ORIGINALS_DIR = "src/central/";

    public static void main(String[] args) {
        CipherBox cipherBox = new CipherBox(DATA_DIR);
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Bienvenido al sistema de cifrado híbrido Brosgor.");

            while (true) {
                System.out.println("\nSelecciona una opción:");
                System.out.println("1. Cifrar un archivo");
                System.out.println("2. Descifrar un archivo");
                System.out.println("3. Salir");

                String option = scanner.nextLine();

                switch (option) {
                    case "1":
                        File sourceFile = listFiles(ORIGINALS_DIR, scanner);
                        if (sourceFile != null) {
                            System.out.print("Ingresa el nombre del archivo cifrado (sin extensión): ");
                            String encryptedFileName = scanner.nextLine();
                            cipherBox.lockFile(sourceFile.getPath(), encryptedFileName);
                        }
                        break;
                    case "2":
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
                        System.out.println("Opción inválida.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Reutiliza el mismo Scanner que se pasa como parámetro
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
}
