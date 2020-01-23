package io.narayana.txdemo.demos.remote;

import java.util.Hashtable;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.TransactionManager;
import org.jboss.logging.Logger;

import io.narayana.txdemo.DemoResult;
import io.narayana.txdemo.demos.Demo;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;

/**
 * Common class for all demos performing remote call using the EJB client.
 * 
 * The JNDI lookup name for a stateful session bean has the syntax of:
 * ejb:<appName>/<moduleName>/<distinctName>/<beanName>!<viewClassName>[?stateful]
 * 
 * <appName> The application name is the name of the EAR that the EJB is deployed in
 * (without the .ear). If the EJB JAR is not deployed in an EAR then this is
 * blank. The app name can also be specified in the EAR's application.xml
 *
 * <moduleName> By the default the module name is the name of the EJB JAR file (without the
 * .jar suffix). The module name might be overridden in the ejb-jar.xml
 *
 * <distinctName> : EAP allows each deployment to have an (optional) distinct name.
 * This example does not use this so leave it blank.
 *
 * <beanName> : The name of the session been to be invoked.
 *
 * <viewClassName>: The fully qualified classname of the remote interface. Must include
 * the whole package name.
 * @author Miloslav Zezulka (mzezulka@redhat.com)
 *
 */
public abstract class RemoteEjbDemo extends Demo {

    protected static final Logger LOG = Logger.getLogger(RemoteEjbOneClientOneServerDemo.class);
    
    protected static final String HTTP = "http";
    protected static final String HOSTNAME = "localhost";
    protected static final String PORT = "8180";
    protected static final String BASE_URL = HTTP + "://" + HOSTNAME + ":" + PORT;
    private static final String uriPrefix = "ejb:/ejb-remote-server-side-jar-with-dependencies/";
    private static Context ctxt = null;
    
    public RemoteEjbDemo(int id, String name, String desc) {
        super(id, name, desc);
    }

    private static final Context getInitialContext() throws NamingException {
        if(ctxt != null) return ctxt;
        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        if(Boolean.getBoolean(HTTP)) {
            //use HTTP based invocation. Each invocation will be a HTTP request
            jndiProperties.put(Context.PROVIDER_URL, BASE_URL + "/wildfly-services");
        } else {
            //use HTTP upgrade, an initial upgrade requests is sent to upgrade to the remoting protocol
            jndiProperties.put(Context.PROVIDER_URL,"remote+" + BASE_URL);
        }
        ctxt = new InitialContext(jndiProperties);
        return ctxt;
    }
    
    /**
     * Looks up and returns the proxy to remote stateless bean of the specified class.
     *
     * @return
     * @throws NamingException
     */
    @SuppressWarnings("unchecked")
     protected static <T> T lookupRemoteStatelessBean(String beanName, Class<T> viewClassName) {
        try {
            return (T) getInitialContext().lookup(uriPrefix + beanName + "!" + viewClassName.getName());
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
    /**
     * Looks up and returns the proxy for a remote stateful bean of the specified class.
     */
    @SuppressWarnings("unchecked")
    protected static <T> T lookupRemoteStatefulBean(String beanName, Class<T> viewClassName) {
        try {
            return (T) getInitialContext().lookup(uriPrefix + beanName + "!" + viewClassName.getName() + "?stateful");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public DemoResult run(TransactionManager tm, EntityManager em) {
        Span span = GlobalTracer.get().buildSpan("ejb-remote-invocation").start();
        try(Scope s = GlobalTracer.get().activateSpan(span)) {
            invokeStatefulBean(tm, em);
            invokeStatelessBean(tm, em);
        } finally {
            span.finish();
        }
        return new DemoResult(0, "EJB remote call");
    }
    
    protected abstract void invokeStatefulBean(TransactionManager tm, EntityManager em);

    protected abstract void invokeStatelessBean(TransactionManager tm, EntityManager em);
}
