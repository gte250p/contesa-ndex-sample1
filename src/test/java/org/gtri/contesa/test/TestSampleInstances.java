package org.gtri.contesa.test;

import gtri.logging.Logger;
import gtri.logging.LoggerFactory;
import org.gtri.contesa.ServiceManager;
import org.gtri.contesa.rules.RuleContext;
import org.gtri.contesa.rules.RuleContextResolutionException;
import org.gtri.contesa.rules.RuleExecutionException;
import org.gtri.contesa.tools.model.ContextAwareRuleResult;
import org.gtri.contesa.tools.model.NextRuleExecutionResult;
import org.gtri.contesa.tools.model.ValidationObject;
import org.gtri.contesa.tools.services.*;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class demonstrates how to validate using the ConTesA jar files.
 * <br/><br/>
 * User: slee35 (contesa@gtri.gatech.edu)
 * Date: 10/16/2013 9:22 PM
 */
public class TestSampleInstances {

    public static final Logger logger = LoggerFactory.get(TestSampleInstances.class);

    protected ValidationObjectProvider getValidationObjectProvider( ServiceManager serviceManager, String mimeType ){
        List<ValidationObjectProvider> validationObjectProviders = serviceManager.getBeans(ValidationObjectProvider.class);
        for( ValidationObjectProvider provider : validationObjectProviders ){
            if( provider.accepts(mimeType) )
                return provider;
        }
        return null;
    }

    @Test
    public void testValidCase()
    throws  ValidationObjectResolutionException,
            RuleExecutionException, RuleResultPostProcessException, RuleContextResolutionException, StatusCalculationException {
        logger.info("Testing the valid case...");

        // First, we must initialize ConTesA.  This is done by initializing the "ServiceManager" class.  Note that
        //  this can take a while...
        logger.debug("Initializing ConTesA...");
        ServiceManager serviceManager = ServiceManager.getInstance();

        // Get the file with XML to validate
        logger.debug("Getting file...");
        String sampleAllFieldsFilePath = "./src/test/resources/Sample-all-fields.xml";
        File sampleAllFieldsFile = new File(sampleAllFieldsFilePath);
        assertThat( sampleAllFieldsFile, notNullValue() );
        assertThat( sampleAllFieldsFile.exists(), is(Boolean.TRUE) );

        // In this case, we know it's an XML file, so we get the "XML Validation Object Provider" to help us get the object.
        logger.debug("Getting ConTesA Validation Object...");
        ValidationObjectProvider validationObjectProvider = getValidationObjectProvider(serviceManager, "text/xml");
        assertThat( validationObjectProvider, notNullValue() );

        // Now we get the ValidationObject, the object ConTesA knows how to process...
        List<ValidationObject> validationObjects = validationObjectProvider.resolve(sampleAllFieldsFile, sampleAllFieldsFile.getName(), "text/xml");
        assertThat(validationObjects, notNullValue());
        assertThat(validationObjects.size(), equalTo(1)); // XML files generally parse 1 to 1, but don't have to.  LEXS & N-DEx always will, however.

        ValidationObject validationObject = validationObjects.get(0);
        assertThat(validationObject, notNullValue());

        // Now we get the abstraction service, which handles interfacing with lower-level ConTesA classes for us...
        ContesaAbstractionService contesaAbstractionService = serviceManager.getBean(ContesaAbstractionService.class);
        assertThat(contesaAbstractionService, notNullValue());

        logger.debug("Validating rule contexts are loaded...");
        List<RuleContext> ruleContexts = contesaAbstractionService.resolveContexts(validationObject);
        assertThat(ruleContexts, notNullValue());
        assertThat(ruleContexts.size(), equalTo(2));
        for( RuleContext context : ruleContexts ){
            logger.debug("   Matched Context: @|green %s|@", context.getName());
        }

        // Execute all rules, until terminated.
        logger.debug("Executing business rules...");
        int ruleCount = 0;
        NextRuleExecutionResult result = null;
        List<ContextAwareRuleResult> allResults = new ArrayList<ContextAwareRuleResult>();
        do{
            result = contesaAbstractionService.executeNext(validationObject);
            if( result != null ){
                logger.debug("    Successfully executed rule[@|cyan %s|@]: @|blue %s|@", result.getBusinessRule().getIdentifier(), result.getBusinessRule().getName());
                allResults.addAll(result.getResults());
                ruleCount++;
            }
        }while( result != null && !result.isFinished() );

        // Assert validate instance has no results (which indicate errors/warnings and/or information messages)
        assertThat(allResults.size(), equalTo(0));
        // Also assert that the proper rules were executed.
        assertThat(ruleCount, equalTo(118));

        logger.info("Successfully tested the valid case!");
    }//end testValidCase()

