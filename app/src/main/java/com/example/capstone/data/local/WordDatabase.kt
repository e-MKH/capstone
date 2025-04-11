package com.example.capstone.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * ✅ [WordDatabase]
 * 단어장 데이터를 저장하기 위한 Room Database 클래스입니다.
 * DB 안에는 WordEntity 테이블이 포함되며, WordDao를 통해 접근합니다.
 */
@Database(entities = [WordEntity::class], version = 1)
abstract class WordDatabase : RoomDatabase() {

    // ✅ 단어 테이블에 접근할 수 있는 DAO 인터페이스 반환
    abstract fun wordDao(): WordDao

    companion object {
        // ✅ 앱 전체에서 DB 인스턴스를 하나만 유지하기 위한 싱글톤 패턴
        @Volatile private var INSTANCE: WordDatabase? = null

        /**
         * ✅ DB 인스턴스를 가져오는 함수
         * 없으면 새로 만들고, 있으면 기존 인스턴스를 반환합니다.
         */
        fun getDatabase(context: Context): WordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "word_database" // ✅ DB 이름
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

