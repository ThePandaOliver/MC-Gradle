package dev.pandasystems.bambooloom

import dev.pandasystems.bambooloom.tasks.AddMinecraftDependenciesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.internal.impldep.com.google.gson.Gson
import org.gradle.internal.impldep.com.google.gson.GsonBuilder

val GSON: Gson = GsonBuilder().setPrettyPrinting().create()

class MCGradlePlugin : Plugin<Project> {
	override fun apply(target: Project) {
		target.plugins.apply("java-library")

		// Register repositories
		target.repositories.maven { repo: MavenArtifactRepository ->
			repo.setUrl("https://libraries.minecraft.net/")
		}

		// Create mapping configurations
		val mappedImplementation = target.configurations.create("mappedImplementation") {
			it.isCanBeConsumed = false
			it.isCanBeResolved = true
		}
		target.configurations.getByName("implementation") { it.extendsFrom(mappedImplementation) }
		val mappedCompileOnly = target.configurations.create("mappedCompileOnly") {
			it.isCanBeConsumed = false
			it.isCanBeResolved = true
		}
		target.configurations.getByName("compileOnly") { it.extendsFrom(mappedCompileOnly) }
		val mappedRuntimeOnly = target.configurations.create("mappedRuntimeOnly") {
			it.isCanBeConsumed = false
			it.isCanBeResolved = true
		}
		target.configurations.getByName("runtimeOnly") { it.extendsFrom(mappedRuntimeOnly) }

		// Create Minecraft configuration
		val minecraft = target.configurations.create("minecraft") {
			it.isCanBeConsumed = false
			it.isCanBeResolved = true
		}
		val mapping = target.configurations.create("mapping") {
			it.isCanBeConsumed = false
			it.isCanBeResolved = true
		}

		// Register the AddMinecraftDependenciesTask
		target.tasks.register("addMinecraftDependencies", AddMinecraftDependenciesTask::class.java)
		target.tasks.getByName("prepareKotlinBuildScriptModel") {it.dependsOn("addMinecraftDependencies")}
	}
}