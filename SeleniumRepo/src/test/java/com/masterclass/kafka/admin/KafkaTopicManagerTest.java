package com.masterclass.kafka.admin;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.masterclass.kafka.support.KafkaTestBase;

/** Integration tests for topic management. Needs a running Kafka broker. */
public class KafkaTopicManagerTest extends KafkaTestBase {

	@Test
	public void createDescribeAndDeleteTopic() {
		String topic = uniqueName("topic-mgmt");

		assertTrue(topicManager.createTopicIfNotExists(topic, 3, (short) 1), "Topic should be newly created");
		try {
			assertTrue(topicManager.topicExists(topic));
			assertTrue(topicManager.listTopics().contains(topic));
			assertEquals(topicManager.partitionCount(topic), 3);
			assertFalse(topicManager.createTopicIfNotExists(topic, 3, (short) 1), "Second create should be a no-op");
		} finally {
			topicManager.deleteTopic(topic);
		}

		assertFalse(topicManager.topicExists(topic), "Topic should be gone after delete");
	}

	@Test
	public void deletingMissingTopicIsNoOp() {
		topicManager.deleteTopic(uniqueName("never-created"));
	}
}
