package course.android.audiolibros_v1;

import android.support.v7.widget.SearchView;

import java.util.Observable;

/**
 * Created by Casa on 28/01/2017.
 */

public class SearchObservable extends Observable implements SearchView.OnQueryTextListener{
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        setChanged();
        notifyObservers(newText);
        return true;
    }
}
