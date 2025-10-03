package ag.ping;

import android.content.res.Resources;
import android.icu.text.MessageFormat;

final class MainI18N {
    private final String hostNotInformed, startPing,
            callPingError, pingFail;

    MainI18N(final Resources res) {
        this.hostNotInformed = res.getString(R.string.host_not_informed);
        this.startPing = res.getString(R.string.start_ping);
        this.callPingError = res.getString(R.string.call_ping_error);
        this.pingFail = res.getString(R.string.ping_fail);
    }

    String getHostNotInformed() {
        return hostNotInformed;
    }

    String startPing(final String host) {
        return MessageFormat.format(startPing, host);
    }

    String callPingError() {
        return callPingError;
    }
    String pingFail(final int exitValue) {
        return MessageFormat.format(pingFail, exitValue);
    }
}
