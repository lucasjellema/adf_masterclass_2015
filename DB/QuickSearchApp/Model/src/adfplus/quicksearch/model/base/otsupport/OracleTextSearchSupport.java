package adfplus.quicksearch.model.base.otsupport;


import adfplus.quicksearch.model.base.BaseViewDefImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oracle.adf.share.logging.ADFLogger;


/**
 * This class translates user input to Oracle Text syntax and RowMatch syntax.
 */
public class OracleTextSearchSupport {
    private static final String SIMPLE_TERM = "([^\" ]+)(?: |$|(?=\"))";
    private static final String DELIMITED_TERM = "\"([^\"]*)\"";
    private static final Pattern SPLIT_PATTERN =
        Pattern.compile("(?:" + SIMPLE_TERM + ")|(?:" + DELIMITED_TERM + ")");

    /**
     * Add words to this list to ignore them in searches.
     */
    private static final Set<String> STOPWORDS = new HashSet<String>(Arrays.asList("vs", "-"));

    private static final Pattern REMOVE_FROM_SEARCH =
        Pattern.compile("[\"()\\[\\]*{}=!?;~>$&|,-]");
    private static final Pattern EXTRA_SPACES = Pattern.compile("\\s++");

    private static final ADFLogger sLog = ADFLogger.createADFLogger(OracleTextSearchSupport.class);


    private OracleTextSearchSupport() {
    }

    /**
     * Converts the given user input (the search command): <code>searchValue</code> to an Oracle Text search-string. The following
     * rules are implemented:
     * <ul>
     * <li>All words should be found in the result (search on all words, not any word)
     * <li>Words are considered prefixes ("al" also finds "alice")
     * <li>All special Oracle Text syntax is removed or escaped
     * </ul>
     * @param searchValue
     * @return
     */
    public static String toOracleTextExpression(String searchValue) {
        StringBuilder sb = new StringBuilder(100);
        for (String term : splitSearchValue(searchValue, "")) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            // Because of the trailing '%' we don't have to escape special words
            // like AND and OR.
            sb.append(term);
            if (!isNumber(term)) {
                // We don't want to search prefixes of numbers (it is supported by Oracle Text though).
                sb.append('%');
            }
        }
        if (sb.length() == 0) {
            return "%";
        }
        return sb.toString();
    }

    /**
     * Returns the Oracle Text where clause with bind variable.
     *
     * @param def de viewDef
     * @param bindVar de bind variabele
     * @return an Oracle Text where clause
     *
     * @see #toCacheModeWhereClause(BaseViewDefImpl, String) toCacheModeWhereClause
     */
    public static String toOracleTextWhereClause(BaseViewDefImpl def, String bindVar) {
        String columnNameForQuery = def.getOracleTextIndexAttribute().getColumnNameForQuery();
        return toOracleTextWhereClause(columnNameForQuery, bindVar);
    }

    private static String toOracleTextWhereClause(String columnNameForQuery, String bindVar) {
        return "contains(" + columnNameForQuery + ", " + bindVar + ") > 0";
    }

    /**
     * Returns the in-memory (RowMatch) where where clause that simulates the Oracle Text search. This is needed to use Oracle Text
     * Search in combination with ADF BC LOV's.
     *
     * @param def the viewDef
     * @param searchValue the user input
     * @return an in-memory where clause
     */
    public static String toCacheModeWhereClause(BaseViewDefImpl def, String searchValue) {
        String searchAttributeName = def.getOracleTextSearchAttribute().getName();
        return toCacheModeWhereClause(searchAttributeName, searchValue);
    }

    private static String toCacheModeWhereClause(String searchAttributeName, String searchValue) {
        //String start = "UPPER(' ' || " + searchAttributeName + ") LIKE UPPER('% ";
        String start = "UPPER(" + searchAttributeName + ") LIKE UPPER('%";
        StringBuilder sb = new StringBuilder(100);
        for (String term : splitSearchValue(searchValue, "%")) {
            if (sb.length() > 0) {
                sb.append("%') and ");
            }
            sb.append(start).append(escape(term));
        }
        if (sb.length() == 0) {
            sb.append(start);
        }
        sb.append("%')");
        return sb.toString();
    }

    private static List<String> splitSearchValue(String searchValue,
                                                 String replaceUnsupportedChars) {
        ArrayList<String> list = new ArrayList<String>();
        if (searchValue == null || searchValue.isEmpty()) {
            return list;
        }

        Matcher matcher = SPLIT_PATTERN.matcher(searchValue);
        int searchStart = 0;
        while (matcher.find()) {
            int match = matcher.start();
            if (match > searchStart && sLog.isWarning()) {
                String skipped = searchValue.substring(searchStart, match);
                if (!EXTRA_SPACES.matcher(skipped).matches()) {
                    // We only log interesting cases (spaces are not interesting).
                    // Probably doesn't happen.
                    sLog.warning("Skipped: \"{0}\" from search expression: \"{1}\"",
                                 new Object[] { skipped, searchValue });
                }
            }
            searchStart = matcher.end();

            String term = matcher.group(1);
            if (term == null) {
                term = matcher.group(2);
            }
            if (term == null) {
                sLog.warning("Got a null term while analyzing search expression: \"{0}\"",
                             searchValue);
            } else if (STOPWORDS.contains(term)) {
                sLog.finest("Ignoring stopword: {0}", term);
            } else {
                term = REMOVE_FROM_SEARCH.matcher(term).replaceAll(replaceUnsupportedChars);
                term = EXTRA_SPACES.matcher(term).replaceAll(" ");

                sLog.finest("Found search term: {0}", term);

                list.add(term.trim());
            }
        }
        return list;
    }

    private static String escape(String input) {
        return input.replace("'", "''");
    }

    private static boolean isNumber(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        ConsoleHandler handler = new ConsoleHandler();
        sLog.addHandler(handler);
        sLog.setLevel(Level.FINEST);
        handler.setLevel(Level.FINEST);

        String searchString =
            "25671 Q. de Boulanger (A25325), Leeman Klaassen Pietersen Lawyers Inc. vs State";
        //    String searchString = "te";
        String cacheModeWhereClause = toCacheModeWhereClause("SearchAttribute", searchString);
        String oracleTextExpression = toOracleTextExpression(searchString);
        System.out.print("Search string: ");
        System.out.println(searchString);
        System.out.print("Cache mode: ");
        System.out.println(cacheModeWhereClause);
        System.out.print("Oracle text expression: ");
        System.out.println(oracleTextExpression);
    }
}
