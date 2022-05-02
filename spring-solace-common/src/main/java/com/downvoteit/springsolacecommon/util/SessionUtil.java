package com.downvoteit.springsolacecommon.util;

import com.solacesystems.jcsmp.CapabilityType;
import com.solacesystems.jcsmp.JCSMPSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionUtil {
  private SessionUtil() {}

  public static void checkCapabilities(JCSMPSession session) {
    if (session.isCapable(CapabilityType.PUB_GUARANTEED)
        && session.isCapable(CapabilityType.SUB_FLOW_GUARANTEED)
        && session.isCapable(CapabilityType.ENDPOINT_MANAGEMENT)
        && session.isCapable(CapabilityType.QUEUE_SUBSCRIPTIONS)) {
      log.info("All required capabilities supported");
    } else {
      String message =
          "Missing required capability"
              + "Capability - PUB_GUARANTEED: {}\n"
              + "Capability - SUB_FLOW_GUARANTEED: {}\n"
              + "Capability - ENDPOINT_MANAGEMENT: {}\n"
              + "Capability - QUEUE_SUBSCRIPTIONS: {}";
      log.error(
          message,
          session.isCapable(CapabilityType.PUB_GUARANTEED),
          session.isCapable(CapabilityType.SUB_FLOW_GUARANTEED),
          session.isCapable(CapabilityType.ENDPOINT_MANAGEMENT),
          session.isCapable(CapabilityType.QUEUE_SUBSCRIPTIONS));
    }
  }
}
