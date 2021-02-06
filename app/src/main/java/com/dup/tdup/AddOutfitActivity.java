package com.dup.tdup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddOutfitActivity extends AppCompatActivity
{
    private ImageView imageView;
    private Button picker_btn;
    private Button insert_btn;
    private SeekBar sensitivity_bar;
    private ImageButton editbtn;
    private String url = "http://" + "192.168.0.108" + ":" + 5000 + "/";
    private DatabaseManager database;
    private LinearLayout progressBar;
    private Bitmap selected_bmp = null;
    MyCustomView myCustomView;
    private int[] background_color = new int[]{255,255,255};

    private static final int PICK_IMAGE = 100;
    private final String TAG = "A- AddOutfit: ";
    static
    {System.loadLibrary("native-lib");
        OpenCVLoader.initDebug();}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_outfit);

        final RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.imglayout);

        imageView = (ImageView) findViewById(R.id.imView_add_outfit);
        picker_btn = (Button) findViewById(R.id.button_searchGallery_add_outfit);
        insert_btn = (Button) findViewById(R.id.button_insert_add_outfit);
        progressBar=(LinearLayout)findViewById(R.id.llProgressBar);
        database = new DatabaseManager(this);
        editbtn = findViewById(R.id.editbutton);
        sensitivity_bar = (SeekBar) findViewById(R.id.sensitivity_bar_add_outfit);
        sensitivity_bar.setMax(155);
        sensitivity_bar.setMin(0);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                try {
                    handleSendText(intent); // Handle text being sent
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }

        picker_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(v == picker_btn) //start gallery activity
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }
            }
        });//end picker_btn onClick

        insert_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PopupMenu popupMenu = new PopupMenu(AddOutfitActivity.this, insert_btn);
                popupMenu.getMenuInflater().inflate(R.menu.menu_outfit_categories, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        Toast.makeText(getApplicationContext(), "please wait . . .",
                                Toast.LENGTH_SHORT);

                        String category="";
                        String title = item.getTitle().toString();
                        if(title.equals("TOP")){category = "top";}
                        if(title.equals("LONG WEARS")){category = "long_wears";}
                        if(title.equals("TROUSERS")){category = "trousers";}
                        if(title.equals("SHORTS AND SKIRTS")){category = "shorts_n_skirts";}

                        Bitmap bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                        boolean result = database.insertOutfit(category, bmp);
                        if(result) {
                            Toast.makeText(getApplicationContext(), "outfit has added into wardrobe", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                        else
                        {Toast.makeText(getApplicationContext(), "outfit can not be added !!", Toast.LENGTH_SHORT).show();}

                        return false;
                    }//end onMenuItemClick
                });//end setOnMenuItemClickListener

                popupMenu.setGravity(Gravity.CENTER);
                popupMenu.show();
                //sensitivity_bar.setVisibility(View.GONE);
            }
        });//end insert_btn onClick
        final Bitmap[] processed_bmp = {null};
        sensitivity_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                ImageProcessor processor = new ImageProcessor();
                processed_bmp[0] = processor.extractOutfit(selected_bmp,
                        progress, background_color);
                imageView.setImageBitmap(processed_bmp[0]);
                myCustomView = new MyCustomView(AddOutfitActivity.this, processed_bmp[0]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
                editbtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
                editbtn.setVisibility(View.VISIBLE);
            }
        });//end sensitivity_bar.setOnSeekBarChangeListener
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensitivity_bar.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                rootLayout.removeAllViews();
                rootLayout.addView(myCustomView);
                imageView.setImageBitmap(myCustomView.getSourceBitmap());
            }
        });
    }//end onCreate
    void handleSendText(Intent intent) throws IOException {
        progressBar.setVisibility(View.VISIBLE);
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        Log.i("TS",sharedText);
        if (sharedText != null)
        {
            String url_act=extractUrls(sharedText);
            url_act=url_act.replaceAll("https://","");
            url_act=url_act.replaceAll("/","+");
            url=url+url_act;
            postRequest("your message here", url);
        }
    }
    public Bitmap getBitmapFromURL(String url){
        ImageDownloader task=new ImageDownloader();
        Bitmap myImage = null;
        try{
            myImage=task.execute(url).get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return myImage;
    }
    public void display(String url) throws IOException {
        Bitmap bmp=getBitmapFromURL(url);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        selected_bmp = decoded.copy(Bitmap.Config.ARGB_8888, true);
        progressBar.setVisibility(View.GONE);
        imageView.setImageBitmap(decoded);
        picker_btn.setVisibility(View.GONE);
        insert_btn.setVisibility(View.VISIBLE);
        sensitivity_bar.setVisibility(View.VISIBLE);
    }
    public class ImageDownloader extends AsyncTask<String, Void , Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url=new URL(urls[0]);
                HttpsURLConnection connection=(HttpsURLConnection) url.openConnection();
                connection.connect();
                InputStream in=connection.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(in);
                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    private RequestBody buildRequestBody(String msg) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody requestBody=RequestBody.create(mediaType,msg);
        System.out.println(requestBody);
        return requestBody;
    }
    private void postRequest(String message,String URL)
    {
        RequestBody requestBody = buildRequestBody(message);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request
                .Builder()
                .post(requestBody)
                .url(URL)
                .build();
        System.out.println(request);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddOutfitActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        call.cancel();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                            Toast.makeText(MainActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
                            display(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            disAble(imageUri);
        }
    }
    public static String extractUrls(String text)
    {
        String containedUrl=null;
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrl=text.substring(urlMatcher.start(0),
                    urlMatcher.end(0));
        }

        return containedUrl;
    }
    public void disAble(Uri selectedImage)
    {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            selected_bmp = decoded.copy(Bitmap.Config.ARGB_8888, true);
            imageView.setImageBitmap(decoded);
            picker_btn.setVisibility(View.GONE);
            insert_btn.setVisibility(View.VISIBLE);
            sensitivity_bar.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Log.d(TAG, "IO exception " + e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(AddOutfitActivity.this,MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && data!=null)
        {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                selected_bmp = decoded.copy(Bitmap.Config.ARGB_8888, true);
                imageView.setImageBitmap(decoded);
                picker_btn.setVisibility(View.GONE);
                insert_btn.setVisibility(View.VISIBLE);
                sensitivity_bar.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.d(TAG, "IO exception " + e);
            }
        }
        else {
            Toast.makeText(this, "No image picked", Toast.LENGTH_SHORT).show();
        }//end if statement
    }
}//End onActivityResult//end activity class