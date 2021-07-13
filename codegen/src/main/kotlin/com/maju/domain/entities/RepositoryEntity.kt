package com.maju.domain.entities

import com.maju.utils.CKType


data class RepositoryEntity(
    val name: String,
    val type: CKType,
    val converters: List<ConverterEntity>,
    val methods: List<MethodEntity>
)


