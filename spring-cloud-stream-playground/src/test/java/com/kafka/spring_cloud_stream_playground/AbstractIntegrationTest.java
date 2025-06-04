package com.kafka.spring_cloud_stream_playground;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@EmbeddedKafka(
		partitions = 1,
		bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@DirtiesContext
@SpringBootTest
public abstract class AbstractIntegrationTest {

	@Autowired
	private EmbeddedKafkaBroker broker;


	@Test
	void contextLoads() {
	}

}
