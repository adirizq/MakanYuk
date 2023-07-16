package com.pnj.makanyuk.data.cart

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CartItem::class], version = 1)
abstract class CartItemDatabase: RoomDatabase() {

    abstract fun getCartItemDao(): CartItemDao

    companion object{
        @Volatile
        private var instance: CartItemDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            CartItemDatabase::class.java,
            "makanyuk-db"
        ).build()
    }

}