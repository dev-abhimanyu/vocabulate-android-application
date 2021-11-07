package com.abhimanyu.vocabulate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.abhimanyu.vocabulate.QuizContract.*;
import com.abhimanyu.vocabulate.db.VocabulateData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by abhimanyu
 */
public class QuizDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "VocabDb.db";
    private static final int DATABASE_VERSION = 1;

    private static QuizDbHelper instance;

    public SQLiteDatabase db;

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized QuizDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new QuizDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " +
                CategoriesTable.TABLE_NAME + "( " +
                CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoriesTable.COLUMN_NAME + " TEXT " +
                ")";

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuestionsTable.TABLE_NAME + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NR + " INTEGER, " +
                QuestionsTable.COLUMN_DIFFICULTY + " TEXT, " +
                QuestionsTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                "FOREIGN KEY(" + QuestionsTable.COLUMN_CATEGORY_ID + ") REFERENCES " +
                CategoriesTable.TABLE_NAME + "(" + CategoriesTable._ID + ")" + "ON DELETE CASCADE" +
                ")";

        final String SQL_CREATE_BOOKMARKS_TABLE = "CREATE TABLE " +
                BookmarkTable.TABLE_NAME + " ( " +
                BookmarkTable._ID + " INTEGER, " +                  // PRIMARY KEY AUTOINCREMENT
                BookmarkTable.WORD + " TEXT, " +
                BookmarkTable.EXPLANATION + " TEXT " +
                ")";
//        final String SQL_CREATE_NOTIFICATION_TABLE = "CREATE TABLE "+
//                NotificationTable.TABLE_NAME + " ( " +
//                NotificationTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                NotificationTable.WORD + " TEXT, " +
//                NotificationTable.EXPLANATION + " TEXT "+
//                ")";

        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        db.execSQL(SQL_CREATE_BOOKMARKS_TABLE);
//        db.execSQL(SQL_CREATE_NOTIFICATION_TABLE);
        fillCategoriesTable();
        fillQuestionsTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoriesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BookmarkTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable() {
        Category c1 = new Category("Synonyms");              // Programming
        insertCategory(c1);
        Category c2 = new Category("Geography");
        insertCategory(c2);
        Category c3 = new Category("Math");
        insertCategory(c3);
    }

    public void addCategory(Category category) {
        db = getWritableDatabase();
        insertCategory(category);
    }

    public void addCategories(List<Category> categories) {
        db = getWritableDatabase();

        for (Category category : categories) {
            insertCategory(category);
        }
    }

    private void insertCategory(Category category) {
        ContentValues cv = new ContentValues();
        cv.put(CategoriesTable.COLUMN_NAME, category.getName());
        db.insert(CategoriesTable.TABLE_NAME, null, cv);
    }

    private void fillQuestionsTable() {
        VocabulateData vocabObj = new VocabulateData();
        HashMap<String, String> hm= vocabObj.VocabData();
        Object[] keys = hm.keySet().toArray();
        Object key = keys[new Random().nextInt(keys.length)];
        Object key2 = keys[new Random().nextInt(keys.length)];
        Object key3 = keys[new Random().nextInt(keys.length)];
        while(key2==key)
        {
            key2 = keys[new Random().nextInt(keys.length)];
        }
        while (key3==key||key3==key2)
        {
            key3 = keys[new Random().nextInt(keys.length)];
        }
        HashSet<String> hs =new HashSet<>();
        Question[] q =new Question[hm.size()];
        Random r = new Random();
        for(int i=0;i<hm.size()-1;i++) {
            int randInt=r.nextInt(4);
            while(randInt==0){
                randInt=r.nextInt(4);
            }
            if(randInt==1) {
                q[i] = new Question(key.toString(),
                        hm.get(key), hm.get(key2), hm.get(key3), 1,
                        Question.DIFFICULTY_EASY, Category.SYNONYMS);
                insertQuestion(q[i]);
            }
            else if(randInt==2) {
                q[i] = new Question(key.toString(),
                        hm.get(key2), hm.get(key), hm.get(key3), 2,
                        Question.DIFFICULTY_EASY, Category.SYNONYMS);
                insertQuestion(q[i]);
            }
            else
            {
                q[i] = new Question(key.toString(),
                        hm.get(key2), hm.get(key3), hm.get(key), 3,
                        Question.DIFFICULTY_EASY, Category.SYNONYMS);
                insertQuestion(q[i]);
            }
            if(!hs.contains(key.toString()))
                hs.add(key.toString());
            key2 = keys[new Random().nextInt(keys.length)];
            key3 = keys[new Random().nextInt(keys.length)];

            while(hs.contains(key.toString()))
            {
                key = keys[new Random().nextInt(keys.length)];
            }
            while(key2==key)
            {
                key2 = keys[new Random().nextInt(keys.length)];
            }
            while (key3==key||key3==key2)
            {
                key3 = keys[new Random().nextInt(keys.length)];
            }
//        Question q2 = new Question("Geography, Medium: B is correct",
//                "A", "B", "C", 2,
//                Question.DIFFICULTY_EASY, Category.SYNONYMS);
//        insertQuestion(q2);
//        Question q3 = new Question("Math, Hard: C is correct",
//                "A", "B", "C", 3,
//                Question.DIFFICULTY_HARD, Category.MATH);
//        insertQuestion(q3);
//        Question q4 = new Question("Math, Easy: A is correct",
//                "A", "B", "C", 1,
//                Question.DIFFICULTY_EASY, Category.MATH);
//        insertQuestion(q4);
//        Question q5 = new Question("Non existing, Easy: A is correct",
//                "A", "B", "C", 1,
//                Question.DIFFICULTY_EASY, 4);
//        insertQuestion(q5);
//        Question q6 = new Question("Non existing, Medium: B is correct",
//                "A", "B", "C", 2,
//                Question.DIFFICULTY_MEDIUM, 5);
//        insertQuestion(q6);
        }
    }

//    private void fillBookmarksTable()
//    {
//
//    }
    public void addQuestion(Question question) {
        db = getWritableDatabase();
        insertQuestion(question);
    }

    public void addQuestions(List<Question> questions) {
        db = getWritableDatabase();

        for (Question question : questions) {
            insertQuestion(question);
        }
    }

    private void insertQuestion(Question question) {
        ContentValues cv = new ContentValues();
        cv.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
        cv.put(QuestionsTable.COLUMN_DIFFICULTY, question.getDifficulty());
        cv.put(QuestionsTable.COLUMN_CATEGORY_ID, question.getCategoryID());
        db.insert(QuestionsTable.TABLE_NAME, null, cv);
    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + CategoriesTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt(c.getColumnIndex(CategoriesTable._ID)));
                category.setName(c.getString(c.getColumnIndex(CategoriesTable.COLUMN_NAME)));
                categoryList.add(category);
            } while (c.moveToNext());
        }

        c.close();
        return categoryList;
    }

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuestionsTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                question.setDifficulty(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }

    public ArrayList<Question> getQuestions(int categoryID, String difficulty) {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        String selection = QuestionsTable.COLUMN_CATEGORY_ID + " = ? " +
                " AND " + QuestionsTable.COLUMN_DIFFICULTY + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(categoryID), difficulty};

        Cursor c = db.query(
                QuestionsTable.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        ArrayList<Integer> quesDone = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuestionsTable._ID)));
                quesDone.add(c.getColumnIndex(QuestionsTable._ID));
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                question.setDifficulty(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);
            } while (c.moveToNext());
        }
        System.out.print(quesDone);
        c.close();
        return questionList;
    }
}