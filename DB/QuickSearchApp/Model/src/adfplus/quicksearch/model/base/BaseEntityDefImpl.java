package adfplus.quicksearch.model.base;

import oracle.jbo.server.EntityDefImpl;

/**
 * Sets UpdateChangedColumns to false to make sure that the Oracle Text index column is always
 * touched by the update statement.
 */
public class BaseEntityDefImpl extends EntityDefImpl {
    public BaseEntityDefImpl(int i, String string, String string1) {
        super(i, string, string1);
    }

    public BaseEntityDefImpl(String string) {
        super(string);
    }

    public BaseEntityDefImpl(int i, String string) {
        super(i, string);
    }

    public BaseEntityDefImpl() {
        super();
    }

    @Override
    protected void finishedLoading() {
        super.finishedLoading();

        // The following is to make sure that the Oracle Text index column is always touched by the
        // update statement. (When UpdateChangedColumns is false all columns are always posted.)
        setUpdateChangedColumns(false);
    }
}
