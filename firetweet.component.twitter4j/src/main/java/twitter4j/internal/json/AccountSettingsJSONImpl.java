/*
 * Firetweet - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package twitter4j.internal.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.AccountSettings;
import twitter4j.Location;
import twitter4j.TimeZone;
import twitter4j.TwitterException;
import twitter4j.conf.Configuration;
import twitter4j.http.HttpResponse;

import static twitter4j.internal.util.InternalParseUtil.getBoolean;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.9
 */
class AccountSettingsJSONImpl extends TwitterResponseImpl implements AccountSettings {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3364568117893571939L;
	private final boolean SLEEP_TIME_ENABLED;
	private final String SLEEP_START_TIME;
	private final String SLEEP_END_TIME;
	private final Location[] TREND_LOCATION;
	private final boolean GEO_ENABLED;
	private final String LANGUAGE;
	private final TimeZone TIMEZONE;
	private final boolean ALWAYS_USE_HTTPS;
	private final boolean DISCOVERABLE_BY_EMAIL;

	private AccountSettingsJSONImpl(final HttpResponse res, final JSONObject json) throws TwitterException {
		super(res);
		try {
			final JSONObject sleepTime = json.getJSONObject("sleep_time");
			SLEEP_TIME_ENABLED = getBoolean("enabled", sleepTime);
			SLEEP_START_TIME = sleepTime.getString("start_time");
			SLEEP_END_TIME = sleepTime.getString("end_time");
			if (json.isNull("trend_location")) {
				TREND_LOCATION = new Location[0];
			} else {
				final JSONArray locations = json.getJSONArray("trend_location");
				TREND_LOCATION = new Location[locations.length()];
				for (int i = 0; i < locations.length(); i++) {
					TREND_LOCATION[i] = new LocationJSONImpl(locations.getJSONObject(i));
				}
			}
			GEO_ENABLED = getBoolean("geo_enabled", json);
			LANGUAGE = json.getString("language");
			ALWAYS_USE_HTTPS = getBoolean("always_use_https", json);
			DISCOVERABLE_BY_EMAIL = getBoolean("discoverable_by_email", json);
			TIMEZONE = new TimeZoneJSONImpl(json.getJSONObject("time_zone"));
		} catch (final JSONException e) {
			throw new TwitterException(e);
		}
	}

	/* package */AccountSettingsJSONImpl(final HttpResponse res, final Configuration conf) throws TwitterException {
		this(res, res.asJSONObject());
	}

	/* package */AccountSettingsJSONImpl(final JSONObject json) throws TwitterException {
		this(null, json);
	}

	@Override
	public String getLanguage() {
		return LANGUAGE;
	}

	@Override
	public String getSleepEndTime() {
		return SLEEP_END_TIME;
	}

	@Override
	public String getSleepStartTime() {
		return SLEEP_START_TIME;
	}

	@Override
	public TimeZone getTimeZone() {
		return TIMEZONE;
	}

	@Override
	public Location[] getTrendLocations() {
		return TREND_LOCATION;
	}

	@Override
	public boolean isAlwaysUseHttps() {
		return ALWAYS_USE_HTTPS;
	}

	@Override
	public boolean isDiscoverableByEmail() {
		return DISCOVERABLE_BY_EMAIL;
	}

	@Override
	public boolean isGeoEnabled() {
		return GEO_ENABLED;
	}

	@Override
	public boolean isSleepTimeEnabled() {
		return SLEEP_TIME_ENABLED;
	}
}
