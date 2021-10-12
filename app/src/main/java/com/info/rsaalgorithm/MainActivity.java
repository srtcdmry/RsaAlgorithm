package com.info.rsaalgorithm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import javax.crypto.Cipher;

public class MainActivity extends AppCompatActivity  {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private TextView textView,textView2,textView3;
    private Button button;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView2 = findViewById(R.id.textView2);
        textView3=findViewById(R.id.textView3);
        button=findViewById(R.id.button2);
        editText=findViewById(R.id.editTextTextPersonName);


        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA"); //ortak ve özel anahtar çiftleri oluşturmak için kullanılır.
            generator.initialize(2048);  //
            KeyPair pair = generator.generateKeyPair(); // her çağırıldığında yeni bir anahtar çifti oluşturur.
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        } catch (Exception ignored) {
        }
        try {
            if(isCorrect()==true){
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String decryptedMessage = null;
                        String encryptedMessage = null;
                        try {
                            encryptedMessage = encrypt(editText.getText().toString());
                            //

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            decryptedMessage = decrypt(encryptedMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        textView2.setText(encryptedMessage);
                        System.out.println("Encrypted:\n"+encryptedMessage);
                        textView3.setText(decryptedMessage);
                        System.out.println("Decrypted:\n"+decryptedMessage);
                    }
                });
            }else {
                Log.e("Error:","İmza doğrulanamadı.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }






    }
//    public KeyPair generateKeyPair() throws Exception {
//        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA"); //ortak ve özel anahtar çiftleri oluşturmak için kullanılır.
//        generator.initialize(2048);  //
//        KeyPair pair = generator.generateKeyPair(); // her çağırıldığında yeni bir anahtar çifti oluşturur.
//        privateKey = pair.getPrivate();
//        publicKey = pair.getPublic();
//        return pair;
//    }


    public String encrypt(String message) throws Exception{

        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        //cipher.update(messageToBytes);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }
    private String encode(byte[] data){
        return Base64.encodeToString(data,Base64.DEFAULT);
   }

    public String decrypt(String encryptedMessage) throws Exception{
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage,"UTF8");
    }
    private byte[] decode(String data){
        return Base64.decode(data,Base64.DEFAULT);
    }
    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes("UTF8"));

        byte[] signature = privateSignature.sign();

        return Base64.encodeToString(signature,Base64.DEFAULT);
    } public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);

        publicSignature.update(plainText.getBytes("UTF8"));

        byte[] signatureBytes = Base64.decode(signature,Base64.DEFAULT);

        return publicSignature.verify(signatureBytes);
    }
    public static boolean isCorrect() throws Exception{
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");

        KeyPair pair = generator.generateKeyPair();
        String signature = sign("123asd", pair.getPrivate());

        boolean isCorrect = verify("123asd", signature, pair.getPublic());
        System.out.println("Signature correct: " + isCorrect);
        return isCorrect;
    }


}