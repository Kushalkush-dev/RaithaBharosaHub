package com.raithabharosa.hub.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.raithabharosa.hub.data.local.entity.CropHistoryEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CropHistoryDao_Impl implements CropHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CropHistoryEntity> __insertionAdapterOfCropHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllForFarmer;

  public CropHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCropHistoryEntity = new EntityInsertionAdapter<CropHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `crop_history` (`id`,`farmerId`,`cropType`,`sowingDate`,`harvestDate`,`yield`,`notes`,`season`,`year`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CropHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getFarmerId());
        statement.bindString(3, entity.getCropType());
        statement.bindLong(4, entity.getSowingDate());
        if (entity.getHarvestDate() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getHarvestDate());
        }
        statement.bindDouble(6, entity.getYield());
        statement.bindString(7, entity.getNotes());
        statement.bindString(8, entity.getSeason());
        statement.bindLong(9, entity.getYear());
      }
    };
    this.__preparedStmtOfDeleteAllForFarmer = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM crop_history WHERE farmerId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertCropHistory(final CropHistoryEntity history,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCropHistoryEntity.insertAndReturnId(history);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllForFarmer(final long farmerId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllForFarmer.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, farmerId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllForFarmer.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CropHistoryEntity>> getCropHistory(final long farmerId) {
    final String _sql = "SELECT * FROM crop_history WHERE farmerId = ? ORDER BY sowingDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, farmerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"crop_history"}, new Callable<List<CropHistoryEntity>>() {
      @Override
      @NonNull
      public List<CropHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfCropType = CursorUtil.getColumnIndexOrThrow(_cursor, "cropType");
          final int _cursorIndexOfSowingDate = CursorUtil.getColumnIndexOrThrow(_cursor, "sowingDate");
          final int _cursorIndexOfHarvestDate = CursorUtil.getColumnIndexOrThrow(_cursor, "harvestDate");
          final int _cursorIndexOfYield = CursorUtil.getColumnIndexOrThrow(_cursor, "yield");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfSeason = CursorUtil.getColumnIndexOrThrow(_cursor, "season");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final List<CropHistoryEntity> _result = new ArrayList<CropHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CropHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final String _tmpCropType;
            _tmpCropType = _cursor.getString(_cursorIndexOfCropType);
            final long _tmpSowingDate;
            _tmpSowingDate = _cursor.getLong(_cursorIndexOfSowingDate);
            final Long _tmpHarvestDate;
            if (_cursor.isNull(_cursorIndexOfHarvestDate)) {
              _tmpHarvestDate = null;
            } else {
              _tmpHarvestDate = _cursor.getLong(_cursorIndexOfHarvestDate);
            }
            final float _tmpYield;
            _tmpYield = _cursor.getFloat(_cursorIndexOfYield);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final String _tmpSeason;
            _tmpSeason = _cursor.getString(_cursorIndexOfSeason);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            _item = new CropHistoryEntity(_tmpId,_tmpFarmerId,_tmpCropType,_tmpSowingDate,_tmpHarvestDate,_tmpYield,_tmpNotes,_tmpSeason,_tmpYear);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CropHistoryEntity>> getCropHistoryBySeason(final long farmerId,
      final String season) {
    final String _sql = "SELECT * FROM crop_history WHERE farmerId = ? AND season = ? ORDER BY sowingDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, farmerId);
    _argIndex = 2;
    _statement.bindString(_argIndex, season);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"crop_history"}, new Callable<List<CropHistoryEntity>>() {
      @Override
      @NonNull
      public List<CropHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfCropType = CursorUtil.getColumnIndexOrThrow(_cursor, "cropType");
          final int _cursorIndexOfSowingDate = CursorUtil.getColumnIndexOrThrow(_cursor, "sowingDate");
          final int _cursorIndexOfHarvestDate = CursorUtil.getColumnIndexOrThrow(_cursor, "harvestDate");
          final int _cursorIndexOfYield = CursorUtil.getColumnIndexOrThrow(_cursor, "yield");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfSeason = CursorUtil.getColumnIndexOrThrow(_cursor, "season");
          final int _cursorIndexOfYear = CursorUtil.getColumnIndexOrThrow(_cursor, "year");
          final List<CropHistoryEntity> _result = new ArrayList<CropHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CropHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final String _tmpCropType;
            _tmpCropType = _cursor.getString(_cursorIndexOfCropType);
            final long _tmpSowingDate;
            _tmpSowingDate = _cursor.getLong(_cursorIndexOfSowingDate);
            final Long _tmpHarvestDate;
            if (_cursor.isNull(_cursorIndexOfHarvestDate)) {
              _tmpHarvestDate = null;
            } else {
              _tmpHarvestDate = _cursor.getLong(_cursorIndexOfHarvestDate);
            }
            final float _tmpYield;
            _tmpYield = _cursor.getFloat(_cursorIndexOfYield);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final String _tmpSeason;
            _tmpSeason = _cursor.getString(_cursorIndexOfSeason);
            final int _tmpYear;
            _tmpYear = _cursor.getInt(_cursorIndexOfYear);
            _item = new CropHistoryEntity(_tmpId,_tmpFarmerId,_tmpCropType,_tmpSowingDate,_tmpHarvestDate,_tmpYield,_tmpNotes,_tmpSeason,_tmpYear);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
