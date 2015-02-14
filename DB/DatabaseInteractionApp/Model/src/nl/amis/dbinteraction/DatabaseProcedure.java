package nl.amis.dbinteraction;
/*******************************************************************************
Created by Paco van der Linden in his spare time, used in Oracle JHeadstart and
several other products.

Notes       : requires weblogic.utils.wrapper.Wrapper class that is found in library com.bea.core.utils.wrapper-1.3.0.0.jar

Open Issues :

$revision_history$
 04-aug-2011   Paco van der Linden
   1.4         oracleReturnValue is now correctly implemented
 02-dec-2010   Paco van der LInden
   1.3         Added support for CLOB types.
 22-nov-2010   Paco van der Linden
   1.2         Added custom parameter types and Oracle return types
 04-oct-2010   Steven Davelaar
   1.1         Added caching of procedures created in define method
 03-jun-2009   Paco van der Linden
   1.0         initial creation

******************************************************************************/

import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.COMMENTS;
import static java.util.regex.Pattern.DOTALL;

import oracle.jbo.CSMessageBundle;
import oracle.jbo.SQLStmtException;
import oracle.jbo.domain.Array;
import oracle.jbo.domain.ClobDomain;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;
import oracle.jbo.server.DBTransaction;
import static oracle.jbo.server.DBTransaction.DEFAULT;

import oracle.jdbc.OracleCallableStatement;

import oracle.sql.ArrayDescriptor;
import oracle.sql.CLOB;
import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;

import oracle.adf.share.logging.ADFLogger;

//import weblogic.utils.wrapper.Wrapper;


/**
 * <code>DatabaseProcedure</code> can be used to call (PL/SQL) stored procedures through a
 * JDBC connection in a very intuitive way.
 *
 * <h3>Examples</h3>
 * The functionality of this class is best explained through some examples. In the following examples
 * the <code>connection</code> variable always points to a <code>{@link java.sql.Connection}</code> object.
 * <p>
 * Instead of a <code>java.sql.Connection</code> it is also possible to use an
 * <code>{@link oracle.jbo.server.DBTransaction}</code>. This is especially useful when
 * this class is used with ADF Business Components.
 * <p>
 *
 * <b>Simple function call example</b><br>
 * To call a database function (<code>hello_world</code>) and get the result of the call in
 * a String variable, one only needs to use the following lines of
 * code:
 *
 * <pre>
 * String name = ...;
 *
 * DatabaseProcedure helloWorldProc =
 * &nbsp;&nbsp;DatabaseProcedure.define("<i>function hello_world(p_name in varchar2) return varchar2</i>");
 *
 * String result = (String) helloWorldProc.call(connection, name).getReturnValue();
 * </pre>
 *
 * <b>Simple procedure call example</b><br>
 * To call a database procedure with several output parameters, do the following.
 *
 * <pre>
 * Integer id = ...;
 *
 * DatabaseProcedure getEmpDetailsProc =
 * &nbsp;&nbsp;DatabaseProcedure.define("<i>procedure get_employee_details
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;( p_id in number
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;, p_name out varchar2
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;, p_address out varchar2
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;, p_age out number)</i>");
 *
 * DatabaseProcedure.Result result = getEmpDetailsProc.call(connection, id);
 *
 * String name = (String) result.getOutputValue("<i>p_name</i>");
 * String address = (String) result.getOutputValue("<i>p_address</i>");
 * Number age = (Number) result.getOutputValue("<i>p_age</i>");
 * </pre>
 *
 * <b>Calling a function/procedure inside a package</b><br>
 * To call a function or a procedure that resides inside a package, just prefix the name
 * of the function/procedure with the package name.
 *
 * <pre>
 * DatabaseProcedure helloWorldProc =
 * &nbsp;&nbsp;DatabaseProcedure.define("<i>function my_package.hello_world(p_name in varchar2) return varchar2</i>");
 * </pre>
 *
 * <h3>Recommended use</h3>
 * A <code>DatabaseProcedure</code> object can be reused multiple times, comparable to {@link Pattern}.
 * It is therefore best practice to store the <code>DatabaseProcedure</code> object in a static variable
 * (as a constant).
 * <p>
 *
 * <b>Proxies</b><br>
 * A nice pattern is to wrap the call to a database procedure inside a stronly typed Java method. The
 * hello world example shown earlier now looks like this:
 * <pre>
 * public class HelloWorldProxy
 * {
 * &nbsp;&nbsp;private static final DatabaseProcedure HELLO_WORLD =
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DatabaseProcedure.define("function hello_world(p_name in varchar2) return varchar2");
 *
 * &nbsp;&nbsp;public static String helloWorld(DBTransaction transaction, String name)
 * &nbsp;&nbsp;{
 * &nbsp;&nbsp;&nbsp;&nbsp;return (String) HELLO_WORLD.call(transaction, name).getReturnValue();
 * &nbsp;&nbsp;}
 * }
 * example function:
 *  create or replace 
 * function right_now 
 * return timestamp 
 * as
 * begin
 *   return systimestamp;
 * end right_now;  
 * </pre>
 *
 * <h3>Supported types</h3>
 * By default the following types are supported:
 * <ul>
 * <li><code>varchar2</code>
 * <li><code>number</code>
 * <li><code>date</code>
 * <li><code>integer</code>
 * <li><code>clob</code>
 * </ul>
 *
 * It is possible to register other types (both simple types and arrays and structs, etc.). See:
 * <code>{@link #registerArrayType}</code>, <code>{@link #registerCustomParamType}</code> and
 * <code>{@link #deregisterCustomParamType}</code>,
 *
 * @author Paco van der Linden
 * @version 1.2
 */
