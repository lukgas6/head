package org.mifos.framework.struts.plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import org.mifos.application.accounts.savings.struts.action.SavingsAction;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ConstantsNotLoadedException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestConstPlugin extends MifosMockStrutsTestCase {

	private SavingsOfferingBO offering;

	@Override
	public void setUp()throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI("org/mifos/framework/struts/util/helpers/struts-config.xml").getPath());

		request.getSession(true);
		createFlowAndAddToRequest(SavingsAction.class);
		request.getSession().setAttribute(Constants.USERCONTEXT, 
			TestUtils.makeUser(1));
		
		offering = TestObjectFactory.createSavingsOffering(
			"Offering1", "s1", 
			SavingsType.MANDATORY, ApplicableTo.CLIENTS);
		addRequestParameter("selectedPrdOfferingId", 
			offering.getPrdOfferingId().toString());
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(offering);
		super.tearDown();
	}

	/**
	 *This method performs an action to load Plugins defined in struts-config.xml.
	 */
	public void testMasterConstants() throws Exception{
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		performNoErrors();
		Map constantMap = (Map)context.getAttribute("MasterConstants");
		assertNotNull(constantMap);
		assertEquals("PaymentType",constantMap.get("PAYMENT_TYPE"));
		assertEquals(Short.valueOf("1"),constantMap.get("CUSTOMFIELD_NUMBER"));
	}

	public void testIfAllConstantFilesAreLoaded(){
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method","load");
		addRequestParameter("recordOfficeId","0");
		addRequestParameter("recordLoanOfficerId","0");
		performNoErrors();
		assertNotNull(context.getAttribute("Constants"));
		assertNotNull(context.getAttribute("MasterConstants"));
		assertNotNull(context.getAttribute("CustomerConstants"));
		assertNotNull(context.getAttribute("AccountStates"));
		assertNotNull(context.getAttribute("SavingsConstants"));
	}

	public void testConstantsPluginException() throws Exception {
		ConstPlugin constPlugin = new ConstPlugin();
		ArrayList<String> constPluginClasses = new ArrayList<String>();
		constPluginClasses.add("org.mifos.doesNotExist");
		try {
			Class doesNotExistClass = ConstPlugin.class;
			Field[] fields = doesNotExistClass.getDeclaredFields();
			Field field = fields[0];
			ConstPlugin.checkModifiers(field);
		} catch (ConstantsNotLoadedException expected) {
		}

		try {
			constPlugin.buildClasses(constPluginClasses);
			fail();
		} catch (ConstantsNotLoadedException expected) {
		}
	}

}
