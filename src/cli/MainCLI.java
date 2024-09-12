package cli;

import core.CryptoBox;
import java.io.File;
import java.util.Scanner;
import utils.Utils;

public class MainCLI {
    private static final String DATA_DIR = "src/data/";
    private static final String ORIGINALS_DIR = "src/central/";

    public static void main(String[] args) {

        Utils.animateBrosgor();
        CryptoBox cipherBox = new CryptoBox(DATA_DIR, "BROSGOR123");
        try (Scanner scanner = new Scanner(System.in)) {
            Utils.clearConsole();
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
                        Utils.clearConsole();
                        File sourceFile = Utils.listFiles(ORIGINALS_DIR, scanner);
                        if (sourceFile != null) {
                            System.out.print("Ingresa el nombre del archivo cifrado (sin extensión): ");
                            String encryptedFileName = scanner.nextLine();
                            cipherBox.lockFile(sourceFile.getPath(), encryptedFileName);
                            System.out.println("Archivo cifrado exitosamente.");
                            Utils.pauseForKeyPress(scanner);
                        }
                        break;
                    case "2":
                        Utils.clearConsole();
                        File encryptedFile = Utils.listFiles(DATA_DIR, scanner);
                        if (encryptedFile != null) {
                            System.out.print("Ingresa el nombre del archivo descifrado (con extensión): ");
                            String decryptedFileName = scanner.nextLine();
                            cipherBox.unlockFile(encryptedFile.getPath(), decryptedFileName);
                            System.out.println("Archivo descifrado exitosamente. La extensión del archivo es: " +
                                    Utils.getFileExtension(decryptedFileName));
                            Utils.pauseForKeyPress(scanner);
                        }
                        break;
                    case "3":
                        Utils.clearConsole();
                        System.out.println("Saliendo del programa.");
                        Utils.pauseForKeyPress(scanner);
                        return;
                    default:
                        Utils.clearConsole();
                        System.out.println("Opción inválida. Por favor, intenta nuevamente.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}