/*
 * 				Firetweet - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package org.getlantern.firetweet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import org.getlantern.firetweet.Constants;
import org.getlantern.firetweet.util.Utils;

import static org.getlantern.firetweet.util.Utils.startRefreshServiceIfNeeded;

public class ConnectivityStateReceiver extends BroadcastReceiver implements Constants {

	private static final String RECEIVER_LOGTAG = LOGTAG + "." + "Connectivity";

	@Override
	public void onReceive(final Context context, final Intent intent) {
		if (Utils.isDebugBuild()) {
			Log.d(RECEIVER_LOGTAG, String.format("Received Broadcast %s", intent));
		}
		if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) return;
		startRefreshServiceIfNeeded(context);
	}
}
