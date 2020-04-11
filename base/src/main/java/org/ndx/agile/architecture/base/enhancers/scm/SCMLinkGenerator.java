package org.ndx.agile.architecture.base.enhancers.scm;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.Keys;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;

import com.structurizr.model.Element;

/**
 * Generate a link to folder containing readme (because it's where the code is)
 * @author nicolas-delsaux
 *
 */
@ApplicationScoped
public class SCMLinkGenerator extends ModelElementAdapter {
	@Inject @ConfigProperty(name="force") boolean force;
	
	@Inject Logger logger;
	
	@Inject Instance<SCMHandler> scmHandlers;

	@Override
	public int priority() {
		return 1000;
	}

	@Override
	protected void processElement(Element element, OutputBuilder builder) {
		writeLinkFor(element, builder);
		
	}

	/**
	 * Write the readme as fetched from any SCM provider
	 * @param element
	 * @param builder
	 */
	void writeLinkFor(Element element, OutputBuilder builder) {
		if(element.getProperties().containsKey(Keys.SCM_PROJECT)) {
			String elementProject = element.getProperties().get(Keys.SCM_PROJECT);
			String elementPath = element.getProperties().getOrDefault(Keys.SCM_PATH, "");
			Optional<SCMHandler> usableHandler = scmHandlers.stream()
				.filter(handler -> handler.canHandle(elementProject))
				.findFirst()
				;
			if(usableHandler.isPresent()) {
				SCMHandler handler = usableHandler.get();
				try {
					FileUtils.write(builder.outputFor(AgileArchitectureSection.code, element, this, "adoc"),
							 String.format("%s[See on %s]", 
									 handler.linkTo(elementProject, elementPath),
									 handler.asciidocText()
									 ), 
							"UTF-8");
				} catch (IOException e) {
					throw new CantWriteLink(String.format("Can't write link for element %s which is linked to %s/%s", 
							element.getCanonicalName(), elementProject, elementPath), 
							e);
				}				
			} else {
				logger.warning(String.format("We have this set of handlers\n%s\nin which we couldn't find one for element %s associated project %s",
						scmHandlers.stream().map(handler -> handler.toString()).collect(Collectors.joining()),
						element.getCanonicalName(),
						elementProject
						));
			}
		}
	}
}