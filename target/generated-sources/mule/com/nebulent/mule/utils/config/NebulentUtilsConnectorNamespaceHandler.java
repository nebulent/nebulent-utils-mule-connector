
package com.nebulent.mule.utils.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * Registers bean definitions parsers for handling elements in <code>http://www.mulesoft.org/schema/mule/nebulent-utils</code>.
 * 
 */
public class NebulentUtilsConnectorNamespaceHandler
    extends NamespaceHandlerSupport
{


    /**
     * Invoked by the {@link DefaultBeanDefinitionDocumentReader} after construction but before any custom elements are parsed. 
     * @see NamespaceHandlerSupport#registerBeanDefinitionParser(String, BeanDefinitionParser)
     * 
     */
    public void init() {
        registerBeanDefinitionParser("config", new NebulentUtilsConnectorConfigDefinitionParser());
        registerBeanDefinitionParser("throttle", new ThrottleDefinitionParser());
    }

}
