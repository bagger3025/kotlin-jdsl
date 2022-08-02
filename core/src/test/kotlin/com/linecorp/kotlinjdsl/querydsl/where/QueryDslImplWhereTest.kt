package com.linecorp.kotlinjdsl.querydsl.where

import com.linecorp.kotlinjdsl.query.clause.where.WhereClause
import com.linecorp.kotlinjdsl.query.spec.predicate.AndSpec
import com.linecorp.kotlinjdsl.query.spec.predicate.PredicateSpec
import com.linecorp.kotlinjdsl.querydsl.QueryDslImpl
import com.linecorp.kotlinjdsl.test.WithKotlinJdslAssertions
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class QueryDslImplWhereTest : WithKotlinJdslAssertions {
    @Test
    fun noWhere() {
        // when
        val actual = QueryDslImpl(Data1::class.java).apply {
            select(distinct = true, Data1::class.java)
            from(entity(Data1::class))
        }

        // then
        val criteriaQuerySpec = actual.createCriteriaQuerySpec()

        assertThat(criteriaQuerySpec.where).isEqualTo(
            WhereClause(PredicateSpec.empty)
        )

        val subquerySpec = actual.createSubquerySpec()

        assertThat(subquerySpec.where).isEqualTo(
            WhereClause(PredicateSpec.empty)
        )
    }

    @Test
    fun where() {
        // given
        val predicateSpec: PredicateSpec = mockk()

        // when
        val actual = QueryDslImpl(Data1::class.java).apply {
            select(distinct = true, Data1::class.java)
            from(entity(Data1::class))
            where(predicateSpec)
        }

        // then
        val criteriaQuerySpec = actual.createCriteriaQuerySpec()

        assertThat(criteriaQuerySpec.where).isEqualTo(
            WhereClause(predicateSpec)
        )

        val subquerySpec = actual.createSubquerySpec()

        assertThat(subquerySpec.where).isEqualTo(
            WhereClause(predicateSpec)
        )
    }

    @Test
    fun wheres() {
        // given
        val predicateSpec1: PredicateSpec = mockk()
        val predicateSpec2: PredicateSpec = mockk()

        // when
        val actual = QueryDslImpl(Data1::class.java).apply {
            select(distinct = true, Data1::class.java)
            from(entity(Data1::class))
            where(predicateSpec1)
            where(predicateSpec2)
        }

        // then
        val criteriaQuerySpec = actual.createCriteriaQuerySpec()

        assertThat(criteriaQuerySpec.where).isEqualTo(
            WhereClause(AndSpec(listOf(predicateSpec1, predicateSpec2)))
        )

        val subquerySpec = actual.createSubquerySpec()

        assertThat(subquerySpec.where).isEqualTo(
            WhereClause(AndSpec(listOf(predicateSpec1, predicateSpec2)))
        )
    }

    @Test
    fun nullInFirstWheres() {
        // given
        val predicateSpec1: PredicateSpec? = null
        val predicateSpec2: PredicateSpec = mockk()
        val predicateSpec3: PredicateSpec = mockk()

        // when
        val actual = QueryDslImpl(Data1::class.java).apply {
            select(distinct = true, Data1::class.java)
            from(entity(Data1::class))
            where(predicateSpec1,predicateSpec2,predicateSpec3)
        }

        // then
        val criteriaQuerySpec = actual.createCriteriaQuerySpec()

        assertThat(criteriaQuerySpec.where).isEqualTo(
            WhereClause(AndSpec(listOf(predicateSpec2, predicateSpec3)))
        )

        val subquerySpec = actual.createSubquerySpec()

        assertThat(subquerySpec.where).isEqualTo(
            WhereClause(AndSpec(listOf(predicateSpec2, predicateSpec3)))
        )
    }

    class Data1
}
