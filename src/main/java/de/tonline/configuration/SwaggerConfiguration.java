package de.tonline.configuration;

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
				.apis(RequestHandlerSelectors.any()).paths(regex("/posts.*")).build();
	}

	private ApiInfo getApiInfo() {
		return new ApiInfoBuilder().title("Async Fetch Api")
				.description("A simple api to provide multiple request and merge the result")
				.contact(new Contact("Saulo Borges", "", "saudborg@gmail.com")).build();
	}
}
