
package com.nebulent.mule.utils.config;

import com.nebulent.mule.utils.adapters.NebulentUtilsConnectorConnectionManager;
import org.apache.commons.lang.StringUtils;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.config.PoolingProfile;
import org.mule.config.spring.MuleHierarchicalBeanDefinitionParserDelegate;
import org.mule.config.spring.parsers.generic.AutoIdUtils;
import org.mule.util.TemplateParser;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class NebulentUtilsConnectorConfigDefinitionParser
    implements BeanDefinitionParser
{

    /**
     * Mule Pattern Info
     * 
     */
    private TemplateParser.PatternInfo patternInfo;

    public NebulentUtilsConnectorConfigDefinitionParser() {
        patternInfo = TemplateParser.createMuleStyleParser().getStyle();
    }

    public BeanDefinition parse(Element element, ParserContext parserContent) {
        String name = element.getAttribute("name");
        if ((name == null)||StringUtils.isBlank(name)) {
            element.setAttribute("name", AutoIdUtils.getUniqueName(element, "mule-bean"));
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(NebulentUtilsConnectorConnectionManager.class.getName());
        if (Initialisable.class.isAssignableFrom(NebulentUtilsConnectorConnectionManager.class)) {
            builder.setInitMethodName(Initialisable.PHASE_NAME);
        }
        if (Disposable.class.isAssignableFrom(NebulentUtilsConnectorConnectionManager.class)) {
            builder.setDestroyMethodName(Disposable.PHASE_NAME);
        }
        if ((element.getAttribute("period")!= null)&&(!StringUtils.isBlank(element.getAttribute("period")))) {
            builder.addPropertyValue("period", element.getAttribute("period"));
        }
        if ((element.getAttribute("requestsLimit")!= null)&&(!StringUtils.isBlank(element.getAttribute("requestsLimit")))) {
            builder.addPropertyValue("requestsLimit", element.getAttribute("requestsLimit"));
        }
        BeanDefinitionBuilder connectionPoolingProfileBuilder = BeanDefinitionBuilder.rootBeanDefinition(PoolingProfile.class.getName());
        Element connectionPoolingProfileElement = DomUtils.getChildElementByTagName(element, "connection-pooling-profile");
        if (connectionPoolingProfileElement!= null) {
            if ((connectionPoolingProfileElement.getAttribute("maxActive")!= null)&&(!StringUtils.isBlank(connectionPoolingProfileElement.getAttribute("maxActive")))) {
                connectionPoolingProfileBuilder.addPropertyValue("maxActive", connectionPoolingProfileElement.getAttribute("maxActive"));
            }
            if ((connectionPoolingProfileElement.getAttribute("maxIdle")!= null)&&(!StringUtils.isBlank(connectionPoolingProfileElement.getAttribute("maxIdle")))) {
                connectionPoolingProfileBuilder.addPropertyValue("maxIdle", connectionPoolingProfileElement.getAttribute("maxIdle"));
            }
            if ((connectionPoolingProfileElement.getAttribute("maxWait")!= null)&&(!StringUtils.isBlank(connectionPoolingProfileElement.getAttribute("maxWait")))) {
                connectionPoolingProfileBuilder.addPropertyValue("maxWait", connectionPoolingProfileElement.getAttribute("maxWait"));
            }
            if ((connectionPoolingProfileElement.getAttribute("exhaustedAction")!= null)&&(!StringUtils.isBlank(connectionPoolingProfileElement.getAttribute("exhaustedAction")))) {
                connectionPoolingProfileBuilder.addPropertyValue("exhaustedAction", PoolingProfile.POOL_EXHAUSTED_ACTIONS.get(connectionPoolingProfileElement.getAttribute("exhaustedAction")));
            }
            if ((connectionPoolingProfileElement.getAttribute("exhaustedAction")!= null)&&(!StringUtils.isBlank(connectionPoolingProfileElement.getAttribute("exhaustedAction")))) {
                connectionPoolingProfileBuilder.addPropertyValue("initialisationPolicy", PoolingProfile.POOL_INITIALISATION_POLICIES.get(connectionPoolingProfileElement.getAttribute("initialisationPolicy")));
            }
            builder.addPropertyValue("connectionPoolingProfile", connectionPoolingProfileBuilder.getBeanDefinition());
        }
        BeanDefinition definition = builder.getBeanDefinition();
        definition.setAttribute(MuleHierarchicalBeanDefinitionParserDelegate.MULE_NO_RECURSE, Boolean.TRUE);
        return definition;
    }

}
