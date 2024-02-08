package com.akilisha.reactive.rx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import com.akilisha.reactive.Data;
import com.akilisha.reactive.Observer;
import com.akilisha.reactive.Reactive;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

public class DataSimulate {

    public static void main(String[] args) throws IOException {
        SomeEventGenerator gen = new SomeEventGenerator();
        gen.getObservable().subscribe(next -> {
            System.out.println("Event generated: " + next);
        });

        Data data = Reactive.observe(Data.class, gen.getObserver());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                data.setName(line);
                data.getName();
            }
        }
    }

    static class SomeEventGenerator {

        Observer watch;

        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
                watch = new Observer() {

                    @Override
                    public void set(Object source, String field, Object oldValue, Object newValue) {
                        emitter.onNext("updating value: " + newValue);
                    }

                    @Override
                    public void get(Object source, String field, Object value) {
                        emitter.onNext("accessing value: " + value);
                    }
                };
            }
        });

        public Observer getObserver() {
            return Objects.requireNonNull(watch, "You have to subscribed to Observable first before this step");
        }

        public Observable<String> getObservable() {
            return observable;
        }
    }
}
