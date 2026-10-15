package com.rocket.myshop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import cn.jiebaba.summer.boot.SummerApplication;

/**
 * 端到端冒烟测试：真实启动 Summer 内嵌 HTTP 服务 + H2 内存库，
 * 覆盖 Controller -> Service -> Mapper(BaseMapper) -> DataSource 全链路。
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagerSmokeTest {

	private static final String H2_URL = "jdbc:h2:mem:myshop;DB_CLOSE_DELAY=-1";

	private SummerApplication app;
	private int port;
	private final HttpClient http = HttpClient.newHttpClient();

	@BeforeAll
	void startApp() throws Exception {
		System.setProperty("server.port", "0");
		System.setProperty("summer.datasource.url", H2_URL);
		System.setProperty("summer.datasource.driver-class-name", "org.h2.Driver");
		System.setProperty("summer.datasource.username", "sa");
		System.setProperty("summer.datasource.password", "");
		System.setProperty("summer.datasource.pool-size", "1");
		initH2Schema();
		app = SummerApplication.run(ManagerApplication.class, new String[0]);
		port = app.webServer().port();
	}

	@AfterAll
	void stopApp() {
		if (app != null) {
			app.webServer().stop();
			app.context().close();
		}
	}

	@Test
	void listDemoShouldReturnDataFromH2() throws Exception {
		HttpResponse<String> resp = http.send(
				HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/listDemo")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, resp.statusCode());
		assertTrue(resp.body().contains("\"total\""), resp.body());
		assertTrue(resp.body().contains("演示数据一"), resp.body());
	}

	@Test
	void listItemShouldReturnPagedData() throws Exception {
		HttpResponse<String> resp = http.send(
				HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/items/listItem.json"))
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString("{\"start\":0,\"length\":10}")).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, resp.statusCode());
		assertTrue(resp.body().contains("\"total\""), resp.body());
		assertTrue(resp.body().contains("迁移测试商品A"), resp.body());
	}

	@Test
	void listItemsCategoryShouldReturnActiveCategories() throws Exception {
		HttpResponse<String> resp = http.send(
				HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/items/listItemsCategory.json"))
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString("{}")).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, resp.statusCode());
		assertTrue(resp.body().contains("测试类别"), resp.body());
	}

	@Test
	void addItemsCategoryShouldRejectBlankName() throws Exception {
		HttpResponse<String> resp = http.send(
				HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/items/addItemsCategory.json"))
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString("{\"name\":\" \"}")).build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(400, resp.statusCode());
		assertTrue(resp.body().contains("violations"), resp.body());
	}

	/**
	 * 品牌 CRUD 全链路：新增 -> 重名被拒 -> 列表可见 -> 更新 -> 删除（软删）。
	 * H2 身份列从 1 起，种子数据 2 条品牌，故本用例新增的品牌 id 恒为 3。
	 */
	@Test
	void brandCrudRoundtripShouldWork() throws Exception {
		HttpResponse<String> addResp = postJson("/items/addItemsBrand.json",
				"{\"name\":\"冒烟品牌\",\"logo\":\"http://logo/1.png\"}");
		assertEquals(200, addResp.statusCode());
		assertTrue(addResp.body().contains("\"success\":true"), addResp.body());

		HttpResponse<String> dupResp = postJson("/items/addItemsBrand.json", "{\"name\":\"冒烟品牌\"}");
		assertTrue(dupResp.body().contains("品牌名称已存在"), dupResp.body());

		HttpResponse<String> listResp = postJson("/items/listItemsBrand.json", "{}");
		assertTrue(listResp.body().contains("冒烟品牌"), listResp.body());

		HttpResponse<String> updResp = postJson("/items/updateItemsBrand.json",
				"{\"id\":3,\"name\":\"冒烟品牌改\"}");
		assertTrue(updResp.body().contains("\"success\":true"), updResp.body());
		assertTrue(updResp.body().contains("成功更新1记录"), updResp.body());

		HttpResponse<String> delResp = postJson("/items/deleteItemsBrand.json?id=3", null);
		assertTrue(delResp.body().contains("\"success\":true"), delResp.body());
	}

	/**
	 * 商品 CRUD 全链路：新增 -> 空标题被拒 -> 详情 -> 更新 -> 删除（软删，详情仍可查但状态为 0）。
	 * 新增商品 id 恒为 3（种子数据 2 条）。
	 */
	@Test
	void itemCrudRoundtripShouldWork() throws Exception {
		HttpResponse<String> blankResp = postJson("/items/addItem", "{\"title\":\"  \",\"price\":1,\"num\":1}");
		assertTrue(blankResp.body().contains("商品名称不能为空"), blankResp.body());

		HttpResponse<String> addResp = postJson("/items/addItem",
				"{\"title\":\"冒烟商品\",\"price\":55.5,\"num\":3,\"itemCategoryName\":\"测试类别\",\"itemBrandName\":\"测试品牌\"}");
		assertEquals(200, addResp.statusCode());
		assertTrue(addResp.body().contains("\"success\":true"), addResp.body());

		HttpResponse<String> detailResp = get("/items/detail.json?id=3");
		assertEquals(200, detailResp.statusCode());
		assertTrue(detailResp.body().contains("冒烟商品"), detailResp.body());

		HttpResponse<String> updResp = postJson("/items/updateItem",
				"{\"id\":3,\"title\":\"冒烟商品改\",\"price\":66}");
		assertTrue(updResp.body().contains("成功更新1记录"), updResp.body());

		HttpResponse<String> delResp = postJson("/items/deleteItem?id=3", null);
		assertTrue(delResp.body().contains("\"success\":true"), delResp.body());

		HttpResponse<String> afterResp = get("/items/detail.json?id=3");
		assertTrue(afterResp.body().contains("\"status\":\"0\""), afterResp.body());
	}

	private HttpResponse<String> postJson(String path, String json) throws Exception {
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
		if (json == null) {
			builder.POST(HttpRequest.BodyPublishers.noBody());
		} else {
			builder.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(json));
		}
		return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> get(String path) throws Exception {
		return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
				HttpResponse.BodyHandlers.ofString());
	}

	@Test
	void userEndpointShouldWork() throws Exception {
		HttpResponse<String> resp = http.send(
				HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/users/jsmith")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(200, resp.statusCode());
		assertTrue(resp.body().contains("jsmith"), resp.body());
	}

	@Test
	void unmappedAjaxShouldReturn404Json() throws Exception {
		HttpResponse<String> resp = http.send(
				HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/not-exist.json")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(404, resp.statusCode());
		assertTrue(resp.body().contains("Not Found"), resp.body());
	}

	@Test
	void errorEndpointShouldUseMessageBundle() throws Exception {
		HttpResponse<String> resp = http.send(
				HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/error")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertEquals(500, resp.statusCode());
		assertTrue(resp.body().contains("Error.1"), resp.body());
		assertTrue(resp.body().contains("服务器异常，请联系管理员"), resp.body());
	}

	/**
	 * 初始化 H2 内存库表结构与数据（H2 Regular 模式语法，列名与生产 MySQL 一致）。
	 */
	private static void initH2Schema() throws Exception {
		try (Connection conn = DriverManager.getConnection(H2_URL, "sa", "");
				Statement st = conn.createStatement()) {
			st.execute("CREATE TABLE IF NOT EXISTS demo ("
					+ " id_ INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
					+ " name_ VARCHAR(40))");
			st.execute("CREATE TABLE IF NOT EXISTS shop_item ("
					+ " id_ INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
					+ " title_ VARCHAR(100) NOT NULL,"
					+ " sell_point_ VARCHAR(500),"
					+ " price_ DECIMAL(10,2) NOT NULL DEFAULT 0,"
					+ " num_ INT NOT NULL DEFAULT 0,"
					+ " barcode_ VARCHAR(40),"
					+ " img_logo_ VARCHAR(500),"
					+ " status_ CHAR(1) NOT NULL DEFAULT '1',"
					+ " create_time_ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ " update_time_ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ " seller_id_ VARCHAR(40),"
					+ " item_category_name_ VARCHAR(40),"
					+ " item_brand_name_ VARCHAR(40),"
					+ " sale_clients_ VARCHAR(100))");
			st.execute("CREATE TABLE IF NOT EXISTS shop_item_brand ("
					+ " id_ INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
					+ " name_ VARCHAR(40) NOT NULL,"
					+ " logo_ VARCHAR(500),"
					+ " status_ TINYINT NOT NULL DEFAULT 1,"
					+ " create_time_ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ " update_time_ TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
			st.execute("CREATE TABLE IF NOT EXISTS shop_item_category ("
					+ " id_ INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,"
					+ " name_ VARCHAR(40) NOT NULL,"
					+ " status_ TINYINT NOT NULL DEFAULT 1,"
					+ " create_time_ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ " update_time_ TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ " parent_id_ INT,"
					+ " items_num_ INT,"
					+ " rank_ SMALLINT,"
					+ " level_ TINYINT DEFAULT 1,"
					+ " sub_category_num_ SMALLINT DEFAULT 0)");
			st.execute("INSERT INTO demo (name_) VALUES ('演示数据一'), ('演示数据二')");
			st.execute("INSERT INTO shop_item (title_, sell_point_, price_, num_, status_, item_category_name_, item_brand_name_) VALUES"
					+ " ('Summer迁移测试商品A', '迁移冒烟测试', 99.00, 10, '1', '测试类别', '测试品牌'),"
					+ " ('Summer迁移测试商品B', '迁移冒烟测试', 199.00, 5, '1', '测试类别', '测试品牌')");
			st.execute("INSERT INTO shop_item_brand (name_, status_) VALUES ('测试品牌', 1), ('备用品牌', 1)");
			st.execute("INSERT INTO shop_item_category (name_, status_, level_, sub_category_num_) VALUES"
					+ " ('测试类别', 1, 1, 0), ('测试子类别', 1, 2, 0)");
			st.execute("UPDATE shop_item_category SET parent_id_ = (SELECT MIN(id_) FROM shop_item_category WHERE level_ = 1)"
					+ " WHERE name_ = '测试子类别'");
		}
	}
}
