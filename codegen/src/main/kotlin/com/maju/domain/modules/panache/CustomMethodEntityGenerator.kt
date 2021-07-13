package com.maju.domain.modules.panache

import com.maju.domain.entities.MethodEntity
import com.maju.domain.entities.ParameterEntity
import com.maju.domain.generator.MethodEntityGenerator
import com.maju.domain.generator.ParameterEntityGenerator
import com.maju.domain.modules.IStatementCreator
import com.maju.utils.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ITERABLE
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.MUTABLE_LIST
import com.squareup.kotlinpoet.metadata.ImmutableKmFunction
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isNullable
import com.squareup.kotlinpoet.metadata.isSuspend
import kotlinx.metadata.KmClassifier

@KotlinPoetMetadataPreview
class CustomMethodEntityGenerator(
    private val function: ImmutableKmFunction,
    private val targetType: CKType,
    private val idPropertyType: CKType,
    private val idPropertyName: String,
    private val statementCreator: IStatementCreator? = null
) : IGenerator<MethodEntity?> {

    companion object {

        @JvmStatic
        private val collectionTypes: List<ClassName> = listOf(
            LIST,
            STREAM.topLevelClassName(),
            ITERABLE.topLevelClassName(),
            MUTABLE_LIST.topLevelClassName()
        )

        @JvmStatic
        private val panacheTypes: List<ClassName> = listOf(
            PANACHE_QUERY.topLevelClassName()
        )
    }



    override fun generate(): MethodEntity? {

        val functionName = function.name

        val originReturnType = function.returnType

        val isSuspend = function.isSuspend

        val targetReturnType = if (originReturnType.classifier is KmClassifier.TypeParameter) {
            targetType.copy(isNullable = originReturnType.isNullable)
        } else {
            if (originReturnType.arguments.isNotEmpty()) {
                originReturnType.toType(targetType)
            } else {
                originReturnType.toType()
            }
        }

        val parameters = function.valueParameters
        val mParameters = mutableListOf<ParameterEntity>()

        if (parameters.map { it.type }.any { it!!.arguments.isNotEmpty() }) {
            return null
        }

        if (targetReturnType.arguments.isNotEmpty()
            && !collectionTypes.contains(targetReturnType.className)
            && !panacheTypes.contains(targetReturnType.className)
        ) return null

        for (parameter in parameters) {
            val parameterType = parameter.type ?: continue
            val parameterName = parameter.name

            val targetParameterType = if (parameter.type!!.classifier is KmClassifier.TypeParameter) {
                if (parameterName == idPropertyName) {
                    idPropertyType.copy(isNullable = false)
                } else {
                    if (parameterType.isNullable) targetType.copy(isNullable = true)
                    else targetType
                }
            } else {
                parameterType.toType()
            }
            val parameterEntity = ParameterEntityGenerator(parameterName, targetParameterType).generate()
            mParameters.add(parameterEntity)
        }

        return MethodEntityGenerator(functionName, mParameters, targetReturnType, isSuspend, statementCreator).generate();


    }
}