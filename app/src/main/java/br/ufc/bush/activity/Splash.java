package br.ufc.bush.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import br.ufc.bush.R;


public class Splash extends Activity {

    ImageView img;
    AnimationDrawable frameAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img = (ImageView)findViewById(R.id.splashOnibus);
        img.setBackgroundResource(R.drawable.spin);

        // Get the background, which has been compiled to an AnimationDrawable object.
        frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                frameAnimation.stop();
                Intent openMainActivity =  new Intent(Splash.this,MainActivity.class);
                startActivity(openMainActivity);
                finish();
            }
        }, getTotalDuration());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public int getTotalDuration() {

        int iDuration = 0;

        for (int i = 0; i < frameAnimation.getNumberOfFrames(); i++) {
            iDuration += frameAnimation.getDuration(i);
        }

        return iDuration;
    }
}
