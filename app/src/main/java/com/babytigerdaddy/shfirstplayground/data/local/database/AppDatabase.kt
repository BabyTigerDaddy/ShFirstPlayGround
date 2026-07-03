package com.babytigerdaddy.shfirstplayground.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * v4 Room Database.
 *
 * v1 → v2: GrowthMilestoneEntity 추가. 미배포 단계라 schema migration 코드 X
 * (DatabaseBuilder.fallbackToDestructiveMigration 적용).
 * v2 → v3: TradeJournalEntity 추가 (v5 매매일지).
 * v3 → v4: TradeJournal에 tickers(종목) 컬럼 + MonthlyGoalEntity 추가 (상용화 확장).
 * v4 → v5: HoldingEntity 추가 (보유 종목 추적 — 와이프 요청).
 * v5 → v6: SoldRecordEntity 추가 (매도 내역 — 보유노트).
 * v6 → v7: AccountEntity + holding/sold_record에 accountId (다중계좌).
 *          기존 데이터 보존 마이그레이션([MIGRATION_6_7]) — 기본 계좌로 이어붙임.
 * v7 → v8: holding에 code(종목코드) 컬럼 — 시세 API 조회용([MIGRATION_7_8], 데이터 보존).
 * v8 → v9: StockMasterEntity(종목 마스터) 추가 — 이름→코드 검색용([MIGRATION_8_9], 데이터 보존).
 * v9 → v10: sold_record에 realizedOverride(실현손익 직접 보정) 컬럼([MIGRATION_9_10], 데이터 보존).
 */
@Database(
    entities = [
        HappyLogEntity::class,
        GrowthMilestoneEntity::class,
        TradeJournalEntity::class,
        MonthlyGoalEntity::class,
        HoldingEntity::class,
        SoldRecordEntity::class,
        AccountEntity::class,
        StockMasterEntity::class,
    ],
    version = 10,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun happyLogDao(): HappyLogDao
    abstract fun growthMilestoneDao(): GrowthMilestoneDao
    abstract fun tradeJournalDao(): TradeJournalDao
    abstract fun monthlyGoalDao(): MonthlyGoalDao
    abstract fun holdingDao(): HoldingDao
    abstract fun soldRecordDao(): SoldRecordDao
    abstract fun accountDao(): AccountDao
    abstract fun stockMasterDao(): StockMasterDao
}
