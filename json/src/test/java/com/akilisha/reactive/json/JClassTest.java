package com.akilisha.reactive.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JClassTest {

    @Test
    void detect_class_is_has_no_super_class() throws IOException {
        Base a = new Base();
        JNode nodeValue = JClass.nodify(a);
        assertThat(nodeValue).isNotNull();
        assertThat(nodeValue.size()).isEqualTo(1);
        assertThat((String) nodeValue.getItem("a")).isEqualTo("A");
    }

    @Test
    void detect_class_is_has_super_class_depth_1() throws IOException {
        SubBase a = new SubBase();
        JNode nodeValue = JClass.nodify(a);
        assertThat(nodeValue).isNotNull();
        assertThat(nodeValue.size()).isEqualTo(2);
        assertThat((String) nodeValue.getItem("a")).isEqualTo("A");
        assertThat((String) nodeValue.getItem("aa")).isEqualTo("AA");
    }

    @Test
    void detect_class_is_has_super_class_depth_2() throws IOException {
        SubSubBase a = new SubSubBase();
        JNode nodeValue = JClass.nodify(a);
        assertThat(nodeValue).isNotNull();
        assertThat(nodeValue.size()).isEqualTo(3);
        assertThat((String) nodeValue.getItem("a")).isEqualTo("A");
        assertThat((String) nodeValue.getItem("aa")).isEqualTo("AA");
        assertThat((String) nodeValue.getItem("aaa")).isEqualTo("AAA");
    }

    @Test
    void detect_class_which_extends_a_collection_type() throws IOException {
        BaseList a = new BaseList();
        JNode nodeValue = JClass.nodify(a);
        assertThat(nodeValue).isNotNull();
        assertThat(nodeValue.size()).isEqualTo(1);
        assertThat(((JObject) nodeValue.getItem(0)).get("a")).isEqualTo("BL");
    }

    @Test
    void detect_class_which_extends_a_map_type() throws IOException {
        BaseMap a = new BaseMap();
        JNode nodeValue = JClass.nodify(a);
        assertThat(nodeValue).isNotNull();
        assertThat(nodeValue.size()).isEqualTo(6);
        assertThat(((JObject) ((JObject) nodeValue.getItem("BMMM")).get("1")).get("aaa")).isEqualTo("Thrice");
    }

    @Test
    void detect_class_which_extends_a_crazier_map_type() throws IOException {
        Map<String, Object> a = Map.of("1", new BaseMap(), "2", new BaseMap());
        JNode nodeValue = JClass.nodify(a);
        assertThat(nodeValue).isNotNull();
        assertThat(nodeValue.size()).isEqualTo(2);
        assertThat(((JObject) ((JObject) ((JObject) nodeValue.getItem("2")).getItem("BMMM")).get("1")).get("aaa")).isEqualTo("Thrice");
        assertThat((JNode) nodeValue.getItem("1")).isEqualTo(nodeValue.getItem("2"));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Base {

        static String sA = "sA";
        String a = "A";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class SubBase extends Base {

        static String sAA = "sAA";
        String aa = "AA";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class SubSubBase extends SubBase {

        static String sAAA = "sAAA";
        String aaa = "AAA";
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class BaseList extends ArrayList<Base> {

        public BaseList() {
            add(new Base("BL"));
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class BaseMap extends HashMap<String, Object> {

        public BaseMap() {
            put("BM", new SubBase("Twice"));
            put("BMM", new SubSubBase("Thrice"));
            put("BMMM", Map.of("1", new SubSubBase("Thrice")));
            put("BL", new BaseList());
            put("bool", "true");
            put("num", 100.44);
        }
    }
}