package com.downvoteit.springsolacecommon.config;

public class SharedProps {
  private SharedProps() {}

  public static final class CreateItemOltp {
    private CreateItemOltp() {}

    public static final class Commit {
      public static final String QUEUE = "create-item-oltp-queue";
      public static final String FLOW_PROPS = "create-item-oltp-queue-flow-props";
      public static final String RECEIVER = "create-item-oltp-queue-receiver";

      private Commit() {}
    }
  }

  public static final class CreateItemOlap {
    private CreateItemOlap() {}

    public static final class Commit {
      public static final String QUEUE = "create-item-olap-queue";
      public static final String FLOW_PROPS = "create-item-olap-queue-flow-props";
      public static final String RECEIVER = "create-item-olap-queue-receiver";

      private Commit() {}
    }

    public static final class Rollback {
      public static final String QUEUE = "create-item-olap-rb-queue";
      public static final String FLOW_PROPS = "create-item-olap-rb-queue-flow-props";
      public static final String RECEIVER = "create-item-olap-rb-queue-receiver";

      private Rollback() {}
    }
  }

  public static final class GetItemOltp {
    public static final String QUEUE = "get-item-oltp-queue";
    public static final String FLOW_PROPS = "get-item-oltp-queue-flow-props";
    public static final String TOPIC = "get-item-oltp-queue-topic";
    public static final String RECEIVER = "get-item-oltp-queue-receiver";

    private GetItemOltp() {}
  }

  public static final class GetItemsOltp {
    public static final String QUEUE = "get-items-oltp-queue";
    public static final String FLOW_PROPS = "get-items-oltp-queue-flow-props";
    public static final String TOPIC = "get-items-oltp-queue-topic";
    public static final String RECEIVER = "get-items-oltp-queue-receiver";

    private GetItemsOltp() {}
  }
}
