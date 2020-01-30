package com.automation.tests.day3;

import com.automation.utilities.ConfigurationReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
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
    /**
     * Warmup!
     *          Given accept type is JSON
     *         When users sends a GET request to "/employees"
     *         Then status code is 200
     *         And Content type is application/json
     *         And response time is less than 3 seconds
     */
       @Test
    public void test9()
       {
          given().
                   accept(ContentType.JSON).
                   when().get("/employees").
                   then().assertThat().statusCode(200).
                   and().assertThat().contentType("application/json").
                   time(lessThan(3L),TimeUnit.SECONDS).
                   log().all(true);
                   //Expected response time was not a value less than <3L> seconds, was 7469 milliseconds (7 seconds).
       }
    /**
    Given accept type is JSON
    And parameters: q = country_id = US
    When users sends a GET request to "/countries"
    Then status code is 200
    And Content type is application/json
    And country_name from payload is "United States of America"
    {"country_id":"US"}
 */
        @Test
        public void test10()
        {
            given().accept(ContentType.JSON).
                    pathParam("country_id","US").
                    when().get("/countries/{country_id}").
                    then().assertThat().statusCode(200).
                    and().assertThat().contentType("application/json").
                    assertThat().body("country_name",is("United States of America")).
            log().all(true);
        }

        @Test
        @DisplayName("Verify that country_name from payload is \"United States of America\"")
        public void test10_2()
    {
        given().
                accept(ContentType.JSON).
                queryParam("q", "{\"country_id\":\"US\"}").
                when().
                get("/countries").
                then().
                assertThat().
                contentType(ContentType.JSON).
                statusCode(200).
                body("items[0].country_name", is("United States of America")).
                log().all(true);
    }


    @Test
    @DisplayName("Get all links and print them")
    public void test11() {
        Response response = given().
                accept(ContentType.JSON).
                queryParam("q", "{\"country_id\":\"US\"}").
                when().
                get("/countries");

        JsonPath jsonPath = response.jsonPath();
        List<?>links =jsonPath.getList("links.href");

        for (Object link:links)
        {
            System.out.println(link);
        }


    }

         @Test//verify payload contains only 25 countries
        public void test12()
        {
            List<?> countries=
            given().accept((ContentType.JSON)).
                    when().get("/countries").
                    thenReturn().jsonPath().getList("items");//items[indexnum] for a specific item

            assertEquals(25,countries.size());


        }
    /**
     * given path parameter is "/countries" and region id is 2
     * when user makes get request
     * then assert that status code is 200
     * and user verifies that body returns following country names
     *  |Argentina                |
     *  |Brazil                   |
     *  |Canada                   |
     *  |Mexico                   |
     *  |United States of America |
     *
     */

    @Test
    @DisplayName("Verify that payload contains following countries")
    public void test13() {
        //to use List.of() set java 9 at least
        List<String> expected = List.of("Argentina", "Brazil", "Canada", "Mexico", "United States of America");

        Response response = given().
                accept(ContentType.JSON).
                queryParam("q", "{\"region_id\":\"2\"}").
                when().
                get("/countries").prettyPeek();
        List<String> actual = response.jsonPath().getList("items.country_name");

        assertEquals(expected, actual);

        ///with assertThat()

        given().
                accept(ContentType.JSON).
                queryParam("q", "{\"region_id\":\"2\"}").
                when().
                get("/countries").
                then().assertThat().body("items.country_name" , contains("Argentina", "Brazil", "Canada", "Mexico", "United States of America"));
    }

    /**
     * given path parameter is "/employees"
     * when user makes get request
     * then assert that status code is 200
     * Then user verifies that every employee has positive salary
     *
     */
    @Test
    @DisplayName("Verify that every employee has positive salary")
    public void test14(){
        given().
                accept(ContentType.JSON).
                when().
                get("/employees").
                then().
                assertThat().
                statusCode(200).
                body("items.salary", everyItem(greaterThan(0)));

        //whenever you specify path as items.salary, you will get collection of salaries
        //then to check every single value
        //we can use everyItem(is()), everyItem(greaterThan())
        /**
         * Creates a matcher for {@link Iterable}s that only matches when a single pass over the
         * examined {@link Iterable} yields items that are all matched by the specified
         * <code>itemMatcher</code>.
         * For example:
         * <pre>assertThat(Arrays.asList("bar", "baz"), everyItem(startsWith("ba")))</pre>*/
    }
    /**
     * given path parameter is "/employees/{id}"
     * and path parameter is 101
     * when user makes get request
     * then assert that status code is 200
     * and verifies that phone number is 515-123-4568
     *
     */

    @Test
    @DisplayName("Verify that employee 101 has following phone number: 515-123-4568")
    public void test15(){
        Response response = given().
                accept(ContentType.JSON).
                when().
                get("/employees/{id}", 101);
        assertEquals(200, response.getStatusCode());

        String expected = "515-123-4568";
        String actual = response.jsonPath().get("phone_number");

        expected = expected.replace("-", ".");
        assertEquals(expected, actual);
    }

    /**
     * given path parameter is "/employees"
     * when user makes get request
     * then assert that status code is 200
     * and verify that body returns following salary information after sorting from higher to lower
     *  24000, 17000, 17000, 12008, 11000,
     *  9000, 9000, 8200, 8200, 8000,
     *  7900, 7800, 7700, 6900, 6500,
     *  6000, 5800, 4800, 4800, 4200,
     *  3100, 2900, 2800, 2600, 2500
     *
     */

    @Test
    @DisplayName("verify that body returns following salary information after sorting from higher to lower(after sorting it in descending order)")
    public void test16(){
        List<Integer> expectedSalaries = List.of(24000, 17000, 17000, 12008, 11000,
                9000, 9000, 8200, 8200, 8000,
                7900, 7800, 7700, 6900, 6500,
                6000, 5800, 4800, 4800, 4200,
                3100, 2900, 2800, 2600, 2500);
        Response response = given().
                accept(ContentType.JSON).
                when().
                get("/employees");
        assertEquals(200, response.statusCode());

        List<Integer> actualSalaries = response.jsonPath().getList("items.salary");

        Collections.sort(actualSalaries, Collections.reverseOrder());

        System.out.println(actualSalaries);

        assertEquals(expectedSalaries, actualSalaries, "Salaries are not matching");
    }


    /** ####TASK#####
     *  Given accept type as JSON
     *  And path parameter is id with value 2900
     *  When user sends get request to /locations/{id}
     *  Then user verifies that status code is 200
     *  And user verifies following json path contains following entries:
     *      |street_address         |city  |postal_code|country_id|state_province|
     *      |20 Rue des Corps-Saints|Geneva|1730       |CH        |Geneve        |
     *
     */

    /**
     *     "location_id": 2900,
     *     "street_address": "20 Rue des Corps-Saints",
     *     "postal_code": "1730",
     *     "city": "Geneva",
     *     "state_province": "Geneve",
     *     "country_id": "CH",
     */
    @Test
    @DisplayName("Verify that JSON body has following entries")
    public void test17(){
        given().
                accept(ContentType.JSON).
                pathParam("id", 2900).
                when().
                get("/locations/{id}").
                then().
                assertThat().
                statusCode(200).
                body("", hasEntry("street_address", "20 Rue des Corps-Saints")).
                body("", hasEntry("city", "Geneva")).
                body("", hasEntry("postal_code", "1730")).
                body("", hasEntry("country_id", "CH")).
                body("", hasEntry("state_province", "Geneve")).
                log().all(true);

    }

    @Test
    @DisplayName("Verify that JSON body has following entries")
    public void test17_2(){
        given().
                accept(ContentType.JSON).
                pathParam("id", 2900).
                when().
                get("/locations/{id}").
                then().
                assertThat().
                statusCode(200).
                body("street_address", is("20 Rue des Corps-Saints")).
                body("city", is("Geneva")).
                body("postal_code", is("1730")).
                body("country_id", is("CH")).
                body("state_province", is("Geneve")).
                log().all(true);
    }

}
