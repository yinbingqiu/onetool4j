### QueryHandler

```java
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
        #parse("File Header.java")

import io.onetool4j.ddd.handler.QueryHandler;
import org.springframework.stereotype.Service;


/**
 * @description $Desccription
 * @author $USER
 * @date $DATE $TIME
 */
@Service
public class ${NAME}QueryHandler extends QueryHandler<$Req,$Rep> {
@Override
public $Rep query($Req request) {
        // 这里编写事件处理逻辑
        return null;
        }
        }
```

### PageQueryHandler

```java
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
#parse("File Header.java")

import io.onetool4j.ddd.handler.PageQueryHandler;
import io.onetool4j.ddd.dto.PageDTO;
import org.springframework.stereotype.Service;


/**
* @description $Desccription
* @author $USER
* @date $DATE $TIME
*/
@Service
public class ${NAME}PageQueryHandler extends PageQueryHandler<$Req,$Rep> {
    @Override
    public PageDTO<$Rep> query($Req request) {
        // 这里编写事件处理逻辑
        return null;
    }
}
```

### EventHandler

```java
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
        #parse("File Header.java")

import io.onetool4j.ddd.handler.EventHandler;
import org.springframework.stereotype.Service;


/**
 * @description $Desccription
 * @author $USER
 * @date $DATE $TIME
 */
@Service
public class ${NAME}EventHandler extends EventHandler<$Event> {
    @Override
    public void onEvent($Event event) {
        // 这里编写事件处理逻辑
    }
}
```

### CommandHandler

```java
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
        #parse("File Header.java")

import io.onetool4j.ddd.handler.CommandHandler;
import org.springframework.stereotype.Service;


/**
 * @description $Desccription
 * @author $USER
 * @date $DATE $TIME
 */
@Service
public class ${NAME}CommandHandler extends CommandHandler<$Req,$Rep> {
    @Override
    public $Rep execute($Req request) {
        // 这里编写事件处理逻辑
        return null;
    }
}
```

### PageQuery

```java
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
        #parse("File Header.java")

import io.onetool4j.ddd.dto.PageQuery;


/**
 * @description $Desccription
 * @author $USER
 * @date $DATE $TIME
 */
@Data
public class ${NAME} extends PageQuery{

}
```