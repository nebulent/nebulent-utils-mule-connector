
package com.nebulent.mule.utils.adapters;

import org.mule.api.Capabilities;
import org.mule.api.Capability;


/**
 * A <code>NebulentUtilsConnectorCapabilitiesAdapter</code> is a wrapper around {@link com.nebulent.mule.utils.NebulentUtilsConnector } that implements {@link org.mule.api.Capabilities} interface.
 * 
 */
public class NebulentUtilsConnectorCapabilitiesAdapter
    extends com.nebulent.mule.utils.NebulentUtilsConnector
    implements Capabilities
{


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

}
