package com.raithabharosa.hub.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.raithabharosa.hub.data.local.dao.CropHistoryDao;
import com.raithabharosa.hub.data.local.dao.CropHistoryDao_Impl;
import com.raithabharosa.hub.data.local.dao.CropProfileDao;
import com.raithabharosa.hub.data.local.dao.CropProfileDao_Impl;
import com.raithabharosa.hub.data.local.dao.FarmerDao;
import com.raithabharosa.hub.data.local.dao.FarmerDao_Impl;
import com.raithabharosa.hub.data.local.dao.SoilDataDao;
import com.raithabharosa.hub.data.local.dao.SoilDataDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile FarmerDao _farmerDao;

  private volatile SoilDataDao _soilDataDao;

  private volatile CropHistoryDao _cropHistoryDao;

  private volatile CropProfileDao _cropProfileDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `farmers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `cropType` TEXT NOT NULL, `plotSize` REAL NOT NULL, `location` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `soil_data` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `farmerId` INTEGER NOT NULL, `moisture` REAL NOT NULL, `nitrogen` REAL NOT NULL, `phosphorus` REAL NOT NULL, `potassium` REAL NOT NULL, `ph` REAL NOT NULL, `temperature` REAL NOT NULL, `recordedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `farmerId` INTEGER NOT NULL, `cropType` TEXT NOT NULL, `sowingDate` INTEGER NOT NULL, `harvestDate` INTEGER, `yield` REAL NOT NULL, `notes` TEXT NOT NULL, `season` TEXT NOT NULL, `year` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `crop_profiles` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `farmerId` INTEGER NOT NULL, `name` TEXT NOT NULL, `cropType` TEXT NOT NULL, `plotSize` REAL NOT NULL, `location` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'b132d3f330cb91e83acbab65886245e7')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `farmers`");
        db.execSQL("DROP TABLE IF EXISTS `soil_data`");
        db.execSQL("DROP TABLE IF EXISTS `crop_history`");
        db.execSQL("DROP TABLE IF EXISTS `crop_profiles`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsFarmers = new HashMap<String, TableInfo.Column>(6);
        _columnsFarmers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmers.put("cropType", new TableInfo.Column("cropType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmers.put("plotSize", new TableInfo.Column("plotSize", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmers.put("location", new TableInfo.Column("location", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFarmers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFarmers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFarmers = new TableInfo("farmers", _columnsFarmers, _foreignKeysFarmers, _indicesFarmers);
        final TableInfo _existingFarmers = TableInfo.read(db, "farmers");
        if (!_infoFarmers.equals(_existingFarmers)) {
          return new RoomOpenHelper.ValidationResult(false, "farmers(com.raithabharosa.hub.data.local.entity.FarmerEntity).\n"
                  + " Expected:\n" + _infoFarmers + "\n"
                  + " Found:\n" + _existingFarmers);
        }
        final HashMap<String, TableInfo.Column> _columnsSoilData = new HashMap<String, TableInfo.Column>(9);
        _columnsSoilData.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoilData.put("farmerId", new TableInfo.Column("farmerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoilData.put("moisture", new TableInfo.Column("moisture", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoilData.put("nitrogen", new TableInfo.Column("nitrogen", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoilData.put("phosphorus", new TableInfo.Column("phosphorus", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoilData.put("potassium", new TableInfo.Column("potassium", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoilData.put("ph", new TableInfo.Column("ph", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoilData.put("temperature", new TableInfo.Column("temperature", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSoilData.put("recordedAt", new TableInfo.Column("recordedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSoilData = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSoilData = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSoilData = new TableInfo("soil_data", _columnsSoilData, _foreignKeysSoilData, _indicesSoilData);
        final TableInfo _existingSoilData = TableInfo.read(db, "soil_data");
        if (!_infoSoilData.equals(_existingSoilData)) {
          return new RoomOpenHelper.ValidationResult(false, "soil_data(com.raithabharosa.hub.data.local.entity.SoilDataEntity).\n"
                  + " Expected:\n" + _infoSoilData + "\n"
                  + " Found:\n" + _existingSoilData);
        }
        final HashMap<String, TableInfo.Column> _columnsCropHistory = new HashMap<String, TableInfo.Column>(9);
        _columnsCropHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropHistory.put("farmerId", new TableInfo.Column("farmerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropHistory.put("cropType", new TableInfo.Column("cropType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropHistory.put("sowingDate", new TableInfo.Column("sowingDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropHistory.put("harvestDate", new TableInfo.Column("harvestDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropHistory.put("yield", new TableInfo.Column("yield", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropHistory.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropHistory.put("season", new TableInfo.Column("season", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropHistory.put("year", new TableInfo.Column("year", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCropHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCropHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCropHistory = new TableInfo("crop_history", _columnsCropHistory, _foreignKeysCropHistory, _indicesCropHistory);
        final TableInfo _existingCropHistory = TableInfo.read(db, "crop_history");
        if (!_infoCropHistory.equals(_existingCropHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "crop_history(com.raithabharosa.hub.data.local.entity.CropHistoryEntity).\n"
                  + " Expected:\n" + _infoCropHistory + "\n"
                  + " Found:\n" + _existingCropHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsCropProfiles = new HashMap<String, TableInfo.Column>(8);
        _columnsCropProfiles.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropProfiles.put("farmerId", new TableInfo.Column("farmerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropProfiles.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropProfiles.put("cropType", new TableInfo.Column("cropType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropProfiles.put("plotSize", new TableInfo.Column("plotSize", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropProfiles.put("location", new TableInfo.Column("location", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropProfiles.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCropProfiles.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCropProfiles = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCropProfiles = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCropProfiles = new TableInfo("crop_profiles", _columnsCropProfiles, _foreignKeysCropProfiles, _indicesCropProfiles);
        final TableInfo _existingCropProfiles = TableInfo.read(db, "crop_profiles");
        if (!_infoCropProfiles.equals(_existingCropProfiles)) {
          return new RoomOpenHelper.ValidationResult(false, "crop_profiles(com.raithabharosa.hub.data.local.entity.CropProfileEntity).\n"
                  + " Expected:\n" + _infoCropProfiles + "\n"
                  + " Found:\n" + _existingCropProfiles);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "b132d3f330cb91e83acbab65886245e7", "1d1883e96d4e6b1c68ba010f31777463");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "farmers","soil_data","crop_history","crop_profiles");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `farmers`");
      _db.execSQL("DELETE FROM `soil_data`");
      _db.execSQL("DELETE FROM `crop_history`");
      _db.execSQL("DELETE FROM `crop_profiles`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(FarmerDao.class, FarmerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SoilDataDao.class, SoilDataDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CropHistoryDao.class, CropHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CropProfileDao.class, CropProfileDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public FarmerDao farmerDao() {
    if (_farmerDao != null) {
      return _farmerDao;
    } else {
      synchronized(this) {
        if(_farmerDao == null) {
          _farmerDao = new FarmerDao_Impl(this);
        }
        return _farmerDao;
      }
    }
  }

  @Override
  public SoilDataDao soilDataDao() {
    if (_soilDataDao != null) {
      return _soilDataDao;
    } else {
      synchronized(this) {
        if(_soilDataDao == null) {
          _soilDataDao = new SoilDataDao_Impl(this);
        }
        return _soilDataDao;
      }
    }
  }

  @Override
  public CropHistoryDao cropHistoryDao() {
    if (_cropHistoryDao != null) {
      return _cropHistoryDao;
    } else {
      synchronized(this) {
        if(_cropHistoryDao == null) {
          _cropHistoryDao = new CropHistoryDao_Impl(this);
        }
        return _cropHistoryDao;
      }
    }
  }

  @Override
  public CropProfileDao cropProfileDao() {
    if (_cropProfileDao != null) {
      return _cropProfileDao;
    } else {
      synchronized(this) {
        if(_cropProfileDao == null) {
          _cropProfileDao = new CropProfileDao_Impl(this);
        }
        return _cropProfileDao;
      }
    }
  }
}
