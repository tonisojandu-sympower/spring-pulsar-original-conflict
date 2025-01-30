plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "net.sympower"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

ext["pulsar.version"] = "4.0.2"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.apache.pulsar:pulsar-client")
//	implementation("org.apache.pulsar:pulsar-client-original")

	testImplementation("org.apache.pulsar:pulsar-client-admin")
//	testImplementation("org.apache.pulsar:pulsar-client-admin-original")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.testcontainers:pulsar")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")


	/*
	To solve:

java.lang.NoClassDefFoundError: javax/ws/rs/core/Configuration
	at org.apache.pulsar.client.admin.internal.PulsarAdminBuilderImpl.build(PulsarAdminBuilderImpl.java:45)
	at net.sympower.spring_pulsar_original_conflict.SpringPulsarOriginalConflictApplicationTests.contextLoads(SpringPulsarOriginalConflictApplicationTests.kt:19)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
Caused by: java.lang.ClassNotFoundException: javax.ws.rs.core.Configuration
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:641)
	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:525)
	... 5 more
	 */
	implementation("javax.ws.rs:javax.ws.rs-api:2.1.1")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

configurations {
	all {
		resolutionStrategy {
			eachDependency {
				if ((requested.group == "org.apache.bookkeeper" || requested.group == "io.streamnative") &&
						requested.name in listOf("circe-checksum", "cpu-affinity", "native-io")
				) {
					// Workaround for invalid metadata for Bookkeeper dependencies which contain
					// <packaging>nar</packaging> in pom.xml
					artifactSelection {
						selectArtifact("jar", null, null)
					}
				} else if (requested.name == "pulsar-client" || requested.name == "pulsar-client-all") {
					// replace pulsar-client and pulsar-client-all with pulsar-client-original
					useTarget("${requested.group}:pulsar-client-original:${ext["pulsar.version"]}")
				} else if (requested.name == "pulsar-client-admin") {
					// replace pulsar-client-admin with pulsar-client-admin-original
					useTarget("${requested.group}:pulsar-client-admin-original:${ext["pulsar.version"]}")
				}
			}
		}
	}
}

