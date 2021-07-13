package com.maju.domain.proxy.statement


import com.maju.domain.modules.IStatementCreator
import com.maju.utils.CKType
import com.maju.utils.STREAM
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ITERABLE
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.MUTABLE_LIST

class CollectionStatementGenerator : IStatementCreator {

    companion object {

        @JvmStatic
        private val collectionTypes: List<ClassName> = listOf(
            LIST,
            STREAM.topLevelClassName(),
            ITERABLE.topLevelClassName(),
            MUTABLE_LIST.topLevelClassName()
        )
    }


    override fun createStatement(
        converterName: String,
        convertExpression: String,
        targetType: CKType,
        paramName: String
    ): String {
        return "${paramName}.map(·$converterName::$convertExpression·) "
    }

    override fun isSupported(type: CKType): Boolean {
        return collectionTypes.contains(type.className)
    }

}