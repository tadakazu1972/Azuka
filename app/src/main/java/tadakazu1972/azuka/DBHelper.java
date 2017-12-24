package tadakazu1972.azuka;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tadakazu on 2017/08/27.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context){
        super(context, "database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        //アイテム
        sqLiteDatabase.execSQL("create table item(_id integer primary key autoincrement, name text, type integer, number integer)");
        sqLiteDatabase.execSQL("insert into item(name, type, number) values('', '', '')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2){

    }

    public void insertItem(SQLiteDatabase db, String _name, int _type, int _number){
        ContentValues cv = new ContentValues();
        cv.put("name", _name);
        cv.put("type", _type);
        cv.put("number", _number);
        long id = db.insert("item", null, cv);
    }

    public void deleteItem(SQLiteDatabase db, String _name){
        String s = "name=" + _name;
        long id = db.delete("item", s, null);
    }
}
