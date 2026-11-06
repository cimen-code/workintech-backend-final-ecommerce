package com.workintech.ecommerce.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workintech.ecommerce.repository.StoreRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Gercek PostgreSQL uzerinde 17 endpoint'in uctan uca testi. Her test geri alinir. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiFlowIT {

    private static final String PUBLIC_PASSWORD = "123456";
    private static final String STRONG_PASSWORD = "Test!1234";
    private static final String VALID_IBAN = "TR330006100519786457841326";

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private StoreRepository storeRepository;
    @Autowired private EntityManager entityManager;

    private String body(Object value) throws Exception {
        return json.writeValueAsString(value);
    }

    private JsonNode read(MockHttpServletRequestBuilder request, int expectedStatus) throws Exception {
        String content = mvc.perform(request).andExpect(status().is(expectedStatus))
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return content.isEmpty() ? json.nullNode() : json.readTree(content);
    }

    private MockHttpServletRequestBuilder jsonRequest(MockHttpServletRequestBuilder builder, Object value)
            throws Exception {
        return builder.contentType(MediaType.APPLICATION_JSON).content(body(value));
    }

    private String login(String email, String password) throws Exception {
        return read(jsonRequest(post("/login"), Map.of("email", email, "password", password)), 200)
            .get("token").asText();
    }

    private String customerToken() throws Exception {
        return login("customer@commerce.com", PUBLIC_PASSWORD);
    }

    private String uniqueEmail() {
        return "it_" + UUID.randomUUID().toString().replace("-", "") + "@example.com";
    }

    private String newUserToken() throws Exception {
        String email = uniqueEmail();
        read(jsonRequest(post("/signup"), Map.of("name", "Other User", "email", email,
            "password", STRONG_PASSWORD, "role_id", 3)), 201);
        return login(email, STRONG_PASSWORD);
    }

    private Map<String, Object> addressBody() {
        Map<String, Object> address = new HashMap<>();
        address.put("title", "ev adresi");
        address.put("name", "Alişan");
        address.put("surname", "Karababa");
        address.put("phone", "05376845834");
        address.put("city", "istanbul");
        address.put("district", "esenler");
        address.put("neighborhood", "mahalle");
        address.put("address", "sokak ve kapı no");
        return address;
    }

    private long createAddress(String token) throws Exception {
        return read(jsonRequest(post("/user/address"), addressBody()).header(HttpHeaders.AUTHORIZATION, token), 201)
            .get("id").asLong();
    }

    private Map<String, Object> orderBody(long addressId) {
        Map<String, Object> order = new HashMap<>();
        order.put("address_id", addressId);
        order.put("order_date", "2026-11-02T20:26:10");
        order.put("card_no", 4111111111111111L);
        order.put("card_name", "Ali Baş");
        order.put("card_expire_month", 12);
        order.put("card_expire_year", 2030);
        order.put("card_ccv", 321);
        order.put("price", 175.98);
        order.put("products", List.of(Map.of("product_id", 254, "count", 2, "detail", "siyah - lg")));
        return order;
    }

    // ---------------- Katalog ----------------

    @Test
    void rolesAreSeeded() throws Exception {
        mvc.perform(get("/roles")).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].code").value("admin"))
            .andExpect(jsonPath("$[1].code").value("store"))
            .andExpect(jsonPath("$[2].code").value("customer"));
    }

    @Test
    void categoriesMatchFrontendFields() throws Exception {
        JsonNode categories = read(get("/categories"), 200);
        assertEquals(14, categories.size());
        JsonNode second = categories.get(1);
        assertEquals("k:ayakkabi", second.get("code").asText());
        assertEquals(4.9, second.get("rating").asDouble());
        assertTrue(second.get("rating").isNumber());
        assertEquals("k", second.get("gender").asText());
        assertTrue(second.hasNonNull("img"));
        assertFalse(second.has("image"));
    }

    @Test
    void defaultProductListUsesCardDefaults() throws Exception {
        JsonNode result = read(get("/products"), 200);
        assertEquals(587, result.get("total").asInt());
        assertEquals(25, result.get("products").size());
        assertEquals(2, result.get("products").get(0).get("id").asInt());
    }

    @Test
    void productJsonMatchesWorkintechShape() throws Exception {
        mvc.perform(get("/products/254")).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(254))
            .andExpect(jsonPath("$.name").value("Kadın Siyah Zincirli"))
            .andExpect(jsonPath("$.price").value(449.0))
            .andExpect(jsonPath("$.stock").value(86))
            .andExpect(jsonPath("$.store_id").value(1))
            .andExpect(jsonPath("$.category_id").value(2))
            .andExpect(jsonPath("$.rating").value(2.02))
            .andExpect(jsonPath("$.sell_count").value(357))
            .andExpect(jsonPath("$.images[0].index").value(0))
            .andExpect(jsonPath("$.images[0].url").isNotEmpty());
    }

    @Test
    void filtersMatchRealApiTotals() throws Exception {
        assertEquals(127, read(get("/products?category=2&limit=1"), 200).get("total").asInt());
        assertEquals(52, read(get("/products?category=2&filter=siyah&limit=1"), 200).get("total").asInt());
        assertEquals(52, read(get("/products?category=2&filter=SIYAH&limit=1"), 200).get("total").asInt());
        assertEquals(0, read(get("/products?category=5"), 200).get("total").asInt());
    }

    @Test
    void sortAndPaginationMatchRealApi() throws Exception {
        JsonNode sorted = read(get("/products?category=2&filter=siyah&sort=price:desc&limit=2"), 200);
        assertEquals(4499.0, sorted.get("products").get(0).get("price").asDouble());
        assertEquals(1099.0, sorted.get("products").get(1).get("price").asDouble());

        JsonNode paged = read(get("/products?limit=2&offset=2"), 200);
        assertEquals(587, paged.get("total").asInt());
        assertEquals(4, paged.get("products").get(0).get("id").asInt());
        assertEquals(5, paged.get("products").get(1).get("id").asInt());
    }

    @Test
    void ratingSortIsDescending() throws Exception {
        JsonNode products = read(get("/products?category=2&sort=rating:desc&limit=9"), 200).get("products");
        assertEquals(9, products.size());
        List<Double> ratings = new ArrayList<>();
        products.forEach(product -> ratings.add(product.get("rating").asDouble()));
        for (int i = 1; i < ratings.size(); i++) {
            assertTrue(ratings.get(i - 1) >= ratings.get(i));
        }
    }

    @Test
    void filterFollowsWorkintechRules() throws Exception {
        assertEquals(12, total("%100"));
        assertEquals(587, total("_"));
        assertEquals(113, total("siyah "));
        assertEquals(11, total("Ş"));
    }

    private int total(String filter) throws Exception {
        return read(get("/products").param("filter", filter).param("limit", "1"), 200).get("total").asInt();
    }

    @Test
    void catalogParametersFollowWorkintechRules() throws Exception {
        assertEquals(587, read(get("/products?limit=abc"), 200).get("products").size());
        assertEquals(0, read(get("/products?limit=0"), 200).get("products").size());
        assertEquals(1, read(get("/products?limit=1.5"), 200).get("products").size());
        assertEquals(0, read(get("/products?category=abc"), 200).get("total").asInt());
        assertEquals(14, read(get("/products?offset=12.5"), 200).get("products").get(0).get("id").asInt());
        assertEquals(78, read(get("/products?sort=price"), 200).get("products").get(0).get("id").asInt());
    }

    @Test
    void invalidCatalogRequestsAreClientErrors() throws Exception {
        read(get("/products/99999999"), 404);
        read(get("/products/abc"), 400);
        read(get("/products?sort=abc"), 400);
        read(get("/products?offset=-1"), 400);
    }

    @Test
    void deletingMissingRecordAnswersLikeWorkintech() throws Exception {
        String token = newUserToken();
        assertEquals("There is no address record found by id 99999999",
            read(delete("/user/address/99999999").header(HttpHeaders.AUTHORIZATION, token), 403).get("error").asText());
        assertEquals("There is no address record found by id abc",
            read(delete("/user/address/abc").header(HttpHeaders.AUTHORIZATION, token), 403).get("error").asText());
        assertEquals("There is no card record found by id: 99999999",
            read(delete("/user/card/99999999").header(HttpHeaders.AUTHORIZATION, token), 403).get("error").asText());
    }

    @Test
    void unknownPathIsNotFound() throws Exception {
        read(get("/nope"), 404);
    }

    // ---------------- Giris ----------------

    @Test
    void publicUserCanLoginWithoutPasswordLeak() throws Exception {
        JsonNode result = read(jsonRequest(post("/login"),
            Map.of("email", "customer@commerce.com", "password", PUBLIC_PASSWORD)), 200);
        assertEquals("Cust Omer", result.get("name").asText());
        assertEquals("customer@commerce.com", result.get("email").asText());
        assertEquals(3, result.get("role_id").asInt());
        assertTrue(result.hasNonNull("token"));
        assertFalse(result.has("password"));
    }

    @Test
    void loginErrors() throws Exception {
        JsonNode wrong = read(jsonRequest(post("/login"),
            Map.of("email", "customer@commerce.com", "password", "wrong")), 401);
        assertEquals("Invalid email or password", wrong.get("message").asText());
        read(jsonRequest(post("/login"), Map.of()), 400);
    }

    @Test
    void verifyUsesRawTokenWithoutBearer() throws Exception {
        String token = customerToken();
        JsonNode result = read(get("/verify").header(HttpHeaders.AUTHORIZATION, token), 200);
        assertEquals("Cust Omer", result.get("name").asText());
        assertTrue(result.hasNonNull("token"));

        assertEquals("Not verified",
            read(get("/verify").header(HttpHeaders.AUTHORIZATION, "Bearer " + token), 401).get("message").asText());
        assertEquals("Not verified",
            read(get("/verify").header(HttpHeaders.AUTHORIZATION, "garbage"), 401).get("message").asText());
        assertEquals("No token", read(get("/verify"), 401).get("message").asText());
    }

    @Test
    void customerSignupThenLogin() throws Exception {
        String email = uniqueEmail();
        read(jsonRequest(post("/signup"), Map.of("name", "Test User", "email", email,
            "password", STRONG_PASSWORD, "role_id", 3)), 201);
        assertNotNull(login(email, STRONG_PASSWORD));
    }

    @Test
    void storeSignupSavesStore() throws Exception {
        long before = storeRepository.count();
        read(jsonRequest(post("/signup"), Map.of("name", "Store Owner", "email", uniqueEmail(),
            "password", STRONG_PASSWORD, "role_id", 2,
            "store", Map.of("name", "Mağazam", "phone", "05551234567", "tax_no", "T1234V123456",
                "bank_account", VALID_IBAN))), 201);
        assertEquals(before + 1, storeRepository.count());
    }

    @Test
    void signupValidation() throws Exception {
        read(jsonRequest(post("/signup"), Map.of()), 400);
        read(jsonRequest(post("/signup"), Map.of("name", "Te", "email", uniqueEmail(),
            "password", STRONG_PASSWORD, "role_id", 3)), 400);
        read(jsonRequest(post("/signup"), Map.of("name", "Test User", "email", "not-an-email",
            "password", STRONG_PASSWORD, "role_id", 3)), 400);
        read(jsonRequest(post("/signup"), Map.of("name", "Test User", "email", uniqueEmail(),
            "password", "weakpass", "role_id", 3)), 400);
        read(jsonRequest(post("/signup"), Map.of("name", "Test User", "email", uniqueEmail(),
            "password", STRONG_PASSWORD, "role_id", 99)), 400);
        read(jsonRequest(post("/signup"), Map.of("name", "Test User", "email", uniqueEmail(),
            "password", STRONG_PASSWORD, "role_id", 2)), 400);
    }

    @Test
    void duplicateEmailIsConflict() throws Exception {
        Map<String, Object> signup = Map.of("name", "Test User", "email", "CUSTOMER@commerce.com",
            "password", STRONG_PASSWORD, "role_id", 3);
        read(jsonRequest(post("/signup"), signup), 409);
    }

    // ---------------- Korumali yollar ----------------

    @Test
    void protectedPathsRequireToken() throws Exception {
        assertEquals("User is not verified", read(get("/user/address"), 401).get("message").asText());
        read(get("/user/card"), 401);
        read(get("/order"), 401);
        read(jsonRequest(post("/order"), Map.of()), 401);
        read(delete("/user/address/1"), 401);
    }

    // ---------------- Adres ----------------

    @Test
    void addressCrud() throws Exception {
        String token = newUserToken();
        assertEquals(0, read(get("/user/address").header(HttpHeaders.AUTHORIZATION, token), 200).size());

        JsonNode created = read(jsonRequest(post("/user/address"), addressBody())
            .header(HttpHeaders.AUTHORIZATION, token), 201);
        long id = created.get("id").asLong();
        assertTrue(created.hasNonNull("user_id"));
        assertEquals("sokak ve kapı no", created.get("address").asText());

        Map<String, Object> update = addressBody();
        update.put("id", id);
        update.put("title", "iş adresi");
        read(jsonRequest(put("/user/address"), update).header(HttpHeaders.AUTHORIZATION, token), 200);

        JsonNode list = read(get("/user/address").header(HttpHeaders.AUTHORIZATION, token), 200);
        assertEquals(1, list.size());
        assertEquals("iş adresi", list.get(0).get("title").asText());

        read(delete("/user/address/" + id).header(HttpHeaders.AUTHORIZATION, token), 200);
        assertEquals(0, read(get("/user/address").header(HttpHeaders.AUTHORIZATION, token), 200).size());
    }

    @Test
    void addressWithoutOptionalDetailIsAccepted() throws Exception {
        Map<String, Object> address = addressBody();
        address.remove("address");
        read(jsonRequest(post("/user/address"), address)
            .header(HttpHeaders.AUTHORIZATION, newUserToken()), 201);
    }

    @Test
    void addressMissingFieldIsRejected() throws Exception {
        Map<String, Object> address = addressBody();
        address.remove("city");
        read(jsonRequest(post("/user/address"), address)
            .header(HttpHeaders.AUTHORIZATION, newUserToken()), 400);
    }

    @Test
    void usersCannotTouchEachOthersAddress() throws Exception {
        String owner = newUserToken();
        String other = newUserToken();
        long id = createAddress(owner);

        Map<String, Object> update = addressBody();
        update.put("id", id);
        read(jsonRequest(put("/user/address"), update).header(HttpHeaders.AUTHORIZATION, other), 404);
        read(delete("/user/address/" + id).header(HttpHeaders.AUTHORIZATION, other), 403);
        assertEquals(0, read(get("/user/address").header(HttpHeaders.AUTHORIZATION, other), 200).size());
        assertEquals(1, read(get("/user/address").header(HttpHeaders.AUTHORIZATION, owner), 200).size());
    }

    // ---------------- Kart ----------------

    @Test
    void cardCrudWithStringIdFromFrontend() throws Exception {
        String token = newUserToken();
        JsonNode created = read(jsonRequest(post("/user/card"), Map.of("card_no", "4111111111111111",
            "expire_month", 12, "expire_year", 2030, "name_on_card", "Ali Baş"))
            .header(HttpHeaders.AUTHORIZATION, token), 201);
        long id = created.get("id").asLong();
        assertTrue(created.get("card_no").isTextual());
        assertTrue(created.get("expire_month").isNumber());

        JsonNode updated = read(jsonRequest(put("/user/card"), Map.of("id", String.valueOf(id),
            "card_no", "4111111111111111", "expire_month", 1, "expire_year", 2031, "name_on_card", "Ahmet Taş"))
            .header(HttpHeaders.AUTHORIZATION, token), 200);
        assertEquals("Ahmet Taş", updated.get("name_on_card").asText());

        assertEquals(1, read(get("/user/card").header(HttpHeaders.AUTHORIZATION, token), 200).size());
        read(delete("/user/card/" + id).header(HttpHeaders.AUTHORIZATION, token), 200);
        assertEquals(0, read(get("/user/card").header(HttpHeaders.AUTHORIZATION, token), 200).size());
    }

    @Test
    void invalidCardIsRejected() throws Exception {
        String token = newUserToken();
        read(jsonRequest(post("/user/card"), Map.of("card_no", "1234", "expire_month", 12,
            "expire_year", 2030, "name_on_card", "Ali")).header(HttpHeaders.AUTHORIZATION, token), 400);
        read(jsonRequest(post("/user/card"), Map.of("card_no", "4111111111111111", "expire_month", 13,
            "expire_year", 2030, "name_on_card", "Ali")).header(HttpHeaders.AUTHORIZATION, token), 400);
    }

    @Test
    void usersCannotTouchEachOthersCard() throws Exception {
        String owner = newUserToken();
        long id = read(jsonRequest(post("/user/card"), Map.of("card_no", "4111111111111111",
            "expire_month", 12, "expire_year", 2030, "name_on_card", "Ali"))
            .header(HttpHeaders.AUTHORIZATION, owner), 201).get("id").asLong();
        read(delete("/user/card/" + id).header(HttpHeaders.AUTHORIZATION, newUserToken()), 403);
    }

    // ---------------- Siparis ----------------

    @Test
    void orderFlowMatchesFrontend() throws Exception {
        String token = newUserToken();
        long addressId = createAddress(token);

        JsonNode created = read(jsonRequest(post("/order"), orderBody(addressId))
            .header(HttpHeaders.AUTHORIZATION, token), 201);
        assertTrue(created.hasNonNull("id"));

        entityManager.flush();
        entityManager.clear();

        JsonNode orders = read(get("/order").header(HttpHeaders.AUTHORIZATION, token), 200);
        assertEquals(1, orders.size());
        JsonNode order = orders.get(0);
        assertEquals("2026-11-02T20:26:10", order.get("order_date").asText());
        assertEquals(175.98, order.get("price").asDouble());
        assertEquals(addressId, order.get("address_id").asLong());
        assertEquals("Ali Baş", order.get("card_name").asText());

        JsonNode item = order.get("products").get(0);
        assertEquals(254, item.get("id").asInt());
        assertEquals("Kadın Siyah Zincirli", item.get("name").asText());
        assertEquals(449.0, item.get("price").asDouble());
        assertEquals(2, item.get("count").asInt());
        assertTrue(item.hasNonNull("description"));
        assertFalse(item.get("images").isEmpty());
    }

    @Test
    void orderKeepsCardDetailsButNeverCcv() throws Exception {
        String token = newUserToken();
        JsonNode created = read(jsonRequest(post("/order"), orderBody(createAddress(token)))
            .header(HttpHeaders.AUTHORIZATION, token), 201);
        assertEquals(4111111111111111L, created.get("card_no").asLong());
        assertTrue(created.hasNonNull("card_expire_month"));
        assertTrue(created.hasNonNull("user_id"));
        assertFalse(created.has("card_ccv"));
    }

    @Test
    void orderValidation() throws Exception {
        String token = newUserToken();
        long addressId = createAddress(token);

        Map<String, Object> noProducts = orderBody(addressId);
        noProducts.put("products", List.of());
        read(jsonRequest(post("/order"), noProducts).header(HttpHeaders.AUTHORIZATION, token), 400);

        Map<String, Object> badCount = orderBody(addressId);
        badCount.put("products", List.of(Map.of("product_id", 254, "count", 0, "detail", "x")));
        read(jsonRequest(post("/order"), badCount).header(HttpHeaders.AUTHORIZATION, token), 400);

        Map<String, Object> unknownProduct = orderBody(addressId);
        unknownProduct.put("products", List.of(Map.of("product_id", 99999999, "count", 1, "detail", "x")));
        read(jsonRequest(post("/order"), unknownProduct).header(HttpHeaders.AUTHORIZATION, token), 404);

        read(jsonRequest(post("/order"), orderBody(addressId))
            .header(HttpHeaders.AUTHORIZATION, newUserToken()), 404);
    }

    @Test
    void ordersAreVisibleOnlyToOwner() throws Exception {
        String token = newUserToken();
        read(jsonRequest(post("/order"), orderBody(createAddress(token)))
            .header(HttpHeaders.AUTHORIZATION, token), 201);
        assertEquals(0, read(get("/order").header(HttpHeaders.AUTHORIZATION, newUserToken()), 200).size());
    }

    @Test
    void orderSurvivesAddressDeletion() throws Exception {
        String token = newUserToken();
        long addressId = createAddress(token);
        read(jsonRequest(post("/order"), orderBody(addressId)).header(HttpHeaders.AUTHORIZATION, token), 201);

        entityManager.flush();
        entityManager.clear();
        read(delete("/user/address/" + addressId).header(HttpHeaders.AUTHORIZATION, token), 200);
        entityManager.flush();
        entityManager.clear();

        JsonNode orders = read(get("/order").header(HttpHeaders.AUTHORIZATION, token), 200);
        assertEquals(1, orders.size());
        assertEquals(addressId, orders.get(0).get("address_id").asLong());
    }

    // ---------------- CORS ----------------

    @Test
    void preflightAllowsFrontendOrigins() throws Exception {
        for (String origin : List.of("https://workintech-frontend-final-ecommerce.vercel.app", "http://localhost:5173")) {
            mvc.perform(options("/order")
                    .header(HttpHeaders.ORIGIN, origin)
                    .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                    .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin));
        }
    }

    @Test
    void preflightRejectsOtherOrigins() throws Exception {
        mvc.perform(options("/order")
                .header(HttpHeaders.ORIGIN, "https://evil.example.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
            .andExpect(status().isForbidden());
    }
}
