package com.ecom.shopperstack.test;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import loginpojo.Login;

import static io.restassured.RestAssured.*;

public class ShopperStackLoginTest {
	int shopperId;
	String token="";
	int productId;

	@Test
	public void loginTest() {
		Login lp = new Login("priyakumargenius15@gmail.com", "Sp@123456", "SHOPPER");
		Response resp = given().contentType(ContentType.JSON).body(lp).when()
				.post("https://www.shoppersstack.com/shopping/users/login");
		resp.then().log().all();
		resp.then().statusCode(200);

		//resp.then().time(Matchers.both(Matchers.greaterThan(300L)).and(Matchers.lessThan(3000L)));
		shopperId = resp.jsonPath().get("data.userId");
		token = resp.jsonPath().get("data.jwtToken");

	}
	@Test//(dependsOnMethods = "loginTest")
	public void viewProduct()
	{
		Response resp=given().auth().oauth2(token).
				get("https://www.shoppersstack.com/shopping/products/alpha");
		resp.then().log().all();
		
		
	}
	@Test(dependsOnMethods = "loginTest")
	public void addToWishlist()
	{
		String add="{\r\n"
				+ "  \"productId\": 4420,\r\n"
				+ "  \"quantity\": 0\r\n"
				+ "}";
		Response resp=given().auth().oauth2(token).pathParam("shopperId",shopperId )
		.contentType(ContentType.JSON).body(add)
		.when().post("https://www.shoppersstack.com/shopping/shoppers/{shopperId}/wishlist");
		resp.then().log().all();
		resp.then().assertThat().statusCode(201);
		
	}
	@Test(dependsOnMethods = "loginTest")
	public void seeWishlist()
	{
		Response resp=given().auth().oauth2(token).pathParam("shopperId",shopperId )
		
		.when().get("https://www.shoppersstack.com/shopping/shoppers/{shopperId}/wishlist");
		resp.then().log().all();
		resp.then().assertThat().statusCode(200);
		
	}
	@Test(dependsOnMethods = "loginTest")
	public void deleteFromWishlist()
	{
		Response resp=given().auth().oauth2(token).pathParam("shopperId",shopperId ).pathParam("productId",4420 )
		.delete("https://www.shoppersstack.com/shopping/shoppers/{shopperId}/wishlist/{productId}");
		resp.then().log().all();
		resp.then().assertThat().statusCode(204);
		///shoppers/{shopperId}/wishlist/{productId}
	}
	
	
	

}
