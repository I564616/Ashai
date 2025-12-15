/**
 *
 */
package com.sabmiller.core.util;

import org.apache.solr.client.solrj.util.ClientUtils;


/**
 * The Class ClientSolrUtils.
 */
public class ClientSolrUtils extends ClientUtils
{

	/**
	 * Escape query chars.
	 *
	 * @param s
	 *           the string
	 * @return the escaped string
	 */
	public static String escapeQueryChars(final String s)
	{
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++)
		{
			final char c = s.charAt(i);

			if ((c == '\\') || (c == '+') || (c == '!') || (c == '(') || (c == ')') || (c == ':') || (c == '^') || (c == '[')
					|| (c == ']') || (c == '"') || (c == '{') || (c == '}') || (c == '~') || (c == '*') || (c == '?') || (c == '|')
					|| (c == '&') || (c == ';') || (c == '/') || (Character.isWhitespace(c)))
			{
				sb.append('\\');
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
