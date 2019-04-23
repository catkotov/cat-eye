package org.cat.eye.common.ignite.client.pool;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.junit.*;

import static org.junit.Assert.*;

public class CatEyeIgniteClientPoolTest {

    private static Ignite ignite;

    private static CatEyeIgniteClientPool pool;

    @BeforeClass
    public static void setUp() throws Exception {
        ignite = Ignition.start("default-config.xml");
        pool = new CatEyeIgniteClientPool(new String[] {"127.0.0.1:10800"}, 10, 10, 5);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (ignite != null) {
            ignite.close();
        }

        if (pool != null) {
            pool.close();
        }
    }

    @Test
    public void poolClientTest() throws Exception {
        assertNotNull(ignite);
        assertNotNull(pool);

        IgniteClient client_1 = pool.getClient();
        ClientCache<String, String> testCache_1 = client_1.getOrCreateCache("testCache");
        testCache_1.put("1", "1-1");

        IgniteClient client_3 = pool.getClient();
        ClientCache<String, String> testCache_3 = client_3.getOrCreateCache("testCache");
        testCache_3.put("2", "2-2");

        IgniteClient client_5 = pool.getClient();
        ClientCache<String, String> testCache_5 = client_5.getOrCreateCache("testCache");
        testCache_5.put("3", "3-3");

        IgniteClient client_7 = pool.getClient();
        ClientCache<String, String> testCache_7 = client_7.getOrCreateCache("testCache");
        testCache_7.put("4", "4-4");

        IgniteClient client_9 = pool.getClient();
        ClientCache<String, String> testCache_9 = client_9.getOrCreateCache("testCache");
        testCache_9.put("5", "5-5");

        IgniteClient client_2 = pool.getClient();
        ClientCache<String, String> testCache_2 = client_2.getOrCreateCache("testCache");
        String result = testCache_2.get("1");
        assertEquals(result, "1-1");

        IgniteClient client_4 = pool.getClient();
        ClientCache<String, String> testCache_4 = client_4.getOrCreateCache("testCache");
        result = testCache_4.get("2");
        assertEquals(result, "2-2");

        IgniteClient client_6 = pool.getClient();
        ClientCache<String, String> testCache_6 = client_6.getOrCreateCache("testCache");
        result = testCache_6.get("3");
        assertEquals(result, "3-3");

        IgniteClient client_8 = pool.getClient();
        ClientCache<String, String> testCache_8 = client_8.getOrCreateCache("testCache");
        result = testCache_8.get("4");
        assertEquals(result, "4-4");

        IgniteClient client_10 = pool.getClient();
        ClientCache<String, String> testCache_10 = client_10.getOrCreateCache("testCache");
        result = testCache_10.get("5");
        assertEquals(result, "5-5");

        pool.releaseClient(client_2);
        pool.releaseClient(client_4);
        pool.releaseClient(client_6);
        pool.releaseClient(client_8);
        pool.releaseClient(client_10);

        pool.releaseClient(client_1);
        pool.releaseClient(client_3);
        pool.releaseClient(client_5);
        pool.releaseClient(client_7);
        pool.releaseClient(client_9);
    }
}