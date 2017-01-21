package com.jikexueyuan.cloudnote.utils;

import com.jikexueyuan.cloudnote.CloudNoteApp;
import com.jikexueyuan.cloudnote.db.dao.NoteEntityDao;
import com.jikexueyuan.cloudnote.db.entity.NoteEntity;

import java.util.List;

/**
 * Created by Happiness on 2016/12/16.
 */
public class NoteUtils {
    public static List<NoteEntity> getNoteEntityList(){
        List<NoteEntity> noteEntities;
        noteEntities = CloudNoteApp.getNoteEntityDao()
                .queryBuilder()
                .orderDesc(NoteEntityDao.Properties.Id)
                .list();
        return noteEntities;
    }

    public static List<NoteEntity> getNotSyncNoteEntityList(){
        List<NoteEntity> noteEntities;
        noteEntities = CloudNoteApp.getNoteEntityDao()
                .queryBuilder()
                .where(NoteEntityDao.Properties.IsSync.eq(false))
                .build()
                .list();
        return noteEntities;
    }
}
