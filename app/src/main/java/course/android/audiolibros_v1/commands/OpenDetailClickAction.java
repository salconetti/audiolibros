package course.android.audiolibros_v1.commands;

import course.android.audiolibros_v1.MainActivity;

/**
 * Created by Casa on 28/01/2017.
 */

public class OpenDetailClickAction implements ClickAction {
    private final MainActivity mainActivity;

    public OpenDetailClickAction(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    @Override
    public void execute(int position) {
        mainActivity.mostrarDetalle(position);
    }
}
