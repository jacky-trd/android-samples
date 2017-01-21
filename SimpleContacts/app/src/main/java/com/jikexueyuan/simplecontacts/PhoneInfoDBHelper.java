package com.jikexueyuan.simplecontacts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类，用于查询和插入通讯录
 */
public class PhoneInfoDBHelper {

    /**
     * 目的：查询通讯录中的所有记录
     * 输入参数：
     * context：传入的环境上下文
     * 输出：
     * List<PersonalPhoneInfo>：所有通讯录记录的列表
    */
    public static List<PersonalPhoneInfo> getPhoneInfo(Context context) {

        List<PersonalPhoneInfo> lists = new ArrayList<PersonalPhoneInfo>();

        Uri uri = Uri.parse(context.getString(R.string.uriContact));
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{Data._ID}, null, null, null);

        while(cursor.moveToNext()){
            StringBuilder strBuilder = new StringBuilder();
            int id = cursor.getInt(0);
            strBuilder.append(context.getString(R.string.idEqualsTo)+id);
            uri = Uri.parse(context.getString(R.string.uriContactPath)+id+context.getString(R.string.plusDataPath));
            Cursor cursor2 = resolver.query(uri, new String[]{Data.DATA1,Data.MIMETYPE}, null,null, null);

            String data = null,phoneName = null,phoneNumber=null;
            while(cursor2.moveToNext()){
                data = cursor2.getString(cursor2.getColumnIndex(context.getString(R.string.data1)));

                if(cursor2.getString(cursor2.getColumnIndex(context.getString(R.string.mimeType))).equals(context.getString(R.string.nameColumn))){
                    phoneName = data;
                }
                else if(cursor2.getString(cursor2.getColumnIndex(context.getString(R.string.mimeType))).equals(context.getString(R.string.phoneColumn))){
                    phoneNumber = data;
                }
            }
            PersonalPhoneInfo info = new PersonalPhoneInfo(phoneName, phoneNumber);
            lists.add(info);
        }
        return lists;
    }

    /**
     * 目的：向通讯录中插入一条新记录
     * 输入参数：
     * context：传入的环境上下文
     * name：要插入的姓名
     * number：要插入的电话号码
     * 输出：无
     */
    public static void insertPhoneInfo(Context context, String name, String number){

        Uri uri = Uri.parse(context.getString(R.string.uriRawContact));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        long contact_id = ContentUris.parseId(resolver.insert(uri, values));

        uri = Uri.parse(context.getString(R.string.uriDataContact));
        //添加姓名
        values.put(context.getString(R.string.rawContactID), contact_id);
        values.put(Data.MIMETYPE,context.getString(R.string.nameColumn));
        values.put(context.getString(R.string.data2), name);
        values.put(context.getString(R.string.data1), name);
        resolver.insert(uri, values);
        values.clear();
        //添加电话号码
        values.put(context.getString(R.string.rawContactID), contact_id);
        values.put(Data.MIMETYPE,context.getString(R.string.phoneColumn));
        values.put(context.getString(R.string.data2), context.getString(R.string.standForPhone));
        values.put(context.getString(R.string.data1), number);
        resolver.insert(uri, values);
        values.clear();
    }
}
