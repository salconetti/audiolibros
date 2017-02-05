package course.android.audiolibros_v1.presenters;

import android.os.Bundle;
import android.view.View;

import course.android.audiolibros_v1.Libro;
import course.android.audiolibros_v1.useCases.GetBookById;
import course.android.audiolibros_v1.useCases.IsAutoplaySelected;

/**
 * Created by Casa on 05/02/2017.
 */

public class DetallePresenter {
//    private LibroStorage libroStorage;
    private DetailView detailView;
    public static String ARG_ID_LIBRO = "id_libro";
    private GetBookById getBookById;
    private IsAutoplaySelected isAutoplaySelected;

    private DetallePresenter(GetBookById getBookById, IsAutoplaySelected isAutoplaySelected, DetailView detailView) {
        this.getBookById = getBookById;
        this.detailView = detailView;
        isAutoplaySelected = isAutoplaySelected;
    }

    public void setBook(Bundle args, View view){
        if (args != null) {
            int position = args.getInt(ARG_ID_LIBRO);
            sendBookToView(position, view);
        } else {
            sendBookToView(0, view);
        }
    }

    public void setBook(int bookId, View view){
        sendBookToView(bookId, view);
    }

    private void sendBookToView(int bookId, View view){
        Libro libro = getBookById.execute(bookId);
        detailView.setBook(libro, view);
    }

    public void autoPlay(){
        if (isAutoplaySelected.execute()) {
            detailView.play();
        }
    }

    public void play(Libro libro){
        detailView.play();
        detailView.updateWidget(libro);
        detailView.showNotification(libro);
    }

    public void exit(){
        detailView.release();
        detailView.clearNotification();
        detailView.clearWidget();
    }

    public static DetallePresenter create(GetBookById getBookById, IsAutoplaySelected isAutoplaySelected, DetailView detailView){
        return new DetallePresenter(getBookById, isAutoplaySelected,detailView);
    }

    public interface DetailView{
        void play();
        void release();
        void clearNotification();
        void clearWidget();
        void updateWidget(Libro libro);
        void showNotification(Libro libro);
        void setBook(Libro libro, View view);
    }
}
