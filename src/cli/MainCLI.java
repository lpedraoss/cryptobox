package cli;

import core.CryptoBox;
import core.models.DataFile;

import java.io.File;
import java.util.Scanner;
import utils.Utils;

public class MainCLI {
    private static final String DATA_DIR = "src/data/";
    private static final String DATA_DIR_KEY = DATA_DIR + "key/";
    private static final String DATA_DIR_DECRYPT = DATA_DIR + "decrypt/";
    private static final String DATA_DIR_ENCRYPT = DATA_DIR + "encrypt/";
    private static final String DATA_DIR_EXT = DATA_DIR + "extension/";
    private static final String ORIGINALS_DIR = "src/central/";

    public static void main(String[] args) {
        Utils.createDirectories(DATA_DIR_KEY, DATA_DIR_DECRYPT, DATA_DIR_ENCRYPT, DATA_DIR_EXT, ORIGINALS_DIR);

        Utils.animateBrosgor();
        CryptoBox cipherBox = new CryptoBox();
        try (Scanner scanner = new Scanner(System.in)) {
            Utils.clearConsole();
            System.out.println("Bienvenido al sistema de cifrado híbrido BROSGOR.");
            System.out.println("Este programa utiliza un método de cifrado híbrido que combina RSA y AES.");
            System.out.println("Puedes cifrar y descifrar archivos con alta seguridad.");

            while (true) {
                System.out.println("\nSelecciona una opción:");
                System.out.println("1. Cifrar un archivo");
                System.out.println("2. Descifrar un archivo");
                System.out.println("3. Leer un archivo .unlocked en la consola");
                System.out.println("4. Salir");

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
                        File encryptedFile = Utils.listFiles(DATA_DIR_ENCRYPT, scanner);
                        if (encryptedFile != null) {
                            System.out.print("Ingresa el nombre del archivo descifrado (con extensión): ");
                            String decryptedFileName = scanner.nextLine();
                            DataFile data = cipherBox.unlockFile(encryptedFile.getPath(), decryptedFileName);
                            System.out.println("Archivo descifrado exitosamente. La extensión del archivo es: " +
                                    data.getExtension());

                            if ("txt".equalsIgnoreCase(data.getExtension())) {
                                Utils.readFileIfText(data.getExtension(), data.getFile(), scanner); // Pasar el scanner
                                                                                                    // principal
                            } else {
                                Utils.pauseForKeyPress(scanner); // Solo si no es un archivo .txt
                            }
                        }
                        break;
                    case "3":
                        Utils.clearConsole();
                        File unlockedFile = Utils.listFiles(DATA_DIR_DECRYPT, scanner);
                        if (unlockedFile != null) {
                            String extension = Utils.getFileExtension(unlockedFile.getName());
                            Utils.readFileIfText(extension, unlockedFile, scanner);
                        }
                        break;
                    case "4":
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