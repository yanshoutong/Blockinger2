package org.blockinger2.game.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ScoreDataSource
{
    // Database fields
    private SQLiteDatabase database;
    private HighscoreOpenHelper dbHelper;
    private String[] allColumns = {HighscoreOpenHelper.COLUMN_ID,
        HighscoreOpenHelper.COLUMN_SCORE,
        HighscoreOpenHelper.COLUMN_PLAYERNAME};

    public ScoreDataSource(Context context)
    {
        dbHelper = new HighscoreOpenHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public Score createScore(long score, String playerName)
    {
        ContentValues values = new ContentValues();
        values.put(HighscoreOpenHelper.COLUMN_SCORE, score);
        values.put(HighscoreOpenHelper.COLUMN_PLAYERNAME, playerName);
        long insertId = database.insert(HighscoreOpenHelper.TABLE_HIGHSCORES, null, values);
        Cursor cursor = database.query(HighscoreOpenHelper.TABLE_HIGHSCORES,
            allColumns, HighscoreOpenHelper.COLUMN_ID + " = " + insertId, null,
            null, null, HighscoreOpenHelper.COLUMN_SCORE + " DESC");
        cursor.moveToFirst();
        Score newScore = cursorToScore(cursor);
        cursor.close();
        return newScore;
    }

    public void deleteScore(Score score)
    {
        long id = score.getId();
        //System.out.println("Comment deleted with id: " + id);
        database.delete(HighscoreOpenHelper.TABLE_HIGHSCORES, HighscoreOpenHelper.COLUMN_ID
            + " = " + id, null);
    }

    private Score cursorToScore(Cursor cursor)
    {
        Score score = new Score();
        score.setId(cursor.getLong(0));
        score.setScore(cursor.getLong(1));
        score.setName(cursor.getString(2));
        return score;
    }

    public Cursor getCursor()
    {
        return database.query(HighscoreOpenHelper.TABLE_HIGHSCORES,
            allColumns, null, null, null, null, HighscoreOpenHelper.COLUMN_SCORE + " DESC");
    }
}
