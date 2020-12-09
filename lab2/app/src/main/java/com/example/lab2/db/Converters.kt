package com.example.lab2.db

import androidx.lifecycle.MutableLiveData
import androidx.room.TypeConverter
import com.example.lab2.db.Kind

class Converters {

    @TypeConverter
    fun toMutableLiveDataKind(arg: Int): MutableLiveData<Kind>
    {
        val ret = MutableLiveData<Kind>()
        ret.postValue(enumValues<Kind>()[arg])
        return ret
    }
    @TypeConverter
    fun fromMutableLiveDataKind(arg: MutableLiveData<Kind>): Int
            = arg.value!!.ordinal

    @TypeConverter
    fun toMutableLiveDataByte(arg: Byte): MutableLiveData<Byte>
    {
        val ret = MutableLiveData<Byte>()
        ret.postValue(arg)
        return ret
    }
    @TypeConverter
    fun fromMutableLiveDataByte(arg: MutableLiveData<Byte>): Byte
            = arg.value!!

    @TypeConverter
    fun toMutableLiveDataBoolean(arg: Boolean): MutableLiveData<Boolean>
    {
        val ret = MutableLiveData<Boolean>()
        ret.postValue(arg)
        return ret
    }
    @TypeConverter
    fun fromMutableLiveDataBoolean(arg: MutableLiveData<Boolean>): Boolean
            = arg.value!!

    @TypeConverter
    fun toMutableLiveDataInt(arg: Int): MutableLiveData<Int>
    {
        val ret = MutableLiveData<Int>()
        ret.postValue(arg)
        return ret
    }
    @TypeConverter
    fun fromMutableLiveDataInt(arg: MutableLiveData<Int>): Int
            = arg.value!!
}