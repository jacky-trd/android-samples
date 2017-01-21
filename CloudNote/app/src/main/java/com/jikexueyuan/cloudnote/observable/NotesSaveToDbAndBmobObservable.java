package com.jikexueyuan.cloudnote.observable;

import com.jikexueyuan.cloudnote.CloudNoteApp;
import com.jikexueyuan.cloudnote.db.entity.Note;
import com.jikexueyuan.cloudnote.db.entity.NoteEntity;
import com.jikexueyuan.cloudnote.utils.NetUtils;
import com.orhanobut.logger.Logger;

import java.util.Date;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 将笔记保存到本地数据库和bmob的Observable类
 */
public class NotesSaveToDbAndBmobObservable {
    public static Observable<NoteEntity> saveToDbAndBmob(final NoteEntity entity, final boolean isNew) {
        return Observable.create(new Observable.OnSubscribe<NoteEntity>() {

            @Override
            public void call(Subscriber<? super NoteEntity> subscriber) {
                //网络是否连接
                boolean isConnected = NetUtils.isNetworkConnected(CloudNoteApp.getContext());
                //是否是新添加的记录
                if (isNew) {
                    entity.setId(new Date().getTime());
                    if (isConnected) {
                        //保存到bmob
                        saveNewBmobByEntity(entity, isNew,subscriber);
                    } else {
                        //保存到本地，同时标记该笔记没有同步
                        entity.setIsSync(false);
                        CloudNoteApp.getNoteEntityDao().insert(entity);
                        subscriber.onNext(entity);
                        subscriber.onCompleted();
                    }
                } else {
                    if (isConnected) {
                        if (entity.getObjId() != null) {
                            //更新bmob上的对应笔记
                            updateBmobByEntity(entity, entity.getObjId(), isNew,subscriber);
                        } else {
                            //该笔记在bmob上不存在，保存到bmob
                            saveNewBmobByEntity(entity, isNew, subscriber);
                        }
                    } else {
                        //更新本地笔记，同时标记该笔记没有同步
                        entity.setIsSync(false);
                        CloudNoteApp.getNoteEntityDao().update(entity);
                        subscriber.onNext(entity);
                        subscriber.onCompleted();
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static void saveNewBmobByEntity(final NoteEntity entity, final boolean isNew, final Subscriber<? super NoteEntity> subscriber) {
        Note note = entity.convert2Bmob();
        note.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    //bmob保存成功，设置对应id，修改同步flag
                    entity.setObjId(objectId);
                    entity.setIsSync(true);
                } else {
                    //bmob保存失败
                    entity.setIsSync(false);
                    Logger.d("bmob保存失败：" + e.getMessage() + "," + e.getErrorCode());
                }

                if (isNew) {
                    //新笔记则插入
                    CloudNoteApp.getNoteEntityDao().insert(entity);
                } else {
                    //旧笔记则更新
                    CloudNoteApp.getNoteEntityDao().update(entity);
                }
                subscriber.onNext(entity);
                subscriber.onCompleted();
            }
        });
    }

    private static void updateBmobByEntity(final NoteEntity entity, String objectId, final boolean isNew, final Subscriber<? super NoteEntity> subscriber) {
        Note note = entity.convert2Bmob();
        note.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //bmob更新成功，修改flag
                    entity.setIsSync(true);
                } else {
                    //bmob更新失败
                    entity.setIsSync(false);
                    Logger.d("bmob更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
                //更新本地数据库笔记
                CloudNoteApp.getNoteEntityDao().update(entity);
                subscriber.onNext(entity);
                subscriber.onCompleted();
            }
        });
    }
}
