package com.jikexueyuan.cloudnote.observable;

import android.text.TextUtils;

import com.jikexueyuan.cloudnote.CloudNoteApp;
import com.jikexueyuan.cloudnote.db.entity.Note;
import com.jikexueyuan.cloudnote.db.entity.NoteEntity;
import com.jikexueyuan.cloudnote.utils.NetUtils;
import com.jikexueyuan.cloudnote.utils.NoteUtils;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 将笔记同步到bmob
 */
public class NotesSyncToBmobObservable {
    public static Observable syncToBmob() {
        //NoteUtils.getNotSyncNoteEntityList()为获取所有没有同步过的笔记
        return Observable.from(NoteUtils.getNotSyncNoteEntityList())
                .map(new Func1<NoteEntity, Note>() {
                    @Override
                    public Note call(final NoteEntity noteEntity) {
                        //判断是否有网络连接
                        boolean isConnected = NetUtils.isNetworkConnected(CloudNoteApp.getContext());
                        //将dao笔记对象转换为bmob笔记对象
                        Note note = noteEntity.convert2Bmob();
                        //判断该笔记是否已经同步过
                        if (TextUtils.isEmpty(noteEntity.getObjId())) {
                            if (isConnected) {
                                //保存到bmob
                                note.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String objectId, BmobException e) {
                                        if (e == null) {
                                            //保存成功则修改bmob id和同步flag
                                            noteEntity.setObjId(objectId);
                                            noteEntity.setIsSync(true);
                                        } else {
                                            noteEntity.setIsSync(false);
                                            Logger.d("bmob保存失败：" + e.getMessage() + "," + e.getErrorCode());
                                        }
                                        //将bmob id和同步flag更新到本地数据库
                                        CloudNoteApp.getNoteEntityDao().update(noteEntity);
                                    }
                                });
                            }
                        } else {
                            note.update(noteEntity.getObjId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        //更新同步flag
                                        noteEntity.setIsSync(true);
                                    } else {
                                        noteEntity.setIsSync(false);
                                        Logger.d("bmob更新失败：" + e.getMessage() + "," + e.getErrorCode());
                                    }
                                    //将同步flag更新到本地数据库
                                    CloudNoteApp.getNoteEntityDao().update(noteEntity);
                                }
                            });
                        }

                        return note;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
