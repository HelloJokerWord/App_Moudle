package com.libhttp

import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convertTo
import java.io.IOException
import java.lang.reflect.Type

/**
 * Created on 2022/9/8.
 * @author Joker
 * Des:
 */

@Parser(name = "ResponseData")
open class ResponseParser<T> : TypeParser<T> {

    protected constructor() : super()

    constructor(type: Type) : super(type)

    @Throws(IOException::class)
    override fun onParse(response: okhttp3.Response): T {
        val responseBean: ResponseBean<T> = response.convertTo(ResponseBean::class, *types)

        var data = responseBean.body //获取data字段

        if (data == null && types[0] === String::class.java) {
            /*
             * 考虑到有些时候服务端会返回：{"errorCode":0,"errorMsg":"关注成功"}  类似没有data的数据
             * 此时code正确，但是data字段为空，直接返回data的话，会报空指针错误，
             * 所以，判断泛型为String类型时，重新赋值，并确保赋值不为null
             */
            @Suppress("UNCHECKED_CAST")
            data = responseBean.message as T
        }

        //code不等于0，说明数据不正确，抛出异常
        if (responseBean.code != 0 || data == null) {
            throw ParseException(responseBean.code.toString(), responseBean.message, response)
        }

        return data
    }
}