package com.cuyum.jbpm.wih.rest.test;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class SimpleRESTApplication extends Application {

	
    @Override
    public Set<Class<?>> getClasses() 
    {
        return new HashSet<Class<?>>();
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> objects = new HashSet<Object>();
        objects.add(new TestRESTResource("mocks_test.csv"));
        return objects;
    }
}
