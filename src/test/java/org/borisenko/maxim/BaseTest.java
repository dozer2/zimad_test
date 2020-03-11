package org.borisenko.maxim;

import io.qameta.allure.Attachment;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.CoreMatchers.not;

public abstract class BaseTest {

    Configuration configuration= Configuration.getInstance();
    ThreadLocal<ByteArrayOutputStream> writer= new ThreadLocal<>();
    ThreadLocal<PrintStream> printStream= new ThreadLocal<>();
    ThreadLocal<RequestSpecification> specification= new ThreadLocal<>();




    protected String getUUID(){
        return UUID.randomUUID().toString();
    }
    protected RequestSpecification getSpecification(){
        if(specification.get()== null) {
            writer.set(new ByteArrayOutputStream());
            printStream.set(new PrintStream(writer.get(), true));
            specification.set(
                    new RequestSpecBuilder()
                            .setBaseUri("https://api.todoist.com/rest/v1")
                            .setContentType(ContentType.JSON)
                            .addHeader("Authorization", "Bearer ".concat(configuration.getAuthorizationKey()))
                            .setConfig(RestAssured
                                    .config()
                                    .encoderConfig(encoderConfig()
                                            .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                            .addFilter(RequestLoggingFilter.logRequestTo(printStream.get()))
                            .addFilter(ResponseLoggingFilter.logResponseTo(printStream.get()))
                            .build());
        } return specification.get();
    }


    @AfterMethod(description = "Лог запросов")
    @Attachment(value = "log.txt", type = "text/plain")
    public  String addOLogAttachmentToReport() {
        String result=writer.get().toString();
        specification.remove();
        return result;
    }

    protected String getTaskName(){
        return "Task ".concat(getUUID());
    }
    protected Long createTestProjectAndReturnId(){
        String projectName="Project "+getUUID();
        Map<String, Object> projects =
        given()
                .spec(getSpecification())
                .body("{ \"name\" : \""+projectName+ "\" }")
                .when()
                .post("/projects")
                .then()
                .statusCode(200)
                .extract()
                .response().as(new TypeRef<Map<String, Object>>() {});
        return (Long)projects.get("id");
    }
    protected void deleteTestProject(Long projectId){
                given()
                        .spec(getSpecification())
                        .pathParam("projectId",projectId)
                        .when()
                        .delete("/projects/{projectId}")
                        .then()
                        .statusCode(204);

    }

    protected Integer createTestSectionAndReturnId(Long projectId){
        String projectName="Section "+getUUID();
        Map<String, Object> projects =
                given()
                        .spec(getSpecification())
                        .body("{ \"name\" : \""+projectName+ "\" , \"project_id\" : "+projectId+" }")
                        .when()
                        .post("/sections")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response().as(new TypeRef<Map<String, Object>>() {});
        return  (Integer)projects.get("id");
    }
    protected void deleteTestSection(Long sectionId){
        given()
                .spec(getSpecification())
                .pathParam("sectionId",sectionId)
                .when()
                .delete("/sections/{sectionId}")
                .then()
                .statusCode(204);

    }

    protected void deleteTestTask(Long taskId){
        given()
                .spec(getSpecification())
                .pathParam("taskId",taskId)
                .when()
                .delete("/tasks/{taskId}")
                .then()
                .statusCode(204);

    }


}
