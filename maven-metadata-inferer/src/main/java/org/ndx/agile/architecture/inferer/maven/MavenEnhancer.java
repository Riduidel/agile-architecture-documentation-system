package org.ndx.agile.architecture.inferer.maven;

public interface MavenEnhancer {

	/**
	 * URL of the maven pom the model element represents.
	 * This URL can be
	 * <ul>
	 * <li>a local file</li>
	 * <li>a http(s)? url</li>
	 * <li>a resource in jar url</li>
	 *  </ul>
	 */
	String AGILE_ARCHITECTURE_MAVEN_POM = "agile.architecture.maven.pom";
	/**
	 * Name of a class we want to load the Maven POM for.
	 */
	String AGILE_ARCHITECTURE_MAVEN_CLASS = "agile.architecture.maven.class";

}
