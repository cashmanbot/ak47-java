package com.tb24.discordbot.util;

import com.google.gson.JsonElement;
import com.tb24.fn.model.FortItemStack;
import com.tb24.fn.util.EAuthClient;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import me.fungames.jfortniteparse.fort.objects.FortMcpQuestObjectiveInfo;

import static com.tb24.fn.util.Utils.isNone;

public class Utils {
	public static final String LOADING_EMOJI = "<a:loading:740564597643149404>";
	public static final long MTX_EMOJI_ID = 751101530626588713L;
	public static final String MTX_EMOJI = "<:vbucks:" + MTX_EMOJI_ID + ">";
	private static final String[] ERROR_TITLES = new String[]{
		"DRAT! WE'RE SORRY",
		"NOT THE LLAMA YOU'RE LOOKING FOR",
		"THAT WASN\'T SUPPOSED TO HAPPEN",
		"THERE WAS AN ERROR",
		"UH OH!  SOMETHING GOOFED",
		"WE HIT A ROADBLOCK",
		"WHOOPS!"
	};
	private static final Random RANDOM = new Random();
	public static final String HOMEBASE_GUILD_INVITE = "https://discord.gg/HCVjeXJ";
	public static final Pattern EMAIL_ADDRESS
		= Pattern.compile(
		"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
			"\\@" +
			"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
			"(" +
			"\\." +
			"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
			")+"
	);

	public static String redirect(EAuthClient client) {
		return String.format("https://www.epicgames.com/id/api/redirect?clientId=%s&responseType=code", client.clientId);
	}

	public static String login(String redirectUrl) {
		if (redirectUrl != null) {
			return "https://www.epicgames.com/id/login?redirectUrl=" + URLEncoder.encode(redirectUrl);
		} else {
			return "https://www.epicgames.com/id/login";
		}
	}

	public static String randomError() {
		return ERROR_TITLES[RANDOM.nextInt(ERROR_TITLES.length)];
	}

	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static String loadingText(String s) {
		return s.contains("%LOADING%") ? s.replace("%LOADING%", LOADING_EMOJI) : (s + " " + LOADING_EMOJI);
	}

	public static String benBotExportAsset(String objectPath) {
		return isNone(objectPath) ? null : "https://benbotfn.tk/api/v1/exportAsset?path=" + objectPath;
	}

	public static int getCompletion(FortMcpQuestObjectiveInfo objective, FortItemStack item) {
		if (item == null || item.attributes == null) {
			return -1;
		}

		String backendName = "completion_" + objective.BackendName.toString().toLowerCase(Locale.ENGLISH);
		JsonElement element = item.attributes.get(backendName);
		return element.isJsonPrimitive() ? element.getAsInt() : -1;
	}

	public static int mod(int a, int b) {
		int r = a % b;
		return r < 0 ? r + b : r;
	}

	public static String progress(int current, int max, int width) {
		int barWidth = width - 2;
		float ratio = (float) current / (float) max;
		int barEnd = (int) (ratio * barWidth + 0.5F);

		StringBuilder sb = new StringBuilder(width);
		sb.append('[');

		for (int i = 0; i < barWidth; ++i) {
			sb.append(i >= barEnd ? ' ' : i == barEnd - 1 ? '>' : '=');
		}

		sb.append(']');
		return sb.toString();
	}

	/**
	 * Computes the Damerau-Levenshtein Distance between two strings. Adapted
	 * from the algorithm at <a href="http://en.wikipedia.org/wiki/Damerau–Levenshtein_distance">Wikipedia: Damerau–Levenshtein distance</a>
	 *
	 * @param s1 The first string being compared.
	 * @param s2 The second string being compared.
	 * @return The number of substitutions, deletions, insertions, and
	 * transpositions required to get from s1 to s2.
	 */
	public static int damerauLevenshteinDistance(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return 0;
		}
		if (s1 != null && s2 == null) {
			return s1.length();
		}
		if (s1 == null && s2 != null) {
			return s2.length();
		}

		int s1Len = s1.length();
		int s2Len = s2.length();
		int[][] H = new int[s1Len + 2][s2Len + 2];

		int INF = s1Len + s2Len;
		H[0][0] = INF;
		for (int i = 0; i <= s1Len; i++) {
			H[i + 1][1] = i;
			H[i + 1][0] = INF;
		}
		for (int j = 0; j <= s2Len; j++) {
			H[1][j + 1] = j;
			H[0][j + 1] = INF;
		}

		Map<Character, Integer> sd = new HashMap<>();
		for (char Letter : (s1 + s2).toCharArray()) {
			if (!sd.containsKey(Letter)) {
				sd.put(Letter, 0);
			}
		}

		for (int i = 1; i <= s1Len; i++) {
			int DB = 0;
			for (int j = 1; j <= s2Len; j++) {
				int i1 = sd.get(s2.charAt(j - 1));
				int j1 = DB;

				if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
					H[i + 1][j + 1] = H[i][j];
					DB = j;
				} else {
					H[i + 1][j + 1] = Math.min(H[i][j], Math.min(H[i + 1][j], H[i][j + 1])) + 1;
				}

				H[i + 1][j + 1] = Math.min(H[i + 1][j + 1], H[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1));
			}
			sd.put(s1.charAt(i - 1), i);
		}

		return H[s1Len + 1][s2Len + 1];
	}
}
