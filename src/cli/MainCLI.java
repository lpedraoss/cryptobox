package cli;

import core.CryptoBox;
import core.models.DataFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
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
                System.out.println("3. Leer un archivo .lock en la consola");
                System.out.println("4. Cambiar la extensión de un archivo .unlocked");
                System.out.println("5. Salir");

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
                        File lockedFile = Utils.listFiles(DATA_DIR_ENCRYPT, scanner);
                        if (lockedFile != null) {
                            String alias = lockedFile.getName().split("\\.")[0];
                            DataFile data = cipherBox.unlockFile(lockedFile.getPath(), alias);
                            String extension = data.getExtension();
                            File unlockedFile = data.getFile();
                            System.out.println("Archivo descifrado exitosamente. La extensión del archivo es: " +
                                    data.getExtension());
                            if ("txt".equalsIgnoreCase(data.getExtension())
                                    || "unlocked".equalsIgnoreCase(data.getExtension())) {
                                Utils.readFileIfText(extension, unlockedFile, scanner); // Pasar el scanner
                                // Leer el contenido del archivo .unlocked
                                byte[] fileContent = Files.readAllBytes(unlockedFile.toPath());

                                // Decodificar el contenido desde Base64
                                byte[] decodedContent = Base64.getDecoder().decode(fileContent);

                                // Escribir el contenido decodificado de nuevo en el archivo
                                Files.write(unlockedFile.toPath(), decodedContent);

                                // Continuar con el proceso de cifrado y eliminación del archivo
                                cipherBox.lockFile(unlockedFile.getPath(), alias);
                                unlockedFile.delete();

                            } else {
                                Utils.pauseForKeyPress(scanner); // Solo si no es un archivo .txt
                            }
                        }

                        break;
                    case "4":

                        Utils.clearConsole();
                        File unlockedFile = Utils.listFiles(DATA_DIR_DECRYPT, scanner);
                        String alias = unlockedFile.getName().split("\\.")[0];
                        if (unlockedFile != null) {
                            String extension = cipherBox.decryptExtension(alias);
                            System.out.print(
                                    "La extensión original del archivo es: " + extension
                                            + ". ¿Deseas cambiarla? (s/n): ");
                            String choice2 = scanner.nextLine();
                            if (choice2.equalsIgnoreCase("s")) {
                                Utils.convertExtension(unlockedFile, extension);
                            }
                        }
                        break;
                    case "5":
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