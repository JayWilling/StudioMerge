package com.example.studiomerge.lib;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    public Hash() {}

    /**
     * Return the hashed version of the given String.
     *
     * Hash the given String using SHA-256 and return its hexadecimal
     * representation. Return null when the hashing operation is not
     * found.
     *
     * @param s String to be hashed
     * @return  Hexadecimal representation of the hashed String
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String hash(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                    s.getBytes(StandardCharsets.UTF_8));  // Requires Android 4.4 (KitKat)
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            // Log: NoSuchAlgorithmException when looking for SHA-256
            //      under MessageDigest object.
        }
        return null;
    }

    /**
     * Return the hexadecimal representation of the given byte array.
     *
     * @param bytes Byte array to be converted to hexadecimal
     * @return      Hexadecimal representation of the converted byte array
     */
    private String bytesToHex(byte[] bytes) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
