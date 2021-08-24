{{ config(schema="test_normalization", tags=["nested-intermediate"]) }}
-- SQL model to cast each column to its adequate SQL type converted from the JSON schema type
select
    {{ QUOTE('_AIRBYTE_PARTITION_HASHID') }},
    cast(currency as {{ dbt_utils.type_string() }}) as currency,
    airbyte_emitted_at
from {{ ref('nested_stream_with_complex_columns_resulting_into_long_names_partition_data_ab1') }}
-- data at nested_stream_with_complex_columns_resulting_into_long_names/partition/DATA
