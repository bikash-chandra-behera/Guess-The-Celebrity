package com.example.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> urlList = new ArrayList<String>();
    ArrayList<String> nameList = new ArrayList<String>();
    int choosenImage = 0;
    int locatioOfCurrectAnswer = 0;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    String[] answers = new String[4];


    ImageView imageView;

    public void choosenButton(View view) {
        if(view.getTag().toString().equals(Integer.toString(locatioOfCurrectAnswer))){
            Toast.makeText(getApplicationContext(),"Currect",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext(),"Wrong! It was "+nameList.get(choosenImage),Toast.LENGTH_LONG).show();
        }
        newCreateQuestion();
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url =  new URL(urls[0]);
                HttpURLConnection urlConnection  = (HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                return bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class  DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url ;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();

                }


            } catch (IOException e) {
                e.printStackTrace();
            }


            return result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        String results = null;

        imageView = (ImageView)findViewById(R.id.imageView);
        button0 = (Button)findViewById(R.id.button);
        button1 = (Button)findViewById(R.id.button1);
        button2= (Button)findViewById(R.id.button2);
        button3= (Button)findViewById(R.id.button3);

        try {
            results = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = results.split("<div class=\"sidebarInnerContainer\">");



            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(results);
            while(m.find()){
               urlList.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(results);
            while(m.find()){
               nameList.add(m.group(1));
            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        newCreateQuestion();
    }
    public  void newCreateQuestion(){
        try {
            Random rand = new Random();
            choosenImage = rand.nextInt(urlList.size());
            DownloadImage downloadImage = new DownloadImage();
            Bitmap bitmap;
            bitmap = downloadImage.execute(urlList.get(choosenImage)).get();
            imageView.setImageBitmap(bitmap);
            locatioOfCurrectAnswer = rand.nextInt(4);
            int incurrectAnswerLocation;
            for (int i = 0; i < 4; i++) {

                if (i == locatioOfCurrectAnswer) {
                    answers[i] = nameList.get(choosenImage);
                } else {
                    incurrectAnswerLocation = rand.nextInt(urlList.size());
                    while (incurrectAnswerLocation == choosenImage) {
                        incurrectAnswerLocation = rand.nextInt(urlList.size());
                    }
                    answers[i] = nameList.get(incurrectAnswerLocation);

                }

            }


            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
