package org.borisenko.maxim;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.borisenko.maxim.model.TaskResponse;
import org.borisenko.maxim.model.TaskRequest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;


@Story("Создание задачи")
@Feature("Создание задачи")
public class CreateNewTaskResponse extends BaseTest{

    @DataProvider(name = "fields")
    public static Object[][] fields() {
        return new Object[][] {{"project_id"},{"section_id"},{"parent"},{"order"},{"priority"},{"label_ids"}};
    }


    @Test(description = "Задача создается с обязательными полями")
    public void taskMustBeCreatedWithContent(){
        String taskName=getTaskName();
        TaskResponse newTask=given()
                .spec(getSpecification())
                .body(new TaskRequest().setContent(taskName))
                .when()
                .post("/tasks")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id",not(empty()))
                .body("url",not(empty()))
                .body("content",equalTo(taskName))
                .extract()
                .as(TaskResponse.class);
        deleteTestTask(newTask.getId());
    }

    @Test(description = "Задача не может быть создана без названия")
    public void taskNotMustBeCreatedWithoutContent(){
        String result=given()
                .spec(getSpecification())
                .when()
                .post("/tasks")
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .asString();
        assertThat("Error! Task created without content",result,equalTo("Bad Request\n"));
    }


    @Test(description = "Задача может быть связана с проектом")
    public void taskCanBeLinkedWithProject(){
        Long projectId= createTestProjectAndReturnId();
        String taskName=getTaskName();
        TaskRequest taskBody = new TaskRequest()
                .setContent(taskName)
                .setProject_id(projectId);
        given()
                .spec(getSpecification())
                .body(taskBody)
                .when()
                .post("/tasks")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id",not(empty()))
                .body("content",equalTo(taskName))
                .body("project_id", is(projectId)) ;
         deleteTestProject(projectId);
    }

    @Test(description = "Задача может быть связана с секцией в проекте")
    public void taskCanBeLinkedWithProjectSection(){
        Long projectId= createTestProjectAndReturnId();
        Integer sectionId= createTestSectionAndReturnId(projectId);
      String taskName=getTaskName();
        TaskRequest taskBody = new TaskRequest()
                .setContent(taskName)
                .setProject_id(projectId)
                .setSection_id(sectionId);
        given()
                .spec(getSpecification())
                .body(taskBody)
                .when()
                .post("/tasks")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id",not(empty()))
                .body("content",equalTo(taskName))
                .body("project_id", is(projectId))
                .body("section_id", is(sectionId)) ;
        deleteTestProject(projectId);
    }

    @Test(description = "Задача может иметь родителя")
    public void taskCanHasParent(){
        TaskResponse parentTask =
        given()
                .spec(getSpecification())
                .body(new TaskRequest().setContent(getTaskName()))
                .when()
                .post("/tasks")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .as(TaskResponse.class);
        String taskName=getTaskName();
        given()
                .spec(getSpecification())
                .body(new TaskRequest().setContent(taskName).setParent(parentTask.getId()))
                .when()
                .post("/tasks")
                .then()
                .statusCode(200)
                .body("content",equalTo(taskName))
                .body("parent",is(parentTask.getId()));
        deleteTestTask(parentTask.getId());
    }

    @Test(description = "Задача может быть упорядоченна")
    public void taskCanHasOrder(){

        String taskName=getTaskName();
        TaskResponse secondTask =
                given()
                        .spec(getSpecification())
                        .body(new TaskRequest().setContent(taskName).setOrder(2))
                        .when()
                        .post("/tasks")
                        .then()
                        .statusCode(200)
                        .assertThat()
                        .body("content",equalTo(taskName))
                        .body("order",is(2))
                        .extract()
                        .as(TaskResponse.class);
        deleteTestTask(secondTask.getId());
    }

    @Test (description = "У задачи есть приоритет")
    public void taskCanHasPriority(){
        String taskName=getTaskName();
        TaskResponse task =
                given()
                        .spec(getSpecification())
                        .body(new TaskRequest().setContent(taskName).setPriority(2))
                        .when()
                        .post("/tasks")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .body("content",equalTo(taskName))
                        .body("priority",is(2))
                        .extract()
                        .as(TaskResponse.class);
        deleteTestTask(task.getId());
    }

