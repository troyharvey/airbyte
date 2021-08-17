package io.airbyte.integrations.destination.jdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.function.Function;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataAdapter.class);

  private final Predicate<JsonNode> filterValueNode;
  private final Function<JsonNode, JsonNode> valueNodeAdapter;

  /**
   * Data adapter allows applying destination data rules.
   * For example, Postgres destination can't process text value with \u0000 unicode.
   * You can describe filter condition for a value node and function which adapts filtered value nodes.
   *
   * @param filterValueNode  - filter condition which decide which value node should be adapted
   * @param valueNodeAdapter - transformation function which returns adapted value node
   */
  public DataAdapter(
      Predicate<JsonNode> filterValueNode,
      Function<JsonNode, JsonNode> valueNodeAdapter) {
    this.filterValueNode = filterValueNode;
    this.valueNodeAdapter = valueNodeAdapter;
  }

  public void adapt(JsonNode messageData) {
    LOGGER.debug("Data before adapt: " + messageData);
    if (messageData != null) {
      adaptAllValueNodes(messageData);
    }
    LOGGER.debug("Data after adapt: " + messageData);
  }

  private void adaptAllValueNodes(JsonNode rootNode) {
    adaptValueNodes(null, rootNode, null);
  }

  private void adaptValueNodes(String fieldName, JsonNode node, JsonNode parentNode) {
    if (node.isValueNode() && filterValueNode.test(node)) {
      var adaptedNode = valueNodeAdapter.apply(node);
      ((ObjectNode)parentNode).set(fieldName, adaptedNode);
    } else if (node.isArray()) {
      node.elements().forEachRemaining(arrayNode -> adaptValueNodes(null, arrayNode, node));
    } else {
        node.fields().forEachRemaining(stringJsonNodeEntry -> adaptValueNodes(stringJsonNodeEntry.getKey(), stringJsonNodeEntry.getValue(), node));
      }
    }
  }
