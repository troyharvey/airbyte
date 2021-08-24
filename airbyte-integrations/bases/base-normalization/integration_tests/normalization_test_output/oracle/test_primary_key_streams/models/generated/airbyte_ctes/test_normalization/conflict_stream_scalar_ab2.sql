{{ config(schema="test_normalization", tags=["top-level-intermediate"]) }}
-- SQL model to cast each column to its adequate SQL type converted from the JSON schema type
select
    cast(id as {{ dbt_utils.type_string() }}) as id,
    cast(conflict_stream_scalar as {{ dbt_utils.type_bigint() }}) as conflict_stream_scalar,
    airbyte_emitted_at
from {{ ref('conflict_stream_scalar_ab1') }}
-- conflict_stream_scalar
