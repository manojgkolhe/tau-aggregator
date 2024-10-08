/*
 * Copyleft (c) 2015. This code is for learning purposes only.
 * Do whatever you like with it but don't take it as perfect code.
 * //Michel Racic (http://rac.su/+)//
 */

package com.mgk.tau.guice;


import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.mgk.tau.AggregatedResourceBundle;
import com.mgk.tau.ConfigEnvironment;
import com.mgk.tau.ConfigProvider;
import org.testng.ITestContext;
import org.testng.xml.XmlTest;

import java.util.List;
import java.util.Locale;

/**
 * Created by rac on 06.04.15.
 */
public class ConfigModule extends AbstractModule {

    private AggregatedResourceBundle testngParams;
    private ConfigEnvironment env;
    private Class testClass;
    private ConfigProvider parent;
    private List<Module> guiceModules;

    public static final String ENVIRONMENT_NAME = "environment.name";
    public static final String ENVIRONMENT_DESCRIPTION = "environment.description";
    public static final String ENVIRONMENT_CODE = "environment.code";
    public static final String ENVIRONMENT_LOCALE = "environment.locale";

    /**
     * Constructor for the guice module to be used outside of TestNG
     *
     * @param env
     * @param testClass
     */
    public ConfigModule(ConfigEnvironment env, Class<?> testClass) {
        this.env = env;
        this.testClass = testClass;
    }

    /**
     * Constructor for the guice module to be used outside of TestNG but giving a map of key value pairs that override
     * all keys from property files.
     *
     * @param env
     * @param testClass
     */
    public ConfigModule(ConfigProvider parent, ConfigEnvironment env, Class<?> testClass, AggregatedResourceBundle params) {
        this.env = env;
        this.testClass = testClass;
        testngParams = params;
        this.parent = parent;
    }

    /**
     * Constructor to be used from the @see ConfigModuleFactory for TestNG
     *
     * @param context
     * @param testClass
     */
    public ConfigModule(ITestContext context, Class<?> testClass) {
        XmlTest test = context.getCurrentXmlTest();
        String envName = test.getParameter(ENVIRONMENT_NAME);
        String envDesc = test.getParameter(ENVIRONMENT_DESCRIPTION);
        String envCode = test.getParameter(ENVIRONMENT_CODE);
        String envLocale = test.getParameter(ENVIRONMENT_LOCALE);
        Locale locale = null;
        if (envLocale != null) {
            locale = Locale.forLanguageTag(envLocale);
        }
        if (envCode != null) {
            env = new ConfigEnvironment(envName, envDesc, envCode, locale);
        }

        this.testClass = testClass;
        this.testngParams = new AggregatedResourceBundle();
        this.testngParams.mergeOverride(context.getCurrentXmlTest().getAllParameters());
        // TODO get current TestNG guice modules from context

    }

    @Override
    protected void configure() {
        bind(ConfigProvider.class).toProvider(new ConfigModuleProvider(parent, env, testClass, testngParams).setGuiceModules(guiceModules));
    }
}
