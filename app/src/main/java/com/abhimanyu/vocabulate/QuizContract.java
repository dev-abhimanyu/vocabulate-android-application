package com.abhimanyu.vocabulate;

import android.provider.BaseColumns;

/**
 * Created by abhimanyu
 */
public class QuizContract {
    private QuizContract() {
    }

    public static class CategoriesTable implements BaseColumns {
        public static final String TABLE_NAME = "quiz_categories";
        public static final String COLUMN_NAME = "name";
    }

    public static class QuestionsTable implements BaseColumns {
        public static final String TABLE_NAME = "quiz_questions";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_OPTION1 = "option1";
        public static final String COLUMN_OPTION2 = "option2";
        public static final String COLUMN_OPTION3 = "option3";
        public static final String COLUMN_ANSWER_NR = "answer_nr";
        public static final String COLUMN_DIFFICULTY = "difficulty";
        public static final String COLUMN_CATEGORY_ID = "category_id";
    }
    public static class BookmarkTable implements BaseColumns{
        public static final String TABLE_NAME = "bookmarks";
        public static final String WORD = "word";
        public static final String EXPLANATION = "explanation";
    }
//    public static class NotificationTable implements BaseColumns{
//        public static final String TABLE_NAME = "notification_table";
//        public static final String WORD = "word";
//        public static final String EXPLANATION = "explanation";
//    }
}
