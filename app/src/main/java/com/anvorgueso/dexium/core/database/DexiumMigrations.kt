package com.anvorgueso.dexium.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * The database ran on `fallbackToDestructiveMigration()` alone up to version 5, which was
 * tolerable while every table was a refetchable cache. Teams are user-authored data, so from
 * here on schema changes get real migrations — dropping someone's saved teams on an app update
 * would be a bug, not a cache miss.
 *
 * The DDL must match what Room generates for [com.anvorgueso.dexium.core.database.entity.TeamEntity]
 * exactly, or Room's identity check fails at open time.
 */
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `team` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`memberIdsJson` TEXT NOT NULL, " +
                "`createdAt` INTEGER NOT NULL)"
        )
    }
}

/** Adds the guess-game records table. Same reasoning as [MIGRATION_5_6]: user-earned data. */
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `high_score` (" +
                "`mode` TEXT NOT NULL, " +
                "`score` INTEGER NOT NULL, " +
                "`total` INTEGER NOT NULL, " +
                "`achievedAt` INTEGER NOT NULL, " +
                "PRIMARY KEY(`mode`))"
        )
    }
}
