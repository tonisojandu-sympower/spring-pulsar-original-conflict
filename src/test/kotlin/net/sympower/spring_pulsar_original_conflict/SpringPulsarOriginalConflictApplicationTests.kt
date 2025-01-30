package net.sympower.spring_pulsar_original_conflict

import org.apache.pulsar.client.admin.PulsarAdmin
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.PulsarContainer
import org.testcontainers.utility.DockerImageName

@SpringBootTest
class SpringPulsarOriginalConflictApplicationTests {

	@Test
	fun contextLoads() {
		val container = PulsarContainer(DockerImageName.parse("apachepulsar/pulsar:4.0.2"))
		container.start()

		val admin = PulsarAdmin.builder()
			.serviceHttpUrl(container.httpServiceUrl)
			.build()

		println(admin.tenants().tenants)

		container.stop()
	}

}
