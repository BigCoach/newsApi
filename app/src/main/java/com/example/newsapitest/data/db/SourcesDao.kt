package com.example.newsapitest.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapitest.data.model.Source

@Dao
abstract class SourceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertAll(users: List<Source>)

    @Query("SELECT * FROM Source WHERE isSelected = :isSelected")
    abstract fun getSelectedSources(isSelected: Boolean = true) : LiveData<List<Source>>

    @Query("SELECT * FROM Source")
    abstract fun getAllSources() : LiveData<List<Source>>

    @Query("UPDATE Source SET isSelected = :isSelected WHERE id = :sourceId")
    abstract fun updateSource(
        sourceId: String,
        isSelected: Boolean
    )
}