package de.factorypal.sauloborges.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Swagger configuration
 */
@Configuration
public class SwaggerConfiguration {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(getApiInfo()).select()
				.apis(RequestHandlerSelectors.any()).paths(regex("/machine.*")).build();
	}

	private ApiInfo getApiInfo() {
		return new ApiInfoBuilder().title("Speed Metrics Service")
				.description("An API to provide data from parameters for different machines")
				.contact(new Contact("Saulo Borges", "", "saudborg@gmail.com")).build();
	}
}
