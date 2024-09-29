import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.plugin.mpp.*
import org.jetbrains.kotlin.konan.target.*
import java.io.*

plugins {
	kotlin("multiplatform") version ("2.0.20") apply false
	id("de.undercouch.download") version ("5.6.0") apply false
}

val stdout = ByteArrayOutputStream()
exec {
	commandLine("git", "describe", "--tags", "--always")
	standardOutput = stdout
}

group = "com.kgl"
version = stdout.toString().trim()

val useSingleTarget: Boolean by extra { System.getProperty("idea.active") == "true" }
val ktorIoVersion: String by extra("2.3.12")
val lwjglVersion: String by extra("3.3.4")
val lwjglNatives: String by extra {
	when {
		HostManager.hostIsLinux -> "natives-linux"
		HostManager.hostIsMac -> "natives-macos"
		HostManager.hostIsMingw -> "natives-windows"
		else -> error("Host platform not supported")
	}
}

allprojects {
	repositories {
		mavenCentral()
	}
}

subprojects {
	group = rootProject.group
	version = rootProject.version

	plugins.withId("org.jetbrains.kotlin.multiplatform") {
		configure<KotlinMultiplatformExtension> {
			sourceSets.all {
				languageSettings.apply {
//					enableLanguageFeature("InlineClasses")
//					useExperimentalAnnotation("kotlin.RequiresOptIn")
//					useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
//					useExperimentalAnnotation("io.ktor.utils.io.core.ExperimentalIoApi")
				}
			}

			// Hack until https://youtrack.jetbrains.com/issue/KT-30498
			// Cross-Platform builds are generally disabled, but linux targets can apparently be built
			// on both macOS and Windows, which breaks on both of those platforms, so we disable the
			// cinterop, compile, and link tasks for any non-host native target
			targets.withType<KotlinNativeTarget> {
				if (konanTarget != HostManager.host) {
					compilations.all {
						cinterops.all { tasks[interopProcessingTaskName].enabled = false }
						compileKotlinTask.enabled = false
					}
					binaries.all { linkTask.enabled = false }
				}

				compilations.all {
					compilerOptions.configure {
						freeCompilerArgs.add("-linker-option") //TODO: Do this right and have konan use an updated libc version
						freeCompilerArgs.add("--allow-shlib-undefined")
						freeCompilerArgs.add("-linker-option")
						freeCompilerArgs.add("--unresolved-symbols=ignore-all")
						freeCompilerArgs.add("-linker-option")
						freeCompilerArgs.add("-L/usr/lib")
					}
				}
			}

			targets.all {
				compilations.all {
					compilerOptions.configure {
						freeCompilerArgs.add("-opt-in=kotlinx.cinterop.ExperimentalForeignApi")
						freeCompilerArgs.add("-Xexpect-actual-classes")
					}
				}
			}
		}
	}

	plugins.withId("maven-publish") {
		configure<PublishingExtension> {
			repositories {
				maven("https://maven.pkg.github.com/Dominaezzz/${rootProject.name}") {
					name = "GitHubPackages"
					credentials {
						username = System.getenv("GITHUB_USER")
						password = System.getenv("GITHUB_TOKEN")
					}
				}
			}

			publications.withType<MavenPublication> {
				val vcs = "https://github.com/Dominaezzz/kgl"
				pom {
					name.set(project.name)
					description.set(project.description)
					url.set(vcs)
					licenses {
						license {
							name.set("The Apache Software License, Version 2.0")
							url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
							distribution.set("repo")
						}
					}
					developers {
						developer {
							id.set("Dominaezzz")
							name.set("Dominic Fischer")
						}
					}
					scm {
						connection.set("$vcs.git")
						developerConnection.set("$vcs.git")
						url.set(vcs)
					}
				}
			}
		}

		val taskPrefixes = when {
			HostManager.hostIsLinux -> listOf(
				"publishLinux",
				"publishJs",
				"publishJvm",
				"publishMetadata",
				"publishKotlinMultiplatform"
			)
			HostManager.hostIsMac -> listOf(
				"publishMacos",
				"publishIos"
			)
			HostManager.hostIsMingw -> listOf(
				"publishMingw"
			)
			else -> error("unknown host")
		}

		val publishTasks = tasks.withType<PublishToMavenRepository>().matching { task ->
			taskPrefixes.any { task.name.startsWith(it) }
		}

		tasks.register("smartPublish") {
			dependsOn(publishTasks)
		}
	}
}
