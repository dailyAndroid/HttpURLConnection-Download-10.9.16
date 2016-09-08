package com.example.hwhong.httpurlconnectiondownload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private ImageView imageView, imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageBitmap(null);
                imageView2.setImageBitmap(null);
                new LoadImg().start();
            }
        });
    }

    private class LoadImg extends Thread {

        private String[] urls = new String[] {"https://developer.spotify.com/wp-content/uploads/2016/07/logo@2x.png", "https://b.fastcompany.net/multisite_files/fastcompany/imagecache/slideshow_large/slideshow/2015/03/3043547-slide-s-3-spotifys-new-look-signals-its-identity-shift.jpg"};

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                double result = (double) msg.obj;
                setTitle("Progress: " + (int) result + "%");
            }
        };

        private Bitmap bitmap = null;
        private InputStream stream = null;
        private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        @Override
        public void run() {
            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                //used to read the byte array
                stream = connection.getInputStream();
                //the full size of the read item (when will it be done)
                double fullSize = connection.getContentLength();
                //the array to be read in
                byte[] array = new byte[64];
                //how much have been read
                int readSize = 0;
                //the percentage being read
                double percentage = 0;

                while ((readSize = stream.read(array)) != -1) {
                    outputStream.write(array, 0, readSize);
                    percentage += (readSize / fullSize) * 100;
                    //updating the progress of download
                    Message message = handler.obtainMessage(1, percentage);
                    handler.sendMessage(message);
                }

                byte[] result = outputStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                    }
                });
                
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                //tidy up
                 try {
                     stream.close();
                     outputStream.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

            }
        }
    }
}
