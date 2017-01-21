package com.jikexueyuan.cloudnote.observable;



import com.jikexueyuan.cloudnote.db.entity.NoteEntity;
import com.jikexueyuan.cloudnote.utils.NoteUtils;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 从数据库获取所有笔记的Observable类
 */
public class NotesFromDatabaseObservable {
    public static Observable<List<NoteEntity>> getObservableFromDB(){
        return Observable.create(new Observable.OnSubscribe<List<NoteEntity>>(){

            @Override
            public void call(Subscriber<? super List<NoteEntity>> subscriber) {

                //获取所有笔记
                List<NoteEntity> noteEntities = NoteUtils.getNoteEntityList();

                if (noteEntities != null) {
                    subscriber.onNext(noteEntities);
                }
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
