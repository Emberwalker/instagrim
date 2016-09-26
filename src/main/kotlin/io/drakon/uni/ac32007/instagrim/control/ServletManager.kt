package io.drakon.uni.ac32007.instagrim.control

import io.drakon.uni.ac32007.instagrim.lib.CassandraHosts
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

/**
 * Implementation of ServletContextListener for cleaning up on servlet destroy.
 */
@WebListener
class ServletManager: ServletContextListener {
    /**
     * Receives notification that the web application initialization
     * process is starting.

     *
     * All ServletContextListeners are notified of context
     * initialization before any filters or servlets in the web
     * application are initialized.

     * @param sce the ServletContextEvent containing the ServletContext that is being initialized
     */
    override fun contextInitialized(sce: ServletContextEvent?) {
        // Not needed.
    }

    /**
     * Receives notification that the ServletContext is about to be
     * shut down.

     *
     * All servlets and filters will have been destroyed before any
     * ServletContextListeners are notified of context
     * destruction.

     * @param sce the ServletContextEvent containing the ServletContext that is being destroyed
     */
    override fun contextDestroyed(sce: ServletContextEvent?) {
        CassandraHosts.close()
        // TODO: Work out if we can kill off Netty's excess threads before exit to stop Tomcat whining.
    }
}