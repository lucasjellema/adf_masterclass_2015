package adfplus.quicksearch.model.base;

import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.AttributeDef;
import oracle.jbo.server.ViewDefImpl;


/**
 * Base implementation to provide the following custom properties on ViewObjects:
 * <ul>
 * <li><b>ORACLE_TEXT_SEARCH_ATTRIBUTE</b>: To mark the column in which the search info is queried.
 * <li><b>ORACLE_TEXT_INDEX_ATTRIBUTE</b>: To mark the database column on which the index was
 * defined in the database.
 * </ul>
 */
public class BaseViewDefImpl extends ViewDefImpl {
    public static final String ORACLE_TEXT_SEARCH_ATTRIBUTE = "ORACLE_TEXT_SEARCH_ATTRIBUTE";
    public static final String ORACLE_TEXT_INDEX_ATTRIBUTE = "ORACLE_TEXT_INDEX_ATTRIBUTE";

    private static final ADFLogger sLog = ADFLogger.createADFLogger(BaseViewDefImpl.class);


    public BaseViewDefImpl(int i, String string, String string1) {
        super(i, string, string1);
    }

    public BaseViewDefImpl(String string) {
        super(string);
    }

    public BaseViewDefImpl(int i, String string) {
        super(i, string);
    }

    public BaseViewDefImpl() {
        super();
    }

    @Override
    protected void finishedLoading() {
        super.finishedLoading();

        initOracleTextSearch();
    }

    /*
   * ORACLE TEXT SEARCH IMPLEMENTATION
   */

    private int oracleTextSearchAttIndex = -1;
    private int oracleTextIndexAttributeIndex = -1;

    /**
     * Returns <code>true</code> iff Oracle Text Search is enabled.
     *
     * @return true iff Oracle Text Search is enabled
     */
    public boolean isOracleTextSearchEnabled() {
        return oracleTextSearchAttIndex >= 0 && oracleTextIndexAttributeIndex >= 0;
    }

    /**
     * Returns the attribute that contains all the searchable information. This
     * attribute should contain the same information that was used to build the
     * Oracle Text index. It is recommended to use a database expression for this.
     *
     * @return the attribute that contains all the searchable information
     */
    public AttributeDef getOracleTextSearchAttribute() {
        return getAttributeDef(oracleTextSearchAttIndex);
    }

    /**
     * Returns the attribute that the Oracle Text index is anchored to, i.e. the
     * database column that was used to create the index.
     *
     * @return the attribute that the Oracle Text index is anchored to
     */
    public AttributeDef getOracleTextIndexAttribute() {
        return getAttributeDef(oracleTextIndexAttributeIndex);
    }

    private void initOracleTextSearch() {
        String indexName = null;
        String searchName = null;

        for (AttributeDef def : getAttributeDefs()) {
            if (def.getProperty(ORACLE_TEXT_INDEX_ATTRIBUTE) != null) {
                if (oracleTextIndexAttributeIndex < 0) {
                    oracleTextIndexAttributeIndex = def.getIndex();
                    indexName = def.getName();
                } else {
                    sLog.severe("Duplicate definition of " + ORACLE_TEXT_INDEX_ATTRIBUTE + " in " +
                                getName() + ".");
                }
            }
            if (def.getProperty(ORACLE_TEXT_SEARCH_ATTRIBUTE) != null) {
                if (oracleTextSearchAttIndex < 0) {
                    oracleTextSearchAttIndex = def.getIndex();
                    searchName = def.getName();
                } else {
                    sLog.severe("Duplicate definition of " + ORACLE_TEXT_SEARCH_ATTRIBUTE +
                                " in " + getName() + ".");
                }
            }
        }
        if (oracleTextSearchAttIndex >= 0 && oracleTextIndexAttributeIndex >= 0) {
            sLog.fine("Oracle Text Search is configured for: " + getName() +
                      " using search attribute: " + searchName + " and index attribute: " +
                      indexName);
        } else if (oracleTextSearchAttIndex >= 0 || oracleTextIndexAttributeIndex >= 0) {
            sLog.warning("Oracle Text Search Implementation needs both: " +
                         ORACLE_TEXT_INDEX_ATTRIBUTE + " and " + ORACLE_TEXT_SEARCH_ATTRIBUTE +
                         " configured.");
        }
    }
}
