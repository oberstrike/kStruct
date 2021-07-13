package com.maju.domain.modules.panache

import com.maju.domain.entities.MethodEntity
import com.maju.domain.modules.AbstractModule
import com.maju.domain.modules.IStatementCreator
import com.maju.utils.CKType
import com.maju.utils.PANACHE_QUERY
import com.maju.utils.toType
import com.squareup.kotlinpoet.metadata.ImmutableKmType
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import kotlin.reflect.KClass

@KotlinPoetMetadataPreview
class PanacheModule : AbstractModule()  {

    override val supportedTypes = listOf<KClass<*>>(
        PanacheRepository::class
    )

    private val panacheQueryCreator = PanacheQueryCreator()

    class PanacheQueryCreator : IStatementCreator{
        override fun createStatement(
            converterName: String,
            convertExpression: String,
            targetType: CKType,
            paramName: String
        ): String {
            return "$paramName.project(${targetType.className.simpleName}::class.java)"
        }

        override fun isSupported(type: CKType): Boolean {
            return type.className == PANACHE_QUERY
        }
    }




    override fun createMethodEntities(
        kmType: ImmutableKmType
    ): List<MethodEntity> {
        val methodEntities = mutableListOf<MethodEntity>()

        val panacheEntityType = kmType.arguments.first().type!!.toType()

        val panacheEntityContainer = elementClassInspector.declarationContainerFor(panacheEntityType.className)

        //TODO: find id property annotated with javax.persistence.Id
        val idProperty = panacheEntityContainer.properties.firstOrNull {
            //TEMPORARY
            it.name == "id"
        } ?: return methodEntities

        val idName = idProperty.name
        val idType = idProperty.returnType.toType()

        //TODO: find converter entity with the right originType
        val converterEntity = converterEntities.firstOrNull { converterEntity ->
            //EXPERIMENTAL
            converterEntity.originType == panacheEntityType
        } ?: throw IllegalStateException("There was no entity found.")

        val panacheRepositoryKmClass = PanacheRepositoryBase::class.toImmutableKmClass()
        val functions = panacheRepositoryKmClass.functions

        val converterTargetType = converterEntity.targetType



        for (function in functions) {
            val methodEntity =
                CustomMethodEntityGenerator(function, converterTargetType, idType, idName, panacheQueryCreator).generate()
            if (methodEntity != null) {
                methodEntities.add(methodEntity)
            }
        }

        return methodEntities
    }


}