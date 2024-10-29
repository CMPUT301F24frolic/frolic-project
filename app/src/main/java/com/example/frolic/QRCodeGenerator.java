package com.example.frolic;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import android.graphics.Bitmap;
/**
 * Utility class for generating QR codes as bitmap images.
 * Provides a method to create a QR code from a specified string of data.
 */
public class QRCodeGenerator {
    /**
     * Generates a QR code bitmap based on the provided data string.
     *
     * @param data the data to encode within the QR code, will be the hashed event id.
     * @return a {@link Bitmap} image of the generated QR code, or {@code null} if an error occurs.
     */
    public static Bitmap generateQRCode(String data) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            // Encode the data into a QR code BitMatrix with specified dimensions.
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

            // Populate the Bitmap with pixels based on the BitMatrix
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
