<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/rich" prefix="af"%>
<f:view>
  <af:document id="d1">
    <af:messages id="m1"/>
    <af:form id="f1">
      <af:panelStretchLayout id="psl1">
        <f:facet name="bottom"/>
        <f:facet name="center">
          <af:panelSplitter id="ps1" splitterPosition="400">
            <f:facet name="first">
                <af:panelHeader text="Auto-Refresh" id="ph2">
              <af:table value="#{bindings.EmployeeViewObj1.collectionModel}"
                        var="row" rows="#{bindings.EmployeeViewObj1.rangeSize}"
                        emptyText="#{bindings.EmployeeViewObj1.viewable ? 'No data to display.' : 'Access Denied.'}"
                        fetchSize="#{bindings.EmployeeViewObj1.rangeSize}"
                        rowBandingInterval="0"
                        selectedRowKeys="#{bindings.EmployeeViewObj1.collectionModel.selectedRow}"
                        selectionListener="#{bindings.EmployeeViewObj1.collectionModel.makeCurrent}"
                        rowSelection="single" id="t1">
                <af:column sortProperty="Empno" sortable="false"
                           headerText="#{bindings.EmployeeViewObj1.hints.Empno.label}"
                           id="c1">
                  <af:inputText value="#{row.bindings.Empno.inputValue}"
                                label="#{bindings.EmployeeViewObj1.hints.Empno.label}"
                                required="#{bindings.EmployeeViewObj1.hints.Empno.mandatory}"
                                columns="#{bindings.EmployeeViewObj1.hints.Empno.displayWidth}"
                                maximumLength="#{bindings.EmployeeViewObj1.hints.Empno.precision}"
                                shortDesc="#{bindings.EmployeeViewObj1.hints.Empno.tooltip}"
                                id="it3">
                    <f:validator binding="#{row.bindings.Empno.validator}"/>
                    <af:convertNumber groupingUsed="false"
                                      pattern="#{bindings.EmployeeViewObj1.hints.Empno.format}"/>
                  </af:inputText>
                </af:column>
                <af:column sortProperty="Ename" sortable="false"
                           headerText="#{bindings.EmployeeViewObj1.hints.Ename.label}"
                           id="c2">
                  <af:inputText value="#{row.bindings.Ename.inputValue}"
                                label="#{bindings.EmployeeViewObj1.hints.Ename.label}"
                                required="#{bindings.EmployeeViewObj1.hints.Ename.mandatory}"
                                columns="#{bindings.EmployeeViewObj1.hints.Ename.displayWidth}"
                                maximumLength="#{bindings.EmployeeViewObj1.hints.Ename.precision}"
                                shortDesc="#{bindings.EmployeeViewObj1.hints.Ename.tooltip}"
                                id="it2">
                    <f:validator binding="#{row.bindings.Ename.validator}"/>
                  </af:inputText>
                </af:column>
                <af:column sortProperty="Sal" sortable="false"
                           headerText="#{bindings.EmployeeViewObj1.hints.Sal.label}"
                           id="c4">
                  <af:inputText value="#{row.bindings.Sal.inputValue}"
                                label="#{bindings.EmployeeViewObj1.hints.Sal.label}"
                                required="#{bindings.EmployeeViewObj1.hints.Sal.mandatory}"
                                columns="#{bindings.EmployeeViewObj1.hints.Sal.displayWidth}"
                                maximumLength="#{bindings.EmployeeViewObj1.hints.Sal.precision}"
                                shortDesc="#{bindings.EmployeeViewObj1.hints.Sal.tooltip}"
                                id="it1">
                    <f:validator binding="#{row.bindings.Sal.validator}"/>
                    <af:convertNumber groupingUsed="false"
                                      pattern="#{bindings.EmployeeViewObj1.hints.Sal.format}"/>
                  </af:inputText>
                </af:column>
                <af:column sortProperty="Job" sortable="true"
                           headerText="#{bindings.EmployeeViewObj1.hints.Job.label}"
                           id="c3">
                  <af:inputText value="#{row.bindings.Job.inputValue}"
                                label="#{bindings.EmployeeViewObj1.hints.Job.label}"
                                required="#{bindings.EmployeeViewObj1.hints.Job.mandatory}"
                                columns="#{bindings.EmployeeViewObj1.hints.Job.displayWidth}"
                                maximumLength="#{bindings.EmployeeViewObj1.hints.Job.precision}"
                                shortDesc="#{bindings.EmployeeViewObj1.hints.Job.tooltip}"
                                id="it4">
                    <f:validator binding="#{row.bindings.Job.validator}"/>
                  </af:inputText>
                </af:column>
                <f:facet name="header">
                  <af:outputText value="Table on Auto-Refreshed ViewObject" id="ot1"/>
                </f:facet>
              </af:table>
              </af:panelHeader>
            </f:facet>
            <f:facet name="second">
                <af:panelHeader text="Non-Auto-Refresh" id="ph1">
                  <f:facet name="context"/>
                  <f:facet name="menuBar"/>
                  <f:facet name="toolbar"/>
                  <f:facet name="legend"/>
                  <f:facet name="info"/>
                  <af:table value="#{bindings.EmployeesNoAutoRefreshViewObj1.collectionModel}"
                            var="row"
                            rows="#{bindings.EmployeesNoAutoRefreshViewObj1.rangeSize}"
                            emptyText="#{bindings.EmployeesNoAutoRefreshViewObj1.viewable ? 'No data to display.' : 'Access Denied.'}"
                            fetchSize="#{bindings.EmployeesNoAutoRefreshViewObj1.rangeSize}"
                            rowBandingInterval="0"
                            selectedRowKeys="#{bindings.EmployeesNoAutoRefreshViewObj1.collectionModel.selectedRow}"
                            selectionListener="#{bindings.EmployeesNoAutoRefreshViewObj1.collectionModel.makeCurrent}"
                            rowSelection="single" id="t2">
                    <af:column sortProperty="Empno" sortable="true"
                               headerText="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Empno.label}"
                               id="c8">
                      <af:inputText value="#{row.bindings.Empno.inputValue}"
                                    label="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Empno.label}"
                                    required="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Empno.mandatory}"
                                    columns="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Empno.displayWidth}"
                                    maximumLength="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Empno.precision}"
                                    shortDesc="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Empno.tooltip}"
                                    id="it5">
                        <f:validator binding="#{row.bindings.Empno.validator}"/>
                        <af:convertNumber groupingUsed="false"
                                          pattern="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Empno.format}"/>
                      </af:inputText>
                    </af:column>
                    <af:column sortProperty="Ename" sortable="true"
                               headerText="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Ename.label}"
                               id="c6">
                      <af:inputText value="#{row.bindings.Ename.inputValue}"
                                    label="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Ename.label}"
                                    required="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Ename.mandatory}"
                                    columns="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Ename.displayWidth}"
                                    maximumLength="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Ename.precision}"
                                    shortDesc="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Ename.tooltip}"
                                    id="it8">
                        <f:validator binding="#{row.bindings.Ename.validator}"/>
                      </af:inputText>
                    </af:column>
                    <af:column sortProperty="Sal" sortable="true"
                               headerText="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Sal.label}"
                               id="c5">
                      <af:inputText value="#{row.bindings.Sal.inputValue}"
                                    label="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Sal.label}"
                                    required="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Sal.mandatory}"
                                    columns="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Sal.displayWidth}"
                                    maximumLength="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Sal.precision}"
                                    shortDesc="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Sal.tooltip}"
                                    id="it6">
                        <f:validator binding="#{row.bindings.Sal.validator}"/>
                        <af:convertNumber groupingUsed="false"
                                          pattern="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Sal.format}"/>
                      </af:inputText>
                    </af:column>
                    <af:column sortProperty="Job" sortable="true"
                               headerText="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Job.label}"
                               id="c7">
                      <af:inputText value="#{row.bindings.Job.inputValue}"
                                    label="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Job.label}"
                                    required="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Job.mandatory}"
                                    columns="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Job.displayWidth}"
                                    maximumLength="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Job.precision}"
                                    shortDesc="#{bindings.EmployeesNoAutoRefreshViewObj1.hints.Job.tooltip}"
                                    id="it7">
                        <f:validator binding="#{row.bindings.Job.validator}"/>
                      </af:inputText>
                    </af:column>
                    <f:facet name="header">
                      <af:outputText value="Table on normal, non-Auto-Refreshed ViewObject"
                                     id="ot2"/>
                    </f:facet>
                  </af:table>
                </af:panelHeader>
            </f:facet>
          </af:panelSplitter>
        </f:facet>
        <f:facet name="start"/>
        <f:facet name="end"/>
        <f:facet name="top">
          <af:panelGroupLayout layout="scroll"
                               xmlns:af="http://xmlns.oracle.com/adf/faces/rich"
                               id="pgl2">
             
            <af:commandButton text="Partial Page Refresh (no requery!)" id="cb1"
                              partialSubmit="true"/>
          </af:panelGroupLayout>
        </f:facet>
      </af:panelStretchLayout>
    </af:form>
  </af:document>
</f:view>