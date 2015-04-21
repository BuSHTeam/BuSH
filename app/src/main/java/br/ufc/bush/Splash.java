package br.ufc.bush;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class Splash extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 2850; //Duration
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img = (ImageView)findViewById(R.id.splashOnibus);
        img.setBackgroundResource(R.drawable.spin);

        // Get the background, which has been compiled to an AnimationDrawable object.
        final AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();

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
        }, SPLASH_DISPLAY_LENGTH);
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
}
