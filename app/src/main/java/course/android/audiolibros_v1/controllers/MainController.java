package course.android.audiolibros_v1.controllers;

import course.android.audiolibros_v1.infraestructure.storages.LibroStorage;

/**
 * Created by Casa on 05/02/2017.
 */

public class MainController {
    LibroStorage libroStorage;

    public MainController(LibroStorage libroStorage) {
        this.libroStorage = libroStorage;
    }

    public void saveLastBook(int id){
        libroStorage.saveLastBook(id);
    }

    public boolean hasLastBook() {
        return libroStorage.hasLastBook();
    }

    public int getLastBook() {
        return libroStorage.getLastBook();
    }
}
