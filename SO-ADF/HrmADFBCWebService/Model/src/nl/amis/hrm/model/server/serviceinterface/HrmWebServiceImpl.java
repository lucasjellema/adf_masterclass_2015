package nl.amis.hrm.model.server.serviceinterface;

import java.lang.reflect.Method;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import javax.interceptor.Interceptors;

import nl.amis.hrm.model.common.EmployeesViewSDO;
import nl.amis.hrm.model.common.serviceinterface.HrmWebService;

import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.common.Diagnostic;
import oracle.jbo.common.sdo.SDOHelper;
import oracle.jbo.common.service.types.FindControl;
import oracle.jbo.common.service.types.FindCriteria;
import oracle.jbo.server.svc.ServiceContextInterceptor;
import oracle.jbo.server.svc.ServiceImpl;
import oracle.jbo.service.errors.ServiceException;

import oracle.webservices.annotations.PortableWebService;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sat Feb 14 10:28:37 CET 2015
// ---------------------------------------------------------------------
@Stateless(name = "nl.amis.hrm.model.common.HrmWebServiceBean", mappedName = "HrmWebServiceBean")
@Remote(HrmWebService.class)
@PortableWebService(targetNamespace = "nl.amis.hrm", serviceName = "HrmWebService",
                    portName = "HrmWebServiceSoapHttpPort",
                    endpointInterface = "nl.amis.hrm.model.common.serviceinterface.HrmWebService")
@Interceptors({ ServiceContextInterceptor.class })
public class HrmWebServiceImpl extends ServiceImpl implements HrmWebService {
    private static boolean _isInited = false;

    /**
     * This is the default constructor (do not remove).
     */
    public HrmWebServiceImpl() {
        init();
        setApplicationModuleDefName("nl.amis.hrm.model.HrmAppModule");
        setConfigurationName("HrmWebService");
    }

    /**
     * Generated method. Do not modify. Do initialization in the constructor
     */
    protected void init() {
        if (_isInited) {
            return;
        }
        synchronized (HrmWebServiceImpl.class) {
            if (_isInited) {
                return;
            }
            try {
                SDOHelper.INSTANCE.defineSchema("nl/amis/hrm/model/common/serviceinterface/", "HrmWebService.xsd");
                _isInited = true;
            } catch (Throwable t) {
                ADFLogger.createADFLogger(Diagnostic.SERVINT_RT_LOGGER).severe(t);
            }
        }
    }

    /**
     * createEmployee: generated method. Do not modify.
     */
    public EmployeesViewSDO createEmployee(EmployeesViewSDO employeesView1) throws ServiceException {
        return (EmployeesViewSDO) create(employeesView1, "EmployeesView1");
    }

    /**
     * updateEmployee: generated method. Do not modify.
     */
    public EmployeesViewSDO updateEmployee(EmployeesViewSDO employeesView1) throws ServiceException {
        return (EmployeesViewSDO) update(employeesView1, "EmployeesView1");
    }

    /**
     * deleteEmployee: generated method. Do not modify.
     */
    public void deleteEmployee(EmployeesViewSDO employeesView1) throws ServiceException {
        delete(employeesView1, "EmployeesView1");
    }

    /**
     * findEmployees: generated method. Do not modify.
     */
    public List<EmployeesViewSDO> findEmployees(FindCriteria findCriteria,
                                                FindControl findControl) throws ServiceException {
        return (List<EmployeesViewSDO>) find(findCriteria, findControl, "EmployeesView1", EmployeesViewSDO.class);
    }
}

