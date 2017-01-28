package course.android.audiolibros_v1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import course.android.audiolibros_v1.fragments.DetalleFragment;

public class AudioLibrosReceiver extends BroadcastReceiver {
    private DetalleFragment detalleFragment;

    public AudioLibrosReceiver(){}

    public AudioLibrosReceiver(DetalleFragment detalleFragment) {
        this.detalleFragment = detalleFragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(this.detalleFragment.isPlaying()){
            this.detalleFragment.pause();
        }
        else {
            this.detalleFragment.start();
        }
    }

    public void setDetalleFragment(DetalleFragment detalleFragment) {
        this.detalleFragment = detalleFragment;
    }
}
