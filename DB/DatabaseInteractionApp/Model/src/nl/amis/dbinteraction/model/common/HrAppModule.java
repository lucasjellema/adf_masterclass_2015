package nl.amis.dbinteraction.model.common;

import java.util.Date;

import java.util.List;

import oracle.jbo.ApplicationModule;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Wed Jan 28 10:56:34 CET 2015
// ---------------------------------------------------------------------
public interface HrAppModule extends ApplicationModule {
    Date rightNow();

    List<Person> getThePeople();

    List<String> getThePeopleNames();

    String[] getPeopleNames();
}

