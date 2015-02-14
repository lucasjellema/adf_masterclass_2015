package nl.amis.dbinteraction.model.common;

import java.lang.String;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.sql.SQLException;

import oracle.jbo.JboException;
import oracle.jbo.StructureDef;
import oracle.jbo.domain.DatumFactory;
import oracle.jbo.domain.DomainAttributeDef;
import oracle.jbo.domain.DomainInterface;
import oracle.jbo.domain.DomainOwnerInterface;
import oracle.jbo.domain.DomainStructureDef;
import oracle.jbo.domain.Struct;

import oracle.sql.*;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sun Feb 01 08:59:56 CET 2015
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class SocialProfileT extends Struct {
    static ORADataFactory[] mCustDatFac = null;
    static int[] mSQLTypes = null;
    static DomainStructureDef mStructureDef = null;
    static ORADataFactory fac;

    public SocialProfileT(Datum value) throws SQLException {
        super(value);
    }

    public SocialProfileT() throws SQLException {
    }

    public static ORADataFactory getORADataFactory() {
        if (fac == null) {
            class facClass implements ORADataFactory {
                public ORAData create(Datum d, int sql_type_code) throws SQLException {
                    if (d != null) {
                        return new SocialProfileT(d);
                    }
                    return null;
                }
            }
            fac = new facClass();
        }
        return fac;
    }

    public int[] getAttrSQLTypes() {
        if (mSQLTypes == null) {
            mSQLTypes = buildAttrSQLTypes();
        }
        return mSQLTypes;
    }

    public StructureDef getStructureDef() {
        return mStructureDef;
    }

    public String getColumnType() {
        return "SOCIAL_PROFILE_T";
    }

    public String getLinkedinAccount() {
        return (String) getAttribute(0);
    }

    public void setLinkedinAccount(String value) {
        setAttribute(0, value);
    }

    public String getTwitterAccount() {
        return (String) getAttribute(1);
    }

    public void setTwitterAccount(String value) {
        setAttribute(1, value);
    }

    public String getFacebookAccount() {
        return (String) getAttribute(2);
    }

    public void setFacebookAccount(String value) {
        setAttribute(2, value);
    }

    public String getMsnAccount() {
        return (String) getAttribute(3);
    }

    public void setMsnAccount(String value) {
        setAttribute(3, value);
    }

    public String getPersonalBlog() {
        return (String) getAttribute(4);
    }

    public void setPersonalBlog(String value) {
        setAttribute(4, value);
    }

    public void initStructureDef() {
        DomainAttributeDef[] attrs = new DomainAttributeDef[5];
        if (mStructureDef == null) {
            attrs[0] =
                new DomainAttributeDef("LinkedinAccount", "LINKEDIN_ACCOUNT", 0, String.class, 12, "VARCHAR", -127, 100,
                                       false);
            attrs[1] =
                new DomainAttributeDef("TwitterAccount", "TWITTER_ACCOUNT", 1, String.class, 12, "VARCHAR", -127, 100,
                                       false);
            attrs[2] =
                new DomainAttributeDef("FacebookAccount", "FACEBOOK_ACCOUNT", 2, String.class, 12, "VARCHAR", -127, 100,
                                       false);
            attrs[3] =
                new DomainAttributeDef("MsnAccount", "MSN_ACCOUNT", 3, String.class, 12, "VARCHAR", -127, 100, false);
            attrs[4] =
                new DomainAttributeDef("PersonalBlog", "PERSONAL_BLOG", 4, String.class, 12, "VARCHAR", -127, 100,
                                       false);
            mStructureDef = new DomainStructureDef(attrs);
        }
    }

    public ORADataFactory[] getAttrORADataFactories() {
        if (mCustDatFac == null) {
            mCustDatFac = new ORADataFactory[5];
            mCustDatFac[0] = null;
            mCustDatFac[1] = null;
            mCustDatFac[2] = null;
            mCustDatFac[3] = null;
            mCustDatFac[4] = null;
        }
        return mCustDatFac;
    }
}