    @Test (description = "Дата задачи может быть установлена в человекопонятном формате")
    public void taskCanHasDateAsHumanFormat(){
        String taskName=getTaskName();
        String tomorrow="tomorrow";
        TaskResponse task =
                given()
                        .spec(getSpecification())
                        .body(new TaskRequest().setContent(taskName).setDue_string(tomorrow))
                        .when()
                        .post("/tasks")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .body("content",equalTo(taskName))
                        .body("due.string",equalTo(tomorrow))
                        .extract()
                        .as(TaskResponse.class);
        deleteTestTask(task.getId());
    }

    @Test(description = "Дата задачи может быть установлена в формате ГГГГ-ММ-ДД")
    public void taskCanHasDateAsDateFormat(){
        String taskName=getTaskName();
        String date=LocalDate.now().toString();
        TaskResponse task =
                given()
                        .spec(getSpecification())
                        .body(new TaskRequest().setContent(taskName).setDue_date(date))
                        .when()
                        .post("/tasks")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .body("content",equalTo(taskName))
                        .body("due.date",equalTo(date))
                        .extract()
                        .as(TaskResponse.class);
        deleteTestTask(task.getId());
    }

    @Test (description = "Дата задачи может быть установлена в формате UTC")
    public void taskCanHasDateAsDateTimeFormat(){
        String taskName=getTaskName();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String date= dateFormatter.format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        TaskResponse task =
                given()
                        .spec(getSpecification())
                        .body(new TaskRequest().setContent(taskName).setDue_datetime(date))
                        .when()
                        .post("/tasks")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .body("content",equalTo(taskName))
                        .body("due.datetime",equalTo(date))
                        .extract()
                        .as(TaskResponse.class);
        deleteTestTask(task.getId());
    }
    @Test (description = "Дата задачи не может быть установлена в формате отличном от UTC")
    public void taskNotMustBeCreatedWithWrongDateTime(){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String date= dateFormatter.format(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        String result=given()
                .spec(getSpecification())
                .body(new TaskRequest().setContent(getTaskName()).setDue_datetime(date))
                .when()
                .post("/tasks")
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .asString();
        assertThat("Error! Task created with wrong datetime",result,equalTo("Bad Request\n"));
    }

    @Test (description = "Дата задачи не может быть установлена в формате отличном от ГГГГ-ММ-ДД")
    public void taskNotMustBeCreatedWithWrongDateFormat(){
        String result=given()
                .spec(getSpecification())
                .body(new TaskRequest().setContent(getTaskName()).setDue_date("11-11-11"))
                .when()
                .post("/tasks")
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .asString();
        assertThat("Error! Task created with wrong date format",result,equalTo("due_date not in YYYY-MM-DD format\n"));
    }

    @Test (description = "Дата задачи не может быть установлена несовместимым типом данных ")
    public void taskNotMustBeCreatedWithInvalidDate(){
        String result=given()
                .spec(getSpecification())
                .body(new TaskRequest().setContent(getTaskName()).setDue_string("date"))
                .when()
                .post("/tasks")
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .asString();
        assertThat("Error! Task created with invalid date ",result,equalTo("Date is invalid\n"));
    }

    @Test (description = "Задача не может быть связана с несуществующим проектом")
    public void taskCanNotLinkedWithInvalidProject(){
        String taskName=getTaskName();
        TaskRequest taskBody = new TaskRequest()
                .setContent(taskName)
                .setProject_id(12345678L);
        String result=given()
                .spec(getSpecification())
                .body(taskBody)
                .when()
                .post("/tasks")
                .then()
                .assertThat()
                .statusCode(500)
                .extract()
                .asString();
        assertThat("Error! Task linked with fake project ",result,equalTo("Internal Server Error\n"));
    }


    @Test(dataProvider = "fields",description = "Задача не должна создаваться с некоррекным типом полей входных данных")
    public void taskCanNotCreateWithUnsupportedTypeParams(String field)
    {
        String taskName=getTaskName();
        String result=given()
                .spec(getSpecification())
                .body("{ \"content\" : \""+taskName+"\" , \""+field+"\" : \"wrong\" }" )
                .when()
                .post("/tasks")
                .then()
                .assertThat()
                .statusCode(400)
                .extract()
                .asString();
        assertThat("Error! JSON wrong. ",result,containsString("JSON decode error:"));
    }
}
