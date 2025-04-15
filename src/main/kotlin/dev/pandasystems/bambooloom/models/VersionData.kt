package dev.pandasystems.bambooloom.models

import dev.pandasystems.bambooloom.utils.LoomPaths
import dev.pandasystems.bambooloom.utils.downloadFrom
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.Project
import java.io.File
import java.net.URI

class VersionData(
	json: JsonObject,
	private val project: Project,
	private val versionManifest: VersionManifest
) {
	val id: String = json["id"]!!.jsonPrimitive.content
	val type: String = json["type"]!!.jsonPrimitive.content
	val time: String = json["time"]!!.jsonPrimitive.content
	val releaseTime: String = json["releaseTime"]!!.jsonPrimitive.content

	val downloads: Downloads = json["downloads"]!!.jsonObject.let {
		Downloads(
			clientJar = JarDependency(
				url = it.jsonObject["client"]!!.jsonObject["url"]!!.jsonPrimitive.content,
				sha1 = it.jsonObject["client"]!!.jsonObject["sha1"]!!.jsonPrimitive.content,
				size = it.jsonObject["client"]!!.jsonObject["size"]!!.jsonPrimitive.content.toLong(),
				path = LoomPaths.versionJarsDir(project, id).resolve("client.jar")
			),
			serverJar = JarDependency(
				url = it.jsonObject["server"]!!.jsonObject["url"]!!.jsonPrimitive.content,
				sha1 = it.jsonObject["server"]!!.jsonObject["sha1"]!!.jsonPrimitive.content,
				size = it.jsonObject["server"]!!.jsonObject["size"]!!.jsonPrimitive.content.toLong(),
				path = LoomPaths.versionJarsDir(project, id).resolve("server.jar")
			)
		)
	}

	val libraries: List<JarDependency> = json["libraries"]!!.jsonArray.map {
		JarDependency(
			url = it.jsonObject["downloads"]!!.jsonObject["artifact"]!!.jsonObject["url"]!!.jsonPrimitive.content,
			sha1 = it.jsonObject["downloads"]!!.jsonObject["artifact"]!!.jsonObject["sha1"]!!.jsonPrimitive.content,
			size = it.jsonObject["downloads"]!!.jsonObject["artifact"]!!.jsonObject["size"]!!.jsonPrimitive.content.toLong(),
			path = LoomPaths.libraryCacheDir(project).resolve(it.jsonObject["downloads"]!!.jsonObject["artifact"]!!.jsonObject["path"]!!.jsonPrimitive.content)
		)
	}

	inner class Downloads(
		val clientJar: JarDependency,
		val serverJar: JarDependency,
	)

	inner class JarDependency(
		val url: String,
		val sha1: String,
		val size: Long,
		val path: File
	) {
		val file by lazy {
			if (!path.exists())
				path.downloadFrom(URI(url).toURL())
			path
		}
	}
}