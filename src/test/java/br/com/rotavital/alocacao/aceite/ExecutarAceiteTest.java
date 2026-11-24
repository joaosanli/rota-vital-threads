package br.com.rotavital.alocacao.aceite;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

/** Executa os cenarios Gherkin de src/test/resources/features. */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.com.rotavital.alocacao.aceite")
@ConfigurationParameter(key = "cucumber.plugin", value = "json:target/surefire-reports/cucumber.json")
public class ExecutarAceiteTest {
}
