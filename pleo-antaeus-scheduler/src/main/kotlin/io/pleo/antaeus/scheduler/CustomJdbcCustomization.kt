package io.pleo.antaeus.scheduler

import com.github.kagkarlsson.scheduler.jdbc.DefaultJdbcCustomization
import java.sql.ResultSet
import java.time.Instant
import java.util.*

/**
 * We needed this custom override because SQLLite does not support Date datatype
 */
class CustomJdbcCustomization: DefaultJdbcCustomization() {

    override fun getInstant(rs: ResultSet, columnName: String): Instant? {
        return Optional.ofNullable(rs.getString(columnName)).map { obj -> Instant.ofEpochMilli(obj.toLong()) }.orElse(null)
    }
}