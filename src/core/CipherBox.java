package core;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class CipherBox {
    private final String vaultDir;

    public CipherBox(String vaultDir) {
        this.vaultDir = vaultDir;
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

    // Método para guardar las claves en archivos
    private void saveKey(byte[] key, String path) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(key);
        }
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
    }

    // Cargar clave pública desde archivo
    private PublicKey loadPublicKey(String filePath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filePath).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    // Cargar clave privada desde archivo
    private PrivateKey loadPrivateKey(String filePath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filePath).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }
}
