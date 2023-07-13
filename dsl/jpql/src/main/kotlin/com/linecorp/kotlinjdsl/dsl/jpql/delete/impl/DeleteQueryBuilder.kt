package com.linecorp.kotlinjdsl.dsl.jpql.delete.impl

import com.linecorp.kotlinjdsl.querymodel.jpql.DeleteQuery
import com.linecorp.kotlinjdsl.querymodel.jpql.Path
import com.linecorp.kotlinjdsl.querymodel.jpql.Predicate
import com.linecorp.kotlinjdsl.querymodel.jpql.impl.JpqlDeleteQuery

internal class DeleteQueryBuilder<T>(
    private val from: Path<T>,
) {
    private var where: Predicate? = null

    fun where(predicate: Predicate): DeleteQueryBuilder<T> {
        where = predicate

        return this
    }

    fun build(): DeleteQuery<T> {
        return JpqlDeleteQuery(
            from = from,
            where = where,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeleteQueryBuilder<*>

        if (from != other.from) return false
        return where == other.where
    }

    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + (where?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "DeleteQueryBuilder(" +
            "from=$from, " +
            "where=$where)"
    }
}