public class DatabaseProcedure
{
  private static final ADFLogger sLog = ADFLogger.createADFLogger(DatabaseProcedure.class);


  private static final Pattern PROCEDURE_DEFINITION =
    Pattern.compile("\\s* (FUNCTION|PROCEDURE) \\s+ ([\\w.$]+) \\s* (?:\\((.*?)\\))? \\s* (?:RETURN\\s+(\\w+))? \\s* ;? \\s*",
                    CASE_INSENSITIVE | COMMENTS | DOTALL);

  private static final Pattern PARAM_DEFINITION =
    Pattern.compile("\\s* (\\w+) \\s+ (?:(IN)\\s+)? (?:(OUT)\\s+)? (\\w+) \\s* ,? \\s*", CASE_INSENSITIVE | COMMENTS);

  private static final Map<String, ParamType> PARAM_TYPES = new HashMap<String, ParamType>();
  static
  {
    registerCustomParamType("VARCHAR2", Types.VARCHAR, null, null);
    registerCustomParamType("NUMBER", Types.NUMERIC, Number.getORADataFactory(), null);
    registerCustomParamType("DATE", Types.DATE, Date.getORADataFactory(), null);
    registerCustomParamType("INTEGER", Types.INTEGER, Number.getORADataFactory(), null);
    registerCustomParamType("CLOB", Types.CLOB, new ClobDomainFactory(), null);
  }

  private static final Map<String, DatabaseProcedure> DEFINITION_CACHE = new HashMap<String, DatabaseProcedure>();


  private final String escapeSequence;
  private final ParamType returnType;
  private final ParamDef[] paramDefs;
  private final int maxParams;
  private final int minParams;


  private DatabaseProcedure(String name, ParamType returnType, ParamDef... params)
  {
    this.returnType = returnType;
    this.paramDefs = params;
    this.escapeSequence = createEscapeSequence(name, returnType != null, params.length);

    if (params == null || params.length == 0)
    {
      minParams = 0;
      maxParams = 0;
    }
    else
    {
      int i = params.length;
      maxParams = i;
      while (i > 0 && !params[i - 1].in)
      {
        i--;
      }
      minParams = i;
    }
  }

