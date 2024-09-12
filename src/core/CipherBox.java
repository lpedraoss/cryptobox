package core;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class CipherBox {
    private final String vaultDir;
    private final String password; // Declarar la variable password

    public CipherBox(String vaultDir, String password) {
        this.vaultDir = vaultDir;
        this.password = password; // Inicializar la variable password
    }

    // Método para generar claves RSA
    public void generateRSAKeys(String alias) throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair pair = keyPairGen.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        saveKey(privateKey.getEncoded(), vaultDir + alias + ".private.key");
        saveKey(publicKey.getEncoded(), vaultDir + alias + ".public.key");

        System.out.println("Claves RSA generadas y guardadas.");
    }

    // Método para guardar las claves en archivos (Codificación Base64 y cifrado con contraseña)
    private void saveKey(byte[] key, String path) throws Exception {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] iv = cipher.getIV();
        byte[] encryptedKey = cipher.doFinal(key);

        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(salt);
            fos.write(iv);
            fos.write(encryptedKey);
        }
    }

    // Método para cargar clave pública desde archivo (Decodificación Base64 y descifrado con contraseña)
    private PublicKey loadPublicKey(String filePath) throws Exception {
        byte[] fileContent = Files.readAllBytes(new File(filePath).toPath());
        byte[] salt = Arrays.copyOfRange(fileContent, 0, 16);
        byte[] iv = Arrays.copyOfRange(fileContent, 16, 32);
        byte[] encryptedKey = Arrays.copyOfRange(fileContent, 32, fileContent.length);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        byte[] decodedKey = cipher.doFinal(encryptedKey);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    // Método para cargar clave privada desde archivo (Decodificación Base64 y descifrado con contraseña)
    private PrivateKey loadPrivateKey(String filePath) throws Exception {
        byte[] fileContent = Files.readAllBytes(new File(filePath).toPath());
        byte[] salt = Arrays.copyOfRange(fileContent, 0, 16);
        byte[] iv = Arrays.copyOfRange(fileContent, 16, 32);
        byte[] encryptedKey = Arrays.copyOfRange(fileContent, 32, fileContent.length);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        byte[] decodedKey = cipher.doFinal(encryptedKey);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    // Método para cifrar un archivo usando AES y RSA
    public void lockFile(String originalFilePath, String alias) throws Exception {
        // Generar claves RSA
        generateRSAKeys(alias);

        // Leer clave pública
        PublicKey publicKey = loadPublicKey(vaultDir + alias + ".public.key");

        // Generar clave AES
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey aesKey = keyGen.generateKey();

        // Cifrar contenido del archivo original
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        Cipher cipherAES = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherAES.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

        byte[] originalData = Files.readAllBytes(new File(originalFilePath).toPath());
        byte[] encryptedData = cipherAES.doFinal(originalData);

        // Guardar archivo cifrado
        String encryptedFilePath = vaultDir + alias + ".lock";
        try (FileOutputStream fos = new FileOutputStream(encryptedFilePath)) {
            fos.write(iv);
            fos.write(encryptedData);
        }

        // Guardar la extensión del archivo original en un archivo cifrado .extinfo
        String extinfoPath = vaultDir + alias + ".extinfo";
        String fileExtension = getFileExtension(originalFilePath);
        byte[] extinfoData = cipherAES.doFinal(fileExtension.getBytes());
        try (FileOutputStream fos = new FileOutputStream(extinfoPath)) {
            fos.write(iv);
            fos.write(extinfoData);
        }

        // Cifrar la clave AES con RSA
        Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherRSA.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedAESKey = cipherRSA.doFinal(aesKey.getEncoded());

        // Guardar clave AES cifrada
        String aesKeyPath = vaultDir + alias + ".key";
        try (FileOutputStream fos = new FileOutputStream(aesKeyPath)) {
            fos.write(encryptedAESKey);
        }

        System.out.println("Archivo cifrado y clave AES guardada.");
    }

    // Método para descifrar un archivo
    public void unlockFile(String encryptedFilePath, String alias) throws Exception {
        // Leer clave privada
        PrivateKey privateKey = loadPrivateKey(vaultDir + alias + ".private.key");

        // Leer clave AES cifrada
        byte[] encryptedAESKey = Files.readAllBytes(new File(vaultDir + alias + ".key").toPath());

        // Descifrar clave AES con RSA
        Cipher cipherRSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipherRSA.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = cipherRSA.doFinal(encryptedAESKey);

        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        // Leer archivo cifrado
        try (FileInputStream fis = new FileInputStream(encryptedFilePath)) {
            byte[] iv = new byte[16];
            fis.read(iv);
            byte[] encryptedData = fis.readAllBytes();

            // Descifrar datos
            Cipher cipherAES = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherAES.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
            byte[] originalData = cipherAES.doFinal(encryptedData);

            String decryptedFilePath = vaultDir + alias + ".unlocked";
            Files.write(new File(decryptedFilePath).toPath(), originalData);
            System.out.println("Archivo descifrado en: " + decryptedFilePath);
        }

        // Leer y descifrar el archivo .extinfo para obtener la extensión original
        String extinfoPath = vaultDir + alias + ".extinfo";
        try (FileInputStream fis = new FileInputStream(extinfoPath)) {
            byte[] iv = new byte[16];
            fis.read(iv);
            byte[] encryptedExtinfo = fis.readAllBytes();

            Cipher cipherAES = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherAES.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
            byte[] extinfoData = cipherAES.doFinal(encryptedExtinfo);

            String originalExtension = new String(extinfoData).trim();
            System.out.println("La extensión original del archivo descifrado es: " + originalExtension);
        }
    }

    // Método para obtener la extensión de un archivo
    private String getFileExtension(String filePath) {
        String extension = "";
        int i = filePath.lastIndexOf('.');
        if (i >= 0) {
            extension = filePath.substring(i + 1);
        }
        return extension;
    }
}