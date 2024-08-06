package nz.ac.canterbury.seng303.as1.datastore

import kotlinx.coroutines.flow.Flow
import nz.ac.canterbury.seng303.as1.models.Identifiable

interface Storage<T> where T: Identifiable {
    fun insert(data: T): Flow<Int>
    fun insertAll(data: List<T>): Flow<Int>
    fun getAll(): Flow<List<T>>
    fun get(where: (T) -> Boolean): Flow<T>
    fun delete(identifier: Int): Flow<Int>
    fun edit(identifier: Int, data: T): Flow<Int>
}