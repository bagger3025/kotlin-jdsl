package com.linecorp.kotlinjdsl.querymodel.sql

import com.linecorp.kotlinjdsl.SinceJdsl

@SinceJdsl("3.0.0")
interface DeleteQuery<T : Any> :
    SqlQuery<DeleteQuery<T>>
