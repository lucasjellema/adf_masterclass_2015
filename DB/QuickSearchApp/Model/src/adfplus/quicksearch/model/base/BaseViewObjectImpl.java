package adfplus.quicksearch.model.base;


import adfplus.quicksearch.model.base.otsupport.OracleTextSearchSupport;

import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaItem;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.server.CriteriaAdapterImpl;
import oracle.jbo.server.ViewDefImpl;
import oracle.jbo.server.ViewObjectImpl;


/**
 * Provides support for Oracle Text searching in ViewCriteria. Used in conjunction with
 * <code>{@link BaseViewDefImpl}</code>.
 */
public class BaseViewObjectImpl extends ViewObjectImpl {
    private static final ADFLogger sLog = ADFLogger.createADFLogger(BaseViewObjectImpl.class);

    public BaseViewObjectImpl(String string, ViewDefImpl viewDefImpl) {
        super(string, viewDefImpl);
    }

    public BaseViewObjectImpl() {
        super();
    }

    @Override
    protected BaseViewDefImpl getViewDef() {
        return (BaseViewDefImpl)super.getViewDef();
    }

    /*
     * ORACLE TEXT SEARCH IMPLEMENTATION
     */

    /**
     * Overridden to enable Oracle Text search with normal ViewCriteria.
     * <p>
     * {@inheritDoc}
     *
     * @param vci {@inheritDoc}
     * @return {@inheritDoc}
     *
     * @see OracleTextSearchSupport
     */
    @Override
    public String getCriteriaItemClause(ViewCriteriaItem vci) {
        BaseViewDefImpl def = getViewDef();
        if (!def.isOracleTextSearchEnabled() ||
            !def.getOracleTextSearchAttribute().getName().equals(vci.getName())) {
            // Oracle Text Search does not apply now.
            return super.getCriteriaItemClause(vci);
        }
        //Oracle Text Search does apply.
        String result;
        ViewCriteria vc = vci.getViewCriteria();

        if (vc.getCriteriaMode() == ViewCriteria.CRITERIA_MODE_CACHE) {
            // Java mode, we don't use bind variables because the where clause changes depending on the
            // amount of search tokens in the current search expression.
            result = OracleTextSearchSupport.toCacheModeWhereClause(def, (String)vci.getValue());
        } else {
            // Database mode, use bind variables!
            if (!vci.isBindVarValue() && !vci.isSqlFragment()) {
                ViewCriteriaRow vcr = vci.getViewCriteriaRow();
                int vciIndex = vci.getIndex();
                ViewCriteriaItem bindVci = vcr.getBindVarCriteriaItem(vciIndex);

                if (bindVci == null) {
                    bindVci = new ViewCriteriaItem(vci.getAttributeDef(), vcr);
                    bindVci.copyFrom(vci);

                    // Translate the value to a proper Oracle Text Search expression.
                    bindVci.setValue(OracleTextSearchSupport.toOracleTextExpression((String)bindVci.getValue()));

                    CriteriaAdapterImpl adapter = new CriteriaAdapterImpl();
                    String bindVar = adapter.createTemporaryBindVar(vc, bindVci, 0);
                    bindVci.setIsBindVarValue(true);
                    bindVci.setValue(":" + bindVar);

                    vcr.setBindVarCriteriaItem(vciIndex, bindVci);
                }

                vci = bindVci;
            }

            result = OracleTextSearchSupport.toOracleTextWhereClause(def, (String)vci.getValue());
        }

        sLog.fine("ViewCriteriaItemClause for mode {0}: {1}",
                  new Object[] { vc.getCriteriaMode(), result });
        System.out.println(("ViewCriteriaItemClause for mode "+vc.getCriteriaMode()+ result ));

        return result;
    }
}
