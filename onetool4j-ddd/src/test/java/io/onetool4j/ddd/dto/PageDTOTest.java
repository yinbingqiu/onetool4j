package io.onetool4j.ddd.dto;

import com.alibaba.fastjson2.JSON;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;

public class PageDTOTest extends TestCase {
    @Test
    public void test() {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(1);
        objects.add(2);
        PageDTO<Object> of = PageDTO.of(objects, 10, 1, 10);
        System.out.println(JSON.toJSONString(of));
        PageDTO<Integer> objectPageDTO = PageDTO.copyAndConvert(of, (r) -> (Integer) r);
        System.out.println(JSON.toJSONString(objectPageDTO));
    }

}