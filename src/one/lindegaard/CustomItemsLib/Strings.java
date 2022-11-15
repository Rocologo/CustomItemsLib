package one.lindegaard.CustomItemsLib;

import java.lang.reflect.Method;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class Strings {

	/**
	 * Decode a string
	 * 
	 * @param string
	 * @return decoded string
	 */
	public static String decode(String string) {
		return new String(Base64.getDecoder().decode(string));
	}

	/**
	 * Encode a string
	 * 
	 * @param string
	 * @return encoded string
	 */
	public static String encode(String string) {
		return Base64.getEncoder().encodeToString(string.getBytes());
	}

	public static String convertColors(String s) {
		Pattern pattern = Pattern.compile(Pattern.quote("{#") + "(.*?)" + Pattern.quote("}"));
		Matcher match = pattern.matcher(s);
		String ns = s;
		while (match.find()) {
			String colorcode = match.group(1);
			ns = ns.replaceAll("\\{#" + colorcode + "\\}", Strings.getColor("#" + colorcode).toString());
		}

		return ns;
	}

	public static ChatColor getColor(String s) {
		try {
			Class<ChatColor> c = ChatColor.class;
			Method m = c.getMethod("of", String.class);
			Object o = m.invoke(null, s);
			return (ChatColor) o;

		} catch (Exception e) {
			return ChatColor.WHITE;
		}
	}
}
