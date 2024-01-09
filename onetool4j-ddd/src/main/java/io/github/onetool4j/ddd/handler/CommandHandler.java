package io.github.onetool4j.ddd.handler;

import io.github.onetool4j.ddd.dto.Command;

/**
 * 命令处理器
 *
 * @author admin
 */
public abstract class CommandHandler<REQ extends Command, REP> extends GenericHandler<REQ, REP> {
    /**
     * 处理命令
     *
     * @param request 命令
     * @return 命令执行结果
     */
    @Override
    REP doHandle(REQ request) {
        return execute(request);
    }

    /**
     * 执行命令
     *
     * @param request 命令
     * @return 命令执行结果
     */
    protected abstract REP execute(REQ request);
}
