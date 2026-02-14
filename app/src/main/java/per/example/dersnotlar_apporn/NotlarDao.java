package per.example.dersnotlar_apporn;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import java.util.ArrayList;

public class NotlarDao {
    SQLiteDatabase dbx;

    public ArrayList<Notlar> tumNotlar(DBConnection dbc) {

        ArrayList<Notlar> notlarArrayList = new ArrayList<>();
        dbx = dbc.getWritableDatabase();
        Cursor c = dbx.rawQuery("SELECT * FROM notlar", null);

        while (c.moveToNext()) {
            Notlar notlar = new Notlar(c.getString(c.getColumnIndexOrThrow("Not_id"))
                    , c.getString(c.getColumnIndexOrThrow("Ders_adi"))
                    , c.getInt(c.getColumnIndexOrThrow("Not1"))
                    , c.getInt(c.getColumnIndexOrThrow("Not2")));
            notlarArrayList.add(notlar);
        }

        return notlarArrayList;
    }

    public void notEkle(DBConnection dbc, String dersAdi, int not1, int not2) {

        dbx = dbc.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Ders_adi", dersAdi);
        cv.put("Not1", not1);
        cv.put("Not2", not2);

        dbx.insertOrThrow("notlar", null, cv);
        dbx.close();
    }

    public void notSil(DBConnection dbc, int notID) {

        dbx = dbc.getWritableDatabase();
        dbx.delete("notlar", "Not_id=?", new String[notID]);
        dbx.close();
    }

    public void notGuncelle(DBConnection dbc, String not_ID, String dersAdi, int not1, int not2) {

        dbx = dbc.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("Ders_adi", dersAdi);
        cv.put("Not1", not1);
        cv.put("Not2", not2);

        dbx.update("notlar", cv, "Not_id=?", new String[]{not_ID});
        dbx.close();
    }
}
