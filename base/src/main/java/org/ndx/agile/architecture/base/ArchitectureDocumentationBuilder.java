package org.ndx.agile.architecture.base;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.jboss.weld.config.ConfigurationKey;

import com.structurizr.Workspace;
import com.structurizr.annotation.Component;
import com.structurizr.annotation.UsesComponent;

/**
 * Main class of agile architecture documentation system.
 * This will start a CDI container and, in this CDI container, instanciate this object.
 * THis instanciation will load {@link #provider} and {@link #enhancer} to generate all asciidoc required content.
 * @author nicolas-delsaux
 *
 */
@Component(technology = "Java/CDI")
@ApplicationScoped
public class ArchitectureDocumentationBuilder {

	public static void main(String[] args) throws Throwable {
		// TODO Disable weld INFO logging, cause it outputs too much things of no interest
		// Disable the Weld thread pool (unless it is defined on command-line)
		System.setProperty(ConfigurationKey.EXECUTOR_THREAD_POOL_TYPE.get(), "NONE");
        SeContainerInitializer containerInit = SeContainerInitializer.newInstance();
        SeContainer container = containerInit.initialize();
        ArchitectureDocumentationBuilder architecture = container.select(ArchitectureDocumentationBuilder.class).get();
        architecture.run();
        container.close();
	}

	@Inject Logger logger;
	@Inject @UsesComponent(description = "Adds information to initial architecture description") ArchitectureEnhancer enhancer;
	@Inject @UsesComponent(description = "Generates initial architecture description") ArchitectureModelProvider provider;

	/**
	 * Run method that will allow the description to be invoked and augmentations to be performed
	 * prior to have elements written. You should not have to overwrite this method.
	 * @throws IOException
	 */
	public void run() throws IOException {
		Workspace workspace = provider.describeArchitecture();
		logger.info("Architecture has been described. Now enhancing it (including writing the diagrams)!");
		enhancer.enhance(workspace);
	}
}