  private static String createEscapeSequence(String name, boolean function, int noParams)
  {
    StringBuilder sb = new StringBuilder(32);
    if (function)
    {
      sb.append("{?= call ");
    }
    else
    {
      sb.append("{call ");
    }
    sb.append(name);

    if (noParams > 0)
    {
      sb.append("(?");
      for (int i = 1; i < noParams; i++)
      {
        sb.append(",?");
      }
      sb.append(")}");
    }
    else
    {
      sb.append("}");
    }

    return sb.toString();
  }

  /**
   * The number of parameters of this defined procedure.
   *
   * @return the number of parameters
   */
  public int getParamCount()
  {
    return paramDefs.length;
  }

  /**
   * Returns the parameter definition at the specified index (zero based).
   *
   * @param paramIndex the parameter index
   * @return the parameter definition
   */
  public ParamDef getParamDef(int paramIndex)
  {
    return paramDefs[paramIndex];
  }

  /**
   * Returns the parameter definition with the specified name.
   *
   * @param paramName the parameter name
   * @return the parameter definition
   * @throws IllegalArgumentException when paramName is unknown
   */
  public ParamDef getParamDef(String paramName)
    throws IllegalArgumentException
  {
    for (int i = 0; i < paramDefs.length; i++)
    {
      if (paramDefs[i].name.equalsIgnoreCase(paramName))
      {
        return paramDefs[i];
      }
    }
    throw new IllegalArgumentException("Unknown parameter name");
  }

  /**
   * The JDBC escape sequence corresponding to this defined procedure.
   *
   * @return the JDBC escape sequence
   */
  public String getEscapeSequence()
  {
    return escapeSequence;
  }

  /**
   * Call this DatabaseProcedure through the provided {@link DBTransaction}. The parameters are
   * position based and need to include at least all IN-parameters.
   *
   * @param transaction the transaction to use
   * @param parameters the parameters to the database procedure
   * @return a {@link Result} object
   */
  public Result call(DBTransaction transaction, Object... parameters)
  {
    checkParameters(parameters);

    OracleCallableStatement call = (OracleCallableStatement) transaction.createCallableStatement(getEscapeSequence(), DEFAULT);
    try
    {
      return _call(call, parameters);
    }
    catch (SQLException e)
    {
      sLog.severe("Failed to execute statement", e);
      throw new SQLStmtException(CSMessageBundle.class, CSMessageBundle.EXC_SQL_EXECUTE_COMMAND, getEscapeSequence(), e);
    }
    finally
    {
      try
      {
        call.close();
      }
      catch (Exception e)
      {
        sLog.severe("Failed to close statement", e);
      }
    }
  }

  /**
   * Call this DatabaseProcedure through the provided {@link Connection}. The parameters are
   * position based and need to include at least all IN-parameters.
   *
   * @param connection the connection to use
   * @param parameters the parameters to the database procedure
   * @return a {@link Result} object
   * @throws SQLException
   */
  public Result call(Connection connection, Object... parameters)
    throws SQLException
  {
    checkParameters(parameters);

    OracleCallableStatement call = (OracleCallableStatement) connection.prepareCall(getEscapeSequence());
    try
    {
      return _call(call, parameters);
    }
    finally
    {
      try
      {
        call.close();
      }
      catch (Exception e)
      {
        sLog.severe("Failed to close statement", e);
      }
    }
  }

  private void checkParameters(Object[] parameters)
  {
    if (parameters.length > maxParams)
    {
      throw new IllegalArgumentException("Too many parameters, maximum: " + maxParams);
    }
    else if (parameters.length < minParams)
    {
      throw new IllegalArgumentException("Not enough parameters, specify at least all IN parameters, minimum: " + minParams);
    }
  }

