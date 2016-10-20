package io.drakon.uni.ac32007.instagrim.lib.db

import com.datastax.driver.core.PreparedStatement
import com.datastax.driver.core.Session
import org.slf4j.LoggerFactory
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Wrapper around CQL queries to perform caching of prepared statements.
 */
class CachedStatement(private val query: String) {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private var cache: PreparedStatement? = null
    private val lock = ReentrantLock()

    override fun toString(): String {
        return "CachedStatement[$query//$cache]"
    }

    private fun get(session: Session): PreparedStatement {
        lock.withLock {
            var __cache = cache // Allow smart-casting by taking snapshot of 'cache'
            if (__cache != null) {
                log.trace("Cache hit: {}", this)
                return __cache
            }

            // No cached copy; create one.
            __cache = session.prepare(query)
            cache = __cache
            log.debug("Cache filled: {}", this)
            return __cache
        }
    }

    /**
     * Get the prepared statement for the query this cache represents.
     *
     * This will create a new prepared statement if there isn't one already cached, and save it.
     *
     * @param session An active Cassandra cluster session.
     * @return The prepared statement.
     */
    operator fun invoke(session: Session): PreparedStatement {
        return get(session)
    }

    fun String.toQuery(): CachedStatement {
        return CachedStatement(this)
    }

}