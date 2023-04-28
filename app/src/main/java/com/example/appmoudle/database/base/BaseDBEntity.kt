package com.example.appmoudle.database.base

import io.objectbox.annotation.BaseEntity
import io.objectbox.annotation.ConflictStrategy
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@BaseEntity
abstract class BaseDBEntity {

    @Id(assignable = true)
    var id = 0L                  //数据库从1开始递增 数据库游标   增加 修改唯一标识符，一样的覆盖，不一样的添加

    @Unique(onConflict = ConflictStrategy.REPLACE) //指定冲突的替换
    var unique: String = ""      //查询唯一标识符

}