  private void _setInParam(OracleCallableStatement call, int index, ParamType type, Object value)
    throws SQLException
  {
    String typeName = type.getTypeName();
    int sqlTypeCode = type.getSqlTypeCode();

    if (sqlTypeCode == Types.CLOB && value instanceof String)
    {
      Connection connection = call.getConnection();
      CLOB clob = CLOB.createTemporary(connection, true, CLOB.DURATION_SESSION);
      clob.setString(1, (String) value);
      call.setObject(index, clob);
    }
    else if (sqlTypeCode == Types.CLOB && value instanceof ClobDomain)
    {
      call.setObject(index, ((ClobDomain) value).toDatum(call.getConnection()));
    }
    else if (typeName == null)
    {
      call.setObject(index, value, sqlTypeCode);
    }
    else if (value == null)
    {
      call.setNull(index, sqlTypeCode, typeName);
    }
    else if (sqlTypeCode == Types.ARRAY && value instanceof Object[])
    {
      Connection connection = call.getConnection();
      ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor(typeName, connection);
      Array array = new Array(descriptor, connection, value);
      call.setObject(index, array);
    }
    else
    {
      call.setObject(index, value);
    }
  }

  private void _setOutParam(OracleCallableStatement call, int index, ParamType type)
    throws SQLException
  {
    String typeName = type.getTypeName();
    int sqlTypeCode = type.getSqlTypeCode();

    if (typeName == null)
    {
      call.registerOutParameter(index, sqlTypeCode);
    }
    else
    {
      call.registerOutParameter(index, sqlTypeCode, typeName);
    }
  }

  private Result _call(OracleCallableStatement call, Object[] parameters)
    throws SQLException
  {
    int offset = returnType != null? 2: 1;
    for (int i = 0; i < paramDefs.length; i++)
    {
      ParamDef paramDef = paramDefs[i];
      if (paramDef.in)
      {
        _setInParam(call, i + offset, paramDef.getType(), parameters[i]);
      }
      if (paramDef.out)
      {
        _setOutParam(call, i + offset, paramDef.getType());
      }
    }

    if (returnType != null)
    {
      _setOutParam(call, 1, returnType);
    }

    call.execute();

    Result r = new Result(paramDefs);

    // Now retrieve both the standard JDBC objects and the Oracle objects (if available).
    for (int i = 0; i < paramDefs.length; i++)
    {
      if (paramDefs[i].out)
      {
        r.paramValues[i] = getObject(call, i + offset);
        r.paramOracleValues[i] = getOracleObject(call, i + offset, paramDefs[i].type);
      }
      else
      {
        r.paramValues[i] = null;
        r.paramOracleValues[i] = null;
      }
    }

    if (returnType != null)
    {
      r.returnValue = getObject(call, 1);
      r.oracleReturnValue = getOracleObject(call, 1, returnType);
    }
    else
    {
      r.returnValue = null;
      r.oracleReturnValue = null;
    }

    return r;
  }

  private Object getObject(OracleCallableStatement call, int index)
    throws SQLException
  {
    Object value = call.getObject(index);
//    if (value instanceof Wrapper)
//    {
//      return ((Datum) ((Wrapper) value).getVendorObj()).toJdbc();
//    }
//    else
    {
      return value;
    }
  }

  private ORAData getOracleObject(OracleCallableStatement call, int index, ParamType type)
    throws SQLException
  {
    // We will use the provided ORADataFactory to create the Oracle object. Because of
    // this we can also use ADFBC Domains, because these provide an ORADataFactory.
    ORADataFactory factory = type.getORADataFactory();
    if (factory == null)
    {
      return null;
    }

    // The method getOracleObject does not work on WebLogic wrapped statements. Therefore we need to check
    // if getObject returns a Wrapper class.
    Datum oracleValue;
    Object value = call.getObject(index);
//    if (value instanceof Wrapper)
//    {
//      oracleValue = (Datum) ((Wrapper) value).getVendorObj();
//    }
//    else
    {
      oracleValue = call.getOracleObject(index);
    }

    if (oracleValue != null)
    {
      return factory.create(oracleValue, type.getSqlTypeCode());
    }
    else
    {
      return null;
    }
  }

