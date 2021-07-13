package com.maju.domain.modules.hibernate

import com.maju.domain.entities.MethodEntity
import com.maju.domain.modules.AbstractModule
import com.maju.domain.modules.IStatementCreator
import com.maju.domain.modules.panache.CustomMethodEntityGenerator
import com.maju.domain.proxy.statement.CollectionStatementGenerator
import com.maju.domain.proxy.statement.SimpleStatementGenerator
import com.maju.utils.CKType
import com.maju.utils.concatAll
import com.maju.utils.toType
import com.squareup.kotlinpoet.metadata.ImmutableKmType
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import org.jetbrains.kotlin.util.collectionUtils.concat
import kotlin.reflect.KClass

@KotlinPoetMetadataPreview
class HibernateModule : AbstractModule() {

    override val supportedTypes: List<KClass<*>> = listOf(
        org.springframework.data.jpa.repository.JpaRepository::class
    )


    override fun createMethodEntities(
        kmType: ImmutableKmType
    ): List<MethodEntity> {
        val methodEntities = mutableListOf<MethodEntity>()

        val idType = kmType.arguments[1].type!!.toType()
        val jpaEntityType = kmType.arguments[0].type!!.toType()

        //TODO: find converter entity with the right originType
        val converterEntity = converterEntities.firstOrNull { converterEntity ->
            //EXPERIMENTAL
            converterEntity.originType == jpaEntityType
        } ?: throw IllegalStateException("There was no entity found.")

        val jpaEntityContainer = elementClassInspector.declarationContainerFor(jpaEntityType.className)

        //TODO: find id property annotated with javax.persistence.Id
        val idProperty = jpaEntityContainer.properties.firstOrNull {
            //TEMPORARY
            it.name == "id"
        } ?: return methodEntities
        val idName = idProperty.name


        val jpaRepositoryKmClass = JpaRepository::class.toImmutableKmClass()
        val crudRepositoryKmClass = CrudRepository::class.toImmutableKmClass()
        val pagingAndSortingRepositoryKmClass = PagingAndSortingRepository::class.toImmutableKmClass()

        val functions = jpaRepositoryKmClass.functions.concatAll(
            crudRepositoryKmClass.functions,
            pagingAndSortingRepositoryKmClass.functions
        )!!

        val converterTargetType = converterEntity.targetType


        for (function in functions) {
            val methodEntity =
                CustomMethodEntityGenerator(function, converterTargetType, idType, idName, null).generate()
            if (methodEntity != null) {
                methodEntities.add(methodEntity)
            }
        }

        return methodEntities
    }


}