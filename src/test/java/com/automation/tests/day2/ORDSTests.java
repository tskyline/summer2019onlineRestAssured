package com.automation.tests.day2;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class ORDSTests
{
    //DATA is coming from SQL Oracle dbase to this web service
    private String baseURI="http://ec2-52-90-141-57.compute-1.amazonaws.com:1000/ords/hr/";

    //verify that status code is 200
    @Test
    public void test1()
    {
        Response response=given().get(baseURI+"employees");
        System.out.println(response.getBody().asString());
        assertEquals(200,response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test2()
    {
        Response response=given().
                header("accept","application/json").
                get(baseURI+"employees/100");
        System.out.println(response.prettyPrint());
        assertEquals(200,response.getStatusCode());

        System.out.println("What kind of content server sends to you, in this response: "+ response.getHeader("Content-Type"));
    }

    @Test
    public void test3()
    {
        Response response=given().
                get(baseURI+"regions");
        assertEquals(200,response.getStatusCode());
        //to get specific header
        Header header=response.getHeaders().get("Content-Type");
        for (Header h:response.getHeaders())
        {
            System.out.println(h);
        }
        System.out.println(response.prettyPrint());
    }
}
