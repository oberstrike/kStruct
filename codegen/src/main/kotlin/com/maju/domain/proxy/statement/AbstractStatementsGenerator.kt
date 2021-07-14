package com.maju.domain.proxy.statement

import com.maju.domain.entities.ConverterEntity
import com.maju.domain.entities.MethodEntity
import com.maju.utils.IGenerator

abstract class AbstractStatementsGenerator(
    protected val methodEntity: MethodEntity,
    protected val converterEntities: List<ConverterEntity>
) : IGenerator<List<String>> {

    class StatementConnector {
        val statements: MutableList<String> = mutableListOf()

        fun addStatement(statement: String) {
            statements.add(statement)
        }

        fun addStatements(newStatements: List<String>) {
            statements.addAll(newStatements)
        }
    }

    fun createStatement(block: StatementConnector.() -> Unit): List<String> {
        val statementConnector = StatementConnector()
        statementConnector.block()
        return statementConnector.statements

    }


}