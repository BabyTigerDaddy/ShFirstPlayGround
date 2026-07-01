package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v6 → v7: 다중계좌 도입.
 *
 * 핵심 — 기존 보유·매도내역을 하나도 날리지 않고 기본 계좌('default')로 이어붙인다.
 * fallbackToDestructiveMigration이 걸려 있어도 이 마이그레이션이 있으면 6→7은 데이터 보존.
 *
 * - account 테이블 생성(Room 기대 스키마와 동일한 형태)
 * - 기본 계좌 1개 삽입('내 계좌')
 * - holding·sold_record에 accountId 컬럼 추가(기존 행은 DEFAULT 'default'로 채워짐)
 */
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `account` (" +
                "`id` TEXT NOT NULL, `name` TEXT NOT NULL, " +
                "`sortOrder` INTEGER NOT NULL, `createdAt` TEXT NOT NULL, " +
                "PRIMARY KEY(`id`))",
        )
        db.execSQL(
            "INSERT OR IGNORE INTO `account` (`id`, `name`, `sortOrder`, `createdAt`) " +
                "VALUES ('default', '내 계좌', 0, '2026-01-01T00:00:00')",
        )
        db.execSQL(
            "ALTER TABLE `holding` ADD COLUMN `accountId` TEXT NOT NULL DEFAULT 'default'",
        )
        db.execSQL(
            "ALTER TABLE `sold_record` ADD COLUMN `accountId` TEXT NOT NULL DEFAULT 'default'",
        )
    }
}

/**
 * v7 → v8: 보유 종목에 종목코드(code) 컬럼 추가 — 시세 API 자동 조회용.
 * 기존 보유는 코드 빈 문자열로 채워짐(수동 입력 fallback 유지).
 */
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE `holding` ADD COLUMN `code` TEXT NOT NULL DEFAULT ''",
        )
    }
}

/**
 * v8 → v9: 종목 마스터(stock_master) 테이블 추가 — 이름→코드 검색용.
 * 새 테이블만 만들 뿐 보유·매도 데이터는 건드리지 않는다(데이터 보존).
 */
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `stock_master` (" +
                "`code` TEXT NOT NULL, `name` TEXT NOT NULL, `market` TEXT NOT NULL, " +
                "PRIMARY KEY(`code`))",
        )
    }
}
