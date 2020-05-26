package com.haw.lebensmittelladen;

import com.haw.lebensmittelladen.article.domain.datatypes.Email;
import com.haw.lebensmittelladen.article.domain.entities.Article;
import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@EnableSwagger2
@EnableDiscoveryClient
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.haw"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Lebensmittelladen REST API",
                "REST API for Architecture-Course @ HAW-Hamburg",
                "API v1",
                "Terms of service",
                new Contact("Simon Kuhnt", "",
                        ""),
                "MIT License", "https://opensource.org/licenses/MIT", Collections.emptyList());
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

@Component
@Profile("default")
class PopulateTestDataRunner implements CommandLineRunner {

    private final ArticleRepository articleRepository;

    @Autowired
    public PopulateTestDataRunner(ArticleRepository articleRepository) {
        super();
        this.articleRepository = articleRepository;
    }

    @Override
    public void run(String... args) {
        Arrays.asList(
                "Tomate,Gurke,Erbse".split(","))
                .forEach(
                        name -> articleRepository.save(new Article(name, "Edeka Bio " + name, "gr",500,"Edeka", 1.55, 2))
                );
        System.out.println(articleRepository.findAll());
    }
}
