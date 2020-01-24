package com.automation.tests.day2;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
public class ExchangeRatesAPITests
{
    private String baseURI="http://api.openrates.io/";
    @Test
    public void test1()
    {
        Response response=given().get(baseURI+"latest");
        assertEquals(200,response.getStatusCode());
        System.out.println(response.prettyPrint());

    }

    //verify that content type is json
    @Test
    public void test2()
    {
        Response response=given().
                get(baseURI+"latest");
        assertEquals(200,response.getStatusCode());
        assertEquals("application/json",response.getHeader("Content-Type"));
        //or like this
        assertEquals("application/json", response.getContentType());
       // System.out.println(response.prettyPrint());
    }

    //GET https://api.exchangeratesapi.io/latest?base=USD HTTP/1.1
    //base it is a query parameter that will ask web service to change currency from eu to smthg else
    @Test
    public void test3()
    {
        //change base rate from euro to dollar
       // Response response = given().get(baseURI+"latest?base=USD");
        Response response = given().
                queryParam("base","USD").
                get(baseURI+"latest");
        assertEquals(200,response.getStatusCode());
        System.out.println(response.prettyPrint());

    }
    //verify that response body, for the latest currency rates, contains today's date(yyyy-mm-dd)
    @Test
    public void test4()
    {
        Response response = given().
                queryParam("base","TRY").
                get(baseURI+"latest");

        String todaysDate= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertTrue(response.getBody().asString().contains(todaysDate));

    }

    //get exchange rate for year 2000
    //GET https://api.exchangeratesapi.io/history?start_at=2018-01-01&end_at=2018-09-01&base=USD&symbols=ILS,JPY
    @Test
    public void test5()
    {
        Response response = given().
                queryParam("start_at", "2010-01-01").
                queryParam("end_at","2010-01-28").
                queryParam("base","USD").
                queryParam("symbols","TRY","EUR").
                get(baseURI + "history");
        System.out.println(response.prettyPrint());
    }
}
