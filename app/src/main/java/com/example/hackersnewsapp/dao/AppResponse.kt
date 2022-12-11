package com.example.hackersnewsapp.dao

import com.example.hackersnewsapp.dao.ResponseStatus.ERROR
import com.example.hackersnewsapp.dao.ResponseStatus.FAIL
import com.example.hackersnewsapp.dao.ResponseStatus.SUCCESS


class AppResponse<T> private constructor(val status: Int, val data: T?, val throwable: Throwable?) {
    companion object {

        fun <T> success(data: T): AppResponse<T>? {
            return AppResponse(SUCCESS, data, null)
        }

        fun <T> failure(data: T): AppResponse<T> {
            return AppResponse(FAIL, data, null)
        }

        fun <T> error(error: Throwable): AppResponse<T> {
            return AppResponse(ERROR, null, error)
        }
    }

}