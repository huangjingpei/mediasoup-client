package com.mediasoup.msclient;

import java.util.Locale;

public class UrlFactory {

  private static final String HOSTNAME = "192.168.10.11";
  //  private static final String HOSTNAME = "192.168.1.103";
  private static final int PORT = 4443;

  public static String getInvitationLink(String roomId, boolean forceH264, boolean forceVP9) {
    String url = String.format(Locale.US, "https://%s/?roomId=%s", HOSTNAME, roomId);
    if (forceH264) {
      url += "&forceH264=true";
    } else if (forceVP9) {
      url += "&forceVP9=true";
    }
    return url;
  }

  public static String getProtooUrl(
      String roomId, String peerId, boolean forceH264, boolean forceVP9) {
    String url =
        String.format(
            Locale.US, "wss://%s:%d/?roomId=%s&peerId=%s", HOSTNAME, PORT, roomId, peerId);
    if (forceH264) {
      url += "&forceH264=true";
    } else if (forceVP9) {
      url += "&forceVP9=true";
    }
    return url;
  }
}