  /**
   * <b>Advanced:</b> <em>Not yet fully tested / documented.</em>
   * <p>
   * Provide a custom parameter type that can subsequently be used
   * in any DatabaseProcedure definition.
   *
   * @param name the name of the parameter as will be used inside the procedure definition
   * @param sqlTypeCode the {@link Types JDBC SQL Type}
   * @param dataFactory the ORADataFactory if available (for use with ADF BC Domains)
   * @param typeName the case-sensitive name with which this type is known in the database.
   *
   * @see Types
   * @see #registerArrayType
   */
  public static void registerCustomParamType(String name, int sqlTypeCode, ORADataFactory dataFactory, String typeName)
  {
    String uName = name.toUpperCase(Locale.ENGLISH);
    PARAM_TYPES.put(uName, new ParamType(uName, sqlTypeCode, dataFactory, typeName));
  }

  /**
   * <b>Advanced:</b> <em>Not yet fully tested / documented.</em>
   * <p>
   * Register an array type. The type can subsequently be used in any DatabaseProcedure
   * definition.
   *
   * @param name the case-sensitive name of this array type in the database.
   */
  public static void registerArrayType(String name)
  {
    registerCustomParamType(name, Types.ARRAY, Array.getORADataFactory(), name);
  }

  /**
   * <b>Advanced:</b> <em>Not yet fully tested / documented.</em>
   * <p>
   * Unregister a custom type.
   *
   * @param name the name of the type
   * @return <code>true</code> iff a type was unregistered
   */
  public static boolean deregisterCustomParamType(String name)
  {
    return PARAM_TYPES.remove(name.toUpperCase(Locale.ENGLISH)) != null;
  }

  /**
   * Construct a DatabaseProcedure from its (original) PL/SQL declaration.
   *
   * <p>
   *
   * Function declaration:
   * <blockquote><pre>function &lt;name&gt; [(&lt;param&gt;[, &lt;param&gt;,...])] return &lt;return-type&gt;</pre></blockquote>
   *
   * Procedure declaration:
   * <blockquote><pre>procedure &lt;name&gt; [(&lt;param&gt;[, &lt;param&gt;,...])]</pre></blockquote>
   *
   * With the optional <code>&lt;param&gt;</code> as:
   * <blockquote><pre>&lt;param-name&gt; [in] [out] &lt;param-type&gt;</pre></blockquote>
   *
   * <p>
   *
   * Example of a function with one parameter returning a <code>varchar2</code>:
   * <blockquote><pre>function hr.hello_world(p_name in out varchar2) return varchar2</pre></blockquote>
   *
   * Example of a procedure with one input and one output parameter:
   * <blockquote><pre>procedure hr.hello_world( p_name in varchar2, p_msg out varchar2)</pre></blockquote>
   *
   * <p>
   *
   * See {@link DatabaseProcedure class documentation} for the possible parametertypes.
   *
   * @param declaration the procedure declaration in PL/SQL style
   * @return a DatabaseProcedure object
   */
  public static DatabaseProcedure define(String declaration)
  {
    if (DEFINITION_CACHE.containsKey(declaration))
    {
      return DEFINITION_CACHE.get(declaration);
    }
    DatabaseProcedure proc;

    Matcher m = PROCEDURE_DEFINITION.matcher(declaration);
    if (!m.matches())
    {
      compileError("Declaration is not valid", declaration);
    }

    boolean function = m.group(1).equalsIgnoreCase("FUNCTION");
    String name = m.group(2);
    String params = m.group(3);
    String returns = m.group(4);

    ParamType returnType = null;
    if (function)
    {
      if (returns == null)
      {
        compileError("A function needs a return type", declaration);
      }
      returnType = PARAM_TYPES.get(returns.toUpperCase(Locale.ENGLISH));
      if (returnType == null)
      {
        compileError("Unsupported type (" + returns + ")", declaration);
      }
    }
    else if (returns != null)
    {
      compileError("A procedure has no return type", declaration);
    }

    if (params != null)
    {
      ArrayList<ParamDef> paramList = new ArrayList<ParamDef>();
      m = PARAM_DEFINITION.matcher(params);
      while (!m.hitEnd())
      {
        if (!m.lookingAt())
        {
          compileError("Parameter declaration not valid", params.substring(m.regionStart()));
        }

        try
        {
          paramList.add(new ParamDef(m.group(1), m.group(2) != null || m.group(3) == null, m.group(3) != null, m.group(4)));
        }
        catch (IllegalArgumentException e)
        {
          compileError(e.getMessage(), declaration);
        }

        // Remove parameter from search window
        m.region(m.end(), m.regionEnd());
      }

      proc = new DatabaseProcedure(name, returnType, paramList.toArray(new DatabaseProcedure.ParamDef[paramList.size()]));
    }
    else
    {
      proc = new DatabaseProcedure(name, returnType);
    }

    DEFINITION_CACHE.put(declaration, proc);
    return proc;
  }

