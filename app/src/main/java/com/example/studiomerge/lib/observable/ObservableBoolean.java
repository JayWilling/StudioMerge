package com.example.studiomerge.lib.observable;

import java.util.Observable;

public class ObservableBoolean extends Observable {
    private boolean value;

    public ObservableBoolean(boolean value){this.value = value;}

    public void setValue(boolean value) {
        this.value = value;
        setChanged();
        notifyObservers(this.value);
    }
}
