package com.example.capstone.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * [WordDatabase]
 * Room 데이터베이스 클래스
 * - 단어장 데이터를 영구 저장
 * - DAO를 통해 데이터 접근을 제공
 */
@Database(entities = [WordEntity::class], version = 1)
abstract class WordDatabase : RoomDatabase() {

    /** 단어 테이블에 접근할 수 있는 DAO 반환 함수 */
    abstract fun wordDao(): WordDao

    companion object {
        // 인스턴스 중복 생성을 방지하기 위한 volatile 선언 (메모리 가시성 보장)
        @Volatile private var INSTANCE: WordDatabase? = null

        /**
         * DB 인스턴스 획득 함수 (싱글톤으로 구성)
         * - 이미 생성된 인스턴스가 있으면 그대로 사용
         * - 없으면 새로 생성하여 반환
         */
        fun getDatabase(context: Context): WordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,       // 앱 전역 context 사용
                    WordDatabase::class.java,         // DB 클래스 지정
                    "word_database"                   // DB 파일 이름
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

