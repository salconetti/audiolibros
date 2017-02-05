package course.android.audiolibros_v1.presenters;

import course.android.audiolibros_v1.useCases.GetLastBook;
import course.android.audiolibros_v1.useCases.HasLastBook;
import course.android.audiolibros_v1.useCases.SaveLastBook;

/**
 * Created by Casa on 05/02/2017.
 */

public class MainPresenter {
    private final View view;
    private final SaveLastBook saveLastBook;
    private final HasLastBook hasLastBook;
    private final GetLastBook getLastBook;

    private MainPresenter(SaveLastBook saveLastBook, HasLastBook hasLastBook,
                          GetLastBook getLastBook, MainPresenter.View view) {
        this.saveLastBook = saveLastBook;
        this.hasLastBook = hasLastBook;
        this.getLastBook = getLastBook;
        this.view = view;
    }
    public void clickFavoriteButton() {
        if (hasLastBook.execute()) {
            view.showDetailFragment(getLastBook.execute());
        } else {
            view.mostrarNoUltimaVisita();
        }
    }
    public void openDetalle(int id) {
        saveLastBook.execute(id);
        view.showDetailFragment(id);
    }
    public interface View {
        void showDetailFragment(int lastBook);
        void mostrarNoUltimaVisita();
    }

    public static MainPresenter create(SaveLastBook saveLastBook, HasLastBook hasLastBook,
                                       GetLastBook getLastBook, MainPresenter.View view){
        return new MainPresenter(saveLastBook, hasLastBook, getLastBook, view);
    }
}
