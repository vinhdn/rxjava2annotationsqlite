package com.vinhdn.rxjavaannotation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.vinhdn.rxjavaannotation.anotations.DBColumnName;
import com.vinhdn.rxjavaannotation.anotations.DBIgnore;
import com.vinhdn.rxjavaannotation.anotations.DBTableName;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * Created by vinh on 8/21/17.
 */

public class LocalDB extends SQLiteAssetHelper{

    @SuppressLint("StaticFieldLeak")
    private static LocalDB db;

    public synchronized static LocalDB getInstance(Context context) {
        if (db == null) return db = new LocalDB(context);
        return db;
    }

    public LocalDB(Context context) {
        super(context,Config.STORE_DATA_NAME, null, Config.STORE_DATA_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    /**
     * Query in db with condition where with struct of sql query
     *
     * @param where ex: "name like '%vinh%' and time between 12345875 and 12366666"
     * @return
     */
    public <T extends DBModel> Single<List<T>> search(Class<T> type, @NonNull String where) {
        return Single.create(e -> {
            String dbName = type.getName();
            //Kiểm tra xem có dùng Annotation DBTableName
            if(type.getAnnotation(DBTableName.class) != null
                    && !TextUtils.isEmpty(type.getAnnotation(DBTableName.class).name())){
                dbName = type.getAnnotation(DBTableName.class).name();
            }
            //Tạo câu lệnh sql select all từ bảng có liên kết với Object <T>
            String sql = "select * from " + dbName + " where 1 = 1 " + where;// 1 = 1 ở đây để bạn có thể không truyền điều kiện search vào
            List<T> listData = new ArrayList<>();
            Cursor cur = null;
            SQLiteDatabase db = getReadableDatabase();
            try {
                //Chạy câu lệnh sql vừa tạo từ bên trên
                cur = db.rawQuery(sql, null);
                while (cur.moveToNext()) {
                    //TODO get and set data
                    T data = type.newInstance();
                    //Mình sẽ tạo ra một object Json có các key tương ứng với các trường của Object để tận dụng Gson parse dữ liệu
                    JSONObject jsonObject = new JSONObject();
                    //Get tất cả các trường của Object để query các cột tương ứng trong DB và put vào jsonObject
                    for(Field field : type.getDeclaredFields()) {
                        DBIgnore dbIgnoreAnnotation = field.getAnnotation(DBIgnore.class);
                        //Bỏ qua các trường được Ignore (sử dụng annotation DBIgnore)
                        if(dbIgnoreAnnotation != null && dbIgnoreAnnotation.value()){
                            continue;
                        }
                        DBColumnName nameAnnotation = field.getAnnotation(DBColumnName.class);
                        String dbField = field.getName();
                        //Kiểm tra sử dụng Annotation cho việc thay thế tên trường liên kết với DB
                        if(nameAnnotation != null && !TextUtils.isEmpty(nameAnnotation.value())) {
                            System.out.println("name : " + nameAnnotation.value());
                            dbField = nameAnnotation.value();
                        }
                        field.setAccessible(true);
                        int index = cur.getColumnIndex(dbField);
                        if(index >= 0) {
                            SerializedName nameFieldAnnotation = field.getAnnotation(SerializedName.class);
                            if(nameFieldAnnotation != null && !TextUtils.isEmpty(nameFieldAnnotation.value())) {
                                dbField = nameFieldAnnotation.value();
                            }else {
                                dbField = field.getName();
                            }
                            jsonObject.put(dbField, cur.getString(index));
                        }
                    }
                    //Mình sửa dụng Gson để parse Json vừa tạo bên trên thành Object
                    data = (new Gson()).fromJson(jsonObject.toString(), type);
                    listData.add(data);
                }
                //Trả về kết quả thành công cho đối tượng subscribe
                e.onSuccess(listData);
            } catch (Exception ex) {
                e.onError(ex);
            } finally {
                if (cur != null) {
                    cur.close();
                }
                if (db != null)
                    db.close();
            }
        });
    }
}
