package com.shizhuang.security.exception1;

import com.alibaba.fastjson2.JSON;
import com.onetool4j.exception.BizException;
import com.onetool4j.exception.ErrorCode;
import junit.framework.TestCase;

import java.time.Duration;
import java.util.Map;

public class BizExceptionTest extends TestCase {

    public void test() {
        System.out.println("A" + System.lineSeparator() + "B");
    }

    public void testBizException() {

        try {
            inner();
        } catch (Exception e) {
            throw BizException.ofMerge(ErrorCode.ofFail("json 解析异常"), e);
        }

    }

    public void testMergeBizException() {

        try {
//            inner();
            inner1();
        } catch (Exception e) {
            throw BizException.ofMerge(ErrorCode.ofFail("json 解析异常"), e);
        }

    }

    public void testBatchBizException() {
        System.setProperty("exception.merge.count.threshold", "10");

        for (int i = 0; i < 100; i++) {
            try{
                testMergeBizException();
            }catch (Throwable e){
                e.printStackTrace();
            }
        }

    }



    private void inner1() {
        try {
            inner();
        } catch (Exception e) {
            throw BizException.of(ErrorCode.ofFail("包装的 json 异常"), e);
        }
    }
    private void inner() {
        JSON.parseObject("{\n" +
                "\"type\":\"exec\"\n" +
                ",\"action\":\"snapshot\"\n" +
                ",\"localIp\":\"10.239.27.45\"\n" +
                ",\"localTime\":1704408744313\n" +
                ",\"serialNumber\":\"al-hzh-xdw-auth-dlb-10261825-001\"\n" +
                ",\"name\":\"tr\"\n" +
                ",\"cmd\":\"tr   \\\"\n" +
                ",\"pid\":3482\n" +
                ",\"state\":\"R (running)\"\n" +
                ",\"tid\":3482\n" +
                ",\"tgid\":3482\n" +
                ",\"uid\":0\n" +
                ",\"user\":\"root\"\n" +
                ",\"euid\":0\n" +
                ",\"suid\":0\n" +
                ",\"fsuid\":0\n" +
                ",\"gid\":\"0(root)\"\n" +
                ",\"egid\":0,\"sgid\":0\n" +
                ",\"fsgid\":0\n" +
                ",\"cwd\":\"\"\n" +
                ",\"env\":{}\n" +
                ",\"exe\":\"/host/proc/3482/exe\"\n" +
                ",\"ppid\":3480\n" +
                ",\"p_name\":\"ilogtaild\"\n" +
                ",\"p_uid\":0,\"p_euid\":0\n" +
                ",\"p_cmd\":\"/bin/bash /etc/init.d/ilogtaild restart\"\n" +
                ",\"pstree\":\"1(systemd)--3281(bash)--3459(ilogtaild)--3480(ilogtaild)--3482(tr)\"\n" +
                ",\"cmd_tree\":\"1(/sbin/init noibrs)--3281(/bin/bash /var/apiroute/logdelete.sh)--3459(/bin/bash /etc/init.d/ilogtaild restart)--3480(/bin/bash /etc/init.d/ilogtaild restart)--3482(tr   \\)\"\n" +
                ",\"start_time\":0\n" +
                ",\"is_docker\":false\n" +
                ",\"docker_name\":\"\"\n" +
                ",\"docker_ip\":\"\"\n" +
                ",\"netinfo\":{\n" +
                "    \"sip\":\"\",\n" +
                "    \"dip\":\"\",\n" +
                "    \"sport\":0,\n" +
                "    \"dport\":0,\n" +
                "    \"protocol\":\"\"\n" +
                "  }\n" +
                ",\"fd_info\":null,\n" +
                "\"sock_info\":null\n" +
                "}", Map.class);
    }
}