import org.liquibase.gradle.LiquibaseExtension

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("java")
    id("org.liquibase.gradle") version "2.2.0"
}

val liquibaseRuntime by configurations.getting

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // MapStruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Чтобы MapStruct корректно работал с Lombok (@Builder, @Accessors и т.п.)
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // Для предупреждений When.MAYBE
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // БД
    implementation("org.postgresql:postgresql") // версию даст BOM Boot

    // Byte Buddy (фикс для Hibernate 6)
    implementation("net.bytebuddy:byte-buddy:1.14.14")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // Логирование
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Тестирование
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // нужно для gradle liquibase:
    liquibaseRuntime("org.liquibase:liquibase-core:4.24.0")
    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("info.picocli:picocli:4.7.6")
    liquibaseRuntime("org.yaml:snakeyaml:2.2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

configurations.all {
    // подстраховка, если какая-то зависимость тянет старый byte-buddy
    resolutionStrategy.force("net.bytebuddy:byte-buddy:1.14.14")
}



configure<LiquibaseExtension> {
    activities.register("main") {
        arguments = mapOf(
            "changelogFile" to "offline/src/main/resources/changelog.yml",
            "url" to "jdbc:postgresql://localhost:5432/MyProjectDB",
            "username" to (System.getenv("DB_USERNAME") ?: "postgres"),
            "password" to (System.getenv("DB_PASSWORD") ?: "changeme")
        )
    }
    runList = "main" // можно опустить, если activity одна
}

