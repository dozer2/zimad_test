package org.borisenko.maxim;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import ru.qatools.properties.Property;
import ru.qatools.properties.PropertyLoader;
import ru.qatools.properties.Resource;


@Resource.Classpath("core.properties")
public class Configuration {
    @Property("authorization.key")
    private String authorizationKey;
    @Property("base.path")
    private String basePath;



    private static  Configuration instance;

    public static Configuration getInstance() {
        if(instance==null){
            instance= new Configuration();
            PropertyLoader.newInstance().populate(instance);
        }
        return instance;
    }

    public String getAuthorizationKey() {
        return authorizationKey;
    }

    public String getBasePath() {
        return basePath;
    }


}
