package com.ecom.shopperstack.test;

import static io.restassured.RestAssured.given;

import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import loginpojo.Login;

public class CartTest {
	int shopperId;
	String token = "";
	int productID;
	int itemID;

	@Test
	public void loginTest() {
		Login lp = new Login("priyakumargenius15@gmail.com", "Sp@123456", "SHOPPER");
		Response resp = given().contentType(ContentType.JSON).body(lp).when()
				.post("https://www.shoppersstack.com/shopping/users/login");
		resp.then().log().all();
		resp.then().statusCode(200);

		// resp.then().time(Matchers.both(Matchers.greaterThan(300L)).and(Matchers.lessThan(3000L)));
		shopperId = resp.jsonPath().get("data.userId");
		token = resp.jsonPath().get("data.jwtToken");

	}

	@Test(dependsOnMethods = "loginTest")
	public void cartTest() {
		String pro = "{\r\n" + "  \"productId\": 4302,\r\n" + "  \"quantity\": 4\r\n" + "}";
		/// shoppers/{shopperId}/carts
		Response resp = given().auth().oauth2(token).pathParam("shopperId", shopperId).contentType(ContentType.JSON)
				.body(pro).when().post("https://www.shoppersstack.com/shopping/shoppers/{shopperId}/carts");
		resp.then().log().all();
		resp.then().assertThat().statusCode(201);
		productID = resp.jsonPath().get("data.productId");
		itemID = resp.jsonPath().get("data.itemId");

	}

	@Test(dependsOnMethods = "loginTest")
	public void viewCart() {
		Response resp = given().auth().oauth2(token).pathParam("shopperId", shopperId)

				.when().get("https://www.shoppersstack.com/shopping/shoppers/{shopperId}/carts");
		resp.then().log().all();
		resp.then().assertThat().statusCode(200);

	}

	@Test(dependsOnMethods = "cartTest")
	public void updateCart() {
		String body = "{\r\n" + "  \"productId\": 4302,\r\n" + "  \"quantity\": 1\r\n" + "}";
		Response resp = given().auth().oauth2(token).pathParam("shopperId", shopperId).pathParam("itemId", itemID)
				.contentType(ContentType.JSON).body(body).when()
				.put("https://www.shoppersstack.com/shopping/shoppers/{shopperId}/carts/{itemId}");
		resp.then().log().all();
		resp.then().assertThat().statusCode(200);

	}

	// @Test(dependsOnMethods = "updateCart")
	public void deleteProductFromCart() {
		Response resp = given().auth().oauth2(token).pathParam("shopperId", shopperId).pathParam("productId", productID)
				.when().delete("https://www.shoppersstack.com/shopping/shoppers/{shopperId}/carts/{productId}");
		resp.then().log().all();
		resp.then().statusCode(200);

	}

}
