package com.munkhtulga.understand

import android.arch.persistence.room.*

@Entity
data class Book(
    @PrimaryKey val title: String
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = arrayOf("title"),
        childColumns = arrayOf("bookTitle")
    )],
    indices = [Index(value = ["bookTitle"])]
)
data class Remark(
    @PrimaryKey val start: Int,
    val end: Int,
    val content: String,
    val bookTitle: String
)

@Dao
interface BookDao {
    @Query("SELECT * FROM book")
    fun getAll(): List<Book>

    @Insert
    fun insertAll(vararg books: Book)

    @Delete
    fun delete(book: Book)
}

@Dao
interface RemarkDao {
    @Query("SELECT * FROM remark")
    fun getAll(): List<Remark>

    @Query("SELECT * FROM remark WHERE bookTitle LIKE :title")
    fun getAllByBookTitle(title: String): List<Remark>

    @Query("SELECT * FROM remark WHERE start IN (:remarkIds)")
    fun loadAllByIds(remarkIds: IntArray): List<Remark>

    @Query("SELECT * FROM remark WHERE start = :start LIMIT 1")
    fun findByStartLocation(start: Int): Remark?



    @Insert
    fun insertAll(vararg remarks: Remark)

    @Delete
    fun delete(remark: Remark)
}


@Database(entities = arrayOf(Remark::class, Book::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun remarkDao(): RemarkDao
    abstract fun bookDao(): BookDao
}