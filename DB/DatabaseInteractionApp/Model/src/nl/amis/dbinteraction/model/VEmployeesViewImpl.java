package nl.amis.dbinteraction.model;

import nl.amis.dbinteraction.DatabaseProcedure;

import oracle.jbo.server.ViewObjectImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sun Feb 01 11:27:27 CET 2015
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class VEmployeesViewImpl extends ViewObjectImpl {

    private static final DatabaseProcedure RESET_APP_CONTEXT_PROC =
        DatabaseProcedure.define("procedure CACHE_MGR.RESET_CONTEXT");

    private static final DatabaseProcedure SET_APP_CONTEXT_PROC =
        DatabaseProcedure.define("procedure CACHE_MGR.PUT_IN_CACHE( p_key in varchar2, p_value in varchar2)");


    /**
     * This is the default constructor (do not remove).
     */
    public VEmployeesViewImpl() {
    }

    /**
     * Returns the variable value for bind_variable.
     * @return variable value for bind_variable
     */
    public String getbind_variable() {
        return (String) ensureVariableManager().getVariableValue("bind_variable");
    }

    /**
     * Sets <code>value</code> for variable bind_variable.
     * @param value value to bind as bind_variable
     */
    public void setbind_variable(String value) {
        ensureVariableManager().setVariableValue("bind_variable", value);
    }

    @Override
    protected void executeQueryForCollection(Object object, Object[] object2, int i) {
       DatabaseProcedure.Result result = RESET_APP_CONTEXT_PROC.call(getDBTransaction());
       if (getbind_param() != null) {
            String[] params = getbind_param().split(";");
            for (int j=0;j<params.length;j++) {
                String[] kv = params[j].split("=");
                DatabaseProcedure.Result result2 = 
                             SET_APP_CONTEXT_PROC.call(getDBTransaction(), kv[0],kv[1]);
                 
            }
        }
        super.executeQueryForCollection(object, object2, i);
    }

    /**
     * Returns the bind variable value for bind_param.
     * @return bind variable value for bind_param
     */
    public String getbind_param() {
        return (String) getNamedWhereClauseParam("bind_param");
    }

    /**
     * Sets <code>value</code> for bind variable bind_param.
     * @param value value to bind as bind_param
     */
    public void setbind_param(String value) {
        setNamedWhereClauseParam("bind_param", value);
    }
}

