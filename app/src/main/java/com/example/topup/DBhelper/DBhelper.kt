package com.example.paymenttopup.DBhelper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.paymenttopup.Model.Topup

class DBhelper(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VER) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY : String = ("CREATE TABLE $TABLE_NAME($COL_ID TEXT PRIMARY KEY,$COL_MONEY INTGEER)")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    companion object{
        private val DATABASE_VER = 1
        private val DATABASE_NAME = "Topup.db"

        private val TABLE_NAME = "Topup"
        val COL_ID = "id"
        private val COL_MONEY = "money"
    }

    val getTopupLog: List<Topup>
        get() {
            val showLog = ArrayList<Topup>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db: SQLiteDatabase = this.writableDatabase
            val cursor: Cursor = db.rawQuery(selectQuery,null)
            if(cursor.moveToFirst())
            {
                do{
                    val topup = Topup()
                    topup.id = cursor.getString(cursor.getColumnIndex(COL_ID))
                    topup.money = cursor.getInt(cursor.getColumnIndex(COL_MONEY))

                    showLog.add(topup)
                }while (cursor.moveToNext())
            }
            return showLog
        }

    fun deleteAll(){
        val selectQuery = "SELECT * FROM Topup"
        val db: SQLiteDatabase = this.writableDatabase
        db.delete("Topup","",null)
        db.close()
    }

    fun checkID(userID: String): List<Topup>{
        val checkUID = ArrayList<Topup>()
        val UID = userID
        val selectQuery = "SELECT * FROM Topup WHERE id = '$UID'"
        val db: SQLiteDatabase = this.writableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery,null)
        if(cursor.moveToFirst())
        {
            do{
                val topup = Topup()
                topup.id = cursor.getString(cursor.getColumnIndex(COL_ID))
                topup.money = cursor.getInt(cursor.getColumnIndex(COL_MONEY))
                checkUID.add(topup)
            }while (cursor.moveToNext())
        }
        return checkUID
    }


    fun getData(): List<Topup> {
        val showLog = ArrayList<Topup>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db: SQLiteDatabase = this.writableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery,null)
        if(cursor.moveToFirst())
        {
            do{
                val topup = Topup()
                topup.id = cursor.getString(cursor.getColumnIndex(COL_ID))
                topup.money = cursor.getInt(cursor.getColumnIndex(COL_MONEY))

                showLog.add(topup)
            }while (cursor.moveToNext())
        }
        return showLog
    }


    fun updateMoney(topup: Topup){
        val db:SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
//        values.put(COL_ID, topup.id)
        values.put(COL_MONEY, topup.money)
        db.update(TABLE_NAME,values, "id = '${topup.id}'",null)
        db.close()
    }

    fun addUser(userID: String):Topup {
        val topup = Topup()
        topup.id = userID
        val db:SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put(COL_ID, topup.id)
        db.insert(TABLE_NAME,null, values)
        db.close()
        return topup
    }

//    fun addMoney(userID: String,money: Int) {
//        val topup = Topup()
//        topup.id = userID
//        topup.money = money
//        val db:SQLiteDatabase = this.writableDatabase
//        val values = ContentValues()
//        values.put(COL_MONEY, topup.money)
//        db.insert(TABLE_NAME,null, values)
//        db.close()
//    }



//    fun addMoney(addMoney: Int) {
//        val topup = Topup()
//        topup.money = addMoney
//        val db:SQLiteDatabase = this.writableDatabase
//        val values = ContentValues()
//        values.put(COL_MONEY, +topup.money)
//
//        db.insert(TABLE_NAME,null, values)
//        db.close()
//    }
//
//    fun deleteMoney(deleteMoney: Int) {
//        val topup = Topup()
//        topup.money = deleteMoney
//        val db:SQLiteDatabase = this.writableDatabase
//        val values = ContentValues()
//        values.put(COL_MONEY, -topup.money)
//
//        db.insert(TABLE_NAME,null, values)
//        db.close()
//    }
}