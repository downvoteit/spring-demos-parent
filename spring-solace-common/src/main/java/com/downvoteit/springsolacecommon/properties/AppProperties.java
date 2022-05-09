package com.downvoteit.springsolacecommon.properties;

public class AppProperties {
  private AppProperties() {}

  public static final class CreateItemOltp {
    private static final String KEY = "create-item-oltp-";
    public static final String QUEUE = KEY + "queue";
    public static final String FLOW_PROPS = KEY + "flow-props";

    private CreateItemOltp() {}
  }

  public static final class CreateItemOlap {
    private static final String KEY = "create-item-olap-";
    public static final String QUEUE = KEY + "queue";
    public static final String FLOW_PROPS = KEY + "flow-props";
    public static final String QUEUE_UNDO = KEY + "queue-undo";
    public static final String FLOW_PROPS_UNDO = KEY + "flow-props-undo";

    private CreateItemOlap() {}
  }

  public static final class DeleteItemOltp {
    private static final String KEY = "delete-item-oltp-";
    public static final String QUEUE = KEY + "queue";
    public static final String FLOW_PROPS = KEY + "flow-props";
    public static final String TOPIC = KEY + "topic";

    private DeleteItemOltp() {}
  }

  public static final class DeleteItemOlap {
    private static final String KEY = "delete-item-olap-";
    public static final String QUEUE = KEY + "queue";
    public static final String FLOW_PROPS = KEY + "flow-props";
    public static final String TOPIC = KEY + "topic";

    private DeleteItemOlap() {}
  }
}
