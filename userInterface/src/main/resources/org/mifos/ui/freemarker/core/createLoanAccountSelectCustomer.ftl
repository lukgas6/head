[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]

[@layout.webflow currentTab="ClientsAndAccounts"
                 currentState="createLoanAccount.flowState.selectCustomer" 
                 states=["createLoanAccount.flowState.selectCustomer", 
                         "createLoanAccount.flowState.enterAccountInfo",
                         "createLoanAccount.flowState.reviewInstallments", 
                         "createLoanAccount.flowState.reviewAndSubmit"]] 

<h1>[@spring.message "createLoanAccount.wizard.title" /] - <span class="standout">[@spring.message "customerSearch.pageSubtitle" /]</span></h1>
<p>[@spring.message "customerSearch.instructions" /]</p>
<br/>

[#if customerSearchResultsDto.pagedDetails?size == customerSearchResultsDto.searchDetails.pageSize]
    [#assign args=[customerSearchResultsDto.searchDetails.pageSize, customerSearchResultsDto.searchDetails.pageSize] /]
    <div class="notice">[@spring.messageArgs "customerSearch.searchLimitReached" args /]</div>
    <br/>
[/#if]

[@form.errors "customerSearchFormBean.*"/]
<form action="${flowExecutionUrl}" method="post">
    <div class="row">
        <label for="searchString">[@spring.message "customerSearch.searchTerm" /]:</label>
        [@form.input path="customerSearchFormBean.searchString" id="cust_search_account.input.searchString" attributes="" /]
        [@form.submitButton label="widget.form.buttonLabel.search" webflowEvent="searchTermEntered" /]
    </div>
</form>

<br/>
<div class="search-results">
<table id="customerSearchResults" class="datatable">
    <thead>
        <tr>
            <th>Branch</th>
            <th>Center</th>
            <th>Group</th>
            <th>Client</th>
        </tr>
    </thead>
    <tbody>
        [#list customerSearchResultsDto.pagedDetails as customer]
            <tr>
                <td>${customer.branchName}</td>
                <td>${customer.centerName}</td>
                <td>${customer.groupName}</td>
                <td><a href="${flowExecutionUrl}&_eventId=customerSelected&customerId=${customer.customerId}">${customer.clientName}: ${customer.globalId}</a></td>
            </tr>
        [/#list]
    </tbody>
</table>
[@widget.datatable "customerSearchResults" /]
</div>
<div class="clear"/>

<form action="${flowExecutionUrl}" method="post" class="webflow-controls">
    <div class="row centered">
        [@form.cancelButton label="widget.form.buttonLabel.cancel" webflowEvent="cancel" /]
    </div>
</form>

[/@layout.webflow]