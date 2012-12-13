
package com.nebulent.mule.utils.adapters;

import com.nebulent.mule.utils.NebulentUtilsConnector;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.mule.api.Capabilities;
import org.mule.api.Capability;
import org.mule.api.ConnectionManager;
import org.mule.api.MuleContext;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.config.PoolingProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@code NebulentUtilsConnectorConnectionManager} is a wrapper around {@link NebulentUtilsConnector } that adds connection management capabilities to the pojo.
 * 
 */
public class NebulentUtilsConnectorConnectionManager
    implements Capabilities, ConnectionManager<NebulentUtilsConnectorConnectionManager.ConnectionKey, NebulentUtilsConnectorLifecycleAdapter> , MuleContextAware, Initialisable
{

    private long period;
    private long requestsLimit;
    private static Logger logger = LoggerFactory.getLogger(NebulentUtilsConnectorConnectionManager.class);
    /**
     * Mule Context
     * 
     */
    private MuleContext muleContext;
    /**
     * Flow construct
     * 
     */
    private FlowConstruct flowConstruct;
    /**
     * Connector Pool
     * 
     */
    private GenericKeyedObjectPool connectionPool;
    protected PoolingProfile connectionPoolingProfile;

    /**
     * Sets period
     * 
     * @param value Value to set
     */
    public void setPeriod(long value) {
        this.period = value;
    }

    /**
     * Retrieves period
     * 
     */
    public long getPeriod() {
        return this.period;
    }

    /**
     * Sets requestsLimit
     * 
     * @param value Value to set
     */
    public void setRequestsLimit(long value) {
        this.requestsLimit = value;
    }

    /**
     * Retrieves requestsLimit
     * 
     */
    public long getRequestsLimit() {
        return this.requestsLimit;
    }

    /**
     * Sets connectionPoolingProfile
     * 
     * @param value Value to set
     */
    public void setConnectionPoolingProfile(PoolingProfile value) {
        this.connectionPoolingProfile = value;
    }

    /**
     * Retrieves connectionPoolingProfile
     * 
     */
    public PoolingProfile getConnectionPoolingProfile() {
        return this.connectionPoolingProfile;
    }

    /**
     * Sets flow construct
     * 
     * @param flowConstruct Flow construct to set
     */
    public void setFlowConstruct(FlowConstruct flowConstruct) {
        this.flowConstruct = flowConstruct;
    }

    /**
     * Set the Mule context
     * 
     * @param context Mule context to set
     */
    public void setMuleContext(MuleContext context) {
        this.muleContext = context;
    }

    public void initialise() {
        GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();
        if (connectionPoolingProfile!= null) {
            config.maxIdle = connectionPoolingProfile.getMaxIdle();
            config.maxActive = connectionPoolingProfile.getMaxActive();
            config.maxWait = connectionPoolingProfile.getMaxWait();
            config.whenExhaustedAction = ((byte) connectionPoolingProfile.getExhaustedAction());
        }
        connectionPool = new GenericKeyedObjectPool(new NebulentUtilsConnectorConnectionManager.ConnectionFactory(this), config);
    }

    public NebulentUtilsConnectorLifecycleAdapter acquireConnection(NebulentUtilsConnectorConnectionManager.ConnectionKey key)
        throws Exception
    {
        return ((NebulentUtilsConnectorLifecycleAdapter) connectionPool.borrowObject(key));
    }

    public void releaseConnection(NebulentUtilsConnectorConnectionManager.ConnectionKey key, NebulentUtilsConnectorLifecycleAdapter connection)
        throws Exception
    {
        connectionPool.returnObject(key, connection);
    }

    public void destroyConnection(NebulentUtilsConnectorConnectionManager.ConnectionKey key, NebulentUtilsConnectorLifecycleAdapter connection)
        throws Exception
    {
        connectionPool.invalidateObject(key, connection);
    }

    /**
     * Returns true if this module implements such capability
     * 
     */
    public boolean isCapableOf(Capability capability) {
        if (capability == Capability.LIFECYCLE_CAPABLE) {
            return true;
        }
        if (capability == Capability.CONNECTION_MANAGEMENT_CAPABLE) {
            return true;
        }
        return false;
    }

    private static class ConnectionFactory
        implements KeyedPoolableObjectFactory
    {

        private NebulentUtilsConnectorConnectionManager connectionManager;

        public ConnectionFactory(NebulentUtilsConnectorConnectionManager connectionManager) {
            this.connectionManager = connectionManager;
        }

        public Object makeObject(Object key)
            throws Exception
        {
            if (!(key instanceof NebulentUtilsConnectorConnectionManager.ConnectionKey)) {
                throw new RuntimeException("Invalid key type");
            }
            NebulentUtilsConnectorLifecycleAdapter connector = new NebulentUtilsConnectorLifecycleAdapter();
            connector.setPeriod(connectionManager.getPeriod());
            connector.setRequestsLimit(connectionManager.getRequestsLimit());
            if (connector instanceof Initialisable) {
                connector.initialise();
            }
            if (connector instanceof Startable) {
                connector.start();
            }
            return connector;
        }

        public void destroyObject(Object key, Object obj)
            throws Exception
        {
            if (!(key instanceof NebulentUtilsConnectorConnectionManager.ConnectionKey)) {
                throw new RuntimeException("Invalid key type");
            }
            if (!(obj instanceof NebulentUtilsConnectorLifecycleAdapter)) {
                throw new RuntimeException("Invalid connector type");
            }
            try {
                ((NebulentUtilsConnectorLifecycleAdapter) obj).disconnect();
            } catch (Exception e) {
                throw e;
            } finally {
                if (((NebulentUtilsConnectorLifecycleAdapter) obj) instanceof Stoppable) {
                    ((NebulentUtilsConnectorLifecycleAdapter) obj).stop();
                }
                if (((NebulentUtilsConnectorLifecycleAdapter) obj) instanceof Disposable) {
                    ((NebulentUtilsConnectorLifecycleAdapter) obj).dispose();
                }
            }
        }

        public boolean validateObject(Object key, Object obj) {
            if (!(obj instanceof NebulentUtilsConnectorLifecycleAdapter)) {
                throw new RuntimeException("Invalid connector type");
            }
            try {
                return ((NebulentUtilsConnectorLifecycleAdapter) obj).isConnected();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return false;
            }
        }

        public void activateObject(Object key, Object obj)
            throws Exception
        {
            if (!(key instanceof NebulentUtilsConnectorConnectionManager.ConnectionKey)) {
                throw new RuntimeException("Invalid key type");
            }
            if (!(obj instanceof NebulentUtilsConnectorLifecycleAdapter)) {
                throw new RuntimeException("Invalid connector type");
            }
            try {
                if (!((NebulentUtilsConnectorLifecycleAdapter) obj).isConnected()) {
                    ((NebulentUtilsConnectorLifecycleAdapter) obj).connect();
                }
            } catch (Exception e) {
                throw e;
            }
        }

        public void passivateObject(Object key, Object obj)
            throws Exception
        {
        }

    }


    /**
     * A tuple of connection parameters
     * 
     */
    public static class ConnectionKey {


        public ConnectionKey() {
        }

        public int hashCode() {
            int hash = 1;
            return hash;
        }

        public boolean equals(Object obj) {
            return (obj instanceof NebulentUtilsConnectorConnectionManager.ConnectionKey);
        }

    }

}