  private static void compileError(String msg, String declaration)
  {
    throw new IllegalArgumentException(msg + ": " + declaration);
  }

  /**
   * The definition of a parameter.
   */
  public static final class ParamDef
  {
    private final String name;
    private final boolean in;
    private final boolean out;
    private final ParamType type;

    private ParamDef(String name, boolean in, boolean out, String type)
    {
      if (!in && !out)
      {
        throw new IllegalArgumentException("A parameter should be IN or OUT or both");
      }

      this.name = name;
      this.in = in;
      this.out = out;
      this.type = PARAM_TYPES.get(type.toUpperCase(Locale.ENGLISH));
      if (this.type == null)
      {
        throw new IllegalArgumentException("Unsupported type (" + type + ")");
      }
    }

    /**
     * The name of the parameter.
     *
     * @return name
     */
    public String getName()
    {
      return name;
    }

    /**
     * Indicates whether the parameter is used as in input parameter.
     *
     * @return true if the parameter is an input parameter
     */
    public boolean isIn()
    {
      return in;
    }

    /**
     * Indicates whether the parameter is used as in output parameter.
     *
     * @return true if the parameter is an output parameter
     */
    public boolean isOut()
    {
      return out;
    }

    /**
     * The type of the parameter.
     *
     * @return type
     */
    public ParamType getType()
    {
      return type;
    }
  }

  /**
   * The result of a call to a DatabaseProcedure.
   */
  public static class Result
  {
    protected final Object[] paramValues;
    protected final ORAData[] paramOracleValues;
    protected final ParamDef[] paramDefs;
    protected Object returnValue;
    protected ORAData oracleReturnValue;

    protected Result(ParamDef[] paramDefs)
    {
      this.paramDefs = paramDefs;
      this.paramValues = new Object[paramDefs.length];
      this.paramOracleValues = new ORAData[paramDefs.length];
    }

    protected Result(Result otherResult)
    {
      this.paramDefs = otherResult.paramDefs;
      this.paramValues = otherResult.paramValues.clone();
      this.paramOracleValues = otherResult.paramOracleValues.clone();
      this.returnValue = otherResult.returnValue;
      this.oracleReturnValue = otherResult.oracleReturnValue;
    }

    /**
     * The returned value of an out parameter. Parameters that have not been marked as out
     * parameters always return <code>null</code>.
     *
     * @param paramIndex a valid parameter index (zero-based)
     * @return outputvalue of the parameter
     */
    public Object getOutputValue(int paramIndex)
    {
      return paramValues[paramIndex];
    }

    /**
     * The returned value of an out parameter as an Oracle object. Parameters that have
     * not been marked as out parameters always return <code>null</code>.
     *
     * @param paramIndex a valid parameter index (zero-based)
     * @return outputvalue of the parameter as an Oracle object.
     */
    public Object getOracleOutputValue(int paramIndex)
    {
      Object value = paramOracleValues[paramIndex];
      if (value == null)
      {
        value = paramValues[paramIndex];
      }
      return value;
    }

