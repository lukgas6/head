package org.mifos.framework.struts.action;

import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.persistence.SessionOpener;
import org.mifos.framework.persistence.ThreadLocalOpener;

public class PersistenceAction extends BaseAction {
	
	private static SessionOpener defaultSessionOpener = new ThreadLocalOpener();
	protected SessionOpener opener;

	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
	}

	public static SessionOpener getDefaultSessionOpener() {
		return defaultSessionOpener;
	}

	public static void setDefaultSessionOpener(SessionOpener defaultSessionOpener) {
		PersistenceAction.defaultSessionOpener = defaultSessionOpener;
	}
	
	public PersistenceAction() {
		opener = PersistenceAction.defaultSessionOpener;
	}

}
package org.mifos.framework.struts.action;

import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.persistence.SessionOpener;
import org.mifos.framework.persistence.ThreadLocalOpener;

public class PersistenceAction extends BaseAction {
	
	private static SessionOpener defaultSessionOpener = new ThreadLocalOpener();
	protected SessionOpener opener;

	@Override
	protected BusinessService getService() throws ServiceException {
		throw new RuntimeException("not implemented");
	}

	public static SessionOpener getDefaultSessionOpener() {
		return defaultSessionOpener;
	}

	public static void setDefaultSessionOpener(SessionOpener defaultSessionOpener) {
		PersistenceAction.defaultSessionOpener = defaultSessionOpener;
	}
	
	public PersistenceAction() {
		opener = PersistenceAction.defaultSessionOpener;
	}

}
