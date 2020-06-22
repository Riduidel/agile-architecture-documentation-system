package org.ndx.agile.architecture.base.enhancers.includes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.ArchitectureEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;

import com.structurizr.model.Element;

/**
 * Enhancer allowing incldues to be auto-generated for each element having interesting content.
 * 
 * Let's take an example.
 * Suppose you want to add some documentation for a component code.
 * Then you just have to write the documentation in the file ${asciidoc.source.dir}/07-code/${component name}.adoc
 * and it will be automagically included in documentation.
 * 
 * @author nicolas-delsaux
 *
 */
public class ImplicitIncludeManager extends ModelElementAdapter {
	
	private static final String ASCIIDOC_SOURCE_DIR = "asciidoc.source.dir";
	private File sourceDir;

	@Inject public void setDocumentsFolder(@ConfigProperty(name=ASCIIDOC_SOURCE_DIR) File sourceDir) {
		if(sourceDir==null) {
			throw new CantCreateImplicitInclude(
					String.format("To have implicit includes working, you have to define the system property %s", 
							ASCIIDOC_SOURCE_DIR)
					);
		}
		this.sourceDir = sourceDir;
	}
	/**
	 * We set it at first element to have those text before all enhancers
	 */
	@Override
	public int priority() {
		return 0;
	}

	@Override
	protected void processElement(Element element, OutputBuilder builder) {
		for(AgileArchitectureSection section : AgileArchitectureSection.values()) {
			generateLinkFor(section, element, builder);
		}
	}

	private void generateLinkFor(AgileArchitectureSection section, Element element, OutputBuilder builder) {
		String sectionFolder = String.format(ArchitectureEnhancer.SECTION_PATTERN, section.index(), section.name());
		String elementName = String.format("%s.adoc", element.getCanonicalName().replace('/', '_'));
		File potentialFile = new File(new File(sourceDir,sectionFolder), elementName);
		if(potentialFile.exists()) {
			File targetFile = builder.outputFor(section, element, this, "adoc");
			if(!targetFile.exists()) {
				try {
					Path relativePath = targetFile.getParentFile().getCanonicalFile().toPath()
							.relativize(potentialFile.getCanonicalFile().toPath());
					FileUtils.write(targetFile, 
							String.format("include::%s[leveloffset=+1]", 
									relativePath.toString()), 
							"UTF-8");
				} catch (IOException e) {
					throw new CantCreateImplicitInclude(String.format("Can't create file %s", targetFile), e);
				}
			}
		}
	}

}