package com.raithabharosa.hub.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.raithabharosa.hub.data.local.entity.SoilDataEntity;
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
public final class SoilDataDao_Impl implements SoilDataDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SoilDataEntity> __insertionAdapterOfSoilDataEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllForFarmer;

  public SoilDataDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSoilDataEntity = new EntityInsertionAdapter<SoilDataEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `soil_data` (`id`,`farmerId`,`moisture`,`nitrogen`,`phosphorus`,`potassium`,`ph`,`temperature`,`recordedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SoilDataEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getFarmerId());
        statement.bindDouble(3, entity.getMoisture());
        statement.bindDouble(4, entity.getNitrogen());
        statement.bindDouble(5, entity.getPhosphorus());
        statement.bindDouble(6, entity.getPotassium());
        statement.bindDouble(7, entity.getPh());
        statement.bindDouble(8, entity.getTemperature());
        statement.bindLong(9, entity.getRecordedAt());
      }
    };
    this.__preparedStmtOfDeleteAllForFarmer = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM soil_data WHERE farmerId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertSoilData(final SoilDataEntity soilData,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSoilDataEntity.insertAndReturnId(soilData);
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
  public Flow<SoilDataEntity> getLatestSoilData(final long farmerId) {
    final String _sql = "SELECT * FROM soil_data WHERE farmerId = ? ORDER BY recordedAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, farmerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"soil_data"}, new Callable<SoilDataEntity>() {
      @Override
      @Nullable
      public SoilDataEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfMoisture = CursorUtil.getColumnIndexOrThrow(_cursor, "moisture");
          final int _cursorIndexOfNitrogen = CursorUtil.getColumnIndexOrThrow(_cursor, "nitrogen");
          final int _cursorIndexOfPhosphorus = CursorUtil.getColumnIndexOrThrow(_cursor, "phosphorus");
          final int _cursorIndexOfPotassium = CursorUtil.getColumnIndexOrThrow(_cursor, "potassium");
          final int _cursorIndexOfPh = CursorUtil.getColumnIndexOrThrow(_cursor, "ph");
          final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
          final int _cursorIndexOfRecordedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedAt");
          final SoilDataEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final float _tmpMoisture;
            _tmpMoisture = _cursor.getFloat(_cursorIndexOfMoisture);
            final float _tmpNitrogen;
            _tmpNitrogen = _cursor.getFloat(_cursorIndexOfNitrogen);
            final float _tmpPhosphorus;
            _tmpPhosphorus = _cursor.getFloat(_cursorIndexOfPhosphorus);
            final float _tmpPotassium;
            _tmpPotassium = _cursor.getFloat(_cursorIndexOfPotassium);
            final float _tmpPh;
            _tmpPh = _cursor.getFloat(_cursorIndexOfPh);
            final float _tmpTemperature;
            _tmpTemperature = _cursor.getFloat(_cursorIndexOfTemperature);
            final long _tmpRecordedAt;
            _tmpRecordedAt = _cursor.getLong(_cursorIndexOfRecordedAt);
            _result = new SoilDataEntity(_tmpId,_tmpFarmerId,_tmpMoisture,_tmpNitrogen,_tmpPhosphorus,_tmpPotassium,_tmpPh,_tmpTemperature,_tmpRecordedAt);
          } else {
            _result = null;
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
  public Flow<List<SoilDataEntity>> getSoilDataHistory(final long farmerId) {
    final String _sql = "SELECT * FROM soil_data WHERE farmerId = ? ORDER BY recordedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, farmerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"soil_data"}, new Callable<List<SoilDataEntity>>() {
      @Override
      @NonNull
      public List<SoilDataEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfMoisture = CursorUtil.getColumnIndexOrThrow(_cursor, "moisture");
          final int _cursorIndexOfNitrogen = CursorUtil.getColumnIndexOrThrow(_cursor, "nitrogen");
          final int _cursorIndexOfPhosphorus = CursorUtil.getColumnIndexOrThrow(_cursor, "phosphorus");
          final int _cursorIndexOfPotassium = CursorUtil.getColumnIndexOrThrow(_cursor, "potassium");
          final int _cursorIndexOfPh = CursorUtil.getColumnIndexOrThrow(_cursor, "ph");
          final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
          final int _cursorIndexOfRecordedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedAt");
          final List<SoilDataEntity> _result = new ArrayList<SoilDataEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SoilDataEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final float _tmpMoisture;
            _tmpMoisture = _cursor.getFloat(_cursorIndexOfMoisture);
            final float _tmpNitrogen;
            _tmpNitrogen = _cursor.getFloat(_cursorIndexOfNitrogen);
            final float _tmpPhosphorus;
            _tmpPhosphorus = _cursor.getFloat(_cursorIndexOfPhosphorus);
            final float _tmpPotassium;
            _tmpPotassium = _cursor.getFloat(_cursorIndexOfPotassium);
            final float _tmpPh;
            _tmpPh = _cursor.getFloat(_cursorIndexOfPh);
            final float _tmpTemperature;
            _tmpTemperature = _cursor.getFloat(_cursorIndexOfTemperature);
            final long _tmpRecordedAt;
            _tmpRecordedAt = _cursor.getLong(_cursorIndexOfRecordedAt);
            _item = new SoilDataEntity(_tmpId,_tmpFarmerId,_tmpMoisture,_tmpNitrogen,_tmpPhosphorus,_tmpPotassium,_tmpPh,_tmpTemperature,_tmpRecordedAt);
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
  public Flow<List<SoilDataEntity>> getSoilDataInRange(final long farmerId, final long startTime) {
    final String _sql = "SELECT * FROM soil_data WHERE farmerId = ? AND recordedAt >= ? ORDER BY recordedAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, farmerId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"soil_data"}, new Callable<List<SoilDataEntity>>() {
      @Override
      @NonNull
      public List<SoilDataEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfMoisture = CursorUtil.getColumnIndexOrThrow(_cursor, "moisture");
          final int _cursorIndexOfNitrogen = CursorUtil.getColumnIndexOrThrow(_cursor, "nitrogen");
          final int _cursorIndexOfPhosphorus = CursorUtil.getColumnIndexOrThrow(_cursor, "phosphorus");
          final int _cursorIndexOfPotassium = CursorUtil.getColumnIndexOrThrow(_cursor, "potassium");
          final int _cursorIndexOfPh = CursorUtil.getColumnIndexOrThrow(_cursor, "ph");
          final int _cursorIndexOfTemperature = CursorUtil.getColumnIndexOrThrow(_cursor, "temperature");
          final int _cursorIndexOfRecordedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "recordedAt");
          final List<SoilDataEntity> _result = new ArrayList<SoilDataEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SoilDataEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final float _tmpMoisture;
            _tmpMoisture = _cursor.getFloat(_cursorIndexOfMoisture);
            final float _tmpNitrogen;
            _tmpNitrogen = _cursor.getFloat(_cursorIndexOfNitrogen);
            final float _tmpPhosphorus;
            _tmpPhosphorus = _cursor.getFloat(_cursorIndexOfPhosphorus);
            final float _tmpPotassium;
            _tmpPotassium = _cursor.getFloat(_cursorIndexOfPotassium);
            final float _tmpPh;
            _tmpPh = _cursor.getFloat(_cursorIndexOfPh);
            final float _tmpTemperature;
            _tmpTemperature = _cursor.getFloat(_cursorIndexOfTemperature);
            final long _tmpRecordedAt;
            _tmpRecordedAt = _cursor.getLong(_cursorIndexOfRecordedAt);
            _item = new SoilDataEntity(_tmpId,_tmpFarmerId,_tmpMoisture,_tmpNitrogen,_tmpPhosphorus,_tmpPotassium,_tmpPh,_tmpTemperature,_tmpRecordedAt);
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
