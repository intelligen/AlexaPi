package project.vyas.audiorecorder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.Toast;

import com.moczul.ok2curl.CurlInterceptor;
import com.moczul.ok2curl.logger.Loggable;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends Activity {
    Button play,stop,record;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    ImageView imageView;
    Animation pulse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play=(Button)findViewById(R.id.button3);
        stop=(Button)findViewById(R.id.button2);
        record=(Button)findViewById(R.id.button);

        stop.setEnabled(false);
        play.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";


        imageView = (ImageView) findViewById(R.id.imageView);
        pulse= AnimationUtils.loadAnimation(this, R.anim.pulse);

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setAudioChannels(1);
        myAudioRecorder.setAudioEncodingBitRate(16);
        myAudioRecorder.setAudioSamplingRate(16000);
        myAudioRecorder.setOutputFile(outputFile);
        pulse.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(pulse);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                }

                catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                record.setEnabled(false);
                stop.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder  = null;

                record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
                Uploader b = new Uploader(MainActivity.this);
                b.execute();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException {
                MediaPlayer m = new MediaPlayer();

                try {
                    m.setDataSource(outputFile);
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      //  if (id == R.id.action_settings) {
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    public class Uploader extends AsyncTask<Void,Void,Void>{

       OkHttpClient client = new OkHttpClient.Builder()
               .addInterceptor(new CurlInterceptor(new Loggable() {
                        @Override
                        public void log(String message) {
                            Log.v("Ok2Curl", message);
                        }
                    }))
               .build();

        Context c;
        public Uploader(Context c){
            this.c =c;
        }

        public ProgressDialog progressDialog;
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(c);
            progressDialog.setMessage("Uploading");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void ...params){
            outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
            File file = new File(outputFile);
            RequestBody formBody = RequestBody.create(MediaType.parse("media/mpeg"), file);
                Request request = new Request.Builder()
                        .header("User-Agent","Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5")
                        .url("Enter url here")
                        .post(formBody)
                        .build();
            try {
                Response response = client.newCall(request).execute();
                Log.d("Resp",response.body().string());
            }catch(Exception e){
                Log.d("Failed",e.toString());}

            return null;
        }

        @Override
        protected void onPostExecute(Void params){
            progressDialog.dismiss();
        }


    }

}