    @Test
    public void testInvalidCase()
            throws  ValidationObjectResolutionException,
            RuleExecutionException, RuleResultPostProcessException, RuleContextResolutionException, StatusCalculationException {
        logger.info("Testing the invalid case...");

        // First, we must initialize ConTesA.  This is done by initializing the "ServiceManager" class.  Note that
        //  this can take a while...
        logger.debug("Initializing ConTesA...");
        ServiceManager serviceManager = ServiceManager.getInstance();

        // Get the file with XML to validate
        logger.debug("Getting file...");
        String dataItemCategoryFilePath = "./src/test/resources/70-data-item-category.xml";
        File dataItemCategoryFile = new File(dataItemCategoryFilePath);
        assertThat( dataItemCategoryFile, notNullValue() );
        assertThat( dataItemCategoryFile.exists(), is(Boolean.TRUE) );

        // In this case, we know it's an XML file, so we get the "XML Validation Object Provider" to help us get the object.
        logger.debug("Getting ConTesA Validation Object...");
        ValidationObjectProvider validationObjectProvider = getValidationObjectProvider(serviceManager, "text/xml");
        assertThat( validationObjectProvider, notNullValue() );

        // Now we get the ValidationObject, the object ConTesA knows how to process...
        List<ValidationObject> validationObjects = validationObjectProvider.resolve(dataItemCategoryFile, dataItemCategoryFile.getName(), "text/xml");
        assertThat(validationObjects, notNullValue());
        assertThat(validationObjects.size(), equalTo(1)); // XML files generally parse 1 to 1, but don't have to.  LEXS & N-DEx always will, however.

        ValidationObject validationObject = validationObjects.get(0);
        assertThat(validationObject, notNullValue());

        // Now we get the abstraction service, which handles interfacing with lower-level ConTesA classes for us...
        ContesaAbstractionService contesaAbstractionService = serviceManager.getBean(ContesaAbstractionService.class);
        assertThat(contesaAbstractionService, notNullValue());

        logger.debug("Validating rule contexts are loaded...");
        List<RuleContext> ruleContexts = contesaAbstractionService.resolveContexts(validationObject);
        assertThat(ruleContexts, notNullValue());
        assertThat(ruleContexts.size(), equalTo(2));
        for( RuleContext context : ruleContexts ){
            logger.debug("   Matched Context: @|green %s|@", context.getName());
        }

        // Execute all rules, until terminated.
        logger.debug("Executing business rules...");
        int ruleCount = 0;
        NextRuleExecutionResult result = null;
        List<ContextAwareRuleResult> allResults = new ArrayList<ContextAwareRuleResult>();
        do{
            result = contesaAbstractionService.executeNext(validationObject);
            if( result != null ){
                logger.debug("    Successfully executed rule[@|cyan %s|@]: @|blue %s|@", result.getBusinessRule().getIdentifier(), result.getBusinessRule().getName());
                allResults.addAll(result.getResults());
                ruleCount++;
            }
        }while( result != null && !result.isFinished() );

        // Also assert that the proper rules were executed.
        assertThat(ruleCount, equalTo(118));
        // Assert validate instance has 40 results
        assertThat(allResults.size(), equalTo(40));

        // TODO We could do additional validation of results here...

        logger.info("Successfully tested the valid case!");
    }//end testValidCase()





}//end TestSampleInstances
