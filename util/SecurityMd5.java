package com.hbsi.util;

import java.security.MessageDigest;

public class SecurityMd5 {
	 public static String bytesToHexString(byte[] src) {
         StringBuilder stringBuilder = new StringBuilder("");
         if (src == null || src.length <= 0) {
             return null;
         }
         for (int i = 0; i < src.length; i++) {
             int v = src[i] & 0xFF;
             String hv = Integer.toHexString(v);
             if (hv.length() < 2) {
                 stringBuilder.append(0);
             }
             stringBuilder.append(hv);
         }
         return stringBuilder.toString();
     }

     /**
      * Analysis
      * @param hexString
      * @return
      */
     public static byte[] hexStringToBytes(String hexString) {
         if (hexString == null || hexString.equals("")) {
             return null;
         }
         hexString = hexString.toUpperCase();
         int length = hexString.length() / 2;
         char[] hexChars = hexString.toCharArray();
         byte[] d = new byte[length];
         for (int i = 0; i < length; i++) {
             int pos = i * 2;
             d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
         }
         return d;
     }

     /**
      * Print the specified byte array to the console
      * in hexadecimal format
      * @param b
      */
     public static void printHexString(byte[] b) {
         for (int i = 0; i < b.length; i++) {
             String hex = Integer.toHexString(b[i] & 0xFF);
             if (hex.length() == 1) {
                 hex = '0' + hex;
             }
             System.out.print(hex.toUpperCase());
         }

     }

     /**
      * Convert char to byte
      * @param c char
      * @return byte
      */
     private static byte charToByte(char c) {
         return (byte) "0123456789abcdef".indexOf(c);
     }

     /**
      * encryption
      * @param str
      * @return
      */
     public static String encode(String str) {
         String strDigest = "";
         try {
             /**
              * This MessageDigest class provides the functionality of
              * the message digest algorithm for the application
              * it uses try and catch
              */
             MessageDigest md5 = MessageDigest.getInstance("MD5");

             byte[] data;
             data = md5.digest(str.getBytes("utf-8"));// Convert to MD5
             strDigest = bytesToHexString(data);
         } catch (Exception ex) {
             throw new RuntimeException(ex);
         }
         return strDigest;
     }


	

}
