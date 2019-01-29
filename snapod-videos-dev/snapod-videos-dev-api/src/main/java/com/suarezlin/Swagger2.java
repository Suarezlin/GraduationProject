package com.suarezlin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    /**
     * swagger2 配置文件
     * @return swagger2 配置
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.suarezlin.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 生成 api info
     * @return api info
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("snapod api 接口文档")
                .contact(new Contact("suarezlin","suarezlin.com", "suarezlinziniu@gmail.com"))
                .description("欢迎使用 snapod 短视频后端 api 接口")
                .version("1.0")
                .build();
    }

}
