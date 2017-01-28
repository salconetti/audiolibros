package course.android.audiolibros_v1;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import course.android.audiolibros_v1.fragments.SelectorFragment;

/**
 * Created by Casa on 09/01/2017.
 */

public class SplashScreen extends AppCompatActivity implements Animator.AnimatorListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Animation();
    }

    private void Animation(){
        TextView textView = (TextView) findViewById(R.id.text_splash);
        Animator anim = AnimatorInflater.loadAnimator(this,
                R.animator.splash_animator);
        anim.addListener(this);
        anim.setTarget(textView);
        anim.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.transition.entrada_izquierda, R.transition.salida_derecha);
        finish();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
