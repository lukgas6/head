/**

 * LabelConfigurationAction.java    version: xxx



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.application.configuration.struts.action;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.business.service.ConfigurationBusinessService;
import org.mifos.application.configuration.persistence.ConfigurationPersistence;
import org.mifos.application.configuration.struts.actionform.LabelConfigurationActionForm;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.configuration.util.helpers.LabelConfigurations;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.office.business.OfficeLevelEntity;
import org.mifos.application.office.business.service.OfficeHierarchyBusinessService;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.util.helpers.GraceTypeConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.components.mifosmenu.MenuRepository;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class LabelConfigurationAction extends BaseAction {

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.CONFIGURATION_LOGGER);

	@Override
	protected boolean skipActionFormToBusinessObjectConversion(String method) {
		return true;
	}

	@Override
	protected BusinessService getService() {
		return ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Configuration);
	}

	@Override
	protected boolean isNewBizRequired(HttpServletRequest request)
			throws ServiceException {
		return false;
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside load method");
		LabelConfigurationActionForm labelConfigurationActionForm = (LabelConfigurationActionForm) form;
		labelConfigurationActionForm.clear();
		UserContext userContext = getUserContext(request);
		setOfficeLevelsInForm(labelConfigurationActionForm, userContext
				.getLocaleId());
		setGracePeriodTypesInForm(labelConfigurationActionForm, userContext
				.getLocaleId());
		setLookupDataInForm(labelConfigurationActionForm, userContext
				.getLocaleId());
		setPaymentModesInForm(labelConfigurationActionForm, userContext
				.getLocaleId());
		setStatusDataInForm(labelConfigurationActionForm, userContext
				.getLocaleId());
		logger.debug("Outside load method");
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("cancel method called");
		return mapping.findForward(ActionForwards.cancel_success.toString());
	}

	@TransactionDemarcate(joinToken = true)
	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside validate method");
		ActionForwards actionForward = ActionForwards.load_failure;
		String method = (String) request.getAttribute("methodCalled");
		if (method != null) {
			if (method.equals(Methods.load.toString())) {
				actionForward = ActionForwards.load_failure;
			}
			else if (method.equals(Methods.update.toString())) {
				actionForward = ActionForwards.update_failure;
			}
		}
		logger.debug("outside validate method");
		return mapping.findForward(actionForward.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Inside update method");
		LabelConfigurationActionForm labelConfigurationActionForm = (LabelConfigurationActionForm) form;
		UserContext userContext = getUserContext(request);
		List<LookUpValueEntity> values = ((ConfigurationBusinessService) getService())
				.getLookupValues();
		List<MifosLookUpEntity> lookupEntities = ((ConfigurationBusinessService) getService())
				.getLookupEntities();
		updateLookupData(labelConfigurationActionForm, userContext
				.getLocaleId(), lookupEntities);
		updateStatusData(labelConfigurationActionForm, userContext
				.getLocaleId(), values);
		updateOfficeData(labelConfigurationActionForm, userContext
				.getLocaleId());
		updateGracePeriodTypes(labelConfigurationActionForm, userContext
				.getLocaleId());
		updatePaymentModes(labelConfigurationActionForm, userContext
				.getLocaleId());

		HibernateUtil.commitTransaction();
		MifosConfiguration.getInstance().init();
		MenuRepository.getInstance().removeLocaleMenu(
				userContext.getPereferedLocale());
		logger.debug("Outside update method");
		return mapping.findForward(ActionForwards.update_success.toString());
	}

	private void setOfficeLevelsInForm(
			LabelConfigurationActionForm labelConfigurationActionForm,
			Short localeId) throws Exception {
		List<OfficeLevelEntity> officeLevels = ((OfficeHierarchyBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.OfficeHierarchy))
				.getOfficeLevels(localeId);
		for (OfficeLevelEntity officeLevelEntity : officeLevels) {
			if (officeLevelEntity.getLevel().equals(OfficeLevel.HEADOFFICE)) {
				labelConfigurationActionForm.setHeadOffice(officeLevelEntity
						.getName(localeId));
			}
			else if (officeLevelEntity.getLevel().equals(
					OfficeLevel.REGIONALOFFICE)) {
				labelConfigurationActionForm
						.setRegionalOffice(officeLevelEntity.getName(localeId));
			}
			else if (officeLevelEntity.getLevel().equals(
					OfficeLevel.SUBREGIONALOFFICE)) {
				labelConfigurationActionForm
						.setSubRegionalOffice(officeLevelEntity
								.getName(localeId));
			}
			else if (officeLevelEntity.getLevel()
					.equals(OfficeLevel.AREAOFFICE)) {
				labelConfigurationActionForm.setAreaOffice(officeLevelEntity
						.getName(localeId));
			}
			else if (officeLevelEntity.getLevel().equals(
					OfficeLevel.BRANCHOFFICE)) {
				labelConfigurationActionForm.setBranchOffice(officeLevelEntity
						.getName(localeId));
			}
		}
	}

	private void setGracePeriodTypesInForm(
			LabelConfigurationActionForm labelConfigurationActionForm,
			Short localeId) throws Exception {
		List<MasterDataEntity> gracePeriodTypes = getMasterEntities(
				GracePeriodTypeEntity.class, localeId);
		for (MasterDataEntity masterDataEntity : gracePeriodTypes) {
			GracePeriodTypeEntity gracePeriodType = (GracePeriodTypeEntity) masterDataEntity;
			if (gracePeriodType.getId().equals(
					GraceTypeConstants.NONE.getValue())) {
				labelConfigurationActionForm.setNone(gracePeriodType
						.getName(localeId));
			}
			else if (gracePeriodType.getId().equals(
					GraceTypeConstants.PRINCIPALONLYGRACE.getValue())) {
				labelConfigurationActionForm
						.setPrincipalOnlyGrace(gracePeriodType
								.getName(localeId));
			}
			else if (gracePeriodType.getId().equals(
					GraceTypeConstants.GRACEONALLREPAYMENTS.getValue())) {
				labelConfigurationActionForm
						.setGraceOnAllRepayments(gracePeriodType
								.getName(localeId));
			}
		}
	}

	private void setLookupDataInForm(
			LabelConfigurationActionForm labelConfigurationActionForm,
			Short localeId) {
		List<MifosLookUpEntity> lookupEntities = ((ConfigurationBusinessService) getService())
				.getLookupEntities();
		for (MifosLookUpEntity entity : lookupEntities) {
			Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
			for (LookUpLabelEntity label : labels) {
				if (entity.getEntityType()
						.equals(ConfigurationConstants.CLIENT)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm
							.setClient(label.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.GROUP)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setGroup(label.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.CENTER)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm
							.setCenter(label.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.LOAN)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setLoans(label.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.SAVINGS)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setSavings(label
							.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.STATE)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setState(label.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.POSTAL_CODE)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setPostalCode(label
							.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.ETHINICITY)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setEthnicity(label
							.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.CITIZENSHIP)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setCitizenship(label
							.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.HANDICAPPED)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setHandicapped(label
							.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.GOVERNMENT_ID)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm
							.setGovtId(label.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.ADDRESS1)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setAddress1(label
							.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.ADDRESS2)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setAddress2(label
							.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.ADDRESS3)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setAddress3(label
							.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.INTEREST)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setInterest(label
							.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.EXTERNALID)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setExternalId(label
							.getLabelName());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.BULKENTRY)
						&& label.getLocaleId().equals(localeId)) {
					labelConfigurationActionForm.setBulkEntry(label
							.getLabelName());
				}
			}
		}
	}

	private void setPaymentModesInForm(
			LabelConfigurationActionForm labelConfigurationActionForm,
			Short localeId) throws Exception {
		List<MasterDataEntity> paymentTypes = getMasterEntities(
				PaymentTypeEntity.class, localeId);
		for (MasterDataEntity masterDataEntity : paymentTypes) {
			PaymentTypeEntity paymentType = (PaymentTypeEntity) masterDataEntity;
			if (paymentType.getId().equals(PaymentTypes.CASH.getValue())) {
				labelConfigurationActionForm.setCash(paymentType
						.getName(localeId));
			}
			else if (paymentType.getId()
					.equals(PaymentTypes.VOUCHER.getValue())) {
				labelConfigurationActionForm.setVouchers(paymentType
						.getName(localeId));
			}
			else if (paymentType.getId().equals(PaymentTypes.CHEQUE.getValue())) {
				labelConfigurationActionForm.setCheck(paymentType
						.getName(localeId));
			}
		}
	}

	private void setStatusDataInForm(
			LabelConfigurationActionForm labelConfigurationActionForm,
			Short localeId) throws Exception {
		List<AccountStateEntity> accountStateEntityList = ((AccountBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Accounts))
				.retrieveAllAccountStateList(AccountTypes.LOANACCOUNT);
		accountStateEntityList
				.addAll(((AccountBusinessService) ServiceFactory.getInstance()
						.getBusinessService(BusinessServiceName.Accounts))
						.retrieveAllAccountStateList(AccountTypes.SAVINGSACCOUNT));
		for (AccountStateEntity accountState : accountStateEntityList) {
			if (accountState.getId().equals(
					AccountState.LOANACC_PARTIALAPPLICATION.getValue())) {
				labelConfigurationActionForm.setPartialApplication(accountState
						.getName(localeId));
			}
			else if (accountState.getId().equals(
					AccountState.LOANACC_PENDINGAPPROVAL.getValue())) {
				labelConfigurationActionForm.setPendingApproval(accountState
						.getName(localeId));
			}
			else if (accountState.getId().equals(
					AccountState.LOANACC_APPROVED.getValue())) {
				labelConfigurationActionForm.setApproved(accountState
						.getName(localeId));
			}
			else if (accountState.getId().equals(
					AccountState.LOANACC_CANCEL.getValue())) {
				labelConfigurationActionForm.setCancel(accountState
						.getName(localeId));
			}
			else if (accountState.getId().equals(
					AccountState.LOANACC_ACTIVEINGOODSTANDING.getValue())) {
				labelConfigurationActionForm
						.setActiveInGoodStanding(accountState.getName(localeId));
			}
			else if (accountState.getId().equals(
					AccountState.LOANACC_BADSTANDING.getValue())) {
				labelConfigurationActionForm
						.setActiveInBadStanding(accountState.getName(localeId));
			}
			else if (accountState.getId().equals(
					AccountState.LOANACC_OBLIGATIONSMET.getValue())) {
				labelConfigurationActionForm
						.setClosedObligationMet(accountState.getName(localeId));
			}
			else if (accountState.getId().equals(
					AccountState.LOANACC_WRITTENOFF.getValue())) {
				labelConfigurationActionForm.setClosedWrittenOff(accountState
						.getName(localeId));
			}
			else if (accountState.getId().equals(
					AccountState.LOANACC_RESCHEDULED.getValue())) {
				labelConfigurationActionForm.setClosedRescheduled(accountState
						.getName(localeId));
			}
			else if (accountState.getId().equals(
					AccountState.SAVINGS_ACC_CLOSED.getValue())) {
				labelConfigurationActionForm.setClosed(accountState
						.getName(localeId));
			}
			else if (accountState.getId().equals(
					AccountState.SAVINGS_ACC_INACTIVE.getValue())) {
				labelConfigurationActionForm.setInActive(accountState
						.getName(localeId));
			}
			else if (accountState.getId().equals(
					AccountState.SAVINGS_ACC_APPROVED.getValue())) {
				labelConfigurationActionForm.setActive(accountState
						.getName(localeId));
			}
		}
		List<CustomerStatusEntity> customerStatusList = ((CustomerBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Customer))
				.retrieveAllCustomerStatusList(CustomerLevel.CLIENT.getValue());
		for (CustomerStatusEntity customerStatus : customerStatusList) {
			if (customerStatus.getId().equals(
					CustomerStatus.CLIENT_HOLD.getValue())) {
				labelConfigurationActionForm.setOnhold(customerStatus
						.getName(localeId));
			}
		}
	}

	private void updateOfficeData(
			LabelConfigurationActionForm labelConfigurationActionForm,
			Short localeId) throws Exception {
		List<OfficeLevelEntity> officeLevels = ((OfficeHierarchyBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.OfficeHierarchy))
				.getOfficeLevels(localeId);
		for (OfficeLevelEntity officeLevelEntity : officeLevels) {
			if (officeLevelEntity.getLevel().equals(OfficeLevel.HEADOFFICE)) {
				officeLevelEntity.update(labelConfigurationActionForm
						.getHeadOffice(), localeId);
			}
			else if (officeLevelEntity.getLevel().equals(
					OfficeLevel.REGIONALOFFICE)) {
				officeLevelEntity.update(labelConfigurationActionForm
						.getRegionalOffice(), localeId);
			}
			else if (officeLevelEntity.getLevel().equals(
					OfficeLevel.SUBREGIONALOFFICE)) {
				officeLevelEntity.update(labelConfigurationActionForm
						.getSubRegionalOffice(), localeId);
			}
			else if (officeLevelEntity.getLevel()
					.equals(OfficeLevel.AREAOFFICE)) {
				officeLevelEntity.update(labelConfigurationActionForm
						.getAreaOffice(), localeId);
			}
			else if (officeLevelEntity.getLevel().equals(
					OfficeLevel.BRANCHOFFICE)) {
				officeLevelEntity.update(labelConfigurationActionForm
						.getBranchOffice(), localeId);
			}
		}
	}

	private void updateGracePeriodTypes(
			LabelConfigurationActionForm labelConfigurationActionForm,
			Short localeId) throws Exception {
		List<MasterDataEntity> gracePeriodTypes = getMasterEntities(
				GracePeriodTypeEntity.class, localeId);
		for (MasterDataEntity masterDataEntity : gracePeriodTypes) {
			GracePeriodTypeEntity gracePeriodType = (GracePeriodTypeEntity) masterDataEntity;
			if (gracePeriodType.getId().equals(
					GraceTypeConstants.NONE.getValue())) {
				gracePeriodType.update(labelConfigurationActionForm.getNone(),
						localeId);
			}
			else if (gracePeriodType.getId().equals(
					GraceTypeConstants.PRINCIPALONLYGRACE.getValue())) {
				gracePeriodType.update(labelConfigurationActionForm
						.getPrincipalOnlyGrace(), localeId);
			}
			else if (gracePeriodType.getId().equals(
					GraceTypeConstants.GRACEONALLREPAYMENTS.getValue())) {
				gracePeriodType.update(labelConfigurationActionForm
						.getGraceOnAllRepayments(), localeId);
			}
		}
	}

	private void updatePaymentModes(
			LabelConfigurationActionForm labelConfigurationActionForm,
			Short localeId) throws Exception {
		List<MasterDataEntity> paymentTypes = getMasterEntities(
				PaymentTypeEntity.class, localeId);
		for (MasterDataEntity masterDataEntity : paymentTypes) {
			PaymentTypeEntity paymentType = (PaymentTypeEntity) masterDataEntity;
			if (paymentType.getId().equals(PaymentTypes.CASH.getValue())) {
				paymentType.update(labelConfigurationActionForm.getCash(),
						localeId);
			}
			else if (paymentType.getId()
					.equals(PaymentTypes.VOUCHER.getValue())) {
				paymentType.update(labelConfigurationActionForm.getVouchers(),
						localeId);
			}
			else if (paymentType.getId().equals(PaymentTypes.CHEQUE.getValue())) {
				paymentType.update(labelConfigurationActionForm.getCheck(),
						localeId);
			}
		}
	}

	private void updateLookupData(
			LabelConfigurationActionForm labelConfigurationActionForm,
			Short localeId, List<MifosLookUpEntity> lookupEntities)
			throws Exception {
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		for (MifosLookUpEntity entity : lookupEntities) {
			Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
			for (LookUpLabelEntity label : labels) {
				if (entity.getEntityType()
						.equals(ConfigurationConstants.CLIENT)
						&& label.getLocaleId().equals(localeId)) {
					label
							.setLabelName(labelConfigurationActionForm
									.getClient());

				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.GROUP)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm.getGroup());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.CENTER)
						&& label.getLocaleId().equals(localeId)) {
					label
							.setLabelName(labelConfigurationActionForm
									.getCenter());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.LOAN)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm.getLoans());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.SAVINGS)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm
							.getSavings());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.STATE)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm.getState());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.POSTAL_CODE)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm
							.getPostalCode());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.ETHINICITY)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm
							.getEthnicity());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.CITIZENSHIP)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm
							.getCitizenship());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.HANDICAPPED)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm
							.getHandicapped());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.GOVERNMENT_ID)
						&& label.getLocaleId().equals(localeId)) {
					label
							.setLabelName(labelConfigurationActionForm
									.getGovtId());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.ADDRESS1)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm
							.getAddress1());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.ADDRESS2)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm
							.getAddress2());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.ADDRESS3)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm
							.getAddress3());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.INTEREST)
						&& label.getLocaleId().equals(localeId)
						|| entity.getEntityType().equals(
								ConfigurationConstants.SERVICE_CHARGE)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm
							.getInterest());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.EXTERNALID)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm
							.getExternalId());
				}
				else if (entity.getEntityType().equals(
						ConfigurationConstants.BULKENTRY)
						&& label.getLocaleId().equals(localeId)) {
					label.setLabelName(labelConfigurationActionForm
							.getBulkEntry());
				}
				configurationPersistence.createOrUpdate(label);
			}
		}
	}

	private void updateStatusData(
			LabelConfigurationActionForm labelConfigurationActionForm,
			Short localeId, List<LookUpValueEntity> values) throws Exception {
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		for (LookUpValueEntity value : values) {
			String name = null;
			if (isPartialApplicationLookup(value)) {
				name = labelConfigurationActionForm.getPartialApplication();
			}
			else if (isPendingApprovalLookup(value)) {
				name = labelConfigurationActionForm.getPendingApproval();
			}
			else if (isActiveLookup(value)) {
				name = labelConfigurationActionForm.getActive();
			}
			else if (value.getLookUpId().equals(
					LabelConfigurations.CLIENT_ON_HOLD.getValue())
					|| value.getLookUpId().equals(
							LabelConfigurations.GROUP_ON_HOLD.getValue())) {
				name = labelConfigurationActionForm.getOnhold();
			}
			else if (isCancelLookup(value)) {
				name = labelConfigurationActionForm.getCancel();
			}
			else if (isClosedLookup(value)) {
				name = labelConfigurationActionForm.getClosed();
			}
			else if (isInActiveLookup(value)) {
				name = labelConfigurationActionForm.getInActive();
			}
			else if (value.getLookUpId().equals(
					LabelConfigurations.LOAN_CLOSED_OBLIGATION_MET.getValue())) {
				name = labelConfigurationActionForm.getClosedObligationMet();
			}
			else if (value.getLookUpId().equals(
					LabelConfigurations.LOAN_CLOSED_WRITTEN_OFF.getValue())) {
				name = labelConfigurationActionForm.getClosedWrittenOff();
			}
			else if (value.getLookUpId().equals(
					LabelConfigurations.LOAN_CLOSED_RESCHEDULE.getValue())) {
				name = labelConfigurationActionForm.getClosedRescheduled();
			}
			else if (value.getLookUpId().equals(
					LabelConfigurations.LOAN_ACTIVE_BAD_STANDING.getValue())) {
				name = labelConfigurationActionForm.getActiveInBadStanding();
			}
			else if (value.getLookUpId().equals(
					LabelConfigurations.LOAN_ACTIVE_GOOD_STANDING.getValue())) {
				name = labelConfigurationActionForm.getActiveInGoodStanding();
			}
			else if (value.getLookUpId().equals(
					LabelConfigurations.LOAN_APPROVED.getValue())) {
				name = labelConfigurationActionForm.getApproved();
			}
			else if (value.getLookUpId().equals(
					LabelConfigurations.FEE_CATEGORY_CLIENT.getValue())) {
				name = labelConfigurationActionForm.getClient();
			}
			else if (value.getLookUpId().equals(
					LabelConfigurations.FEE_CATEGORY_GROUP.getValue())) {
				name = labelConfigurationActionForm.getGroup();
			}
			else if (value.getLookUpId().equals(
					LabelConfigurations.FEE_CATEGORY_CENTER.getValue())
					|| value.getLookUpId().equals(Integer.valueOf("499"))) {
				name = labelConfigurationActionForm.getCenter();
			}
			else if (isLoanLookup(value)) {
				name = labelConfigurationActionForm.getLoans();
			}
			else if (value.getLookUpId().equals(Integer.valueOf("55"))
					|| value.getLookUpId().equals(Integer.valueOf("87"))) {
				name = labelConfigurationActionForm.getSavings();
			}
			if (name != null) {
				Set<LookUpValueLocaleEntity> localeValues = value
						.getLookUpValueLocales();
				for (LookUpValueLocaleEntity lookuplocale : localeValues) {
					if (lookuplocale.getLocaleId().equals(localeId)) {
						lookuplocale.setLookUpValue(name);
					}
				}
				configurationPersistence.createOrUpdate(value);
			}
		}
	}

	private boolean isLoanLookup(LookUpValueEntity value) {
		return value.getLookUpId().equals(
				LabelConfigurations.FEE_CATEGORY_LOAN.getValue())
				|| value.getLookUpId().equals(Integer.valueOf("54"))
				|| value.getLookUpId().equals(Integer.valueOf("126"));
	}

	private boolean isClosedLookup(LookUpValueEntity value) {
		return value.getLookUpId().equals(
				LabelConfigurations.CLIENT_CLOSED.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.GROUP_CLOSED.getValue())
				|| value.getLookUpId().equals(Integer.valueOf("117"))
				|| value.getLookUpId().equals(
						LabelConfigurations.SAVINGS_CLOSED.getValue());
	}

	private boolean isCancelLookup(LookUpValueEntity value) {
		return value.getLookUpId().equals(
				LabelConfigurations.CLIENT_CANCEL.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.GROUP_CANCEL.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.LOAN_CANCEL.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.SAVINGS_CANCEL.getValue());
	}

	private boolean isPendingApprovalLookup(LookUpValueEntity value) {
		return value.getLookUpId().equals(
				LabelConfigurations.CLIENT_PENDING_APPROVAL.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.GROUP_PENDING_APPROVAL.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.LOAN_PENDING_APPROVAL.getValue())
				|| value.getLookUpId()
						.equals(
								LabelConfigurations.SAVINGS_PENDING_APPROVAL
										.getValue());
	}

	private boolean isActiveLookup(LookUpValueEntity value) {
		return value.getLookUpId().equals(
				LabelConfigurations.CLIENT_ACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.GROUP_ACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.CENTER_ACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.OFFICE_ACTIVE.getValue())
				|| value.getLookUpId().equals(Integer.valueOf("26"))
				|| value.getLookUpId().equals(Integer.valueOf("51"))
				|| value.getLookUpId().equals(
						LabelConfigurations.PRD_CATEGORY_ACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.PRD_ACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.PERSONNEL_ACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.FEE_ACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.SAVINGS_ACTIVE.getValue());
	}

	private boolean isInActiveLookup(LookUpValueEntity value) {
		return value.getLookUpId().equals(
				LabelConfigurations.CENTER_INACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.OFFICE_INACTIVE.getValue())
				|| value.getLookUpId().equals(Integer.valueOf("27"))
				|| value.getLookUpId().equals(Integer.valueOf("52"))
				|| value.getLookUpId().equals(
						LabelConfigurations.PRD_CATEGORY_INACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.PRD_INACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.PERSONNEL_INACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.FEE_INACTIVE.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.SAVINGS_INACTIVE.getValue());
	}

	private boolean isPartialApplicationLookup(LookUpValueEntity value) {
		return value.getLookUpId().equals(
				LabelConfigurations.CLIENT_PARTIAL_APPLICATION.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.GROUP_PARTIAL_APPLICATION
								.getValue())
				|| value.getLookUpId()
						.equals(
								LabelConfigurations.LOAN_PARTIAL_APPLICATION
										.getValue())
				|| value.getLookUpId().equals(
						LabelConfigurations.SAVINGS_PARTIAL_APPLICATION
								.getValue());
	}
}
