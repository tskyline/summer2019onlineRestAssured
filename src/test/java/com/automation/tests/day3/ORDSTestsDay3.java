package com.automation.tests.day3;

import com.automation.utilities.ConfigurationReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;
public class ORDSTestsDay3
{
    @BeforeAll
    public static void setup()
    {
        RestAssured.baseURI= ConfigurationReader.getProperty("ords.uri");
    }

    @Test
    public void test1()
    {
        given().
                accept("application.json").
                get("/employees").
                then().assertThat().statusCode(200).
                and().assertThat().contentType("application/json").
                log().all(true);
    }

    //path parameter - to point on specific resource
    //query parameter - to filter results, or describe new resource
    @Test
    public void test2()
    {
        given().
                accept("application.json").
                pathParam("id",100).
                when().get("/employees/{id}").
                then().assertThat().statusCode(200).
                and().assertThat().body("employee_id", is(100),
                                "department_id", is(90),
                                             "last_name", is("King")).
        log().all(true);
    }

    @Test
    public void test3()
    {
        given().
                accept("application.json").
                pathParam("id",1).
                when().get("/regions/{id}").
                then().assertThat().statusCode(200).
                and().assertThat().body("region_name",is("Europe")).
                time(lessThan(3L), TimeUnit.SECONDS).
                log().body(true);
    }

    @Test
    public void test4()
    {
        JsonPath json=given().
                accept("application/json").
                when().get("/employees").
                thenReturn().jsonPath();

        String nameof1stemp=json.getString("items[0].first_name");
        System.out.println(nameof1stemp);   //Steven

        String nameoflastemp=json.getString("items[-1].first_name");
        System.out.println(nameoflastemp);  //Kevin

        //gets the info for the 1st employee
        Map<String,?>firstEmp=json.get("items[0]");//? means any data type

        for (Map.Entry<String,?>entry:firstEmp.entrySet())
        {
            System.out.println("key :"+entry.getKey()+", value: "+entry.getValue());
        }

        List<String> lastNames=json.get("items.last_name");
        for (String str : lastNames)
        {
            System.out.println("Last Name : "+str);
        }
    }


    //get info from countries as List<Map<String,?>>
    @Test
    public void test5()
    {
        JsonPath json=given().
                accept("application/json").
                when().get("/countries").
                prettyPeek()
                .jsonPath();
        List<Map<String,?>> countries=json.get("items");


        for (Map<String,?> map:countries)
        {
            System.out.println(map);
        }
    }

    //get collection of emp salaries,sort and print
    @Test
    public void test6()
    {
        List<Integer> salaries=given().
                accept("application/json").
                when().get("/employees").
                thenReturn().
                jsonPath().get("items.salary");
        Collections.sort(salaries);
        Collections.reverse(salaries);
        System.out.println(salaries);
    }


    @Test
    public void test7()
    {
        List<String> phonenum=given().
                accept("application/json").
                when().get("/employees").
                thenReturn().
                jsonPath().get("items.phone_number");

        phonenum.replaceAll(p->p.replace(".","-"));
        System.out.println(phonenum);
    }

    /*
    /** ####TASK#####
 *  Given accept type as JSON
 *  And path parameter is id with value 1700
 *  When user sends get request to /locations
 *  Then user verifies that status code is 200
 *  And user verifies following json path information:
 *      |location_id|postal_code|city   |state_province|
 *      |1700       |98199      |Seattle|Washington    |
 *
 */

    @Test
    public void test8()
    {
        Response response=given().
                accept(ContentType.JSON).
                pathParam("id",1700).
                when().get("/locations/{id}");

        response.then().assertThat().body("location_id",is(1700),
                "postal_code", is("98199"),
                "city", is("Seattle"),
                "state_province", is("Washington")).
        log().body();

    }

}
