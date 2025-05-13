package com.example.capstone.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.capstone.data.local.converter.DateConverter
import com.example.capstone.data.local.entity.User

@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    // DAO 접근 메서드
//    abstract fun userDao(): UserDao

    companion object {
        // 싱글톤 인스턴스
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "capstone_database"
                )
                    .fallbackToDestructiveMigration() // 스키마 버전 변경 시 기존 데이터 삭제
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}