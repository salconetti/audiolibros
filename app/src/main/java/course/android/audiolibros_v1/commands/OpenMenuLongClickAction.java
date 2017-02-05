package course.android.audiolibros_v1.commands;

import android.view.View;

import course.android.audiolibros_v1.MainActivity;
import course.android.audiolibros_v1.fragments.SelectorFragment;

/**
 * Created by Casa on 28/01/2017.
 */
public class OpenMenuLongClickAction implements LongClickAction{
    private SelectorFragment selectorFragment;

    public OpenMenuLongClickAction(SelectorFragment selectorFragment) {
        this.selectorFragment = selectorFragment;
    }

    @Override
    public void execute(View view, int position) {
        selectorFragment.showItemMenu(view, position);
    }
}