    protected int getParamIndex(String paramName)
    {
      for (int i = 0; i < paramDefs.length; i++)
      {
        if (paramDefs[i].name.equalsIgnoreCase(paramName))
        {
          return i;
        }
      }
      throw new IllegalArgumentException("Unknown parameter name");
    }

    /**
     * The returned value of an out parameter. Parameters that have not been marked as out
     * parameters always return <code>null</code>.
     *
     * @param paramName a valid parameter name
     * @return outputvalue of the parameter
     * @throws IllegalArgumentException if paramName is unknown
     */
    public Object getOutputValue(String paramName)
      throws IllegalArgumentException
    {
      return getOutputValue(getParamIndex(paramName));
    }

    /**
     * The returned value of an out parameter as an Oracle object. Parameters that have
     * not been marked as out parameters always return <code>null</code>.
     *
     * @param paramName a valid parameter name
     * @return outputvalue of the parameter as an Oracle object.
     * @throws IllegalArgumentException if paramName is unknown
     */
    public Object getOracleOutputValue(String paramName)
    {
      return getOracleOutputValue(getParamIndex(paramName));
    }

    /**
     * The returned value of a function. Returns null in case of procedures.
     *
     * @return returnvalue of a function or null in case of a procedure
     */
    public Object getReturnValue()
    {
      return returnValue;
    }

    /**
     * The returned value of a function as an Oracle type. Returns null in case of procedures.
     *
     * @return returnvalue of a function or null in case of a procedure
     */
    public Object getOracleReturnValue()
    {
      if (oracleReturnValue != null)
      {
        return oracleReturnValue;
      }
      else
      {
        return returnValue;
      }
    }
  }


  public static class ParamType
  {
    private final String name;
    private final int sqlTypeCode;
    private final ORADataFactory dataFactory;
    private final String typeName;

    private ParamType(String name, int sqlTypeCode, ORADataFactory dataFactory, String typeName)
    {
      this.name = name;
      this.sqlTypeCode = sqlTypeCode;
      this.dataFactory = dataFactory;
      this.typeName = typeName;
    }

    public int getSqlTypeCode()
    {
      return sqlTypeCode;
    }

    public ORADataFactory getORADataFactory()
    {
      return dataFactory;
    }

    public String getName()
    {
      return name;
    }

    public String getTypeName()
    {
      return typeName;
    }

    @Override
    public String toString()
    {
      return getName();
    }
  }

  private static final class ClobDomainFactory
    implements ORADataFactory
  {
    public ORAData create(Datum datum, int i)
    {
      return datum != null? new ClobDomain((Clob) datum): null;
    }
  }

  public static void main(String... args)
  {
    DatabaseProcedure.define("function hr.hello_world(p_name in out varchar2) return varchar2").dumpInfo();
    DatabaseProcedure.define("procedure hr.hello_world( p_name varchar2" +
                             "                        , p_msg out varchar2)").dumpInfo();

    DatabaseProcedure.registerCustomParamType("custom_type", Types.ARRAY, Array.getORADataFactory(), "custom_type_tab");
    DatabaseProcedure.define("function test(p_in in varchar2) return custom_type").dumpInfo();
  }

  private void dumpInfo()
  {
    System.out.println("JDBC escape sequence: " + getEscapeSequence());
    System.out.println("Type: " + (returnType == null? "PROCEDURE": "FUNCTION"));
    if (returnType != null)
    {
      System.out.println("Returntype: " + returnType);
    }
    System.out.println();
    for (ParamDef pd: paramDefs)
    {
      System.out.println("Param: " + pd.name.toUpperCase());
      System.out.println("Type: " + pd.type);
      System.out.println("In/out: " + (pd.in? "IN ": "") + (pd.out? "OUT": ""));
      System.out.println();
    }
  }
}